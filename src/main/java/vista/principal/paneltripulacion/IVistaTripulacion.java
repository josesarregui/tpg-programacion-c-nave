package vista.principal.paneltripulacion;

import vista.principal.panelcreartripulacion.IVistaCrearTripulacion;
import vista.principal.paneldetallestripulacion.IVistaDetallesTripulacion;

/**
 * Interfaz contenedora de la sección de Tripulación (creación y detalles).
 */
public interface IVistaTripulacion {
    String CREAR_TRIPULACION = "CREAR_TRIPULACION";
    String DETALLES_TRIPULACION = "DETALLES_TRIPULACION";

    void mostrarCrearTripulacion();

    void mostrarDetalleTripulacion();

    IVistaCrearTripulacion getVistaCrearTripulacion();

    IVistaDetallesTripulacion getVistaDetallesTripulacion();
}
