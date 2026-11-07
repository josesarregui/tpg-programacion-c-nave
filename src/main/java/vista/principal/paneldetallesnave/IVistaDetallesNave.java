package vista.principal.paneldetallesnave;

import modelo.nave.ObservadorNave;

/**
 * Interfaz de la vista de detalles y estado de la nave.
 * Extiende {@link ObservadorNave} para recibir directamente las notificaciones
 * de cambio de estado de la nave (patrón Observer entre Modelo y Vista),
 * siguiendo el mismo lineamiento que {@link vista.concurrencia.IVistaConcurrencia}.
 */
public interface IVistaDetallesNave extends ObservadorNave {

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
