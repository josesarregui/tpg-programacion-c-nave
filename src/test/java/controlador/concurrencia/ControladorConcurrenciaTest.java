package controlador.concurrencia;

import modelo.concurrencia.GeneradoraDeEnergia;
import modelo.concurrencia.NaveConsumidora;
import modelo.concurrencia.PlataformaDeCarga;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vista.concurrencia.IVistaConcurrencia;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de ControladorConcurrencia (Pasos 4 y 5 de la E2): suscripción de la vista, creación dinámica de entidades
 * con un hilo cada una, publicación del alta y de la baja, detención individual y total sin hilos vivos ni esperando,
 * y manejo de errores. Las esperas se sincronizan con los eventos publicados, sin pausas fijas.
 */
public class ControladorConcurrenciaTest {

    private static final long LIMITE_MS = 5000;

    /**
     * Vista de prueba. Los eventos llegan desde los hilos de la plataforma: los que se registran por el Observer se
     * guardan en un monitor (synchronized, while + wait con tiempo límite, notifyAll) para poder esperarlos.
     * observar y dejarDeObservar no se sincronizan con este monitor para no cruzar el orden de bloqueo con la
     * plataforma (actualizar se ejecuta con el monitor de la plataforma tomado).
     */
    private static class VistaConcurrenciaMock implements IVistaConcurrencia {
        ActionListener controlador;
        private final List<String> eventos = new ArrayList<>();
        PlataformaDeCarga observada;
        volatile int ultimasBaterias = -1;
        int ultimasGeneradoras = -1;
        int ultimasNaves = -1;
        boolean consolaLimpiada = false;
        boolean entidadesLimpiadas = false;
        String ultimoError;
        final List<String> generadorasListadas = new ArrayList<>();
        final List<String> navesListadas = new ArrayList<>();

        @Override
        public void setControlador(ActionListener controlador) {
            this.controlador = controlador;
        }

        @Override
        public synchronized void agregarEvento(String evento) {
            this.eventos.add(evento);
            notifyAll();
        }

        @Override
        public synchronized void limpiarConsola() {
            this.consolaLimpiada = true;
            this.eventos.clear();
        }

        /** Como la vista real, se vincula ella misma con la plataforma observada. */
        @Override
        public void observar(PlataformaDeCarga plataforma) {
            dejarDeObservar();
            this.observada = plataforma;
            plataforma.agregarObservador(this);
        }

        @Override
        public void dejarDeObservar() {
            if (this.observada != null) {
                this.observada.quitarObservador(this);
                this.observada = null;
            }
        }

        /** Push: el mensaje; pull: el stock se consulta a la plataforma. */
        @Override
        public synchronized void actualizar(String mensaje) {
            this.ultimasBaterias = this.observada.getBaterias();
            this.eventos.add(mensaje);
            notifyAll();
        }

        synchronized List<String> eventos() {
            return new ArrayList<>(this.eventos);
        }

        synchronized boolean esperarEvento(String mensaje) throws InterruptedException {
            long limite = System.currentTimeMillis() + LIMITE_MS;
            while (!this.eventos.contains(mensaje)) {
                long resta = limite - System.currentTimeMillis();
                if (resta <= 0) {
                    return false;
                }
                wait(resta);
            }
            return true;
        }

        @Override
        public void actualizarBaterias(int bateriasActuales, int capacidadMaxima) {
            this.ultimasBaterias = bateriasActuales;
        }

        @Override
        public void actualizarEntidades(int generadorasActivas, int navesActivas) {
            this.ultimasGeneradoras = generadorasActivas;
            this.ultimasNaves = navesActivas;
        }

        @Override
        public void mostrarError(String mensaje) {
            this.ultimoError = mensaje;
        }

        @Override
        public void agregarGeneradora(String idGeneradora) {
            this.generadorasListadas.add(idGeneradora);
        }

        @Override
        public void agregarNaveConsumidora(String idNave) {
            this.navesListadas.add(idNave);
        }

