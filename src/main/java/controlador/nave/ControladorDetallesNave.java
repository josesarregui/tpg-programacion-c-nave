package controlador.nave;

import modelo.asistente.Asistente;
import modelo.nave.ObservadorNave;
import vista.principal.paneldetallesnave.IVistaDetallesNave;

/**
 * Controlador del panel de detalles de la nave.
 * Coordina la presentación inicial de datos y suscribe la vista como observadora
 * de la nave (patrón Observer) para garantizar actualizaciones en tiempo real.
 */
public class ControladorDetallesNave {
    private final IVistaDetallesNave vista;
    private final Asistente asistente;

    public ControladorDetallesNave(IVistaDetallesNave vista, Asistente asistente) {
        this.vista = vista;
        this.asistente = asistente;

        suscribirObservador();
    }

    private void suscribirObservador() {
        if (this.vista instanceof ObservadorNave observador && this.asistente != null) {
            this.asistente.agregarObservador(observador);
        }
    }

    /**
     * Desuscribe la vista del modelo.
     */
    public void desuscribir() {
        if (this.vista instanceof ObservadorNave observador && this.asistente != null) {
            this.asistente.eliminarObservador(observador);
        }
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
