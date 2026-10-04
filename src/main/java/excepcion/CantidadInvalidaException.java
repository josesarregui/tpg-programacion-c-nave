package excepcion;

import modelo.nave.TipoRecurso;

/**
 * Excepción lanzada cuando se pide cargar o consumir una cantidad negativa de un recurso.
 * La cantidad llega desde fuera del modelo (el Asistente o, en la E2, un controlador),
 * por eso el error se propaga al invocante junto con el recurso y la cantidad recibida.
 */
public class CantidadInvalidaException extends Exception {

    private final TipoRecurso recurso;
    private final int cantidadRecibida;

    public CantidadInvalidaException(TipoRecurso recurso, int cantidadRecibida) {
        super("La cantidad de " + recurso + " no puede ser negativa (se recibió " + cantidadRecibida + ").");
        this.recurso = recurso;
        this.cantidadRecibida = cantidadRecibida;
    }

    public TipoRecurso getRecurso() {
        return recurso;
    }

    public int getCantidadRecibida() {
        return cantidadRecibida;
    }
}
