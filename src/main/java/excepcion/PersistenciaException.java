package excepcion;

/**
 * Excepción lanzada por la capa de persistencia cuando no se puede acceder a la base de datos
 * (base inexistente o bloqueada, tabla faltante, error de SQL).
 * Transforma la {@code java.sql.SQLException} en una excepción propia, de modo que ni el servicio ni el
 * controlador dependan de JDBC. Conserva la excepción original como causa ({@link #getCause()}) para el diagnóstico.
 */
public class PersistenciaException extends Exception {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
