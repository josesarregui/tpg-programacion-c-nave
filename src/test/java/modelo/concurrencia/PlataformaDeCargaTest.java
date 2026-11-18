package modelo.concurrencia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static modelo.concurrencia.RegistroDeEventos.LIMITE_MS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del monitor PlataformaDeCarga (Paso 5 de la E2): contratos, bloqueo con la plataforma llena o vacía,
 * notifyAll() + while, interrupción durante la espera y varios productores y consumidores a la vez.
 *
 * No dependen de pausas fijas: cada aviso de espera se publica con el monitor tomado, justo antes de wait(). Por eso,
 * una vez recibido el aviso, cualquier método synchronized de la plataforma (getBaterias, depositarBateria,
 * retirarBateria) recién entra cuando el hilo que espera liberó el monitor con wait().
 */
public class PlataformaDeCargaTest {

    private static Thread lanzar(String nombre, Accion accion) {
        Thread hilo = new Thread(() -> {
            try {
                accion.ejecutar();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, nombre);
        hilo.start();
        return hilo;
    }

    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws InterruptedException;
    }

    @Test
    @DisplayName("Plataforma se inicializa con capacidad válida y sin baterías")
    public void testInicializacion() {
        PlataformaDeCarga p = new PlataformaDeCarga(3);
        assertEquals(3, p.getCapacidadMaxima());
        assertEquals(0, p.getBaterias());
        assertEquals(PlataformaDeCarga.CAPACIDAD_POR_DEFECTO, new PlataformaDeCarga().getCapacidadMaxima());

        assertThrows(IllegalArgumentException.class, () -> new PlataformaDeCarga(0));
        assertThrows(IllegalArgumentException.class, () -> new PlataformaDeCarga(-1));
    }

    @Test
    @DisplayName("Postcondiciones: depositar suma una batería y retirar resta una")
    public void testDepositarYRetirar() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(3);

        p.depositarBateria("G-1");
        assertEquals(1, p.getBaterias());
        p.depositarBateria("G-2");
        assertEquals(2, p.getBaterias());
        p.retirarBateria("Nave-1");
        assertEquals(1, p.getBaterias());
    }

    @Test
    @DisplayName("Precondiciones: un identificador nulo o vacío se rechaza sin publicar ni cambiar el stock")
    public void testPrecondicionesDeIdentificador() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(2);
        p.depositarBateria("G-1");
        RegistroDeEventos registro = new RegistroDeEventos(p);

        assertThrows(AssertionError.class, () -> p.depositarBateria(null));
        assertThrows(AssertionError.class, () -> p.depositarBateria("  "));
        assertThrows(AssertionError.class, () -> p.retirarBateria(null));
        assertThrows(AssertionError.class, () -> p.retirarBateria(""));

