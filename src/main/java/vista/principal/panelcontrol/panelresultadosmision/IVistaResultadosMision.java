package vista.principal.panelcontrol.panelresultadosmision;

import java.awt.event.ActionListener;
import java.util.List;
import modelo.asistente.Asistente;
import modelo.mision.InformeMision;
import modelo.universo.Observador;

/**
 * Interfaz de la vista del historial de informes finales de misiones (Enunciado E2, Vistas, punto 4).
 *
 * Extiende {@link Observador} (Versión 2 de la cátedra): cuando el asistente notifica, la vista copia los informes
 * de sus misiones realizadas (modelo pull) en el hilo que notifica y los muestra en el hilo de eventos de Swing con
 * invokeLater. La vista se vincula sola con el observado: el controlador sólo le entrega el asistente con
 * {@link #observar(Asistente)}.
 *
 * Salvo {@code actualizar(String)}, los métodos se invocan desde el hilo de eventos de Swing.
 */
public interface IVistaResultadosMision extends Observador {
    String COMANDO_VOLVER = "VOLVER_A_MISIONES";

    void setControlador(ActionListener controlador);

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
     * Muestra los informes finales, en el orden en que se cerraron las misiones.
     */
    void mostrarInformes(List<InformeMision> informes);
}
