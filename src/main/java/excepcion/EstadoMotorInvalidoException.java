
package excepcion; // o 'package modelo.warp;' si elegís dejarla en tu paquete

/**
 * Excepción lanzada cuando se intenta realizar una transición de estado
 * no permitida en el ciclo operativo del Motor Warp.
 */
public class EstadoMotorInvalidoException extends IllegalStateException {

    public EstadoMotorInvalidoException(String mensaje) {
        super(mensaje);
    }
}