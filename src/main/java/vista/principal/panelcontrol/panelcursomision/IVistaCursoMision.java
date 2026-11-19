package vista.principal.panelcontrol.panelcursomision;

import java.awt.event.ActionListener;
import modelo.asistente.Asistente;
import modelo.universo.Observador;

/**
 * Interfaz de la vista de la Misión en Curso: el tablero en tiempo real ({@link IVistaTablero}), una consola con
 * los eventos que notifica el asistente y los controles para cancelar la misión, ver los informes o volver a la
 * selección.
 *
 * Extiende {@link Observador} (Versión 2 de la cátedra): la consola agrega el mensaje recibido (modelo push) en el
 * hilo de eventos de Swing con invokeLater. La vista se vincula sola con el observado: el controlador sólo le
 * entrega el asistente con {@link #observar(Asistente)}.
 *
 * {@link #misionFinalizada(String)} y {@link #mostrarError(String)} pueden invocarse desde el hilo de la misión: la
 * vista modifica sus componentes en el hilo de eventos de Swing con invokeLater. El resto se invoca desde el hilo de
 * eventos.
 */
public interface IVistaCursoMision extends Observador {
    String COMANDO_CANCELAR_MISION = "CANCELAR_MISION";
    String COMANDO_VER_RESULTADOS = "VER_RESULTADOS";
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
     * @post La vista no recibe más notificaciones del asistente.
     */
    void dejarDeObservar();

    /**
     * @return la vista del tablero contenida en esta vista.
     */
    IVistaTablero getVistaTablero();

    /**
     * Prepara la vista para la misión que comienza: vacía la consola, habilita "Cancelar" y deshabilita "Volver"
     * y "Ver informes".
     */
    void misionIniciada(String codigo, String nombre);

    /**
     * Muestra el resultado de la misión que terminó (completa, cancelada o rechazada), deshabilita "Cancelar" y
     * habilita "Volver" y "Ver informes". Puede invocarse desde cualquier hilo.
     */
    void misionFinalizada(String resumen);

    /**
     * Informa un error. Puede invocarse desde cualquier hilo.
     */
    void mostrarError(String mensaje);
}