        @Override
        public void removerGeneradora(String idGeneradora) {
            this.generadorasListadas.remove(idGeneradora);
        }

        @Override
        public void removerNaveConsumidora(String idNave) {
            this.navesListadas.remove(idNave);
        }

        @Override
        public void limpiarEntidades() {
            this.entidadesLimpiadas = true;
            this.generadorasListadas.clear();
            this.navesListadas.clear();
        }

        @Override
        public void setOyenteDetenerGeneradora(Consumer<String> oyente) {
        }

        @Override
        public void setOyenteDetenerNave(Consumer<String> oyente) {
        }
    }

    private static String esperaDeNave(String id) {
        return "[" + id + "] esperando por falta de baterías en la Plataforma.";
    }

    private static String egreso(String tipo, String id) {
        return "⛔ Hilo detenido: " + tipo + " [" + id + "] ha finalizado su ejecución.";
    }

    @Test
    @DisplayName("Inicialización vincula vista y controlador con contadores iniciales")
    public void testInicializacion() {
        VistaConcurrenciaMock vistaMock = new VistaConcurrenciaMock();
        PlataformaDeCarga plataforma = new PlataformaDeCarga(4);

        ControladorConcurrencia ctrl = new ControladorConcurrencia(vistaMock, plataforma);

        assertNotNull(vistaMock.controlador);
        assertEquals(0, vistaMock.ultimasBaterias);
        assertEquals(0, vistaMock.ultimasGeneradoras);
        assertEquals(0, vistaMock.ultimasNaves);
        assertSame(plataforma, ctrl.getPlataforma());
        ctrl.detenerTodo();
    }

