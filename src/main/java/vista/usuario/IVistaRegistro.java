package vista.usuario;

/**
 * Vista de registro (Enunciado E2, "Acceso al Sistema"): permite dar de alta un usuario nuevo con nombre y
 * contraseña. La vista pide repetir la contraseña y sólo habilita el registro si ambas coinciden
 * (validación de formato propia de la vista).
 */
public interface IVistaRegistro extends IVistaUsuario {

    // Comandos de acción de los botones: el controlador los usa para saber qué botón se presionó.
    String REGISTRAR = "REGISTRAR";
    String VOLVER = "VOLVER";
}
