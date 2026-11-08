package vista.principal.panelselectmision;

import java.awt.GraphicsEnvironment;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.SwingUtilities;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.mision.Mision;
import modelo.mision.MisionIntercepcion;
import modelo.mision.MisionRecoleccion;
import modelo.mision.MisionRetorno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Pruebas unitarias para la interfaz gráfica VistaSelectMision.
 */
public class VistaSelectMisionTest {

    @BeforeEach
    public void verificarEntornoGrafico() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Las pruebas de componentes Swing requieren entorno gráfico.");
    }

    private void esperarEDT() throws Exception {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeAndWait(() -> {
            });
        }
    }

    @Test
    @DisplayName("Vista: mostrarBitacora carga filas correctamente en JTable")
    public void testMostrarBitacora() throws Exception {
        VistaSelectMision vista = new VistaSelectMision();

        List<Evento> eventos = List.of(
                new Evento(LocalDateTime.now(), TipoEvento.SISTEMA, "Evento informativo inicial"),
                new Evento(LocalDateTime.now(), TipoEvento.ERROR, "Alerta de prueba"));

        vista.mostrarBitacora(eventos);
        esperarEDT();

        assertEquals(2, vista.getModeloTablaBitacora().getRowCount());
        assertEquals("SISTEMA", vista.getModeloTablaBitacora().getValueAt(0, 1));
        assertEquals("Evento informativo inicial", vista.getModeloTablaBitacora().getValueAt(0, 2));
        assertEquals("ERROR", vista.getModeloTablaBitacora().getValueAt(1, 1));
        assertEquals("Alerta de prueba", vista.getModeloTablaBitacora().getValueAt(1, 2));
    }

    @Test
    @DisplayName("Vista: mostrarMisionesDisponibles puebla JComboBox y getMisionSeleccionada")
    public void testMostrarMisionesDisponibles() throws Exception {
        VistaSelectMision vista = new VistaSelectMision();

        Mision m1 = new MisionIntercepcion();
        Mision m2 = new MisionRecoleccion();
        Mision m3 = new MisionRetorno();

        vista.mostrarMisionesDisponibles(List.of(m1, m2, m3));
        esperarEDT();

        assertEquals(3, vista.getComboMisiones().getItemCount());
        assertEquals("M-01", vista.getComboMisiones().getItemAt(0));
        assertEquals("M-02", vista.getComboMisiones().getItemAt(1));
        assertEquals("M-03", vista.getComboMisiones().getItemAt(2));
        assertNotNull(vista.getMisionSeleccionada());
        assertEquals("M-01", vista.getMisionSeleccionada());

        vista.getComboMisiones().setSelectedIndex(1);
        esperarEDT();
        assertEquals("M-02", vista.getMisionSeleccionada());
    }

    @Test
    @DisplayName("Vista: mostrarDetallesMision habilita o deshabilita el botón según operatividad")
    public void testMostrarDetallesMisionHabilitaBoton() throws Exception {
        VistaSelectMision vista = new VistaSelectMision();
        Mision m1 = new MisionIntercepcion();

        // Recursos insuficientes
        vista.mostrarDetallesMision(m1, false, 0, 0);
        esperarEDT();
        assertFalse(vista.getBtnIniciarMision().isEnabled());

        // Recursos y estado suficientes
        vista.mostrarDetallesMision(m1, true, 50, 50);
        esperarEDT();
        assertTrue(vista.getBtnIniciarMision().isEnabled());
    }
}
