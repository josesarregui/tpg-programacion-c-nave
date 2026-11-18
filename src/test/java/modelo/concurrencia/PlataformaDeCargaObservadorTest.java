package modelo.concurrencia;

import modelo.universo.Observador;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del patrón Observer con la PlataformaDeCarga como sujeto (Paso 4 de la E2, Observable / Observador
 * de la cátedra, Versión 2). Verifican los mensajes de ejemplo del Enunciado (push), el stock que el
 * observador consulta con getBaterias() en el momento de cada evento (pull) y que no haya duplicados.
 */
public class PlataformaDeCargaObservadorTest {

    /**
     * Observador de prueba que se vincula él mismo con la plataforma y, por cada evento, guarda el mensaje y el
     * stock consultado. Los eventos pueden llegar desde otros hilos: los accesos se sincronizan.
     */
    private static class Registro implements Observador {
        private final PlataformaDeCarga observada;
        private final List<String> mensajes = new ArrayList<>();
        private final List<Integer> stocks = new ArrayList<>();
        private final String esperado;
        private final CountDownLatch llegoEsperado = new CountDownLatch(1);

        Registro(PlataformaDeCarga observada, String esperado) {
            this.observada = observada;
            this.esperado = esperado;
            observada.agregarObservador(this);
        }

        Registro(PlataformaDeCarga observada) {
            this(observada, null);
        }

        @Override
        public synchronized void actualizar(String mensaje) {
            mensajes.add(mensaje);
            stocks.add(observada.getBaterias());
            if (mensaje.equals(esperado)) {
                llegoEsperado.countDown();
            }
        }

        synchronized List<String> mensajes() {
            return new ArrayList<>(mensajes);
        }

        synchronized int stockAl(String mensaje) {
            return stocks.get(mensajes.indexOf(mensaje));
        }

        boolean esperar() throws InterruptedException {
            return llegoEsperado.await(2, TimeUnit.SECONDS);
        }
    }

    @Test
    @DisplayName("Producción: depositar publica, en el orden del Enunciado, la batería producida, la solicitud y el depósito; el stock nuevo es consultable")
    public void testProduccion() throws Exception {
        PlataformaDeCarga plataforma = new PlataformaDeCarga(3);
        Registro registro = new Registro(plataforma);

        plataforma.depositarBateria("G-1");

        assertEquals(List.of("Batería producida por [G-1].", "[G-1] solicitando acceso a la Plataforma.",
                "Batería depositada por [G-1] en la Plataforma."), registro.mensajes());
        assertEquals(0, registro.stockAl("Batería producida por [G-1]."));
        assertEquals(0, registro.stockAl("[G-1] solicitando acceso a la Plataforma."));
        assertEquals(1, registro.stockAl("Batería depositada por [G-1] en la Plataforma."));
    }

    @Test
    @DisplayName("Retiro: retirar publica la solicitud y la batería retirada; el stock nuevo es consultable")
    public void testRetiro() throws Exception {
        PlataformaDeCarga plataforma = new PlataformaDeCarga(3);
        plataforma.depositarBateria("G-1");
        Registro registro = new Registro(plataforma);

        plataforma.retirarBateria("Nave-1");

        assertEquals(List.of("[Nave-1] solicitando batería.", "Batería retirada y utilizada por [Nave-1]."),
                registro.mensajes());
        assertEquals(0, registro.stockAl("Batería retirada y utilizada por [Nave-1]."));
    }

