package vista.concurrencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class VistaConcurrenciaTest {

    @BeforeEach
    public void requiereEntornoGrafico() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Sin entorno gráfico no se pueden crear componentes Swing.");
    }

    @Test
    @DisplayName("VistaConcurrencia inicializa componentes y botones con action commands")
    public void testInicializacion() {
        VistaConcurrencia vista = new VistaConcurrencia();

        assertNotNull(vista.getLblBaterias());
        assertNotNull(vista.getLblGeneradoras());
        assertNotNull(vista.getLblNaves());
        assertNotNull(vista.getAreaConsola());

        assertEquals(IVistaConcurrencia.COMANDO_CREAR_GENERADORA, vista.getBtnCrearGeneradora().getActionCommand());
        assertEquals(IVistaConcurrencia.COMANDO_CREAR_NAVE, vista.getBtnCrearNave().getActionCommand());
        assertEquals(IVistaConcurrencia.COMANDO_LIMPIAR_CONSOLA, vista.getBtnLimpiarConsola().getActionCommand());
    }

    @Test
    @DisplayName("agregarEvento y limpiarConsola manipulan el área de texto")
    public void testAgregarYLimpiarEventos() {
        VistaConcurrencia vista = new VistaConcurrencia();

        vista.agregarEvento("Prueba de evento 1");
        vista.agregarEvento("Prueba de evento 2");

        String texto = vista.getAreaConsola().getText();
        assertTrue(texto.contains("Prueba de evento 1"));
        assertTrue(texto.contains("Prueba de evento 2"));

        vista.limpiarConsola();
        assertEquals("", vista.getAreaConsola().getText());
    }

    @Test
    @DisplayName("VistaConcurrencia es una IVistaConcurrencia y un ObservadorConcurrencia")
    public void testImplementacionInterfaces() {
        VistaConcurrencia vista = new VistaConcurrencia();
        assertTrue(vista instanceof IVistaConcurrencia);
        assertTrue(vista instanceof modelo.concurrencia.ObservadorConcurrencia);
    }

    @Test
    @DisplayName("actualizarBaterias y actualizarEntidades modifican los labels de métricas")
    public void testActualizarContadores() {
        VistaConcurrencia vista = new VistaConcurrencia();

        vista.actualizarBaterias(3, 5);
        vista.actualizarEntidades(2, 4);

        assertEquals("3 / 5", vista.getLblBaterias().getText());
        assertEquals("2 activas", vista.getLblGeneradoras().getText());
        assertEquals("4 activas", vista.getLblNaves().getText());
    }

    @Test
    @DisplayName("Las columnas laterales cuentan con sus listas y modelos de entidades")
    public void testModelosListasEntidades() throws Exception {
        VistaConcurrencia vista = new VistaConcurrencia();

        assertNotNull(vista.getModeloGeneradoras());
        assertNotNull(vista.getModeloNaves());
        assertNotNull(vista.getListaGeneradoras());
        assertNotNull(vista.getListaNaves());

        javax.swing.SwingUtilities.invokeAndWait(() -> {
            vista.agregarGeneradora("G-1");
            vista.agregarNaveConsumidora("Nave-1");
        });

        assertEquals(1, vista.getModeloGeneradoras().size());
        assertTrue(vista.getModeloGeneradoras().get(0).contains("G-1"));

        assertEquals(1, vista.getModeloNaves().size());
        assertTrue(vista.getModeloNaves().get(0).contains("Nave-1"));

        javax.swing.SwingUtilities.invokeAndWait(vista::limpiarEntidades);
        assertEquals(0, vista.getModeloGeneradoras().size());
        assertEquals(0, vista.getModeloNaves().size());
    }
}
