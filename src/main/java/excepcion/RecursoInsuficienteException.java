package excepcion;

import modelo.nave.TipoRecurso;

/**
 * Excepción lanzada cuando la nave no dispone del recurso necesario para una operación
 * (Escenario B: recursos insuficientes). La operación se rechaza sin modificar la nave y
 * la excepción guarda el recurso, lo disponible y lo solicitado, para que el invocante
 * pueda registrar el motivo en la Bitácora.
 */
public class RecursoInsuficienteException extends Exception {

    private final TipoRecurso recurso;
    private final int disponible;
    private final int cantidadSolicitada;

    public RecursoInsuficienteException(TipoRecurso recurso, int disponible, int cantidadSolicitada) {
        super(recurso + " insuficiente: se necesitan " + cantidadSolicitada + " y hay " + disponible + ".");
        this.recurso = recurso;
        this.disponible = disponible;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public TipoRecurso getRecurso() {
        return recurso;
    }

    public int getDisponible() {
        return disponible;
    }

    public int getCantidadSolicitada() {
        return cantidadSolicitada;
    }
}
