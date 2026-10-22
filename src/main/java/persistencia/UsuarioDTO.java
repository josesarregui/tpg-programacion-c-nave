package persistencia;

/**
 * DTO (Data Transfer Object) de las credenciales de un usuario: transporta, sin lógica de negocio, los datos
 * de una fila de la tabla {@code usuario} entre el DAO y el servicio.
 *
 * Es inmutable: los datos se fijan al construirlo. Es lo único que se persiste en el sistema
 * (Enunciado E2, "Alcance de la persistencia": sólo nombre de usuario y contraseña).
 */
public class UsuarioDTO {

    private final String nombreUsuario;
    private final String contrasena;

    public UsuarioDTO(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    /**
     * No incluye la contraseña, para que no aparezca en mensajes ni registros.
     */
    @Override
    public String toString() {
        return "UsuarioDTO[" + nombreUsuario + "]";
    }
}
