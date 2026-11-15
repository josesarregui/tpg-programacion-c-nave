package modelo.mision;

import modelo.universo.Destino;
import modelo.universo.Posicion;
import modelo.universo.Tablero;

/**
 * M-03 — Retorno a punto seguro (Enunciado E2): regresar a la base de origen, el punto de partida del recorrido,
 * para recargar recursos y reparar el desgaste de la nave.
 *
 * <ul>
 *   <li>Objetivo: el centro del tablero, donde comienza la nave (Documentación de Destino y Tablero; Plan de la E2,
 *       Paso 3). No se registra como objetivo del tablero, porque {@link Tablero#obtenerObjetivo()} siempre elige
 *       una casilla al azar.</li>
 *   <li>Sin costo adicional de la acción final (Tabla de Recursos, IV.4).</li>
 *   <li>Cierre (Fase 4): reparación del desgaste y recarga de combustible y energía hasta la capacidad máxima.</li>
 * </ul>
 *
 * Sólo redefine sus pasos particulares; el ciclo lo fija {@link Mision#realizar()} (Template Method).
 */
public class MisionRetorno extends Mision {

    private static final int ENERGIA_ADICIONAL = 0;
    /**
     * Recarga en la base: igual a la capacidad máxima de cada recurso (Ficha de Inicio), de modo que el
     * reabastecimiento, que no supera el tope, los deja completos.
     */
    public static final int RECARGA_EN_BASE = 100;

    /**
     * @pre tablero y destino no nulos, y destino creado sobre ese tablero.
     * @post La misión queda creada.
     */
    public MisionRetorno(Tablero tablero, Destino destino) {
        super("M-03", "Retorno seguro", tablero, destino);
    }

    /**
     * @post El objetivo es el centro del tablero: new Posicion(tablero.getX() / 2, tablero.getY() / 2).
     */
    @Override
    protected Posicion fijarObjetivo(Tablero tablero) {
        return new Posicion(tablero.getX() / 2, tablero.getY() / 2);
    }

    @Override
    protected int getEnergiaAdicional() {
        return ENERGIA_ADICIONAL;
    }

    @Override
    protected void realizarAccion() {
        registrarAccion("Ejecución: arribo confirmado a la base de origen (la recarga y la reparación se aplican en el cierre).");
    }

    /**
     * Repara y recarga la nave en la base a través del asistente (Fase 4).
     *
     * @pre La misión fue exitosa y la nave no está destruida.
     * @post Desgaste 0; combustible y energía en su capacidad máxima.
     */
    @Override
    protected void aplicarCierre() {
        getAsistente().realizarMantenimiento();
        getAsistente().reabastecer(RECARGA_EN_BASE, RECARGA_EN_BASE);
    }

    @Override
    protected String getCondicionDeExito() {
        return "regresar a la base de origen con la nave en servicio";
    }
}
