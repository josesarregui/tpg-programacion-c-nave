package modelo.asistente;

import excepcion.CapacidadExcedidaException;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.mision.MisionRetorno;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;
import modelo.universo.DestinoSinMovimiento;
import modelo.universo.Observador;
import modelo.universo.Posicion;
import modelo.universo.TableroDePrueba;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del patrón Observer con el asistente como sujeto (Paso 4 de la E2, Observable / Observador de la
 * cátedra, Versión 2). Verifican, para cada orden, el mensaje recibido (push), el estado que el observador
 * consulta con los getters en el momento de la notificación (pull) y que no haya notificaciones duplicadas
 * ni faltantes: cada entrada de la Bitácora produce exactamente una notificación con su descripción.
 */
public class AsistenteObservadorTest {

    private Asistente asistente;

    /**
     * Observador de prueba que, como indica el apunte de Observer, recibe al observado por constructor y se
     * vincula él mismo. En cada notificación guarda el mensaje y consulta el estado del asistente (pull).
     */
    private static class Registro implements Observador {
        private final Asistente observado;
        final List<String> mensajes = new ArrayList<>();
        final List<Integer> combustibles = new ArrayList<>();
        final List<Integer> energias = new ArrayList<>();
        final List<Integer> desgastes = new ArrayList<>();
        final List<String> motores = new ArrayList<>();
        final List<Boolean> tripulaciones = new ArrayList<>();
        final List<Boolean> destrucciones = new ArrayList<>();
        final List<Integer> misionesRealizadas = new ArrayList<>();
        final List<Boolean> misionesPendientes = new ArrayList<>();
        final int eventosAlSuscribirse;

        Registro(Asistente observado) {
            this.observado = observado;
            this.eventosAlSuscribirse = observado.getEventos().size();
            observado.agregarObservador(this);
        }

        @Override
        public void actualizar(String mensaje) {
            mensajes.add(mensaje);
            combustibles.add(observado.getCombustible());
            energias.add(observado.getEnergia());
            desgastes.add(observado.getDesgaste());
            motores.add(observado.getEstadoMotor());
            tripulaciones.add(observado.tieneTripulacion());
            destrucciones.add(observado.naveDestruida());
            misionesRealizadas.add(observado.getMisionesRealizadas().size());
            misionesPendientes.add(observado.tieneMisionPendiente());
        }

        int cantidad() {
            return mensajes.size();
        }

        String ultimo() {
            return mensajes.get(mensajes.size() - 1);
        }

        /**
         * Sin duplicados ni faltantes: los mensajes recibidos son, en orden, las descripciones de las entradas
         * que se agregaron a la Bitácora desde la suscripción.
         */
        void verificarIgualALaBitacora() {
            List<Evento> eventos = observado.getEventos();
            List<String> descripciones = new ArrayList<>();
            for (Evento evento : eventos.subList(eventosAlSuscribirse, eventos.size())) {
                descripciones.add(evento.getDescripcion());
            }
            assertEquals(descripciones, mensajes, "Cada entrada de la Bitácora debe notificarse una sola vez.");
        }
    }

