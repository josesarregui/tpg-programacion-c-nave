
package vista.usuario;

import controlador.usuario.ControladorLogin;


/**
 *
 */
public interface IVistaLogin extends IVistaUsuario {
    public String LOGIN = "LOGIN";
    public String IR_REGISTRO = "IR_REGISTRO";

    public void setControlador(ControladorLogin controlador);
}
