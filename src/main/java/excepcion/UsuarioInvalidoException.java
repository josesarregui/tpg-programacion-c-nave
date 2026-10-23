package excepcion;

/**
 * Excepción lanzada cuando el nombre de usuario recibido no cumple las reglas del registro
 * (vacío, con espacios o más largo de lo permitido). Guarda el nombre rechazado.
 */
public class UsuarioInvalidoException extends Exception {

    private final String nombreRecibido;

    public UsuarioInvalidoException(String mensaje, String nombreRecibido) {
        super(mensaje);
        this.nombreRecibido = nombreRecibido;
    }

    public String getNombreRecibido() {
        return nombreRecibido;
    }
}