    @Test
    @DisplayName("Creación dinámica: CREAR_GENERADORA y CREAR_NAVE lanzan un hilo por entidad, también con la simulación en marcha")
    public void testCreacionDinamica() throws InterruptedException {
        VistaConcurrenciaMock vistaMock = new VistaConcurrenciaMock();
        PlataformaDeCarga plataforma = new PlataformaDeCarga(5);
        ControladorConcurrencia ctrl = new ControladorConcurrencia(vistaMock, plataforma);

        ctrl.actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaConcurrencia.COMANDO_CREAR_GENERADORA));
        assertEquals(1, ctrl.getGeneradorasActivas());
        Thread g1 = ctrl.getHilo("G-1");
        assertTrue(g1.isAlive());

        ctrl.actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaConcurrencia.COMANDO_CREAR_NAVE));
        assertEquals(1, ctrl.getNavesActivas());
        Thread nave1 = ctrl.getHilo("Nave-1");
        assertTrue(nave1.isAlive());
        assertNotSame(g1, nave1, "Cada entidad se ejecuta en su propio hilo.");

        // La simulación está en marcha (la nave espera baterías) y se agrega otra generadora.
        assertTrue(vistaMock.esperarEvento(esperaDeNave("Nave-1")));
        GeneradoraDeEnergia g2 = ctrl.crearGeneradora();
        assertEquals("G-2", g2.getIdGeneradora());
        assertEquals(2, ctrl.getGeneradorasActivas());
        Thread hiloG2 = ctrl.getHilo("G-2");
        assertTrue(hiloG2.isAlive());
        assertEquals(List.of("G-1", "G-2"), vistaMock.generadorasListadas);
        assertEquals(List.of("Nave-1"), vistaMock.navesListadas);
        assertEquals(2, vistaMock.ultimasGeneradoras);
        assertEquals(1, vistaMock.ultimasNaves);

        ctrl.actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaConcurrencia.COMANDO_LIMPIAR_CONSOLA));
        assertTrue(vistaMock.consolaLimpiada);

        ctrl.detenerTodo();
        assertFalse(g1.isAlive());
        assertFalse(nave1.isAlive());
        assertFalse(hiloG2.isAlive());
        assertEquals(0, ctrl.getGeneradorasActivas());
        assertEquals(0, ctrl.getNavesActivas());
    }

    @Test
    @DisplayName("Observer: la vista se suscribe a la plataforma al crear el controlador y deja de observarla al detener todo")
    public void testSuscripcionYDesuscripcionDeLaVista() throws Exception {
        VistaConcurrenciaMock vistaMock = new VistaConcurrenciaMock();
        PlataformaDeCarga plataforma = new PlataformaDeCarga(3);
        ControladorConcurrencia ctrl = new ControladorConcurrencia(vistaMock, plataforma);

        assertSame(plataforma, vistaMock.observada);
        plataforma.depositarBateria("G-1");
        List<String> eventos = vistaMock.eventos();
        assertEquals("Batería depositada por [G-1] en la Plataforma.", eventos.get(eventos.size() - 1));
        assertEquals(1, vistaMock.ultimasBaterias);

        ctrl.detenerTodo();
        assertNull(vistaMock.observada);
        int cantidad = vistaMock.eventos().size();
        plataforma.depositarBateria("G-2");
        assertEquals(cantidad, vistaMock.eventos().size(), "Tras dejar de observar, la vista no recibe más eventos.");
    }

    @Test
    @DisplayName("Observer: el alta publica el ingreso del Enunciado antes que cualquier evento de la entidad")
    public void testAltaDeEntidadPublicaIngreso() throws Exception {
        VistaConcurrenciaMock vistaMock = new VistaConcurrenciaMock();
        ControladorConcurrencia ctrl = new ControladorConcurrencia(vistaMock, new PlataformaDeCarga(3));

        GeneradoraDeEnergia generadora = ctrl.crearGeneradora();
        NaveConsumidora nave = ctrl.crearNave();
        String solicitud = "[" + nave.getIdNave() + "] solicitando batería.";
        assertTrue(vistaMock.esperarEvento(solicitud));

        List<String> eventos = vistaMock.eventos();
        assertTrue(eventos.contains(
                "¡Ingresó una nueva Generadora de Energía [" + generadora.getIdGeneradora() + "] al universo paralelo!"));
        int ingresoNave = eventos.indexOf(
                "¡Ingresó una nueva Nave Consumidora [" + nave.getIdNave() + "] al universo paralelo!");
        assertTrue(ingresoNave >= 0 && ingresoNave < eventos.indexOf(solicitud));
        ctrl.detenerTodo();
    }

    @Test
    @DisplayName("Controlador captura excepciones y envía mensaje de error a la vista sin relanzarla ni registrar la entidad")
    public void testCapturaDeExcepcionHaciaVista() {
        VistaConcurrenciaMock vistaMock = new VistaConcurrenciaMock();
        PlataformaDeCarga plataformaFalla = new PlataformaDeCarga(3) {
            @Override
            public synchronized void notificarIngreso(String tipo, String id) {
                throw new IllegalStateException("Simulación de fallo en plataforma");
            }
        };

        ControladorConcurrencia ctrl = new ControladorConcurrencia(vistaMock, plataformaFalla);

        // Al ejecutar la acción, no debe lanzarse excepción hacia afuera
        assertDoesNotThrow(() -> {
            ctrl.actionPerformed(
                    new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaConcurrencia.COMANDO_CREAR_GENERADORA));
        });

        // La excepción fue capturada por el controlador y enviada a la vista como mensaje
        assertEquals("Simulación de fallo en plataforma", vistaMock.ultimoError);
        // Si la publicación del alta falla, no queda ninguna entidad registrada ni hilo lanzado.
        assertEquals(0, ctrl.getGeneradorasActivas());
        assertNull(ctrl.getHilo("G-1"));
        assertTrue(vistaMock.generadorasListadas.isEmpty());

        ctrl.detenerTodo();
    }

    @Test
    @DisplayName("Detención individual: el hilo interrumpido (en su pausa o en wait()) ya terminó al volver y se publica su egreso")
    public void testDetencionIndividual() throws Exception {
        VistaConcurrenciaMock vistaMock = new VistaConcurrenciaMock();
        PlataformaDeCarga plataforma = new PlataformaDeCarga(4);
        ControladorConcurrencia ctrl = new ControladorConcurrencia(vistaMock, plataforma);

        GeneradoraDeEnergia gen = ctrl.crearGeneradora();
        NaveConsumidora nave = ctrl.crearNave();
        assertTrue(vistaMock.esperarEvento(esperaDeNave(nave.getIdNave())));
        Thread hiloGen = ctrl.getHilo(gen.getIdGeneradora());
        Thread hiloNave = ctrl.getHilo(nave.getIdNave());
        assertEquals(1, ctrl.getGeneradorasActivas());
        assertEquals(1, ctrl.getNavesActivas());

        // La generadora está en su pausa de producción.
        assertTrue(ctrl.detenerGeneradora(gen.getIdGeneradora()));
        assertFalse(hiloGen.isAlive());
        assertNull(ctrl.getHilo(gen.getIdGeneradora()));
        assertEquals(0, ctrl.getGeneradorasActivas());
        assertTrue(vistaMock.eventos().contains(egreso("Generadora de Energía", gen.getIdGeneradora())));
        assertTrue(vistaMock.generadorasListadas.isEmpty());

        // La nave está bloqueada en wait() por falta de baterías.
        assertTrue(ctrl.detenerNave(nave.getIdNave()));
        assertFalse(hiloNave.isAlive());
        assertEquals(0, ctrl.getNavesActivas());
        assertTrue(vistaMock.eventos().contains(egreso("Nave Consumidora", nave.getIdNave())));
        assertTrue(vistaMock.navesListadas.isEmpty());
        assertEquals(0, vistaMock.ultimasGeneradoras);
        assertEquals(0, vistaMock.ultimasNaves);
        assertNull(vistaMock.ultimoError);

        // Detener algo inexistente o ya detenido devuelve false
        assertFalse(ctrl.detenerGeneradora("ID_FANTASMA"));
        assertFalse(ctrl.detenerNave("ID_FANTASMA"));
        assertFalse(ctrl.detenerGeneradora(gen.getIdGeneradora()));
        assertFalse(ctrl.detenerNave(null));
        assertFalse(ctrl.detenerGeneradora(nave.getIdNave()), "Cada tipo se detiene con su propio método.");

        ctrl.detenerTodo();
    }

    @Test
    @DisplayName("Detención total: no deja hilos vivos ni esperando, publica cada egreso y después la vista deja de observar")
    public void testDetenerTodo() throws Exception {
        VistaConcurrenciaMock vistaMock = new VistaConcurrenciaMock();
        ControladorConcurrencia ctrl = new ControladorConcurrencia(vistaMock, new PlataformaDeCarga(2));
        ctrl.crearGeneradora();
        ctrl.crearGeneradora();
        ctrl.crearNave();
        ctrl.crearNave();
        assertTrue(vistaMock.esperarEvento(esperaDeNave("Nave-1")));
        assertTrue(vistaMock.esperarEvento(esperaDeNave("Nave-2")));
        List<Thread> hilos = List.of(ctrl.getHilo("G-1"), ctrl.getHilo("G-2"), ctrl.getHilo("Nave-1"),
                ctrl.getHilo("Nave-2"));

        ctrl.detenerTodo();

        for (Thread hilo : hilos) {
            assertFalse(hilo.isAlive());
        }
        List<String> eventos = vistaMock.eventos();
        assertTrue(eventos.contains(egreso("Generadora de Energía", "G-1")));
        assertTrue(eventos.contains(egreso("Generadora de Energía", "G-2")));
        assertTrue(eventos.contains(egreso("Nave Consumidora", "Nave-1")));
        assertTrue(eventos.contains(egreso("Nave Consumidora", "Nave-2")));
        assertNull(vistaMock.observada);
        assertTrue(vistaMock.entidadesLimpiadas);
        assertEquals(0, ctrl.getGeneradorasActivas());
        assertEquals(0, ctrl.getNavesActivas());
        assertEquals(0, vistaMock.ultimasGeneradoras);
        assertEquals(0, vistaMock.ultimasNaves);
        assertNull(vistaMock.ultimoError);
    }
}
