package controlador.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import modelo.asistente.AsistenteComando;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.mision.Mision;
import modelo.mision.MisionIntercepcion;
import modelo.mision.MisionRecoleccion;
import modelo.mision.MisionRetorno;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.tripulacion.Alferez;
import modelo.tripulacion.Capitan;
import modelo.tripulacion.Consejero;
import modelo.tripulacion.Origen;
import modelo.tripulacion.Teniente;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vista.principal.panelcontrol.IVistaControl;
import vista.principal.panelcontrol.panelselectmision.IVistaSelectMision;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para ControladorControl y ControladorSelectMision.
 * Verifica el desacoplamiento MVC mediante mocks de IVistaControl e IVistaSelectMision.
 */
public class ControladorControlTest {

    private VistaSelectMisionMock mockVistaSelect;
    private VistaControlMock mockVistaControl;
    private AsistenteComando asistente;

    @BeforeEach
    public void setUp() {
        Nave nave = new NaveFactory().crearNave(TipoNave.EXPLORADORA);
        asistente = new AsistenteComando(nave);

        mockVistaSelect = new VistaSelectMisionMock();
        mockVistaControl = new VistaControlMock(mockVistaSelect);
    }

    private void equiparTripulacionCompleta() throws Exception {
        List<Tripulante> integrantes = List.of(
                new Capitan("James Kirk", 15, Origen.TERRICOLA),
                new Consejero("Deanna Troi", 8, Origen.VULCANO),
                new Teniente("Spock", 12, Origen.VULCANO),
                new Alferez("Hikaru Sulu", 5, Origen.TERRICOLA),
                new Alferez("Pavel Chekov", 2, Origen.MARCIANO)
        );
        Tripulacion trip = new Tripulacion(integrantes);
        asistente.asignarTripulacion(trip);
    }

    @Test
    @DisplayName("Inicialización: ControladorSelectMision carga misiones disponibles y bitácora")
    public void testInicializacionCargaMisionesYBitacora() {
        int eventosIniciales = asistente.getEventos().size();
        asistente.registrarEvento(TipoEvento.SISTEMA, "Prueba de evento 1");
        asistente.registrarEvento(TipoEvento.RELEVANTE, "Prueba de evento 2");

        ControladorControl controlador = new ControladorControl(mockVistaControl, asistente);

        assertNotNull(mockVistaSelect.controlador);
        assertNotNull(mockVistaSelect.misionesDisponibles);
        assertEquals(3, mockVistaSelect.misionesDisponibles.size());

        assertNotNull(mockVistaSelect.eventosMostrados);
        assertEquals(eventosIniciales + 2, mockVistaSelect.eventosMostrados.size());
    }

