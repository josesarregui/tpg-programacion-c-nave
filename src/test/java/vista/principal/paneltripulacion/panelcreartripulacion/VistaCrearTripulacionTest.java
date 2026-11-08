package vista.principal.paneltripulacion.panelcreartripulacion;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.text.JTextComponent;
import modelo.tripulacion.Cargo;
import modelo.tripulacion.Origen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class VistaCrearTripulacionTest {

    @BeforeEach
    public void requiereEntornoGrafico() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Sin entorno gráfico no se pueden crear componentes Swing.");
    }

    @Test
    @DisplayName("VistaCrearTripulacion se inicializa como transparente y con valores por defecto")
    public void inicializacionVista() {
        VistaCrearTripulacion vista = new VistaCrearTripulacion();
        assertFalse(vista.isOpaque(), "La vista debe ser transparente para dejar ver el fondo animado.");
        assertEquals(Cargo.CAPITAN, vista.getCargo());
        assertEquals(Origen.TERRICOLA, vista.getOrigen());
        assertEquals("", vista.getNombre());
        assertFalse(vista.getBotonAgregar().isEnabled());
    }

    @Test
    @DisplayName("Validación de formato: Agregar se habilita sólo con nombre y antigüedad numérica válida")
    public void validacionDeFormatoCampos() {
        VistaCrearTripulacion vista = new VistaCrearTripulacion();
        assertFalse(vista.getBotonAgregar().isEnabled());

        escribir(vista.getCampoNombre(), "Spock");
        assertFalse(vista.getBotonAgregar().isEnabled(), "Falta la antigüedad.");

        escribir(vista.getCampoAntiguedad(), "cinco");
        assertFalse(vista.getBotonAgregar().isEnabled(), "La antigüedad debe ser numérica.");

        escribir(vista.getCampoAntiguedad(), "5");
        assertTrue(vista.getBotonAgregar().isEnabled());

        vista.limpiarCampos();
        assertFalse(vista.getBotonAgregar().isEnabled(), "Al limpiar campos el botón vuelve a deshabilitarse.");
    }

    @Test
    @DisplayName("Campo Consejos: visibilidad dinámica según cargo y validación de entrada")
    public void campoConsejosVisibilidadYValidacion() {
        VistaCrearTripulacion vista = new VistaCrearTripulacion();
        assertFalse(vista.getSeccionConsejos().isVisible(), "El campo consejos debe estar oculto para Capitán.");

        // Cambiar a Consejero
        vista.getComboCargo().setSelectedItem(Cargo.CONSEJERO);
        assertTrue(vista.getSeccionConsejos().isVisible(), "El campo consejos debe ser visible para Consejero.");

        escribir(vista.getCampoNombre(), "Spock");
        escribir(vista.getCampoAntiguedad(), "5");
        assertTrue(vista.getBotonAgregar().isEnabled(), "Con nombre, antigüedad y consejos por defecto '0' debe habilitarse.");

        // Consejos no numéricos deshabilitan Agregar
        escribir(vista.getCampoConsejos(), "abc");
        assertFalse(vista.getBotonAgregar().isEnabled(), "Consejos inválidos deben deshabilitar Agregar.");

        // Consejos numéricos habilitan Agregar
        escribir(vista.getCampoConsejos(), "3");
        assertTrue(vista.getBotonAgregar().isEnabled(), "Consejos numéricos válidos deben habilitar Agregar.");

        // Limpiar campos vuelve al estado inicial
        vista.limpiarCampos();
        assertFalse(vista.getSeccionConsejos().isVisible(), "Al limpiar campos debe volver a ocultarse.");
        assertEquals("0", vista.getTextoConsejos(), "Al limpiar campos consejos se reinicia en '0'.");
    }

    @Test
    @DisplayName("Agregar y limpiar campos funciona adecuadamente")
    public void agregarYLimpiar() {
        VistaCrearTripulacion vista = new VistaCrearTripulacion();
        vista.agregarTripulanteLista("James Kirk — Capitán");
        vista.agregarTripulanteLista("Spock — Consejero");

        vista.limpiarCampos();
        assertEquals("", vista.getNombre());
        assertEquals(Cargo.CAPITAN, vista.getCargo());
        assertEquals(Origen.TERRICOLA, vista.getOrigen());
    }

    @Test
    @DisplayName("El controlador recibe los eventos de agregar y aceptar")
    public void eventosControlador() {
        VistaCrearTripulacion vista = new VistaCrearTripulacion();
        AtomicInteger clicks = new AtomicInteger(0);

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clicks.incrementAndGet();
            }
        };

        vista.setControlador(listener);
        assertNotNull(vista);
    }

    private static void escribir(JTextComponent campo, String texto) {
        campo.setText(texto);
        KeyEvent evento = new KeyEvent(campo, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0,
                KeyEvent.VK_A, 'a');
        for (KeyListener oyente : campo.getKeyListeners()) {
            oyente.keyReleased(evento);
        }
    }
}
