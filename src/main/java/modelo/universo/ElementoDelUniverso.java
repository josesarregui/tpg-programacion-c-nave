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
     * Resuelve la interacción con la nave que opera el asistente.
     *
     * @pre asistente != null.
     * @post Las consecuencias del encuentro quedaron aplicadas a la nave del asistente y registradas en su Bitácora.
     */
    void interactuarCon(Asistente asistente);
}
