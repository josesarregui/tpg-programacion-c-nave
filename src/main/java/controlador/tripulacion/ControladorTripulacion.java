package controlador.tripulacion;

import controlador.principal.ControladorPrincipal;
import modelo.asistente.Asistente;
import vista.principal.panelcreartripulacion.IVistaCrearTripulacion;
import vista.principal.paneldetallestripulacion.IVistaDetallesTripulacion;
import vista.principal.paneltripulacion.IVistaTripulacion;

/**
 * Controlador coordinador del subsistema de tripulación.
 * Trabaja exclusivamente contra interfaces de vista y contra {@link Asistente}.
 */
public class ControladorTripulacion {
    private final IVistaTripulacion vista;
    private final Asistente asistente;
    private final ControladorPrincipal controladorPrincipal;

    private ControladorCrearTripulacion controladorCrearTripulacion;
    private ControladorDetallesTripulacion controladorDetallesTripulacion;

    public ControladorTripulacion(IVistaTripulacion vista, Asistente asistente) {
        this(vista, asistente, null);
    }

    public ControladorTripulacion(
            IVistaTripulacion vista,
            Asistente asistente,
            ControladorPrincipal controladorPrincipal) {
        this.vista = vista;
        this.asistente = asistente;
        this.controladorPrincipal = controladorPrincipal;

        inicializarControladores();
    }

    private void inicializarControladores() {
        IVistaCrearTripulacion vistaCrear = this.vista.getVistaCrearTripulacion();
        IVistaDetallesTripulacion vistaDetalles = this.vista.getVistaDetallesTripulacion();

        if (vistaCrear != null) {
            this.controladorCrearTripulacion = new ControladorCrearTripulacion(vistaCrear, this.asistente, this);
        }
        if (vistaDetalles != null) {
            this.controladorDetallesTripulacion = new ControladorDetallesTripulacion(vistaDetalles, this.asistente);
        }
    }

    public void tripulacionAsignada() {
        if (this.controladorPrincipal != null) {
            this.controladorPrincipal.tripulacionCreada();
        }
        mostrarDetallesTripulacion();
    }

    public void mostrarCrearTripulacion() {
        this.vista.mostrarCrearTripulacion();
    }

    public void mostrarDetallesTripulacion() {
        if (this.controladorDetallesTripulacion != null) {
            this.controladorDetallesTripulacion.actualizarVista();
        }
        this.vista.mostrarDetalleTripulacion();
    }
}
