package excepcion;

/**
 * Excepción lanzada cuando se intenta registrar un nombre de usuario que ya está registrado.
 * Guarda el nombre rechazado, para que el invocante pueda informarlo.
 */
public class UsuarioExistenteException extends Exception {

    private final String nombreUsuario;

    public UsuarioExistenteException(String nombreUsuario) {
        super("El usuario \"" + nombreUsuario + "\" ya está registrado.");
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }
}
