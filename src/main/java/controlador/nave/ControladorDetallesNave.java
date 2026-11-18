package controlador.nave;

import modelo.asistente.Asistente;
import vista.principal.panelnave.paneldetallesnave.IVistaDetallesNave;

/**
 * Controlador del panel de detalles de la nave.
 * Coordina la presentación inicial de datos y le entrega a la vista el asistente que debe observar
 * (patrón Observer). La suscripción la hace la propia vista con {@code asistente.agregarObservador(this)}:
 * según el apunte de Observer, es responsabilidad del observador vincularse con el observado; el controlador
 * sólo tiene acceso al modelo y a la vista (apunte de MVC).
 */
public class ControladorDetallesNave {
    private final IVistaDetallesNave vista;
    private final Asistente asistente;

    /**
     * @pre vista != null y asistente != null.
     * @post La vista observa al asistente.
     */
    public ControladorDetallesNave(IVistaDetallesNave vista, Asistente asistente) {
        assert vista != null && asistente != null : "El controlador necesita la vista y el asistente.";
        this.vista = vista;
        this.asistente = asistente;

        this.vista.observar(this.asistente);
    }

    /**
     * La vista deja de observar al asistente.
     */
    public void desuscribir() {
        this.vista.dejarDeObservar();
    }

    public void actualizarVista() {
        this.vista.mostrarId(this.asistente.getIdNave());
        this.vista.mostrarTipo(this.asistente.getTipoNave().getDescripcion());
        this.vista.mostrarCombustible(this.asistente.getCombustible());
        this.vista.mostrarEnergia(this.asistente.getEnergia());
        this.vista.mostrarDesgaste(this.asistente.getDesgaste());
        this.vista.mostrarMantenimiento(this.asistente.requiereMantenimiento());
        this.vista.mostrarEstadoMotor(this.asistente.getEstadoMotor());
        this.vista.mostrarTripulacion(this.asistente.tieneTripulacion());
        this.vista.mostrarEstadoOperativo(this.asistente.naveListaParaOperar());
    }
}