    @Test
    @DisplayName("Selección de misión: actualiza detalles y costos en la vista")
    public void testSeleccionarMisionActualizaDetalles() throws Exception {
        equiparTripulacionCompleta();
        ControladorControl controlador = new ControladorControl(mockVistaControl, asistente);

        mockVistaSelect.misionSeleccionadaCodigo = "M-02";

        mockVistaSelect.controlador.actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaSelectMision.COMANDO_SELECCIONAR_MISION)
        );

        assertNotNull(mockVistaSelect.ultimaMisionDetallada);
        assertEquals("M-02", mockVistaSelect.ultimaMisionDetallada.getCodigo());
        assertNotNull(controlador.getControladorSelectMision().getMisionSeleccionada());
        assertEquals("M-02", controlador.getControladorSelectMision().getMisionSeleccionada().getCodigo());
        assertTrue(mockVistaSelect.naveListaDetallada);
        assertEquals(60, mockVistaSelect.combustibleDetallado);
        assertEquals(80, mockVistaSelect.energiaDetallada);
    }

    @Test
    @DisplayName("Actualizar bitácora: refresca eventos tras nuevas entradas")
    public void testActualizarBitacora() {
        int eventosIniciales = asistente.getEventos().size();
        ControladorControl controlador = new ControladorControl(mockVistaControl, asistente);
        assertEquals(eventosIniciales, mockVistaSelect.eventosMostrados.size());

        asistente.registrarEvento(TipoEvento.ERROR, "Alerta de prueba");

        mockVistaSelect.controlador.actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaSelectMision.COMANDO_ACTUALIZAR_BITACORA)
        );

        assertEquals(eventosIniciales + 1, mockVistaSelect.eventosMostrados.size());
        assertEquals("Alerta de prueba",
                mockVistaSelect.eventosMostrados.get(mockVistaSelect.eventosMostrados.size() - 1).getDescripcion());
    }

    @Test
    @DisplayName("Iniciar misión con nave no lista: muestra error y no encomienda")
    public void testIniciarMisionNaveNoListaMuestraError() {
        // La nave no tiene tripulación, por lo tanto no está lista
        assertFalse(asistente.naveListaParaOperar());

        ControladorControl controlador = new ControladorControl(mockVistaControl, asistente);
        mockVistaSelect.misionSeleccionadaCodigo = "M-01";

        mockVistaSelect.controlador.actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaSelectMision.COMANDO_INICIAR_MISION)
        );

        assertNotNull(mockVistaSelect.ultimoError);
        assertFalse(asistente.tieneMisionPendiente());
        assertFalse(mockVistaControl.mostrandoMisionCurso);
    }

    @Test
    @DisplayName("Iniciar misión con nave lista: encomienda misión y pasa a Mision en Curso")
    public void testIniciarMisionExitosoPasaAMisionCurso() throws Exception {
        equiparTripulacionCompleta();
        assertTrue(asistente.naveListaParaOperar());

        ControladorControl controlador = new ControladorControl(mockVistaControl, asistente);
        mockVistaSelect.misionSeleccionadaCodigo = "M-01";

        mockVistaSelect.controlador.actionPerformed(
                new ActionEvent(this, ActionEvent.ACTION_PERFORMED, IVistaSelectMision.COMANDO_INICIAR_MISION)
        );

        assertNull(mockVistaSelect.ultimoError);
        assertTrue(asistente.tieneMisionPendiente());
        assertEquals("M-01", asistente.getMisionPendiente().getCodigo());
        assertTrue(mockVistaControl.mostrandoMisionCurso);
    }

    @Test
    @DisplayName("Transiciones de vista en VistaControlMock")
    public void testTransicionesVistaControl() {
        ControladorControl controlador = new ControladorControl(mockVistaControl, asistente);

        controlador.mostrarMisionCurso();
        assertTrue(mockVistaControl.mostrandoMisionCurso);

        controlador.mostrarResultadosMision();
        assertTrue(mockVistaControl.mostrandoResultados);

        controlador.mostrarSelectMision();
        assertTrue(mockVistaControl.mostrandoSelectMision);
    }

    // =========================================================================
    // MOCKS DE VISTA PARA PRUEBAS SIN SWING
    // =========================================================================

    private static class VistaSelectMisionMock implements IVistaSelectMision {
        ActionListener controlador;
        List<Evento> eventosMostrados = new ArrayList<>();
        List<Mision> misionesDisponibles = new ArrayList<>();
        String misionSeleccionadaCodigo;
        Mision ultimaMisionDetallada;
        boolean naveListaDetallada;
        int combustibleDetallado;
        int energiaDetallada;
        boolean botonIniciarHabilitado;
        String ultimoError;
        String ultimoMensaje;

        @Override
        public void setControlador(ActionListener controlador) {
            this.controlador = controlador;
        }

        @Override
        public void mostrarBitacora(List<Evento> eventos) {
            this.eventosMostrados = eventos != null ? new ArrayList<>(eventos) : new ArrayList<>();
        }

        @Override
        public void mostrarMisionesDisponibles(List<Mision> misiones) {
            this.misionesDisponibles = misiones != null ? new ArrayList<>(misiones) : new ArrayList<>();
            if (!this.misionesDisponibles.isEmpty() && this.misionSeleccionadaCodigo == null) {
                this.misionSeleccionadaCodigo = this.misionesDisponibles.get(0).getCodigo();
            }
        }

        @Override
        public String getMisionSeleccionada() {
            return this.misionSeleccionadaCodigo;
        }

        @Override
        public void mostrarDetallesMision(Mision mision, boolean naveListaParaOperar, int combustibleActual, int energiaActual) {
            this.ultimaMisionDetallada = mision;
            this.naveListaDetallada = naveListaParaOperar;
            this.combustibleDetallado = combustibleActual;
            this.energiaDetallada = energiaActual;
        }

        @Override
        public void habilitarIniciarMision(boolean habilitar) {
            this.botonIniciarHabilitado = habilitar;
        }

        @Override
        public void mostrarError(String mensaje) {
            this.ultimoError = mensaje;
        }

        @Override
        public void mostrarMensaje(String mensaje) {
            this.ultimoMensaje = mensaje;
        }
    }

    private static class VistaControlMock implements IVistaControl {
        final IVistaSelectMision vistaSelect;
        boolean mostrandoSelectMision = true;
        boolean mostrandoMisionCurso = false;
        boolean mostrandoResultados = false;

        VistaControlMock(IVistaSelectMision vistaSelect) {
            this.vistaSelect = vistaSelect;
        }

        @Override
        public void mostrarSelectMision() {
            this.mostrandoSelectMision = true;
            this.mostrandoMisionCurso = false;
            this.mostrandoResultados = false;
        }

        @Override
        public void mostrarMisionCurso() {
            this.mostrandoSelectMision = false;
            this.mostrandoMisionCurso = true;
            this.mostrandoResultados = false;
        }

        @Override
        public void mostrarResultadosMision() {
            this.mostrandoSelectMision = false;
            this.mostrandoMisionCurso = false;
            this.mostrandoResultados = true;
        }

        @Override
        public IVistaSelectMision getVistaSelectMision() {
            return this.vistaSelect;
        }
    }
}
