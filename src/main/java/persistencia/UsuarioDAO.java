package persistencia;

import excepcion.PersistenciaException;

/**
 * DAO (Data Access Object) de las credenciales de los usuarios: declara las operaciones de acceso a datos
 * que necesita el servicio, sin decir cómo se guardan.
 *
 * El servicio depende de esta interfaz y no de JDBC (inversión de dependencias, el mismo criterio que
 * {@code CentroDeControl} con {@code Asistente}): cambiar de base de datos o usar otra fuente en las pruebas
 * no modifica el servicio.
 */
public interface UsuarioDAO {

    /**
     * Agrega las credenciales de un usuario nuevo.
     *
     * @pre usuario != null, con nombre y contraseña ya validados por el servicio.
     * @throws PersistenciaException si no se pudo guardar (por ejemplo, el nombre ya existe en la tabla).
     */
    void insertar(UsuarioDTO usuario) throws PersistenciaException;

    /**
     * @return las credenciales guardadas con ese nombre, o null si no hay un usuario registrado con ese nombre.
     * @throws PersistenciaException si no se pudo consultar la base de datos.
     */
    UsuarioDTO buscarPorNombre(String nombreUsuario) throws PersistenciaException;
}
