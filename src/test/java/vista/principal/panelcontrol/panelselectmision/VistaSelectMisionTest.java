package vista.principal.panelcontrol.panelselectmision;

import java.awt.GraphicsEnvironment;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.SwingUtilities;
import modelo.asistente.AsistenteComando;
import modelo.bitacora.Evento;
import modelo.bitacora.TipoEvento;
import modelo.mision.Mision;
import modelo.mision.MisionIntercepcion;
import modelo.mision.MisionRecoleccion;
import modelo.mision.MisionRetorno;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.universo.Observador;
import modelo.universo.Destino;
import modelo.universo.FabricaDeUniverso;
import modelo.universo.Tablero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static vista.EdtDePrueba.conEdtBloqueado;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Pruebas unitarias para la interfaz gráfica VistaSelectMision.
 */
public class VistaSelectMisionTest {

    private final Tablero tablero = new FabricaDeUniverso().crearTablero();
    private final Destino destino = new Destino(tablero);

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

        Mision m1 = new MisionIntercepcion(tablero, destino);
        Mision m2 = new MisionRecoleccion(tablero, destino);
        Mision m3 = new MisionRetorno(tablero, destino);

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
        Mision m1 = new MisionIntercepcion(tablero, destino);

        // Recursos insuficientes
        vista.mostrarDetallesMision(m1, false, 0, 0);
        esperarEDT();
        assertFalse(vista.getBtnIniciarMision().isEnabled());

        // Recursos y estado suficientes para cualquier sorteo de suministros (hasta 50 de cada uno, Anexo)
        vista.mostrarDetallesMision(m1, true, 100, 100);
        esperarEDT();
        assertTrue(vista.getBtnIniciarMision().isEnabled());
    }

    @Test
    @DisplayName("Vista: los detalles describen cada misión por polimorfismo (nombre y condición de éxito)")
    public void testDetallesPorPolimorfismo() throws Exception {
        VistaSelectMision vista = new VistaSelectMision();
        Mision m2 = new MisionRecoleccion(tablero, destino);

        vista.mostrarDetallesMision(m2, true, 50, 50);

        assertEquals("M-02", vista.getLblCodigoMision().getText());
        assertEquals("Condición de éxito: " + m2.getCondicionDeExito() + ".", vista.getLblCondicionExito().getText());
    }

    @Test
    @DisplayName("Observer: la vista se suscribe sola y la Bitácora se actualiza en el EDT, sin bloquear al que notifica")
    public void testBitacoraPorObserverEnElEdt() throws Exception {
        VistaSelectMision vista = new VistaSelectMision();
        Observador comoObservador = vista;
        assertNotNull(comoObservador);
        AsistenteComando asistente = new AsistenteComando(new NaveFactory().crearNave(TipoNave.CARGUERO));
        vista.observar(asistente);
        int filasAntes = vista.getModeloTablaBitacora().getRowCount();

        conEdtBloqueado(
                () -> asistente.registrarEvento(TipoEvento.RELEVANTE, "Evento desde otro hilo"),
                () -> assertEquals(filasAntes, vista.getModeloTablaBitacora().getRowCount(),
                        "Con el EDT bloqueado la tabla todavía no cambió."));

        int filas = vista.getModeloTablaBitacora().getRowCount();
        assertEquals(asistente.getEventos().size(), filas);
        assertEquals("Evento desde otro hilo", vista.getModeloTablaBitacora().getValueAt(filas - 1, 2));

        vista.dejarDeObservar();
        asistente.registrarEvento(TipoEvento.SISTEMA, "Evento sin observador");
        esperarEDT();
        assertEquals(filas, vista.getModeloTablaBitacora().getRowCount(), "Tras dejar de observar no cambia.");
    }
}
