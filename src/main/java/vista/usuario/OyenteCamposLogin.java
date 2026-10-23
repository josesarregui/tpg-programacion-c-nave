package vista.usuario;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Clase oyente de los campos del login (apunte "Interfaces gráficas", ejemplo de la ventana de login: el botón
 * permanece deshabilitado si los campos están vacíos).
 *
 * Extiende la clase adaptadora {@link KeyAdapter}, de modo que sólo redefine el evento que le interesa.
 * Recibe por constructor la referencia a la ventana y obtiene los componentes con sus getters.
 */
class OyenteCamposLogin extends KeyAdapter {

    private final VistaLogin ventana;

    OyenteCamposLogin(VistaLogin ventana) {
        this.ventana = ventana;
    }

    @Override
    public void keyReleased(KeyEvent evento) {
        verificarCampos();
    }

    /**
     * Habilita "Ingresar" sólo si el usuario y la contraseña están completos. La ventana también lo invoca
     * después de vaciar los campos, porque vaciarlos por código no produce eventos de teclado.
     */
    void verificarCampos() {
        boolean usuarioCompleto = !ventana.getCampoUsuario().getText().isBlank();
        boolean contrasenaCompleta = ventana.getCampoContrasena().getPassword().length > 0;
        ventana.getBotonIngresar().setEnabled(usuarioCompleto && contrasenaCompleta);
    }
}
