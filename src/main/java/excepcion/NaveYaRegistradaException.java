package excepcion;

/**
 * Excepción lanzada cuando se intenta registrar en el centro de control una nave que ya está registrada.
 * Cada nave tiene un único asistente (Aclaración, R2: toda orden a la nave pasa por su asistente),
 * por eso no se admite un segundo registro de la misma nave. Guarda el id de la nave rechazada.
 */
public class NaveYaRegistradaException extends Exception {

    private final int idNave;

    public NaveYaRegistradaException(int idNave) {
        super("La nave con id " + idNave + " ya está registrada en el centro de control.");
        this.idNave = idNave;
    }

    public int getIdNave() {
        return idNave;
    }
}