        assertEquals(1, p.getBaterias());
        assertTrue(registro.mensajes().isEmpty());
    }

    @Test
    @DisplayName("Plataforma vacía: la nave queda bloqueada en wait() hasta que una generadora deposita")
    public void testPlataformaVaciaBloqueaAlConsumidor() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(2);
        RegistroDeEventos registro = new RegistroDeEventos(p);
        AtomicBoolean retiro = new AtomicBoolean(false);

        Thread nave = lanzar("Nave-1", () -> {
            p.retirarBateria("Nave-1");
            retiro.set(true);
        });

        assertTrue(registro.esperar("[Nave-1] esperando por falta de baterías en la Plataforma.", 1));
        // Consultar el stock exige el monitor: la nave ya lo liberó con wait() y sigue sin retirar.
        assertEquals(0, p.getBaterias());
        assertFalse(retiro.get());
        assertTrue(nave.isAlive());

        p.depositarBateria("G-1");

        nave.join(LIMITE_MS);
        assertFalse(nave.isAlive());
        assertTrue(retiro.get());
        assertEquals(0, p.getBaterias());
        assertTrue(registro.posicion("Batería depositada por [G-1] en la Plataforma.")
                < registro.posicion("Batería retirada y utilizada por [Nave-1]."));
    }

    @Test
    @DisplayName("Plataforma llena: la generadora queda bloqueada en wait() hasta que una nave retira")
    public void testPlataformaLlenaBloqueaAlProductor() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(1);
        p.depositarBateria("G-1");
        RegistroDeEventos registro = new RegistroDeEventos(p);
        AtomicBoolean deposito = new AtomicBoolean(false);

        Thread generadora = lanzar("G-2", () -> {
            p.depositarBateria("G-2");
            deposito.set(true);
        });

        assertTrue(registro.esperar("[G-2] esperando a que se libere espacio en la Plataforma.", 1));
        assertEquals(1, p.getBaterias());
        assertFalse(deposito.get());
        assertTrue(generadora.isAlive());

        p.retirarBateria("Nave-1");

        generadora.join(LIMITE_MS);
        assertFalse(generadora.isAlive());
        assertTrue(deposito.get());
        assertEquals(1, p.getBaterias());
        assertTrue(registro.posicion("Batería retirada y utilizada por [Nave-1].")
                < registro.posicion("Batería depositada por [G-2] en la Plataforma."));
    }

    @Test
    @DisplayName("notifyAll(): un depósito despierta a todas las naves en espera; una retira y las demás reevalúan el while y vuelven a esperar")
    public void testNotifyAllDespiertaATodosLosQueEsperan() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(3);
        RegistroDeEventos registro = new RegistroDeEventos(p);
        String espera = " esperando por falta de baterías en la Plataforma.";
        List<Thread> naves = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            String id = "Nave-" + i;
            naves.add(lanzar(id, () -> p.retirarBateria(id)));
        }
        for (int i = 1; i <= 3; i++) {
            assertTrue(registro.esperar("[Nave-" + i + "]" + espera, 1));
        }

        p.depositarBateria("G-1");

        // Con notify() despertaría una sola: nunca se llegaría a 5 avisos de espera.
        assertTrue(registro.esperarQueContengan("Batería retirada y utilizada por", 1));
        assertTrue(registro.esperarQueContengan(espera, 5),
                "Las dos naves que no consiguieron la batería deben volver a evaluar la guarda: " + registro.mensajes());
        assertEquals(0, p.getBaterias());

        p.depositarBateria("G-1");
        p.depositarBateria("G-1");
        for (Thread nave : naves) {
            nave.join(LIMITE_MS);
            assertFalse(nave.isAlive());
        }
        assertEquals(3, registro.contarQueContengan("Batería retirada y utilizada por"));
        assertEquals(0, p.getBaterias());
    }

    @Test
    @DisplayName("Interrumpir un hilo en wait() lanza InterruptedException, no cambia el stock y la plataforma sigue operativa")
    public void testInterrumpirEnEsperaNoCambiaElStock() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(1);
        RegistroDeEventos registro = new RegistroDeEventos(p);
        AtomicBoolean interrumpida = new AtomicBoolean(false);

        Thread nave = new Thread(() -> {
            try {
                p.retirarBateria("Nave-1");
            } catch (InterruptedException e) {
                interrumpida.set(true);
                Thread.currentThread().interrupt();
            }
        });
        nave.start();
        assertTrue(registro.esperar("[Nave-1] esperando por falta de baterías en la Plataforma.", 1));

        nave.interrupt();
        nave.join(LIMITE_MS);

        assertFalse(nave.isAlive());
        assertTrue(interrumpida.get());
        assertEquals(0, p.getBaterias());
        assertEquals(0, registro.contarQueContengan("Batería retirada"));

        p.depositarBateria("G-1");
        assertEquals(1, p.getBaterias());
    }

    @Test
    @DisplayName("Varios productores y consumidores a la vez: el stock nunca supera la capacidad ni baja de 0 y no se pierde ninguna batería")
    public void testVariosProductoresYConsumidoresRespetanLaCapacidad() throws Exception {
        final int capacidad = 2;
        final int productores = 4;
        final int consumidores = 4;
        final int vueltas = 200;
        PlataformaDeCarga p = new PlataformaDeCarga(capacidad);
        RegistroDeEventos registro = new RegistroDeEventos(p);
        CountDownLatch largada = new CountDownLatch(1);
        AtomicReference<Throwable> falla = new AtomicReference<>();
        List<Thread> hilos = new ArrayList<>();

        for (int i = 1; i <= productores; i++) {
            String id = "G-" + i;
            hilos.add(new Thread(() -> repetir(largada, falla, vueltas, () -> p.depositarBateria(id)), id));
        }
        for (int i = 1; i <= consumidores; i++) {
            String id = "Nave-" + i;
            hilos.add(new Thread(() -> repetir(largada, falla, vueltas, () -> p.retirarBateria(id)), id));
        }
        for (Thread hilo : hilos) {
            hilo.start();
        }
        largada.countDown();
        for (Thread hilo : hilos) {
            hilo.join(LIMITE_MS * 2);
            assertFalse(hilo.isAlive(), "El hilo " + hilo.getName() + " no terminó: posible bloqueo.");
        }

        assertNull(falla.get(), "Un hilo violó un contrato del monitor.");
        assertEquals(0, p.getBaterias());
        assertTrue(registro.stockMinimo() >= 0);
        assertTrue(registro.stockMaximo() <= capacidad);
        assertEquals(productores * vueltas, registro.contarQueContengan("Batería depositada por"));
        assertEquals(consumidores * vueltas, registro.contarQueContengan("Batería retirada y utilizada por"));
    }

    /** Espera la largada y repite la acción; registra cualquier violación de contrato para que la prueba falle. */
    private static void repetir(CountDownLatch largada, AtomicReference<Throwable> falla, int vueltas, Accion accion) {
        try {
            largada.await();
            for (int v = 0; v < vueltas; v++) {
                accion.ejecutar();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (RuntimeException | Error e) {
            falla.set(e);
        }
    }
}
