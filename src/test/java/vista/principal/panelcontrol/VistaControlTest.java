package vista.principal.panelcontrol;

import java.awt.GraphicsEnvironment;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vista.principal.VistaPrincipal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * La ventana principal arma sus cuatro secciones y la sección Control de la Nave provee todas sus vistas.
 */
public class VistaControlTest {

    @BeforeEach
    public void requiereEntornoGrafico() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Sin entorno gráfico no se pueden crear componentes Swing.");
    }

    @Test
    @DisplayName("La ventana principal provee las secciones y la de Control, sus vistas de recursos, misión, tablero e informes")
    public void secciones() throws Exception {
        AtomicReference<VistaPrincipal> principal = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> principal.set(new VistaPrincipal()));

        assertNotNull(principal.get().getPanelNave());
        assertNotNull(principal.get().getPanelTripulacion());
        assertNotNull(principal.get().getPanelVisor());
        IVistaControl control = principal.get().getPanelControl();
        assertNotNull(control.getVistaRecursos());
        assertNotNull(control.getVistaSelectMision());
        assertNotNull(control.getVistaCursoMision());
        assertNotNull(control.getVistaCursoMision().getVistaTablero());
        assertNotNull(control.getVistaResultadosMision());

        SwingUtilities.invokeAndWait(() -> {
            control.mostrarMisionCurso();
            control.mostrarResultadosMision();
            control.mostrarSelectMision();
            principal.get().cerrar();
        });
    }
}
