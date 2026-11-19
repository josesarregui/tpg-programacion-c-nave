package vista.principal.panelcontrol.panelcursomision;

import modelo.universo.Observador;
import modelo.universo.Tablero;

/**
 * Interfaz de la vista del tablero de 20 × 20 en tiempo real (Enunciado E2, Vistas, punto 4; Plan, Paso 6).
 *
 * Extiende {@link Observador} (Versión 2 de la cátedra). El {@link Tablero} notifica cada movimiento de la nave, la
 * fijación del objetivo y cada turno de Destino. Ante cada notificación la vista consulta, en el hilo que notifica,
 * {@link Tablero#verTablero()}, la posición de la nave y el objetivo (modelo pull), obtiene el símbolo de cada
 * elemento por polimorfismo ({@code ElementoDelUniverso.getSimbolo()}) y repinta en el hilo de eventos de Swing con
 * invokeLater. Además muestra el mensaje recibido (modelo push).
 *
 * El {@link Tablero} es de la cátedra y su lista de observadores no está sincronizada: la vista debe suscribirse
 * antes de que se lance el hilo de la misión y quitarse cuando ese hilo ya terminó.
 */
public interface IVistaTablero extends Observador {

    /**
     * La vista pasa a observar el tablero recibido: deja de observar al anterior, si lo había, se suscribe con
     * {@code tablero.agregarObservador(this)} y muestra su estado actual.
     *
     * @pre tablero != null y ningún hilo está modificando el tablero.
     * @post La vista observa sólo a ese tablero.
     */
    void observar(Tablero tablero);

    /**
     * La vista se quita de la lista de observadores del tablero. Si no observa ninguno, no hace nada.
     *
     * @pre Ningún hilo está modificando el tablero.
     * @post La vista no recibe más notificaciones.
     */
    void dejarDeObservar();
}
