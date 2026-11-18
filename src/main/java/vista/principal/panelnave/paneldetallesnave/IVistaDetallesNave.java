package vista.principal.panelnave.paneldetallesnave;

import modelo.asistente.Asistente;
import modelo.universo.Observador;

/**
 * Interfaz de la vista de detalles y estado de la nave.
 * Extiende {@link Observador} (Versión 2 de la cátedra) para recibir las notificaciones del asistente que
 * opera la nave (patrón Observer entre Modelo y Vista), siguiendo el mismo lineamiento que
 * {@link vista.concurrencia.IVistaConcurrencia}.
 *
 * La vista es la responsable de vincularse con el observado (apunte de Observer): el controlador sólo le
 * entrega el asistente con {@link #observar(Asistente)} y es la vista la que invoca
 * {@code asistente.agregarObservador(this)}.
 */
public interface IVistaDetallesNave extends Observador {

    /**
     * La vista pasa a observar el asistente recibido: deja de observar al anterior, si lo había, y se suscribe
     * con {@code asistente.agregarObservador(this)}.
     *
     * @pre asistente != null.
     * @post La vista observa sólo a ese asistente y, ante cada notificación, consulta su estado (modelo pull).
     */
    void observar(Asistente asistente);

    /**
     * La vista se quita de la lista de observadores del asistente que observa. Si no observa ninguno, no hace
     * nada.
     *
     * @post La vista no recibe más notificaciones.
     */
    void dejarDeObservar();

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
