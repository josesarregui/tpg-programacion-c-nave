package modelo.universo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Doble de prueba del {@link Tablero} provisto por la cátedra, para que las pruebas de misiones sean
 * deterministas sin modificar la clase original:
 * <ul>
 *   <li>ubica cada elemento en la casilla pedida, usando la operación de paquete {@code moverElemento} después de
 *       la distribución aleatoria del constructor;</li>
 *   <li>permite fijar el objetivo que devuelve {@code obtenerObjetivo()} (por defecto, al azar).</li>
 * </ul>
 */
public class TableroDePrueba extends Tablero {

    private Posicion objetivoFijo;

    /**
     * @pre Las posiciones están dentro del tablero y son distintas; cabe al menos el doble de elementos que casillas
     *      pedidas (para estacionar los que estorban mientras se ubican).
     * @post Cada elemento quedó en la posición indicada y la nave en el centro.
     */
    public TableroDePrueba(int filas, int columnas, Map<Posicion, ElementoDelUniverso> ubicaciones)
            throws DimensionesDeTableroInconsistentesException, CantidadDeElementosSuperaCasillaException {
        super(filas, columnas, new ArrayList<>(ubicaciones.values()));
        ubicar(ubicaciones);
    }

    /**
     * @post Tablero vacío, con la nave en el centro.
     */
    public TableroDePrueba(int filas, int columnas)
            throws DimensionesDeTableroInconsistentesException, CantidadDeElementosSuperaCasillaException {
        this(filas, columnas, new LinkedHashMap<>());
    }

    /**
     * @post obtenerObjetivo() y getObjetivo() devuelven objetivo.
     */
    public void fijarObjetivo(Posicion objetivo) {
        this.objetivoFijo = objetivo;
    }

    @Override
    public Posicion obtenerObjetivo() {
        return objetivoFijo == null ? super.obtenerObjetivo() : objetivoFijo;
    }

    @Override
    public Posicion getObjetivo() {
        return objetivoFijo == null ? super.getObjetivo() : objetivoFijo;
    }

    /**
     * Traslada un elemento ya ubicado (lo usa también {@link DestinoProgramado}).
     *
     * @pre desde está ocupada y hacia está libre, dentro del tablero y no es el objetivo.
     */
    void trasladar(Posicion desde, Posicion hacia) {
        moverElemento(desde, hacia);
    }

    private void ubicar(Map<Posicion, ElementoDelUniverso> ubicaciones) {
        Set<Posicion> pedidas = new HashSet<>(ubicaciones.keySet());
        // 1) Se estacionan fuera de las casillas pedidas los elementos que hoy ocupan alguna de ellas.
        for (Map.Entry<Posicion, ElementoDelUniverso> ocupada : obtenerElementos().entrySet()) {
            if (pedidas.contains(ocupada.getKey())) {
                moverElemento(ocupada.getKey(), casillaLibreFuera(pedidas));
            }
        }
        // 2) Con las casillas pedidas libres, cada elemento va a la suya.
        for (Map.Entry<Posicion, ElementoDelUniverso> pedido : ubicaciones.entrySet()) {
            moverElemento(posicionDe(pedido.getValue()), pedido.getKey());
        }
    }

    private Posicion casillaLibreFuera(Set<Posicion> pedidas) {
        HashMap<Posicion, ElementoDelUniverso> ocupacion = obtenerElementos();
        for (int fila = 0; fila < getX(); fila++) {
            for (int columna = 0; columna < getY(); columna++) {
                Posicion posicion = new Posicion(fila, columna);
                if (!pedidas.contains(posicion) && !ocupacion.containsKey(posicion)) {
                    return posicion;
                }
            }
        }
        throw new IllegalStateException("No hay casillas libres para estacionar elementos.");
    }

    private Posicion posicionDe(ElementoDelUniverso elemento) {
        for (Map.Entry<Posicion, ElementoDelUniverso> ocupada : obtenerElementos().entrySet()) {
            if (ocupada.getValue() == elemento) {
                return ocupada.getKey();
            }
        }
        throw new IllegalStateException("El elemento no está en el tablero.");
    }
}
