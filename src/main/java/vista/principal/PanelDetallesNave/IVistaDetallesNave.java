package vista.principal.paneldetallesnave;

/**
 * Interfaz de la vista de detalles y estado de la nave.
 */
public interface IVistaDetallesNave {

    void mostrarId(int id);

    void mostrarTipo(String tipo);

    void mostrarCombustible(int combustible);

    void mostrarEnergia(int energia);

    void mostrarDesgaste(int desgaste);

    void mostrarMantenimiento(boolean requiereMantenimiento);

    void mostrarEstadoMotor(String estado);

    void mostrarTripulacion(boolean tieneTripulacion);

    void mostrarEstadoOperativo(boolean listaParaOperar);
}
