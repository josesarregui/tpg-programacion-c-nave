package modelo.concurrencia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class PlataformaDeCargaTest {

    @Test
    @DisplayName("Plataforma se inicializa con capacidad válida y sin baterías")
    public void testInicializacion() {
        PlataformaDeCarga p = new PlataformaDeCarga(3);
        assertEquals(3, p.getCapacidadMaxima());
        assertEquals(0, p.getBaterias());

        assertThrows(IllegalArgumentException.class, () -> new PlataformaDeCarga(0));
        assertThrows(IllegalArgumentException.class, () -> new PlataformaDeCarga(-1));
    }

    @Test
    @DisplayName("Depositar y retirar actualizan la cantidad de baterías")
    public void testDepositarYRetirar() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(3);
        List<String> eventos = new ArrayList<>();
        p.agregarObservador((mensaje, baterias) -> eventos.add(mensaje));

        p.depositar("Gen-1");
        assertEquals(1, p.getBaterias());

        p.depositar("Gen-2");
        assertEquals(2, p.getBaterias());

        p.retirar("Nave-1");
        assertEquals(1, p.getBaterias());

        assertFalse(eventos.isEmpty());
    }

    @Test
    @DisplayName("Retirar sobre plataforma vacía bloquea al hilo hasta que se deposita")
    public void testEsperaPorPlataformaVacia() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(2);
        AtomicBoolean retiroExitoso = new AtomicBoolean(false);
        CountDownLatch iniciado = new CountDownLatch(1);

        Thread consumidor = new Thread(() -> {
            try {
                iniciado.countDown();
                p.retirar("Nave-Test");
                retiroExitoso.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        consumidor.start();
        assertTrue(iniciado.await(1, TimeUnit.SECONDS));
        Thread.sleep(150); // Dar tiempo a que ingrese a wait()

        // Como la plataforma está vacía, no debe haber retirado todavía
        assertFalse(retiroExitoso.get());
        assertEquals(0, p.getBaterias());

        // Ahora producimos una batería para desbloquearlo
        p.depositar("Gen-Test");

        consumidor.join(1000);
        assertFalse(consumidor.isAlive());
        assertTrue(retiroExitoso.get());
        assertEquals(0, p.getBaterias()); // Se depositó 1 y se retiró 1
    }

    @Test
    @DisplayName("Depositar sobre plataforma llena bloquea al hilo hasta que se retira")
    public void testEsperaPorPlataformaLlena() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(1);
        p.depositar("Gen-Inicial");
        assertEquals(1, p.getBaterias());

        AtomicBoolean depositoExitoso = new AtomicBoolean(false);
        CountDownLatch iniciado = new CountDownLatch(1);

        Thread productor = new Thread(() -> {
            try {
                iniciado.countDown();
                p.depositar("Gen-Bloqueada");
                depositoExitoso.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        productor.start();
        assertTrue(iniciado.await(1, TimeUnit.SECONDS));
        Thread.sleep(150); // Dar tiempo a que ingrese a wait()

        // No debe haber podido depositar porque la capacidad es 1 y ya está llena
        assertFalse(depositoExitoso.get());
        assertEquals(1, p.getBaterias());

        // Liberamos espacio retirando una batería
        p.retirar("Nave-Test");

        productor.join(1000);
        assertFalse(productor.isAlive());
        assertTrue(depositoExitoso.get());
        assertEquals(1, p.getBaterias());
    }
}
