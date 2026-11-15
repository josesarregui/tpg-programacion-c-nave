package modelo.nave;

/**
 * Nave exploradora (E1-01). Se diferencia de las demás por su configuración
 * inicial de recursos
 * (Ficha de Inicio, punto 2). Sólo puede crearse mediante {@link NaveFactory}.
 */
public class Exploradora extends Nave {

    private static final int COMBUSTIBLE_INICIAL = 60;
    private static final int ENERGIA_INICIAL = 80;
    private static final int DESGASTE_INICIAL = 0;

    /**
     * @post La nave tiene combustible 60, energía 80 y desgaste 0.
     */
    Exploradora() {
        super(COMBUSTIBLE_INICIAL, ENERGIA_INICIAL, DESGASTE_INICIAL);
    }

    @Override
    public TipoNave getTipo() {
        return TipoNave.EXPLORADORA;
    }

    /**
     * Ida del Double Dispatch: le avisa a la enemiga que la enfrenta una Exploradora (vuelta).
     */
    @Override
    public ResultadoBatalla enfrentarseCon(Nave enemiga) {
        assert enemiga != null : "La nave enemiga no puede ser nula.";
        return enemiga.serEnfrentadaPorExploradora(this);
    }

    /**
     * Columna "vs. Exploradora" de la tabla de enfrentamientos: mismo tipo: pacto de paz.
     *
     * @post Retorna PACTO_DE_PAZ.
     */
    @Override
    ResultadoBatalla serEnfrentadaPorExploradora(Exploradora propia) {
        assert propia != null : "La nave propia no puede ser nula.";
        return ResultadoBatalla.PACTO_DE_PAZ;
    }

    /**
     * Columna "vs. Exploradora" de la tabla de enfrentamientos: un Carguero pierde contra una Exploradora.
     *
     * @post Retorna PIERDE.
     */
    @Override
    ResultadoBatalla serEnfrentadaPorCarguero(Carguero propia) {
        assert propia != null : "La nave propia no puede ser nula.";
        return ResultadoBatalla.PIERDE;
    }

    /**
     * Columna "vs. Exploradora" de la tabla de enfrentamientos: una nave de Combate gana a una Exploradora.
     *
     * @post Retorna GANA.
     */
    @Override
    ResultadoBatalla serEnfrentadaPorCombate(Combate propia) {
        assert propia != null : "La nave propia no puede ser nula.";
        return ResultadoBatalla.GANA;
    }
}
