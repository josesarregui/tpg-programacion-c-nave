
package excepcion; // o 'package modelo.warp;' si elegís dejarla en tu paquete

/**
 * Excepción lanzada cuando se intenta realizar una transición de estado
 * no permitida en el ciclo operativo del Motor Warp.
 */

public class EstadoMotorInvalidoException extends IllegalStateException {

    private final String estadoActual;

    public EstadoMotorInvalidoException(String mensaje) {
        super(mensaje);
        this.estadoActual = null;
    }

    public EstadoMotorInvalidoException(String mensaje, String estadoActual) {
        super(mensaje);
        this.estadoActual = estadoActual;
    }

    public String getEstadoActual() {
        return estadoActual;
    }
}

/*
* extends IllegalStateException --> Esta clase viene integrada adentro del paquete estandar de java.lang
*
* En las diapositivas 38 y 39, se muestra que una excepción personalizada no solo puede llevar un mensaje de texto (super("...")),
* sino también atributos propios con sus getters para aportar contexto:
* */