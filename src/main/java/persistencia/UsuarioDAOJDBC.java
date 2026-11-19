package persistencia;

import excepcion.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Implementación JDBC del {@link UsuarioDAO} sobre la tabla {@code usuario}, definida en {@code database/schema.sql}.
 *
 * Criterios:
 *  - La definición de la tabla está en un solo lugar, el script {@code database/schema.sql} (Reglas Operativas,
 *    punto 5.9): Maven lo copia al classpath (ver pom.xml) y {@link #crearTablaSiNoExiste()} lo ejecuta.
 *  - Todas las consultas con datos del usuario usan {@link PreparedStatement} con parámetros ({@code ?}):
 *    el texto ingresado nunca se concatena al SQL, lo que evita la inyección de SQL.
 *  - {@code Connection}, {@code PreparedStatement} y {@code ResultSet} se cierran siempre, aunque ocurra un error,
 *    mediante try-with-resources: es la forma abreviada del uso de {@code finally} visto en el apunte de
 *    Excepciones ("si ocurre pre, debe ocurrir post": si se abrió el recurso, debe cerrarse).
 *  - Toda {@link SQLException} se transforma en {@link PersistenciaException}, conservándola como causa:
 *    las capas superiores no dependen de JDBC.
 */
public class UsuarioDAOJDBC implements UsuarioDAO {

    /** Ubicación en el classpath del script DDL del sistema ({@code database/schema.sql}). */
    static final String RECURSO_ESQUEMA = "/database/schema.sql";
    /** Sentencia de H2 que ejecuta el script del classpath (codificado en UTF-8, como todo el proyecto). */
    private static final String EJECUTAR_ESQUEMA = "RUNSCRIPT FROM 'classpath:" + RECURSO_ESQUEMA + "' CHARSET 'UTF-8'";
    private static final String INSERTAR = "INSERT INTO usuario (nombre_usuario, contrasena) VALUES (?, ?)";
    private static final String BUSCAR_POR_NOMBRE =
            "SELECT nombre_usuario, contrasena FROM usuario WHERE nombre_usuario = ?";

    private final ConexionBD conexionBD;

    /**
     * @pre conexionBD != null.
     */
    public UsuarioDAOJDBC(ConexionBD conexionBD) {
        assert conexionBD != null : "El DAO necesita los datos de conexión.";
        this.conexionBD = conexionBD;
    }

    /**
     * Crea la tabla de usuarios si todavía no existe, ejecutando el script {@code database/schema.sql}
     * ({@code CREATE TABLE IF NOT EXISTS}). Se invoca una vez al iniciar el sistema; si la tabla ya existe, no la
     * modifica.
     *
     * @throws PersistenciaException si no se pudo acceder a la base de datos o al script.
     */
    public void crearTablaSiNoExiste() throws PersistenciaException {
        try (Connection conexion = conexionBD.abrir();
             Statement sentencia = conexion.createStatement()) {
            sentencia.execute(EJECUTAR_ESQUEMA);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo preparar la base de datos de usuarios.", e);
        }
    }

    @Override
    public void insertar(UsuarioDTO usuario) throws PersistenciaException {
        assert usuario != null : "Se deben indicar las credenciales a guardar.";
        try (Connection conexion = conexionBD.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(INSERTAR)) {
            sentencia.setString(1, usuario.getNombreUsuario());
            sentencia.setString(2, usuario.getContrasena());
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo guardar el usuario " + usuario.getNombreUsuario() + ".", e);
        }
    }

    @Override
    public UsuarioDTO buscarPorNombre(String nombreUsuario) throws PersistenciaException {
        try (Connection conexion = conexionBD.abrir();
             PreparedStatement sentencia = conexion.prepareStatement(BUSCAR_POR_NOMBRE)) {
            sentencia.setString(1, nombreUsuario);
            try (ResultSet resultado = sentencia.executeQuery()) {
                UsuarioDTO encontrado = null;
                if (resultado.next()) {
                    encontrado = new UsuarioDTO(resultado.getString("nombre_usuario"), resultado.getString("contrasena"));
                }
                return encontrado;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo consultar la base de datos de usuarios.", e);
        }
    }
}
