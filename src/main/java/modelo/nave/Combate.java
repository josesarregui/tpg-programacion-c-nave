package modelo.nave;

/**
 * Nave de combate (E1-01). Se diferencia de las demás por su configuración inicial de recursos
 * (Ficha de Inicio, punto 2). Sólo puede crearse mediante {@link NaveFactory}.
 */
public class Combate extends Nave {

    private static final int COMBUSTIBLE_INICIAL = 80;
    private static final int ENERGIA_INICIAL = 100;
    private static final int DESGASTE_INICIAL = 0;

    /**
     * @post La nave tiene combustible 80, energía 100 y desgaste 0.
     */
    Combate() {
        super(COMBUSTIBLE_INICIAL, ENERGIA_INICIAL, DESGASTE_INICIAL);
    }

    @Override
    public TipoNave getTipo() {
        return TipoNave.COMBATE;
    }

    /**
     * Ida del Double Dispatch: le avisa a la enemiga que la enfrenta una nave de Combate (vuelta).
     */
    @Override
    public ResultadoBatalla enfrentarseCon(Nave enemiga) {
        assert enemiga != null : "La nave enemiga no puede ser nula.";
        return enemiga.serEnfrentadaPorCombate(this);
    }

    /**
     * Columna "vs. Combate" de la tabla de enfrentamientos: una Exploradora pierde contra una nave de Combate.
     *
     * @post Retorna PIERDE.
     */
    @Override
    ResultadoBatalla serEnfrentadaPorExploradora(Exploradora propia) {
        assert propia != null : "La nave propia no puede ser nula.";
        return ResultadoBatalla.PIERDE;
    }

    /**
     * Columna "vs. Combate" de la tabla de enfrentamientos: un Carguero gana a una nave de Combate.
     *
     * @post Retorna GANA.
     */
    @Override
    ResultadoBatalla serEnfrentadaPorCarguero(Carguero propia) {
        assert propia != null : "La nave propia no puede ser nula.";
        return ResultadoBatalla.GANA;
    }

    /**
     * Columna "vs. Combate" de la tabla de enfrentamientos: mismo tipo: pacto de paz.
     *
     * @post Retorna PACTO_DE_PAZ.
     */
    @Override
    ResultadoBatalla serEnfrentadaPorCombate(Combate propia) {
        assert propia != null : "La nave propia no puede ser nula.";
        return ResultadoBatalla.PACTO_DE_PAZ;
    }
}
