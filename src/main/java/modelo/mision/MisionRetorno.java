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
 *   <li>Energía inicial: 0 (Anexo, consumo inicial); al regresar tampoco hay consumos adicionales.</li>
 *   <li>Cierre (Fase 4): reparación del desgaste y recarga total (100 %) de combustible y energía (Anexo). El
 *       informe (Fase 3) ya las incluye en el balance, como las gemas de M-02 y los suministros de M-01.</li>
 * </ul>
 *
 * Sólo redefine sus pasos particulares; el ciclo lo fija {@link Mision#realizar()} (Template Method).
 */
public class MisionRetorno extends Mision {

    /** Energía específica de M-03 al ponerse en marcha (Anexo, consumo inicial). */
    private static final int ENERGIA_INICIAL = 0;
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
    protected int getEnergiaInicial() {
        return ENERGIA_INICIAL;
    }

    @Override
    protected void realizarAccion() {
        registrarAccion("Ejecución: arribo confirmado a la base de origen (la recarga y la reparación se aplican en el cierre).");
    }

    /**
     * Lo que el cierre recarga, para el balance del informe (Fase 3): lo que le falta a la nave para la capacidad
     * máxima, porque el reabastecimiento no la supera.
     *
     * @pre La misión fue exitosa y todavía no se cerró (se consulta en la evaluación, con la nave en la base).
     */
    @Override
    protected int getCombustibleARecargar() {
        return RECARGA_EN_BASE - getAsistente().getCombustible();
    }

    /**
     * @pre La misión fue exitosa y todavía no se cerró (se consulta en la evaluación, con la nave en la base).
     */
    @Override
    protected int getEnergiaARecargar() {
        return RECARGA_EN_BASE - getAsistente().getEnergia();
    }

    /**
     * Lo que el cierre repara, para el balance del informe (Fase 3): el mantenimiento lleva el desgaste a 0.
     *
     * @pre La misión fue exitosa y todavía no se cerró (se consulta en la evaluación, con la nave en la base).
     */
    @Override
    protected int getDesgasteAReparar() {
        return getAsistente().getDesgaste();
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
    public String getCondicionDeExito() {
        return "regresar a la base de origen con la nave en servicio";
    }
}
