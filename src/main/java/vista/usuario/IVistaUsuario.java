
package vista.usuario;


/**
 *
 */
public interface IVistaUsuario {
    
    public String getNombreUsuario();

    public String getContrasena();

    public void mostrarError(String mensaje);

    public void mostrarMensaje(String mensaje);

    public void limpiarCampos();

    public void arrancar();
    
    public void cerrar();
}
