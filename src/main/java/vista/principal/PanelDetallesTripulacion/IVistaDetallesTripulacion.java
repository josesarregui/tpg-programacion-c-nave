package vista.principal.paneldetallestripulacion;

/**
 * Interfaz de la vista de detalles de la tripulación.
 */
public interface IVistaDetallesTripulacion {

    void mostrarCantidad(int cantidad);

    void mostrarCapitan(String nombre);

    void limpiarTripulantes();

    void agregarTripulante(int id, String nombre, String cargo);
}
