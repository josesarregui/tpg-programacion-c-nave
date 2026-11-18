package modelo.concurrencia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static modelo.concurrencia.RegistroDeEventos.LIMITE_MS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de los hilos GeneradoraDeEnergia y NaveConsumidora (Paso 5 de la E2): contratos de construcción, orden de
 * los mensajes del ciclo del Enunciado, finalización por interrupción (en la pausa o en wait()) con la interrupción
 * restaurada, y creación dinámica de entidades mientras la simulación está activa.
 *
 * Las esperas se sincronizan con los eventos publicados por la plataforma (sin pausas fijas) y cada join tiene tiempo
 * límite.
 */
public class HilosProductorConsumidorTest {

    /** Pausa que nunca termina dentro de una prueba: sólo la corta la interrupción. */
    private static final int PAUSA_LARGA_MS = 60_000;

    /**
     * Lanza la tarea en un hilo nuevo con start() y registra si, al salir de run(), la interrupción quedó restaurada.
     * La tarea se ejecuta dentro del hilo lanzado; sólo se envuelve para poder leer su estado al terminar.
     */
    private static Thread lanzar(Runnable tarea, AtomicBoolean interrupcionRestaurada) {
        Thread hilo = new Thread(() -> {
            tarea.run();
            interrupcionRestaurada.set(Thread.currentThread().isInterrupted());
        });
        hilo.start();
        return hilo;
    }

    private static Thread lanzar(Runnable tarea) {
        Thread hilo = new Thread(tarea);
        hilo.start();
        return hilo;
    }

    private static void detener(Thread hilo) throws InterruptedException {
        hilo.interrupt();
        hilo.join(LIMITE_MS);
        assertFalse(hilo.isAlive(), "El hilo debe terminar al ser interrumpido.");
    }

    @Test
    @DisplayName("Contratos: identificador, plataforma y pausa válidos; los valores por defecto son los del módulo")
    public void testConstructores() {
        PlataformaDeCarga p = new PlataformaDeCarga(2);

        assertEquals("G-1", new GeneradoraDeEnergia(" G-1 ", p).getIdGeneradora());
        assertEquals("Nave-1", new NaveConsumidora(" Nave-1 ", p).getIdNave());

        assertThrows(IllegalArgumentException.class, () -> new GeneradoraDeEnergia(null, p));
        assertThrows(IllegalArgumentException.class, () -> new GeneradoraDeEnergia(" ", p));
        assertThrows(IllegalArgumentException.class, () -> new GeneradoraDeEnergia("G-1", null));
        assertThrows(IllegalArgumentException.class, () -> new GeneradoraDeEnergia("G-1", p, 0));
        assertThrows(IllegalArgumentException.class, () -> new NaveConsumidora(null, p));
        assertThrows(IllegalArgumentException.class, () -> new NaveConsumidora("", p));
        assertThrows(IllegalArgumentException.class, () -> new NaveConsumidora("Nave-1", null));
        assertThrows(IllegalArgumentException.class, () -> new NaveConsumidora("Nave-1", p, -1));
    }

    @Test
    @DisplayName("Generadora: cada ciclo publica producción, solicitud y depósito en el orden del Enunciado")
    public void testCicloDeLaGeneradora() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(3);
        RegistroDeEventos registro = new RegistroDeEventos(p);

        Thread hilo = lanzar(new GeneradoraDeEnergia("G-1", p, 1));
        assertTrue(registro.esperar("Batería depositada por [G-1] en la Plataforma.", 1));
        detener(hilo);

