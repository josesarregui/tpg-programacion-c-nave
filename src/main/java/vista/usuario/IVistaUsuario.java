package vista.usuario;

import java.awt.event.ActionListener;

/**
 * Operaciones comunes a las vistas de acceso al sistema (login y registro), como la InterfazVista del ejemplo
 * del conversor (apunte "MVC - interfaz de usuario").
 *
 * El controlador conoce a la vista sólo por esta interfaz: podría reemplazarse la ventana Swing por otra vista
 * sin modificar el controlador. A su vez, la vista recibe al controlador como un {@link ActionListener}, de modo
 * que tampoco depende de la clase concreta del controlador ("las vistas y el modelo no conocen de la existencia
 * del controlador").
 */
public interface IVistaUsuario {

    /**
     * Vincula los botones de la vista con el controlador que atenderá sus comandos de acción.
     */
    void setControlador(ActionListener controlador);

    /**
     * Comienza la visualización.
     */
    void arrancar();

    /**
     * Cierra la vista y libera sus recursos.
     */
    void cerrar();

    /**
     * @return el nombre de usuario ingresado, sin espacios al principio ni al final.
     */
    String getNombreUsuario();

    /**
     * @return la contraseña ingresada.
     */
    String getContrasena();

    void mostrarError(String mensaje);

    void mostrarMensaje(String mensaje);

    /**
     * Vacía todos los campos de la vista.
     */
    void limpiarCampos();
}
