
package vista.usuario;

import controlador.usuario.ControladorRegistro;


/**
 *
 */
public interface IVistaRegistro extends IVistaUsuario {
    public String REGISTRAR = "REGISTRAR";
    public String VOLVER = "VOLVER";

    
    public String getRepetirContrasena();

    public void setControlador(ControladorRegistro controlador);
}