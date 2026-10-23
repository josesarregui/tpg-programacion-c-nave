package excepcion;

/**
 * Excepción lanzada cuando las credenciales no permiten iniciar sesión.
 * El mensaje es el mismo si el usuario no existe o si la contraseña es incorrecta, para no revelar
 * qué nombres de usuario están registrados.
 */
public class LoginInvalidoException extends Exception {

    public LoginInvalidoException(String mensaje) {
        super(mensaje);
    }
}
