package excepcion;

/**
 * Excepción lanzada cuando se violan las precondiciones o invariantes
 * en la instanciación de un tripulante o cálculo de sus haberes.
 */
public class TripulacionInvalidaException extends IllegalArgumentException {
    public TripulacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
