package vista.principal.panelcontrol.panelrecursos;

import modelo.asistente.Asistente;
import modelo.universo.Observador;

/**
 * Interfaz de la franja de recursos de la sección Control de la Nave (Enunciado E2, Vistas, punto 4: "Estado
 * actual de los recursos de la nave").
 *
 * Extiende {@link Observador} (Versión 2 de la cátedra): ante cada notificación del asistente consulta sus getters
 * (modelo pull) en el hilo que notifica y modifica los componentes en el hilo de eventos de Swing con invokeLater.
 * La vista se vincula sola con el observado: el controlador sólo le entrega el asistente con
 * {@link #observar(Asistente)}.
 */
public interface IVistaRecursos extends Observador {

    /**
     * La vista pasa a observar el asistente recibido: deja de observar al anterior, si lo había, y se suscribe con
     * {@code asistente.agregarObservador(this)}.
     *
     * @pre asistente != null.
     * @post La vista observa sólo a ese asistente.
     */
    void observar(Asistente asistente);

    /**
     * La vista se quita de la lista de observadores del asistente que observa. Si no observa ninguno, no hace nada.
     *
     * @post La vista no recibe más notificaciones.
     */
    void dejarDeObservar();

    /**
     * Muestra el estado de la nave. Se invoca desde el hilo de eventos de Swing.
     */
    void mostrarRecursos(int combustible, int energia, int desgaste, int gemas, String estadoMotor,
            boolean listaParaOperar, boolean destruida);
}
