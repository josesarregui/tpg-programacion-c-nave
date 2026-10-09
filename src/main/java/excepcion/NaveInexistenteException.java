package excepcion;

/**
 * Excepción lanzada cuando se le pide al centro de control una nave que no está registrada.
 * El problema no puede resolverse dentro del centro de control, por eso se propaga
 * al invocante junto con el id buscado.
 */
public class NaveInexistenteException extends Exception {

    private final int idNaveBuscada;

    public NaveInexistenteException(int idNaveBuscada) {
        super("No hay una nave registrada con id " + idNaveBuscada + " en el centro de control.");
        this.idNaveBuscada = idNaveBuscada;
    }

    public int getIdNaveBuscada() {
        return idNaveBuscada;
    }
}
