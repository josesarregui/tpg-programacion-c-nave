package vista.principal.paneltripulacion.paneldetallestripulacion;

import modelo.liquidacion.ReciboHaberes;

/**
 * Interfaz de la vista de detalles de la tripulación: la nómina con el sueldo de cada tripulante y su recibo de
 * haberes, que contiene el desglose por concepto.
 */
public interface IVistaDetallesTripulacion {

    void mostrarCantidad(int cantidad);

    void mostrarCapitan(String nombre);

    default void mostrarCostoNomina(double totalNomina) {}

    void limpiarTripulantes();

    void agregarTripulante(int id, String nombre, String cargo);

    default void agregarTripulante(int id, String nombre, String cargo, double sueldoTotal, ReciboHaberes recibo) {
        agregarTripulante(id, nombre, cargo);
    }

    default void mostrarReciboDetallado(ReciboHaberes recibo) {}

    /**
     * Informa un rechazo del modelo (por ejemplo, una liquidación inválida).
     */
    void mostrarError(String mensaje);
}
