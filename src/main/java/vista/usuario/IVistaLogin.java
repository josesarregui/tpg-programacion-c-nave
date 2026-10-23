package vista.usuario;

/**
 * Vista de login (Enunciado E2, "Acceso al Sistema"): permite ingresar el nombre de usuario y la contraseña
 * de un operador existente.
 */
public interface IVistaLogin extends IVistaUsuario {

    // Comandos de acción de los botones: el controlador los usa para saber qué botón se presionó.
    String INGRESAR = "INGRESAR";
    String IR_A_REGISTRO = "IR_A_REGISTRO";

    /**
     * Vacía sólo la contraseña, para reintentar después de un login rechazado sin volver a escribir el usuario.
     */
    void limpiarContrasena();
}
