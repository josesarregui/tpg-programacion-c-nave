package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Datos de conexión a la base de datos y apertura de conexiones JDBC.
 *
 * Se usa H2 en modo archivo (Reglas Operativas del TPG, punto 5.10): la base queda en {@code datos/nave.mv.db},
 * relativa a la carpeta desde la que se ejecuta el programa, y no requiere instalar un servidor.
 * El controlador JDBC de H2 se declara en el pom.xml y se registra solo en {@link DriverManager} (JDBC 4).
 *
 * Cada operación del DAO abre su propia conexión y la cierra al terminar: así ninguna conexión queda abierta
 * entre una acción del usuario y la siguiente.
 */
public class ConexionBD {

    public static final String URL_POR_DEFECTO = "jdbc:h2:./datos/nave";
    private static final String USUARIO_BD = "sa";
    private static final String CONTRASENA_BD = "";

    private final String url;

    /**
     * Conexión a la base del sistema ({@link #URL_POR_DEFECTO}).
     */
    public ConexionBD() {
        this(URL_POR_DEFECTO);
    }

    /**
     * Conexión a otra base; las pruebas la usan con una base H2 en memoria.
     *
     * @pre url != null y no vacía.
     */
    public ConexionBD(String url) {
        assert url != null && !url.isBlank() : "Se debe indicar la URL de la base de datos.";
        this.url = url;
    }

    /**
     * Abre una conexión nueva. Quien la pide es responsable de cerrarla.
     *
     * @throws SQLException si la base no está disponible.
     */
    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(url, USUARIO_BD, CONTRASENA_BD);
    }
}
