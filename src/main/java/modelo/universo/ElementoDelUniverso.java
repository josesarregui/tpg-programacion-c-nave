package modelo.universo;

import modelo.asistente.Asistente;

/**
 * Entidad que ocupa una casilla del {@link Tablero} y con la cual la nave interactúa al coincidir en la misma
 * coordenada (Enunciado E2: otras naves, asteroides y estaciones de carga).
 *
 * El {@link Tablero} y el {@link Destino} manipulan los elementos sin conocer su tipo concreto; cada elemento
 * resuelve su interacción por polimorfismo, sin condicionales de tipo.
 *
 * La interacción recibe el {@link Asistente} y no la nave: toda orden a la nave pasa por su asistente, que
 * además lleva la Bitácora (Aclaración, R2 y R5).
 */
public interface ElementoDelUniverso {

    /**
     * @post El valor retornado no es nulo ni vacío.
     * @return nombre descriptivo del elemento.
     */
    String getNombre();

    /**
     * Identificación simbólica para que la vista renderice la grilla sin condicionales de tipo.
     *
     * @post El valor retornado no es nulo ni vacío y es el mismo para todos los elementos de una misma clase.
     * @return símbolo del elemento.
     */
    String getSimbolo();

    /**
     * Indica si el elemento es una amenaza para la navegación (Plan de la E2, Paso 3: la misión prioriza las
     * casillas libres o con estaciones de carga sobre las amenazas). Lo usa el sistema de navegación para
     * planificar la ruta sin condicionales de tipo; no cambia lo que ocurre si la nave coincide con el elemento.
     *
     * @post El valor retornado es el mismo para todos los elementos de una misma clase y no modifica el elemento.
     * @return true si conviene evitar la casilla del elemento (asteroides y naves); false si puede atravesarse
     *         sin riesgo (estaciones de carga).
     */
    boolean esAmenaza();

    /**
     * Resuelve la interacción con la nave que opera el asistente.
     *
     * @pre asistente != null.
     * @post Las consecuencias del encuentro quedaron aplicadas a la nave del asistente y registradas en su Bitácora.
     */
    void interactuarCon(Asistente asistente);
}
