package modelo.universo;

import modelo.nave.Nave;

/**
 * Representa cualquier entidad o cuerpo presente en una casilla del tablero
 * del universo con la cual la nave puede interactuar.
 */
public interface ElementoDelUniverso {

    /**
     * @return Nombre descriptivo del elemento.
     */
    String getNombre();

    /**
     * @return Simbolo o emoji para visualizar en el tablero.
     */
    String getSimbolo();

    /**
     * Resuelve la interaccion al coincidir la nave con este elemento.
     *
     * @param nave Nave principal que colisiona o interactúa.
     * @return Balance de la interacción.
     */
    ResultadoInteraccion interactuar(Nave nave);
}
