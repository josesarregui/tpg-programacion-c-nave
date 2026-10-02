package excepcion;

/**
 * Excepción lanzada cuando se busca en la tripulación un id que no pertenece a ningún tripulante.
 * El problema no puede resolverse dentro del método de búsqueda, por eso se propaga
 * al invocante junto con el id buscado.
 */
public class TripulanteInexistenteException extends Exception {

    private final int idBuscado;

    public TripulanteInexistenteException(int idBuscado) {
        super("No existe un tripulante con id " + idBuscado + " en la tripulación.");
        this.idBuscado = idBuscado;
    }

    public int getIdBuscado() {
        return idBuscado;
    }
}
