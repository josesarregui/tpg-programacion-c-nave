package modelo.universo;

import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;

import java.util.ArrayList;

/**
 * Crea el tablero de 20 x 20 sobre el que operan las misiones (Guía 4.2; Plan de la E2, punto 2), con los
 * elementos del universo del Enunciado E2: asteroides, estaciones de carga y naves de los tres tipos creadas por
 * {@link NaveFactory}. El {@link Tablero} provisto por la cátedra los distribuye al azar.
 *
 * Quien usa el sistema (hoy los controladores) crea el tablero con esta fábrica y el {@link Destino} sobre ese
 * mismo tablero, y se los pasa a las misiones. Las cantidades son constantes y respetan el cupo del tablero
 * (x * y / 4 * 3).
 */
public class FabricaDeUniverso {

    public static final int FILAS = 20;
    public static final int COLUMNAS = 20;
    public static final int ASTEROIDES = 30;
    public static final int ESTACIONES_DE_CARGA = 10;
    public static final int NAVES_POR_TIPO = 3;

    private final NaveFactory naveFactory = new NaveFactory();

    /**
     * @post Devuelve un tablero nuevo de FILAS x COLUMNAS con ASTEROIDES asteroides, ESTACIONES_DE_CARGA
     *       estaciones y NAVES_POR_TIPO naves de cada tipo, la nave propia en el centro y sin objetivo.
     * @return el tablero creado.
     */
    public Tablero crearTablero() {
        ArrayList<ElementoDelUniverso> elementos = new ArrayList<>();
        for (int i = 1; i <= ASTEROIDES; i++) {
            elementos.add(new Asteroide("Asteroide " + i));
        }
        for (int i = 1; i <= ESTACIONES_DE_CARGA; i++) {
            elementos.add(new EstacionDeCarga("Estación de carga " + i));
        }
        for (TipoNave tipo : TipoNave.values()) {
            for (int i = 0; i < NAVES_POR_TIPO; i++) {
                elementos.add(naveFactory.crearNave(tipo));
            }
        }
        try {
            return new Tablero(FILAS, COLUMNAS, elementos);
        } catch (DimensionesDeTableroInconsistentesException | CantidadDeElementosSuperaCasillaException e) {
            // No puede ocurrir: las dimensiones y las cantidades son constantes válidas. Si ocurriera, sería un
            // error de programación (apunte de Excepciones: RuntimeException).
            throw new IllegalStateException("Error de programación: configuración inválida del universo.", e);
        }
    }
}
