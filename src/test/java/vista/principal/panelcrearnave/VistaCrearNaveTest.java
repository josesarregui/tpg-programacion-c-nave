package vista.principal.panelcrearnave;

import controlador.nave.ControladorCrearNave;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import modelo.nave.TipoNave;
import modelo.universo.CentroDeControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class VistaCrearNaveTest {

    @BeforeEach
    public void requiereEntornoGrafico() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Sin entorno gráfico no se pueden crear componentes Swing.");
    }

    @Test
    @DisplayName("VistaCrearNave se inicializa transparente y sin nave seleccionada")
    public void inicializacionVista() {
        VistaCrearNave vista = new VistaCrearNave();
        assertFalse(vista.isOpaque(), "La vista debe ser transparente para apreciar el fondo animado.");
        assertNull(vista.getTipoSeleccionado());
        assertEquals("Ninguna nave seleccionada", vista.getLblSeleccion().getText());
    }

    @Test
    @DisplayName("Seleccionar cada tarjeta actualiza el estado visual y renderiza sin errores")
    public void seleccionYRenderizadoTarjetas() {
        VistaCrearNave vista = new VistaCrearNave();
        vista.setSize(1020, 660);
        vista.doLayout();

        vista.setTipoSeleccionado(TipoNave.EXPLORADORA);
        assertEquals(TipoNave.EXPLORADORA, vista.getTipoSeleccionado());
        assertEquals("Nave seleccionada: Exploradora", vista.getLblSeleccion().getText());

        vista.setTipoSeleccionado(TipoNave.CARGUERO);
        assertEquals(TipoNave.CARGUERO, vista.getTipoSeleccionado());
        assertEquals("Nave seleccionada: Carguero", vista.getLblSeleccion().getText());

        vista.setTipoSeleccionado(TipoNave.COMBATE);
        assertEquals(TipoNave.COMBATE, vista.getTipoSeleccionado());
        assertEquals("Nave seleccionada: Combate", vista.getLblSeleccion().getText());

        BufferedImage lienzo = new BufferedImage(1020, 660, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = lienzo.createGraphics();
        assertDoesNotThrow(() -> vista.printAll(g2));
        g2.dispose();
    }

    @Test
    @DisplayName("Integración con ControladorCrearNave: clic en tarjeta y confirmar registra la nave en CentroDeControl")
    public void integracionConControlador() {
        VistaCrearNave vista = new VistaCrearNave();
        CentroDeControl centro = new CentroDeControl();
        new ControladorCrearNave(vista, null, centro);

        vista.getBotonCarguera().doClick();
        assertEquals(TipoNave.CARGUERO, vista.getTipoSeleccionado());

        vista.getBotonAceptar().doClick();
        assertNotNull(centro.getAsistenteEnUso());
        assertEquals(TipoNave.CARGUERO, centro.getAsistenteEnUso().getTipoNave());
    }
}