    @Test
    @DisplayName("Espera: con la plataforma llena, la generadora publica que espera; el stock consultado es la capacidad")
    public void testEsperaPorPlataformaLlena() throws Exception {
        PlataformaDeCarga plataforma = new PlataformaDeCarga(1);
        plataforma.depositarBateria("G-1");
        String espera = "[G-2] esperando a que se libere espacio en la Plataforma.";
        Registro registro = new Registro(plataforma, espera);

        Thread generadora = new Thread(() -> {
            try {
                plataforma.depositarBateria("G-2");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        generadora.start();

        assertTrue(registro.esperar(), "La generadora debe publicar que espera.");
        assertEquals(1, registro.stockAl(espera));

        plataforma.retirarBateria("Nave-1");
        generadora.join(1000);
        assertFalse(generadora.isAlive());
        List<String> mensajes = registro.mensajes();
        int producida = mensajes.indexOf("Batería producida por [G-2].");
        int solicitud = mensajes.indexOf("[G-2] solicitando acceso a la Plataforma.");
        int esperaPublicada = mensajes.indexOf(espera);
        int deposito = mensajes.indexOf("Batería depositada por [G-2] en la Plataforma.");
        assertTrue(producida >= 0 && producida < solicitud && solicitud < esperaPublicada && esperaPublicada < deposito,
                "La generadora publica producción, solicitud, espera y depósito en ese orden: " + mensajes);
        assertEquals(1, registro.stockAl("Batería depositada por [G-2] en la Plataforma."));
    }

    @Test
    @DisplayName("Espera: con la plataforma vacía, la nave publica que espera por falta de baterías")
    public void testEsperaPorPlataformaVacia() throws Exception {
        PlataformaDeCarga plataforma = new PlataformaDeCarga(2);
        String espera = "[Nave-1] esperando por falta de baterías en la Plataforma.";
        Registro registro = new Registro(plataforma, espera);

        Thread nave = new Thread(() -> {
            try {
                plataforma.retirarBateria("Nave-1");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        nave.start();

        assertTrue(registro.esperar(), "La nave debe publicar que espera.");
        assertEquals(0, registro.stockAl(espera));

        plataforma.depositarBateria("G-1");
        nave.join(1000);
        assertFalse(nave.isAlive());
        assertTrue(registro.mensajes().contains("Batería retirada y utilizada por [Nave-1]."));
    }

    @Test
    @DisplayName("Alta y baja de entidades: publican el ingreso (Enunciado) y la detención sin cambiar el stock")
    public void testAltaYBajaDeEntidades() throws Exception {
        PlataformaDeCarga plataforma = new PlataformaDeCarga(3);
        Registro registro = new Registro(plataforma);

        plataforma.notificarIngreso("Nave Consumidora", "Nave-1");
        plataforma.notificarEgreso("Nave Consumidora", "Nave-1");

        List<String> mensajes = registro.mensajes();
        assertEquals(2, mensajes.size());
        assertEquals("¡Ingresó una nueva Nave Consumidora [Nave-1] al universo paralelo!", mensajes.get(0));
        assertTrue(mensajes.get(1).contains("Nave Consumidora [Nave-1] ha finalizado su ejecución."));
        assertEquals(0, registro.stockAl(mensajes.get(0)));
    }

    @Test
    @DisplayName("Suscripción: sin registro duplicado, quitar deja de notificar y un observador nulo viola la precondición")
    public void testSuscripcion() throws Exception {
        PlataformaDeCarga plataforma = new PlataformaDeCarga(3);
        AtomicInteger recibidas = new AtomicInteger();
        Observador observador = mensaje -> recibidas.incrementAndGet();

        plataforma.agregarObservador(observador);
        plataforma.agregarObservador(observador);
        plataforma.notificarIngreso("Generadora de Energía", "G-1");
        assertEquals(1, recibidas.get());

        plataforma.quitarObservador(observador);
        plataforma.notificarIngreso("Generadora de Energía", "G-2");
        assertEquals(1, recibidas.get());

        assertThrows(AssertionError.class, () -> plataforma.agregarObservador(null));
    }

    @Test
    @DisplayName("Concurrencia: suscribir y quitar observadores mientras los hilos publican no rompe la lista ni pierde avisos")
    public void testSuscripcionesConcurrentesConNotificaciones() throws Exception {
        final int vueltas = 2000;
        PlataformaDeCarga plataforma = new PlataformaDeCarga(3);
        AtomicInteger recibidas = new AtomicInteger();
        plataforma.agregarObservador(mensaje -> recibidas.incrementAndGet());
        AtomicReference<Throwable> falla = new AtomicReference<>();

        Thread universo = new Thread(() -> {
            try {
                for (int i = 0; i < vueltas; i++) {
                    plataforma.notificarIngreso("Generadora de Energía", "G-" + i);
                }
            } catch (RuntimeException | Error e) {
                falla.set(e);
            }
        });
        Thread vista = new Thread(() -> {
            try {
                for (int i = 0; i < vueltas; i++) {
                    Observador pasajero = mensaje -> { };
                    plataforma.agregarObservador(pasajero);
                    plataforma.quitarObservador(pasajero);
                }
            } catch (RuntimeException | Error e) {
                falla.set(e);
            }
        });
        universo.start();
        vista.start();
        universo.join(10000);
        vista.join(10000);

        assertNull(falla.get(), "No debe haber accesos concurrentes inválidos a la lista de observadores.");
        assertEquals(vueltas, recibidas.get());
    }
}
