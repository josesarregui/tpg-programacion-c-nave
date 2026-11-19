package persistencia;

import excepcion.PersistenciaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia del script {@code database/schema.sql} (Reglas Operativas, punto 5.9; Plan de la E2, Paso 7).
 * El script se ejecuta tal cual está en la raíz del proyecto (Surefire corre las pruebas desde esa carpeta) sobre
 * bases H2 en memoria, sin repetir su sentencia en la prueba.
 */
public class EsquemaSQLTest {

    private static final Path SCRIPT = Path.of("database", "schema.sql");

    private static int numeroDeBase = 0;

    private static ConexionBD baseNueva() {
        numeroDeBase++;
        return new ConexionBD("jdbc:h2:mem:esquema" + numeroDeBase + ";DB_CLOSE_DELAY=-1");
    }

    /** Ejecuta el archivo database/schema.sql de la raíz del proyecto sobre la base indicada. */
    private static void ejecutarScript(ConexionBD base) throws SQLException {
        try (Connection conexion = base.abrir();
             Statement sentencia = conexion.createStatement()) {
            sentencia.execute("RUNSCRIPT FROM '" + SCRIPT.toString().replace('\\', '/') + "' CHARSET 'UTF-8'");
        }
    }

    /** Describe cada columna de la tabla usuario: nombre, tipo, longitud y si admite nulos. */
    private static List<String> columnasDeUsuario(ConexionBD base) throws SQLException {
        List<String> columnas = new ArrayList<>();
        try (Connection conexion = base.abrir();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(
                     "SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE "
                     + "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'USUARIO' ORDER BY ORDINAL_POSITION")) {
            while (resultado.next()) {
                columnas.add(resultado.getString(1) + " " + resultado.getString(2) + "(" + resultado.getInt(3) + ") "
                        + resultado.getString(4));
            }
        }
        return columnas;
    }

    @Test
    @DisplayName("Script SQL: database/schema.sql sobre una H2 en memoria crea la tabla con la que el DAO guarda y recupera credenciales")
    public void scriptCreaLaTablaDelDAO() throws Exception {
        ConexionBD base = baseNueva();
        ejecutarScript(base);
        UsuarioDAOJDBC dao = new UsuarioDAOJDBC(base);

        dao.insertar(new UsuarioDTO("sulu", "timon"));

        UsuarioDTO encontrado = dao.buscarPorNombre("sulu");
        assertNotNull(encontrado);
        assertEquals("sulu", encontrado.getNombreUsuario());
        assertEquals("timon", encontrado.getContrasena());
        assertNull(dao.buscarPorNombre("nadie"));
    }

    @Test
    @DisplayName("Script SQL: la clave primaria rechaza un nombre repetido con PersistenciaException y conserva el original")
    public void scriptDefineLaClavePrimaria() throws Exception {
        ConexionBD base = baseNueva();
        ejecutarScript(base);
        UsuarioDAOJDBC dao = new UsuarioDAOJDBC(base);
        dao.insertar(new UsuarioDTO("chekov", "clave1"));

        PersistenciaException e = assertThrows(PersistenciaException.class,
                () -> dao.insertar(new UsuarioDTO("chekov", "clave2")));

        assertInstanceOf(SQLException.class, e.getCause());
        assertEquals("clave1", dao.buscarPorNombre("chekov").getContrasena());
    }

    @Test
    @DisplayName("Script SQL: ejecutarlo otra vez no borra los usuarios guardados (CREATE TABLE IF NOT EXISTS)")
    public void scriptEsIdempotente() throws Exception {
        ConexionBD base = baseNueva();
        ejecutarScript(base);
        UsuarioDAOJDBC dao = new UsuarioDAOJDBC(base);
        dao.insertar(new UsuarioDTO("scotty", "motores"));

        ejecutarScript(base);

        assertNotNull(dao.buscarPorNombre("scotty"));
    }

    @Test
    @DisplayName("Script SQL: el sistema ejecuta database/schema.sql: su copia del classpath es idéntica y crea la misma tabla")
    public void elSistemaEjecutaElScript() throws Exception {
        byte[] script = Files.readAllBytes(SCRIPT);
        byte[] recurso;
        try (InputStream entrada = UsuarioDAOJDBC.class.getResourceAsStream(UsuarioDAOJDBC.RECURSO_ESQUEMA)) {
            assertNotNull(entrada, "Maven debe copiar database/schema.sql al classpath.");
            recurso = entrada.readAllBytes();
        }
        assertArrayEquals(script, recurso);

        ConexionBD conScript = baseNueva();
        ejecutarScript(conScript);
        ConexionBD delSistema = baseNueva();
        new UsuarioDAOJDBC(delSistema).crearTablaSiNoExiste();

        List<String> columnas = columnasDeUsuario(conScript);
        assertEquals(2, columnas.size(), "La tabla usuario tiene nombre de usuario y contraseña.");
        assertEquals(columnas, columnasDeUsuario(delSistema));
    }
}
