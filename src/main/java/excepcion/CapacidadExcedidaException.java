package excepcion;

import modelo.nave.TipoRecurso;

/**
 * Excepción lanzada cuando una operación dejaría un recurso por encima de su máximo
 * (Ficha de Inicio, punto 2): una carga que supera la capacidad de combustible o de energía,
 * o un desgaste que superaría el límite permitido.
 * La operación se rechaza sin modificar la nave; la excepción guarda los datos del rechazo.
 */
public class CapacidadExcedidaException extends Exception {

    private final TipoRecurso recurso;
    private final int valorActual;
    private final int cantidadSolicitada;
    private final int maximo;

    public CapacidadExcedidaException(TipoRecurso recurso, int valorActual, int cantidadSolicitada, int maximo) {
        super("No se puede agregar " + cantidadSolicitada + " de " + recurso + ": hay " + valorActual
                + " y el máximo es " + maximo + ".");
        this.recurso = recurso;
        this.valorActual = valorActual;
        this.cantidadSolicitada = cantidadSolicitada;
        this.maximo = maximo;
    }

    public TipoRecurso getRecurso() {
        return recurso;
    }

    public int getValorActual() {
        return valorActual;
    }

    public int getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    public int getMaximo() {
        return maximo;
    }
}
