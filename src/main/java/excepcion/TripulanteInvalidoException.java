package excepcion;

import modelo.tripulacion.Origen;

/**
 * Excepción lanzada cuando se intenta crear un tripulante con datos que no cumplen
 * las reglas del dominio (E1-04): nombre vacío, antigüedad negativa u origen no informado.
 * Guarda los datos recibidos para que la zona de recuperación (por ejemplo, un controlador
 * de la E2) pueda informar qué valor fue rechazado.
 */
public class TripulanteInvalidoException extends Exception {

    private final String nombreRecibido;
    private final int antiguedadRecibida;
    private final Origen origenRecibido;

    public TripulanteInvalidoException(String mensaje, String nombreRecibido, int antiguedadRecibida, Origen origenRecibido) {
        super(mensaje);
        this.nombreRecibido = nombreRecibido;
        this.antiguedadRecibida = antiguedadRecibida;
        this.origenRecibido = origenRecibido;
    }

    public String getNombreRecibido() {
        return nombreRecibido;
    }

    public int getAntiguedadRecibida() {
        return antiguedadRecibida;
    }

    public Origen getOrigenRecibido() {
        return origenRecibido;
    }
}
