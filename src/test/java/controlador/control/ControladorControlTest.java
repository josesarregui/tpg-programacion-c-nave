package controlador.control;

import controlador.VistasDePrueba.ControlDePrueba;
import java.util.List;
import modelo.asistente.AsistenteComando;
import modelo.bitacora.TipoEvento;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.universo.DestinoSinMovimiento;
import modelo.universo.TableroDePrueba;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vista.principal.panelcontrol.IVistaControl;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de ControladorControl y ControladorSelectMision con vistas de prueba que implementan las interfaces
 * {@code IVista...} (sin Swing): inicialización, suscripciones, selección de misión, Bitácora por Observer y
 * transiciones entre etapas.
 */
public class ControladorControlTest {

    private ControlDePrueba vista;
    private AsistenteComando asistente;
    private TableroDePrueba tablero;

    @BeforeEach
    public void setUp() throws Exception {
        asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.EXPLORADORA));
        vista = new ControlDePrueba();
        tablero = new TableroDePrueba(20, 20);
    }

    private ControladorControl crearControl() {
        return new ControladorControl(vista, asistente, tablero, new DestinoSinMovimiento(tablero), 0);
    }

    private void equiparTripulacionCompleta() throws Exception {
        asistente.asignarTripulacion(new Tripulacion(List.of(
                new Capitan("James Kirk", 15, Origen.TERRICOLA),
                new Consejero("Deanna Troi", 8, Origen.VULCANO),
                new Teniente("Spock", 12, Origen.VULCANO),
                new Alferez("Hikaru Sulu", 5, Origen.TERRICOLA),
                new Alferez("Pavel Chekov", 2, Origen.MARCIANO))));
    }

    @Test
    @DisplayName("Inicialización: las vistas se suscriben, se cargan el catálogo, la Bitácora y los recursos")
    public void testInicializacion() {
        asistente.registrarEvento(TipoEvento.SISTEMA, "Prueba de evento 1");
        asistente.registrarEvento(TipoEvento.RELEVANTE, "Prueba de evento 2");

        ControladorControl controlador = crearControl();

        assertSame(asistente, vista.recursos.getObservado());
        assertSame(asistente, vista.select.getObservado());
        assertSame(asistente, vista.curso.getObservado());
        assertSame(asistente, vista.resultados.getObservado());
        assertSame(tablero, vista.curso.grilla.getObservado(), "La vista del tablero observa el tablero de la misión.");
        assertSame(tablero, controlador.getTablero());

        assertNotNull(vista.select.controlador);
        assertNotNull(vista.curso.controlador);
        assertNotNull(vista.resultados.controlador);
        assertEquals(List.of("M-01", "M-02", "M-03"),
                vista.select.misionesDisponibles.stream().map(m -> m.getCodigo()).toList());
        assertEquals(asistente.getEventos().size(), vista.select.eventosMostrados.size());
        assertEquals(asistente.getCombustible(), vista.recursos.getCombustibleMostrado());
        assertTrue(vista.resultados.getInformesMostrados().isEmpty());
        assertEquals(IVistaControl.SELECT_MISION, vista.getEtapa());
        assertFalse(controlador.hayMisionEnCurso());
    }

    @Test
    @DisplayName("Selección de misión: actualiza detalles y costos en la vista")
    public void testSeleccionarMisionActualizaDetalles() throws Exception {
        equiparTripulacionCompleta();
        ControladorControl controlador = crearControl();

        vista.select.seleccionar("M-02");

        assertEquals("M-02", vista.select.ultimaMisionDetallada.getCodigo());
        assertEquals("M-02", controlador.getControladorSelectMision().getMisionSeleccionada().getCodigo());
        assertTrue(vista.select.naveListaDetallada);
        assertEquals(asistente.getCombustible(), vista.select.combustibleDetallado);
        assertEquals(asistente.getEnergia(), vista.select.energiaDetallada);
    }

    @Test
    @DisplayName("Bitácora por Observer: cada evento nuevo llega a la vista, que copia la Bitácora en el hilo que notifica")
    public void testBitacoraPorObserver() {
        crearControl();
        int notificacionesAntes = vista.select.notificaciones.get();

        asistente.registrarEvento(TipoEvento.ERROR, "Alerta de prueba");

        assertEquals(notificacionesAntes + 1, vista.select.notificaciones.get());
        assertEquals(asistente.getEventos().size(), vista.select.getEventosVistos());
        assertTrue(vista.curso.mensajes.contains("Alerta de prueba"), "La consola recibe el mensaje (push).");
        assertEquals(asistente.getEnergia(), vista.recursos.getEnergiaVista(), "Los recursos se consultan (pull).");
    }

    @Test
    @DisplayName("Iniciar misión con nave no lista: muestra error, no encomienda ni lanza hilo")
    public void testIniciarMisionNaveNoListaMuestraError() {
        assertFalse(asistente.naveListaParaOperar());
        ControladorControl controlador = crearControl();

        vista.select.seleccionar("M-01");
        vista.select.iniciar();

        assertNotNull(vista.select.ultimoError());
        assertFalse(asistente.tieneMisionPendiente());
        assertFalse(controlador.hayMisionEnCurso());
        assertEquals(IVistaControl.SELECT_MISION, vista.getEtapa());
    }

    @Test
    @DisplayName("Transiciones entre selección, misión en curso e informes")
    public void testTransicionesVistaControl() {
        ControladorControl controlador = crearControl();

        controlador.mostrarMisionCurso();
        assertEquals(IVistaControl.MISION_CURSO, vista.getEtapa());

        controlador.mostrarResultadosMision();
        assertEquals(IVistaControl.RESULTADOS_MISION, vista.getEtapa());

        controlador.mostrarSelectMision();
        assertEquals(IVistaControl.SELECT_MISION, vista.getEtapa());

        controlador.actualizarVista();
        assertEquals(IVistaControl.SELECT_MISION, vista.getEtapa());
    }

    @Test
    @DisplayName("Detener sin misión en curso: todas las vistas dejan de observar al asistente y al tablero")
    public void testDetenerDesuscribe() {
        ControladorControl controlador = crearControl();

        assertTrue(controlador.detener());

        assertNull(vista.recursos.getObservado());
        assertNull(vista.select.getObservado());
        assertNull(vista.curso.getObservado());
        assertNull(vista.curso.grilla.getObservado());
        assertNull(vista.resultados.getObservado());
    }
}
