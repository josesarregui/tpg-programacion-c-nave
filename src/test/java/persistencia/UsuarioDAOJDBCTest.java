package persistencia;

import excepcion.PersistenciaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de la persistencia de la E2 (patrones DAO y DTO con JDBC): alta y consulta de credenciales,
 * consultas parametrizadas y transformación de SQLException en PersistenciaException.
 * Cada prueba usa su propia base H2 en memoria, de modo que no toca la base real del sistema.
 */
public class UsuarioDAOJDBCTest {

    private static int numeroDeBase = 0;

    private UsuarioDAOJDBC dao;

    @BeforeEach
    public void inicializar() throws PersistenciaException {
        numeroDeBase++;
        dao = new UsuarioDAOJDBC(new ConexionBD("jdbc:h2:mem:dao" + numeroDeBase + ";DB_CLOSE_DELAY=-1"));
        dao.crearTablaSiNoExiste();
    }

    @Test
    @DisplayName("DAO/DTO: las credenciales insertadas se recuperan por nombre de usuario")
    public void insertarYBuscar() throws PersistenciaException {
        dao.insertar(new UsuarioDTO("kirk", "enterprise"));

        UsuarioDTO encontrado = dao.buscarPorNombre("kirk");

        assertNotNull(encontrado);
        assertEquals("kirk", encontrado.getNombreUsuario());
        assertEquals("enterprise", encontrado.getContrasena());
    }

    @Test
    @DisplayName("DAO: buscar un usuario no registrado devuelve null")
    public void buscarInexistente() throws PersistenciaException {
        assertNull(dao.buscarPorNombre("nadie"));
    }

    @Test
    @DisplayName("DAO: crear la tabla dos veces no borra los usuarios ya guardados")
    public void crearTablaEsIdempotente() throws PersistenciaException {
        dao.insertar(new UsuarioDTO("spock", "vulcano"));
        dao.crearTablaSiNoExiste();
        assertNotNull(dao.buscarPorNombre("spock"));
    }

    @Test
    @DisplayName("JDBC: un nombre con comillas se guarda tal cual (PreparedStatement, sin inyección de SQL)")
    public void consultaParametrizada() throws PersistenciaException {
        dao.insertar(new UsuarioDTO("x'OR'1'='1", "clave"));
        assertNotNull(dao.buscarPorNombre("x'OR'1'='1"));
        assertNull(dao.buscarPorNombre("' OR '1'='1"));
    }

    @Test
    @DisplayName("JDBC: insertar un nombre repetido transforma la SQLException en PersistenciaException")
    public void insertarRepetido() throws PersistenciaException {
        dao.insertar(new UsuarioDTO("uhura", "clave1"));

        PersistenciaException e = assertThrows(PersistenciaException.class,
                () -> dao.insertar(new UsuarioDTO("uhura", "clave2")));
        assertInstanceOf(SQLException.class, e.getCause());
        assertEquals("clave1", dao.buscarPorNombre("uhura").getContrasena(), "El usuario original no cambia.");
    }

    @Test
    @DisplayName("JDBC: si la tabla no existe, la consulta falla con PersistenciaException y no con SQLException")
    public void baseSinTabla() {
        UsuarioDAOJDBC sinTabla = new UsuarioDAOJDBC(new ConexionBD("jdbc:h2:mem:sinTabla;DB_CLOSE_DELAY=-1"));
        assertThrows(PersistenciaException.class, () -> sinTabla.buscarPorNombre("kirk"));
    }

    @Test
    @DisplayName("DTO: toString no muestra la contraseña")
    public void dtoSinContrasenaEnTexto() {
        assertFalse(new UsuarioDTO("kirk", "secreta").toString().contains("secreta"));
    }
}