        assertEquals(List.of("Batería producida por [G-1].", "[G-1] solicitando acceso a la Plataforma.",
                "Batería depositada por [G-1] en la Plataforma."), registro.mensajes().subList(0, 3));
        assertEquals(1, registro.stockAl("Batería depositada por [G-1] en la Plataforma."));
    }

    @Test
    @DisplayName("Generadora con la plataforma llena: produce, solicita, espera y deposita cuando una nave retira")
    public void testGeneradoraEsperaConPlataformaLlena() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(1);
        p.depositarBateria("G-0");
        RegistroDeEventos registro = new RegistroDeEventos(p);

        Thread hilo = lanzar(new GeneradoraDeEnergia("G-1", p, 1));
        assertTrue(registro.esperar("[G-1] esperando a que se libere espacio en la Plataforma.", 1));
        p.retirarBateria("Nave-1");
        assertTrue(registro.esperar("Batería depositada por [G-1] en la Plataforma.", 1));
        detener(hilo);

        int producida = registro.posicion("Batería producida por [G-1].");
        int solicitud = registro.posicion("[G-1] solicitando acceso a la Plataforma.");
        int espera = registro.posicion("[G-1] esperando a que se libere espacio en la Plataforma.");
        int retiro = registro.posicion("Batería retirada y utilizada por [Nave-1].");
        int deposito = registro.posicion("Batería depositada por [G-1] en la Plataforma.");
        assertTrue(producida == 0 && producida < solicitud && solicitud < espera && espera < retiro && retiro < deposito,
                "Orden de mensajes: " + registro.mensajes());
    }

    @Test
    @DisplayName("Nave: solicita, retira y consume fuera del monitor; interrumpirla durante la pausa la termina")
    public void testCicloDeLaNave() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(2);
        p.depositarBateria("G-1");
        RegistroDeEventos registro = new RegistroDeEventos(p);
        AtomicBoolean restaurada = new AtomicBoolean(false);

        Thread hilo = lanzar(new NaveConsumidora("Nave-1", p, PAUSA_LARGA_MS), restaurada);
        assertTrue(registro.esperar("Batería retirada y utilizada por [Nave-1].", 1));
        // Mientras consume no tiene el monitor: la plataforma sigue disponible para otros.
        p.depositarBateria("G-2");
        detener(hilo);

        assertTrue(restaurada.get(), "La interrupción debe quedar restaurada al salir de run().");
        assertEquals(List.of("[Nave-1] solicitando batería.", "Batería retirada y utilizada por [Nave-1]."),
                registro.mensajes().subList(0, 2));
        assertEquals(1, p.getBaterias());
    }

    @Test
    @DisplayName("Interrumpir una nave bloqueada en wait() la termina, restaura la interrupción y no cambia el stock")
    public void testInterrumpirNaveEnEspera() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(2);
        RegistroDeEventos registro = new RegistroDeEventos(p);
        AtomicBoolean restaurada = new AtomicBoolean(false);

        Thread hilo = lanzar(new NaveConsumidora("Nave-1", p, 1), restaurada);
        assertTrue(registro.esperar("[Nave-1] esperando por falta de baterías en la Plataforma.", 1));
        detener(hilo);

        assertTrue(restaurada.get());
        assertEquals(0, p.getBaterias());
        assertEquals(0, registro.contarQueContengan("Batería retirada"));
    }

    @Test
    @DisplayName("Interrumpir una generadora bloqueada en wait() la termina, restaura la interrupción y no deposita")
    public void testInterrumpirGeneradoraEnEspera() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(1);
        p.depositarBateria("G-0");
        RegistroDeEventos registro = new RegistroDeEventos(p);
        AtomicBoolean restaurada = new AtomicBoolean(false);

        Thread hilo = lanzar(new GeneradoraDeEnergia("G-1", p, 1), restaurada);
        assertTrue(registro.esperar("[G-1] esperando a que se libere espacio en la Plataforma.", 1));
        detener(hilo);

        assertTrue(restaurada.get());
        assertEquals(1, p.getBaterias());
        assertEquals(0, registro.contar("Batería depositada por [G-1] en la Plataforma."));
    }

    @Test
    @DisplayName("Interrumpir una generadora durante la producción la termina sin publicar nada")
    public void testInterrumpirGeneradoraEnPausa() throws Exception {
        PlataformaDeCarga p = new PlataformaDeCarga(2);
        RegistroDeEventos registro = new RegistroDeEventos(p);
        AtomicBoolean restaurada = new AtomicBoolean(false);

        Thread hilo = lanzar(new GeneradoraDeEnergia("G-1", p, PAUSA_LARGA_MS), restaurada);
        detener(hilo);

        assertTrue(restaurada.get());
        assertTrue(registro.mensajes().isEmpty());
        assertEquals(0, p.getBaterias());
    }

    @Test
    @DisplayName("Creación dinámica: con naves esperando, se agregan generadoras y otra nave en plena simulación; todas comparten el monitor y terminan al interrumpirlas")
    public void testCreacionDinamicaConLaSimulacionActiva() throws Exception {
        final int capacidad = 2;
        PlataformaDeCarga p = new PlataformaDeCarga(capacidad);
        RegistroDeEventos registro = new RegistroDeEventos(p);
        List<Thread> hilos = new ArrayList<>();

        hilos.add(lanzar(new NaveConsumidora("Nave-1", p, 2)));
        hilos.add(lanzar(new NaveConsumidora("Nave-2", p, 2)));
        assertTrue(registro.esperar("[Nave-1] esperando por falta de baterías en la Plataforma.", 1));
        assertTrue(registro.esperar("[Nave-2] esperando por falta de baterías en la Plataforma.", 1));

        hilos.add(lanzar(new GeneradoraDeEnergia("G-1", p, 1)));
        hilos.add(lanzar(new GeneradoraDeEnergia("G-2", p, 1)));
        assertTrue(registro.esperar("Batería retirada y utilizada por [Nave-1].", 3));
        assertTrue(registro.esperar("Batería retirada y utilizada por [Nave-2].", 3));

        hilos.add(lanzar(new NaveConsumidora("Nave-3", p, 2)));
        assertTrue(registro.esperar("Batería retirada y utilizada por [Nave-3].", 1));

        for (Thread hilo : hilos) {
            hilo.interrupt();
        }
        for (Thread hilo : hilos) {
            hilo.join(LIMITE_MS);
            assertFalse(hilo.isAlive(), "Ningún hilo debe quedar vivo ni esperando tras la interrupción.");
        }
        assertTrue(registro.stockMinimo() >= 0);
        assertTrue(registro.stockMaximo() <= capacidad);
        assertTrue(p.getBaterias() >= 0 && p.getBaterias() <= capacidad);
    }
}
