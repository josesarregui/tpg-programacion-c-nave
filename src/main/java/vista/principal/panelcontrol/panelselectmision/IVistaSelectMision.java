package vista.principal.panelcontrol.panelselectmision;

import java.awt.event.ActionListener;
import java.util.List;
import modelo.asistente.Asistente;
import modelo.bitacora.Evento;
import modelo.mision.Mision;
import modelo.universo.Observador;

/**
 * Interfaz de la vista del panel de Selección de Misión y Bitácora de eventos.
 *
 * Extiende {@link Observador} (Versión 2 de la cátedra): la Bitácora se actualiza sola. Ante cada notificación del
 * asistente la vista copia sus eventos (modelo pull) en el hilo que notifica y modifica la tabla en el hilo de
 * eventos de Swing con invokeLater. La vista se vincula sola con el observado: el controlador sólo le entrega el
 * asistente con {@link #observar(Asistente)}.
 *
 * Salvo {@code actualizar(String)}, los métodos se invocan desde el hilo de eventos de Swing.
 */
public interface IVistaSelectMision extends Observador {
    public String COMANDO_INICIAR_MISION = "INICIAR_MISION";
    public String COMANDO_SELECCIONAR_MISION = "SELECCIONAR_MISION";

    /**
     * La vista pasa a observar el asistente recibido: deja de observar al anterior, si lo había, y se suscribe con
     * {@code asistente.agregarObservador(this)}.
     *
     * @pre asistente != null.
     * @post La vista observa sólo a ese asistente.
     */
    public void observar(Asistente asistente);

    /**
     * La vista se quita de la lista de observadores del asistente que observa. Si no observa ninguno, no hace nada.
     *
     * @post La vista no recibe más notificaciones.
     */
    public void dejarDeObservar();

    public void setControlador(ActionListener controlador);

    public void mostrarBitacora(List<Evento> eventos);

    public void mostrarMisionesDisponibles(List<Mision> misiones);

    public String getMisionSeleccionada();

    public void mostrarDetallesMision(Mision mision, boolean naveListaParaOperar, int combustibleActual,
            int energiaActual);

    public void habilitarIniciarMision(boolean habilitar);

    public void mostrarError(String mensaje);

    public void mostrarMensaje(String mensaje);
}
