package modelo.mision;

/**
 * Etapa en la que se encuentra una misión dentro de su ciclo (E1-06):
 * CREADA -> PREPARADA -> EJECUTADA -> EVALUADA -> CERRADA.
 *
 * {@link Mision} la usa para verificar con aserciones que cada paso se haga después del anterior
 * (Observaciones: "Una Misión no podrá ejecutarse sin preparación previa" y
 * "Una Misión no podrá cerrarse sin resultado e informe").
 */
public enum EtapaMision {
    CREADA,
    PREPARADA,
    EJECUTADA,
    EVALUADA,
    CERRADA
}
