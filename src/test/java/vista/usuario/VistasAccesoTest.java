package vista.usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.text.JTextComponent;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Evidencia de las validaciones de formato de las vistas de acceso, hechas por las clases oyentes
 * (apunte "Interfaces gráficas": el botón permanece deshabilitado si los campos están vacíos).
 * Las ventanas se crean pero no se muestran. Sin entorno gráfico (headless) las pruebas se omiten.
 */
public class VistasAccesoTest {

    @BeforeEach
    public void requiereEntornoGrafico() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Sin entorno gráfico no se pueden crear ventanas Swing.");
    }

    @Test
    @DisplayName("Login: Ingresar se habilita sólo con usuario y contraseña completos, y se deshabilita al limpiar")
    public void botonIngresar() {
        VistaLogin vista = new VistaLogin();
        assertFalse(vista.getBotonIngresar().isEnabled());

        escribir(vista.getCampoUsuario(), "kirk");
        assertFalse(vista.getBotonIngresar().isEnabled(), "Falta la contraseña.");

        escribir(vista.getCampoContrasena(), "enterprise");
        assertTrue(vista.getBotonIngresar().isEnabled());

        vista.limpiarContrasena();
        assertFalse(vista.getBotonIngresar().isEnabled());
        assertEquals("kirk", vista.getNombreUsuario(), "Limpiar la contraseña conserva el usuario.");
        vista.cerrar();
    }

    @Test
    @DisplayName("Login: el nombre de usuario se entrega sin espacios al principio ni al final")
    public void nombreSinEspacios() {
        VistaLogin vista = new VistaLogin();
        escribir(vista.getCampoUsuario(), "  kirk ");
        assertEquals("kirk", vista.getNombreUsuario());
        vista.cerrar();
    }

    @Test
    @DisplayName("Registro: con contraseñas distintas se avisa y Registrar queda deshabilitado; iguales lo habilitan")
    public void botonRegistrar() {
        VistaRegistro vista = new VistaRegistro();
        escribir(vista.getCampoUsuario(), "picard");
        escribir(vista.getCampoContrasena(), "clave");
        assertFalse(vista.getBotonRegistrar().isEnabled(), "Falta repetir la contraseña.");
        assertEquals(" ", vista.getEtiquetaAviso().getText());

        escribir(vista.getCampoRepetirContrasena(), "otra");
        assertFalse(vista.getBotonRegistrar().isEnabled());
        assertEquals("Las contraseñas no coinciden.", vista.getEtiquetaAviso().getText());

        escribir(vista.getCampoRepetirContrasena(), "clave");
        assertTrue(vista.getBotonRegistrar().isEnabled());
        assertEquals(" ", vista.getEtiquetaAviso().getText());

        vista.limpiarCampos();
        assertFalse(vista.getBotonRegistrar().isEnabled());
        vista.cerrar();
    }

    /**
     * Escribe el texto en el campo y notifica la tecla soltada a los oyentes registrados en él, como lo haría
     * Swing (una ventana que no se muestra no tiene el foco, por eso no recibe eventos de teclado reales).
     */
    private static void escribir(JTextComponent campo, String texto) {
        campo.setText(texto);
        KeyEvent evento = new KeyEvent(campo, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0,
                KeyEvent.VK_A, 'a');
        for (KeyListener oyente : campo.getKeyListeners()) {
            oyente.keyReleased(evento);
        }
    }
}
