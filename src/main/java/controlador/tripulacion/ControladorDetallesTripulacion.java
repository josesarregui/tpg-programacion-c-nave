package controlador.tripulacion;

import modelo.asistente.Asistente;
import modelo.tripulacion.Tripulante;
import vista.principal.paneldetallestripulacion.IVistaDetallesTripulacion;

/**
 * Controlador del panel de detalles de la tripulación.
 * Trabaja contra la interfaz {@link IVistaDetallesTripulacion} y {@link Asistente}.
 */
public class ControladorDetallesTripulacion {
    private final IVistaDetallesTripulacion vista;
    private final Asistente asistente;

    public ControladorDetallesTripulacion(IVistaDetallesTripulacion vistaDetalles, Asistente asistente) {
        this.vista = vistaDetalles;
        this.asistente = asistente;
    }

    public void actualizarVista() {
        this.vista.mostrarCantidad(this.asistente.getCantidadTripulantes());
        this.vista.mostrarCapitan(this.asistente.getCapitan().getNombre());
        this.vista.limpiarTripulantes();

        for (Tripulante tripulante : this.asistente.getTripulantes()) {
            this.vista.agregarTripulante(
                    tripulante.getId(),
                    tripulante.getNombre(),
                    tripulante.getCargo().toString()
            );
        }
    }
}
