package excepcion;

import modelo.tripulacion.Tripulante;

/**
 * Excepción lanzada cuando una operación dejaría a la tripulación en un estado inválido:
 * sin capitán/a, con menos integrantes que el mínimo o con tripulantes repetidos.
 * Guarda el tripulante que provocó el rechazo (puede ser null si el problema es
 * la composición completa y no un tripulante en particular).
 */
public class TripulacionInvalidaException extends Exception {

    private final Tripulante tripulanteRechazado;

    public TripulacionInvalidaException(String mensaje, Tripulante tripulanteRechazado) {
        super(mensaje);
        this.tripulanteRechazado = tripulanteRechazado;
    }

    public Tripulante getTripulanteRechazado() {
        return tripulanteRechazado;
    }
}
