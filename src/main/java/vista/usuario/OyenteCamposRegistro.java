package vista.usuario;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Clase oyente de los campos del registro (apunte "Interfaces gráficas", ejemplo de la ventana de login).
 * Recibe la referencia a la ventana y obtiene los componentes con sus getters.
 *
 * Hace las validaciones de formato de la pantalla: campos completos y contraseñas repetidas iguales.
 * Las reglas de negocio (nombre sin espacios, longitud máxima, usuario no repetido) las aplica el servicio.
 */
class OyenteCamposRegistro extends KeyAdapter {

    private final VistaRegistro ventana;

    OyenteCamposRegistro(VistaRegistro ventana) {
        this.ventana = ventana;
    }

    @Override
    public void keyReleased(KeyEvent evento) {
        verificarCampos();
    }

    /**
     * Habilita "Registrar" sólo si los tres campos están completos y las contraseñas coinciden, y avisa cuando
     * no coinciden. La ventana también lo invoca después de vaciar los campos.
     */
    void verificarCampos() {
        String contrasena = new String(ventana.getCampoContrasena().getPassword());
        String repeticion = new String(ventana.getCampoRepetirContrasena().getPassword());
        boolean usuarioCompleto = !ventana.getCampoUsuario().getText().isBlank();
        boolean contrasenasCompletas = !contrasena.isEmpty() && !repeticion.isEmpty();
        boolean contrasenasCoinciden = contrasena.equals(repeticion);

        if (contrasenasCompletas && !contrasenasCoinciden) {
            ventana.getEtiquetaAviso().setText("Las contraseñas no coinciden.");
        } else {
            ventana.getEtiquetaAviso().setText(" ");
        }
        ventana.getBotonRegistrar().setEnabled(usuarioCompleto && contrasenasCompletas && contrasenasCoinciden);
    }
}