    @BeforeEach
    public void inicializar() {
        // Exploradora: combustible 60, energía 80, desgaste 0.
        asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.EXPLORADORA));
    }

    private Tripulacion crearTripulacion() throws Exception {
        List<Tripulante> integrantes = new ArrayList<>();
        integrantes.add(new Capitan("Picard", 10, Origen.TERRICOLA));
        integrantes.add(new Consejero("Troi", 5, Origen.VULCANO));
        integrantes.add(new Teniente("Worf", 4, Origen.MARCIANO));
        integrantes.add(new Alferez("Crusher", 1, Origen.TERRICOLA));
        integrantes.add(new Alferez("Data", 0, Origen.MARCIANO));
        return new Tripulacion(integrantes);
    }

    // --- Suscripción a través de la interfaz Asistente ---

    @Test
    @DisplayName("Suscripción: un observador registrado dos veces recibe una sola notificación por cambio")
    public void testSinRegistroDuplicado() throws Exception {
        AtomicInteger notificaciones = new AtomicInteger();
        Observador observador = mensaje -> notificaciones.incrementAndGet();

        asistente.agregarObservador(observador);
        asistente.agregarObservador(observador);
        asistente.cargarCombustible(5);

        assertEquals(1, notificaciones.get());
    }

    @Test
    @DisplayName("Suscripción: tras quitarObservador no se reciben más notificaciones; quitar uno no registrado no falla")
    public void testQuitarObservador() throws Exception {
        Registro registro = new Registro(asistente);
        asistente.cargarCombustible(5);
        assertEquals(1, registro.cantidad());

        asistente.quitarObservador(registro);
        asistente.cargarCombustible(5);
        assertEquals(1, registro.cantidad());

        assertDoesNotThrow(() -> asistente.quitarObservador(registro));
    }

    @Test
    @DisplayName("Suscripción: agregar un observador nulo viola la precondición")
    public void testAgregarObservadorNulo() {
        assertThrows(AssertionError.class, () -> asistente.agregarObservador(null));
    }

    // --- Recursos ---

    @Test
    @DisplayName("Combustible: una notificación con el mensaje de la Bitácora y el combustible nuevo consultable")
    public void testCargarCombustible() throws Exception {
        Registro registro = new Registro(asistente);

        asistente.cargarCombustible(15);

        assertEquals(1, registro.cantidad());
        assertTrue(registro.ultimo().startsWith("Carga de 15 de combustible."));
        assertEquals(75, registro.combustibles.get(0));
        registro.verificarIgualALaBitacora();
    }

    @Test
    @DisplayName("Energía: una notificación con la energía nueva consultable")
    public void testCargarEnergia() throws Exception {
        Registro registro = new Registro(asistente);

        asistente.cargarEnergia(10);

        assertEquals(1, registro.cantidad());
        assertTrue(registro.ultimo().startsWith("Carga de 10 de energía."));
        assertEquals(90, registro.energias.get(0));
        registro.verificarIgualALaBitacora();
    }

    @Test
    @DisplayName("Consumo: una notificación con combustible, energía y desgaste nuevos consultables")
    public void testConsumirRecursos() throws Exception {
        Registro registro = new Registro(asistente);

        asistente.consumirRecursos(10, 20, 15);

        assertEquals(1, registro.cantidad());
        assertEquals(50, registro.combustibles.get(0));
        assertEquals(60, registro.energias.get(0));
        assertEquals(15, registro.desgastes.get(0));
        registro.verificarIgualALaBitacora();
    }

    @Test
    @DisplayName("Desgaste: el mantenimiento notifica una vez con el desgaste en 0")
    public void testRealizarMantenimiento() throws Exception {
        asistente.consumirRecursos(0, 0, 80);
        Registro registro = new Registro(asistente);

        asistente.realizarMantenimiento();

        assertEquals(1, registro.cantidad());
        assertTrue(registro.ultimo().startsWith("Mantenimiento realizado."));
        assertEquals(0, registro.desgastes.get(0));
        registro.verificarIgualALaBitacora();
    }

    @Test
    @DisplayName("Impacto y reabastecimiento: una notificación cada uno con la energía consultable")
    public void testImpactoYReabastecimiento() {
        Registro registro = new Registro(asistente);

        asistente.recibirImpacto(30);
        asistente.reabastecer(10, 20);

        assertEquals(2, registro.cantidad());
        assertEquals(50, registro.energias.get(0));
        assertEquals(70, registro.combustibles.get(1));
        assertEquals(70, registro.energias.get(1));
        registro.verificarIgualALaBitacora();
    }

    @Test
    @DisplayName("Rechazo: la orden rechazada notifica su entrada de error y el estado consultable no cambió")
    public void testOrdenRechazada() {
        Registro registro = new Registro(asistente);

        assertThrows(CapacidadExcedidaException.class, () -> asistente.cargarCombustible(50));

        assertEquals(1, registro.cantidad());
        assertTrue(registro.ultimo().startsWith("Carga de combustible rechazada"));
        assertEquals(60, registro.combustibles.get(0));
        registro.verificarIgualALaBitacora();
    }

    // --- Motor y tripulación ---

    @Test
    @DisplayName("Motor: el salto normal y cada turno warp notifican una sola vez con el estado del motor nuevo")
    public void testEstadoDelMotor() throws Exception {
        Registro registro = new Registro(asistente);

        asistente.saltar();
        assertEquals(1, registro.cantidad());
        assertTrue(registro.ultimo().startsWith("Salto normal"));
        assertEquals(asistente.getEstadoMotor(), registro.motores.get(0));

        asistente.saltarWarp();
        assertEquals(2, registro.cantidad());
        assertTrue(registro.ultimo().startsWith("Maniobra warp"));
        assertEquals(asistente.getEstadoMotor(), registro.motores.get(1));
        assertNotEquals("Disponible", registro.motores.get(1), "El turno warp saca al motor de Disponible.");
        registro.verificarIgualALaBitacora();
    }

    @Test
    @DisplayName("Tripulación: la asignación notifica una vez y la tripulación ya es consultable")
    public void testAsignarTripulacion() throws Exception {
        Registro registro = new Registro(asistente);

        asistente.asignarTripulacion(crearTripulacion());

        assertEquals(1, registro.cantidad());
        assertEquals("Tripulación asignada (5 integrantes).", registro.ultimo());
        assertTrue(registro.tripulaciones.get(0));
        registro.verificarIgualALaBitacora();
    }

    // --- Bitácora ---

    @Test
    @DisplayName("Bitácora: cada entrada registrada por una misión o el centro de control se notifica con su descripción")
    public void testEntradaDeBitacora() {
        Registro registro = new Registro(asistente);

        asistente.registrarEvento(TipoEvento.SISTEMA, "Nave registrada en el centro de control.");

        assertEquals(List.of("Nave registrada en el centro de control."), registro.mensajes);
        registro.verificarIgualALaBitacora();
    }

    @Test
    @DisplayName("Bitácora: un evento inválido se rechaza sin notificar")
    public void testEventoInvalidoNoNotifica() {
        Registro registro = new Registro(asistente);

        assertThrows(IllegalArgumentException.class, () -> asistente.registrarEvento(TipoEvento.SISTEMA, " "));

        assertEquals(0, registro.cantidad());
    }

    // --- Destrucción ---

    @Test
    @DisplayName("Destrucción: el consumo que agota la energía notifica el consumo y, aparte, la destrucción")
    public void testDestruccionPorConsumo() throws Exception {
        Registro registro = new Registro(asistente);

        asistente.consumirRecursos(0, 80, 0);

        assertEquals(2, registro.cantidad());
        assertTrue(registro.mensajes.get(0).startsWith("Consumo de 0 de combustible y 80 de energía"));
        assertTrue(registro.mensajes.get(1).startsWith("Nave destruida por el consumo de recursos."));
        assertTrue(registro.destrucciones.get(0));
        assertTrue(registro.destrucciones.get(1));
        registro.verificarIgualALaBitacora();
    }

    @Test
    @DisplayName("Destrucción: al notificar la batalla perdida, la nave ya figura destruida (pull consistente)")
    public void testDestruccionPorBatalla() {
        Registro registro = new Registro(asistente);

        // Combate gana a Exploradora (Enunciado E2, punto A).
        asistente.enfrentarseCon(new NaveFactory().crearNave(TipoNave.COMBATE));

        assertEquals(2, registro.cantidad());
        assertTrue(registro.mensajes.get(0).startsWith("Batalla contra Combate"));
        assertTrue(registro.destrucciones.get(0), "El resultado se aplica antes de notificar la batalla.");
        assertTrue(registro.mensajes.get(1).startsWith("Nave destruida por la derrota"));
        registro.verificarIgualALaBitacora();
    }

    // --- Cierre del informe de misión ---

    @Test
    @DisplayName("Informe: el cierre de la misión se notifica al final, con la misión ya entre las realizadas")
    public void testCierreDelInformeDeMision() throws Exception {
        asistente.asignarTripulacion(crearTripulacion());
        TableroDePrueba tablero = new TableroDePrueba(10, 10);
        tablero.moverNave(new Posicion(5, 7));
        MisionRetorno mision = new MisionRetorno(tablero, new DestinoSinMovimiento(tablero));
        mision.configurarPausaPorTurno(0);
        asistente.encomendarMision(mision);
        Registro registro = new Registro(asistente);

        asistente.ejecutarMision();

        assertTrue(registro.ultimo().startsWith("Informe de "), "La última notificación es el cierre del informe.");
        int ultima = registro.cantidad() - 1;
        assertEquals(1, registro.misionesRealizadas.get(ultima));
        assertFalse(registro.misionesPendientes.get(ultima));
        assertEquals(0, registro.misionesRealizadas.get(ultima - 1), "Antes del cierre, la misión no estaba realizada.");
        registro.verificarIgualALaBitacora();
    }

    // --- Concurrencia ---

    @Test
    @DisplayName("Concurrencia: suscribir y quitar observadores mientras otro hilo notifica no rompe la lista ni pierde avisos")
    public void testSuscripcionesConcurrentesConNotificaciones() throws Exception {
        final int vueltas = 2000;
        AtomicInteger recibidas = new AtomicInteger();
        asistente.agregarObservador(mensaje -> recibidas.incrementAndGet());
        AtomicReference<Throwable> falla = new AtomicReference<>();

        Thread mision = new Thread(() -> {
            try {
                for (int i = 0; i < vueltas; i++) {
                    asistente.registrarEvento(TipoEvento.SISTEMA, "Turno " + i);
                }
            } catch (RuntimeException | Error e) {
                falla.set(e);
            }
        });
        Thread vista = new Thread(() -> {
            try {
                for (int i = 0; i < vueltas; i++) {
                    Observador pasajero = mensaje -> { };
                    asistente.agregarObservador(pasajero);
                    asistente.quitarObservador(pasajero);
                }
            } catch (RuntimeException | Error e) {
                falla.set(e);
            }
        });
        mision.start();
        vista.start();
        mision.join(10000);
        vista.join(10000);

        assertNull(falla.get(), "No debe haber accesos concurrentes inválidos a la lista de observadores.");
        assertEquals(vueltas, recibidas.get());
    }
}
