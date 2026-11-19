package vista.principal.panelcontrol.panelcursomision;

import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;
import modelo.asistente.AsistenteComando;
import modelo.bitacora.TipoEvento;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static vista.EdtDePrueba.conEdtBloqueado;
import static vista.EdtDePrueba.esperarEdt;

/**
 * Vista de la Misión en Curso: controles según el estado de la misión y modificaciones en el EDT de lo que llega
 * desde el hilo de la misión (consola, fin de misión).
 */
public class VistaCursoMisionTest {

    @BeforeEach
    public void requiereEntornoGrafico() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Sin entorno gráfico no se pueden crear componentes Swing.");
    }

    @Test
    @DisplayName("Controles: comandos de acción y habilitación al iniciar la misión")
    public void controles() throws Exception {
        VistaCursoMision vista = new VistaCursoMision();
        assertNotNull(vista.getVistaTablero());
        assertEquals(IVistaCursoMision.COMANDO_CANCELAR_MISION, vista.getBtnCancelar().getActionCommand());
        assertEquals(IVistaCursoMision.COMANDO_VOLVER, vista.getBtnVolver().getActionCommand());
        assertFalse(vista.getBtnCancelar().isEnabled());

        SwingUtilities.invokeAndWait(() -> vista.misionIniciada("M-02", "Recolección"));

        assertTrue(vista.getBtnCancelar().isEnabled());
        assertFalse(vista.getBtnVolver().isEnabled());
    }

    @Test
    @DisplayName("Fin de misión: llega desde otro hilo y se aplica en el EDT")
    public void finDeMisionEnElEdt() throws Exception {
        VistaCursoMision vista = new VistaCursoMision();
        SwingUtilities.invokeAndWait(() -> vista.misionIniciada("M-02", "Recolección"));
        String estadoInicial = vista.getTextoEstado();

        conEdtBloqueado(
                () -> vista.misionFinalizada("Informe de M-02: EXITOSA."),
                () -> assertEquals(estadoInicial, vista.getTextoEstado(), "Con el EDT bloqueado no cambió."));

        assertEquals("Informe de M-02: EXITOSA.", vista.getTextoEstado());
        assertFalse(vista.getBtnCancelar().isEnabled());
        assertTrue(vista.getBtnVolver().isEnabled());
    }

    @Test
    @DisplayName("Observer: la consola agrega el mensaje del asistente (push) en el EDT")
    public void consolaPorObserver() throws Exception {
        VistaCursoMision vista = new VistaCursoMision();
        AsistenteComando asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.EXPLORADORA));
        vista.observar(asistente);

        conEdtBloqueado(
                () -> asistente.registrarEvento(TipoEvento.RELEVANTE, "Encuentro con un asteroide."),
                () -> assertFalse(vista.getTextoConsola().contains("Encuentro con un asteroide.")));

        assertTrue(vista.getTextoConsola().contains("Encuentro con un asteroide."));

        vista.dejarDeObservar();
        asistente.registrarEvento(TipoEvento.SISTEMA, "Sin observador.");
        esperarEdt();
        assertFalse(vista.getTextoConsola().contains("Sin observador."));
    }
}
