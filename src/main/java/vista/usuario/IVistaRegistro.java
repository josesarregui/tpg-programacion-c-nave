
package vista;

import controlador.ControladorRegistro;

/**
 *
 */
public interface IVistaRegistro extends IVistaUsuario {
    public String REGISTRAR = "REGISTRAR";
    public String VOLVER = "VOLVER";

    
    public String getRepetirContrasena();

    public void setControlador(ControladorRegistro controlador);
}