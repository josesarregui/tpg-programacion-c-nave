package vista.principal.paneldetallestripulacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class VistaDetallesTripulacionTest {

    @BeforeEach
    public void requiereEntornoGrafico() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Sin entorno gráfico no se pueden crear componentes Swing.");
    }

    @Test
    @DisplayName("VistaDetallesTripulacion se inicializa como transparente y con valores iniciales por defecto")
    public void inicializacionVista() {
        VistaDetallesTripulacion vista = new VistaDetallesTripulacion();
        assertFalse(vista.isOpaque(), "La vista debe ser transparente para apreciar el fondo animado.");
        assertEquals("Sin Asignar", vista.getLblCapitan().getText());
        assertEquals("0 Tripulantes", vista.getLblCantidad().getText());
        assertEquals("Pendiente de Asignación", vista.getLblEstadoOperativo().getText());
        assertEquals(0, vista.getModeloLista().getSize());
        assertEquals("Sin oficiales a bordo", vista.getLblContadorLista().getText());
    }

    @Test
    @DisplayName("mostrarCantidad y mostrarCapitan actualizan los indicadores y badges")
    public void actualizacionIndicadores() {
        VistaDetallesTripulacion vista = new VistaDetallesTripulacion();

        vista.mostrarCantidad(5);
        assertEquals("5 Tripulantes", vista.getLblCantidad().getText());
        assertEquals("Dotación Lista para Zarpar", vista.getLblEstadoOperativo().getText());
        assertEquals("5 oficiales a bordo", vista.getLblContadorLista().getText());

        vista.mostrarCapitan("James T. Kirk");
        assertEquals("James T. Kirk", vista.getLblCapitan().getText());

        vista.mostrarCantidad(1);
        assertEquals("1 Tripulante", vista.getLblCantidad().getText());
        assertEquals("1 oficial a bordo", vista.getLblContadorLista().getText());

        vista.mostrarCantidad(0);
        assertEquals("0 Tripulantes", vista.getLblCantidad().getText());
        assertEquals("Pendiente de Asignación", vista.getLblEstadoOperativo().getText());

        vista.mostrarCapitan(null);
        assertEquals("Sin Asignar", vista.getLblCapitan().getText());
    }

    @Test
    @DisplayName("agregarTripulante y limpiarTripulantes manipulan el modelo y el contador")
    public void manipulacionLista() {
        VistaDetallesTripulacion vista = new VistaDetallesTripulacion();

        vista.agregarTripulante(1, "James Kirk", "Capitán");
        vista.agregarTripulante(2, "Spock", "Consejero");
        vista.agregarTripulante(3, "Montgomery Scott", "Teniente");

        assertEquals(3, vista.getModeloLista().getSize());
        assertEquals("3 oficiales a bordo", vista.getLblContadorLista().getText());
        assertTrue(vista.getModeloLista().getElementAt(0).contains("James Kirk"));
        assertTrue(vista.getModeloLista().getElementAt(1).contains("Consejero"));

        vista.limpiarTripulantes();
        assertEquals(0, vista.getModeloLista().getSize());
        assertEquals("Sin oficiales a bordo", vista.getLblContadorLista().getText());
    }

    @Test
    @DisplayName("El renderer de celda genera componentes válidos para diferentes cargos")
    public void testRendererCelda() {
        VistaDetallesTripulacion vista = new VistaDetallesTripulacion();
        ListCellRenderer<? super String> renderer = vista.getListaTripulantes().getCellRenderer();
        assertNotNull(renderer);

        String[] muestras = {
                "ID: 1   |   Kirk   |   Capitán",
                "ID: 2   |   Spock   |   Consejero",
                "ID: 3   |   Sulu   |   Teniente",
                "ID: 4   |   Chekov   |   Alférez",
                "#5 Uhura (Teniente, Terrícola, 3 años)"
        };

        for (int i = 0; i < muestras.length; i++) {
            Component c = renderer.getListCellRendererComponent(
                    vista.getListaTripulantes(),
                    muestras[i],
                    i,
                    i == 0,
                    false
            );
            assertNotNull(c);
        }
    }
}
