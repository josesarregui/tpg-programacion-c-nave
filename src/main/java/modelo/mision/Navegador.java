package modelo.mision;

import modelo.universo.ElementoDelUniverso;
import modelo.universo.Posicion;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Sistema de navegación de la misión (Guía 4.2: "los sensores leen y detectan; el sistema de navegación
 * interpreta y decide"). Interpreta la lectura del radar de largo alcance ({@code Tablero.verTablero()}) y
 * calcula la ruta hacia el objetivo. No controla la nave ni el tablero: sólo devuelve posiciones.
 *
 * La ruta se calcula por búsqueda en anchura (BFS) en ocho direcciones, como se desplazan los elementos del
 * tablero y como mide distancias {@code Tablero.obtenerObjetivo()}. Evita las casillas cuyos elementos son
 * amenazas ({@link ElementoDelUniverso#esAmenaza()}), de modo que prioriza las casillas libres y las estaciones
 * de carga sin condicionales de tipo (Plan de la E2, Paso 3). Si no existe una ruta sin amenazas, devuelve la
 * más corta admitiéndolas: en una grilla con movimientos en ocho direcciones siempre hay una ruta.
 *
 * No tiene estado: es de paquete porque sólo la usa {@link Mision}.
 */
final class Navegador {

    /**
     * @pre mapa != null, no vacío y rectangular (como lo devuelve verTablero()); origen y objetivo no nulos y
     *      dentro del mapa.
     * @post La ruta no incluye el origen; si origen.equals(objetivo) está vacía y, si no, termina en el objetivo.
     *       Cada posición está dentro del mapa y es vecina de la anterior (la primera, del origen). Si existe una
     *       ruta sin amenazas (salvo, quizás, el objetivo), la devuelta no pasa por ninguna. No modifica el mapa.
     * @return lista nueva con los pasos desde el origen hasta el objetivo.
     */
    List<Posicion> calcularRuta(ElementoDelUniverso[][] mapa, Posicion origen, Posicion objetivo) {
        assert mapa != null && mapa.length > 0 && mapa[0].length > 0 : "El mapa del radar no puede estar vacío.";
        assert origen != null && objetivo != null : "El origen y el objetivo no pueden ser nulos.";
        assert estaDentro(origen, mapa.length, mapa[0].length) && estaDentro(objetivo, mapa.length, mapa[0].length)
                : "El origen y el objetivo deben estar dentro del tablero.";

        List<Posicion> ruta = buscar(mapa, origen, objetivo, true);
        if (ruta == null) {
            ruta = buscar(mapa, origen, objetivo, false);
        }
        assert ruta != null : "Fallo postcondición: con ocho direcciones siempre existe una ruta.";
        assert ruta.isEmpty() == origen.equals(objetivo) : "Fallo postcondición: la ruta debe terminar en el objetivo.";
        return ruta;
    }

    /**
     * @return true si la posición está dentro de un tablero de esas dimensiones.
     */
    static boolean estaDentro(Posicion posicion, int filas, int columnas) {
        return posicion.getX() >= 0 && posicion.getX() < filas && posicion.getY() >= 0 && posicion.getY() < columnas;
    }

    /**
     * @return true si las posiciones son distintas y vecinas en alguna de las ocho direcciones.
     */
    static boolean sonVecinas(Posicion una, Posicion otra) {
        int dx = Math.abs(una.getX() - otra.getX());
        int dy = Math.abs(una.getY() - otra.getY());
        return Math.max(dx, dy) == 1;
    }

    /**
     * BFS en ocho direcciones. Con evitarAmenazas, no expande casillas con amenazas salvo el objetivo, que
     * siempre debe poder alcanzarse.
     *
     * @return la ruta sin el origen, o null si no hay ruta con esa restricción.
     */
    private List<Posicion> buscar(ElementoDelUniverso[][] mapa, Posicion origen, Posicion objetivo,
                                  boolean evitarAmenazas) {
        int filas = mapa.length;
        int columnas = mapa[0].length;
        Map<Posicion, Posicion> predecesores = new HashMap<>();
        Queue<Posicion> cola = new ArrayDeque<>();
        predecesores.put(origen, null);
        cola.add(origen);
        boolean encontrado = origen.equals(objetivo);
        while (!cola.isEmpty() && !encontrado) {
            Posicion actual = cola.poll();
            for (int dx = -1; dx <= 1 && !encontrado; dx++) {
                for (int dy = -1; dy <= 1 && !encontrado; dy++) {
                    Posicion vecina = new Posicion(actual.getX() + dx, actual.getY() + dy);
                    if ((dx != 0 || dy != 0) && estaDentro(vecina, filas, columnas)
                            && !predecesores.containsKey(vecina)
                            && (vecina.equals(objetivo) || !evitarAmenazas || esTransitable(mapa, vecina))) {
                        predecesores.put(vecina, actual);
                        cola.add(vecina);
                        encontrado = vecina.equals(objetivo);
                    }
                }
            }
        }
        return encontrado ? reconstruir(predecesores, objetivo) : null;
    }

    private boolean esTransitable(ElementoDelUniverso[][] mapa, Posicion posicion) {
        ElementoDelUniverso elemento = mapa[posicion.getX()][posicion.getY()];
        return elemento == null || !elemento.esAmenaza();
    }

    private List<Posicion> reconstruir(Map<Posicion, Posicion> predecesores, Posicion objetivo) {
        LinkedList<Posicion> ruta = new LinkedList<>();
        Posicion paso = objetivo;
        // El origen es la única posición sin predecesor: no se agrega a la ruta.
        while (predecesores.get(paso) != null) {
            ruta.addFirst(paso);
            paso = predecesores.get(paso);
        }
        return ruta;
    }
}
