package modelo.nave;

/**
 * Nave de tipo carguero (E1-01). Se diferencia de las demás por su
 * configuración inicial de recursos
 * (Ficha de Inicio, punto 2). Sólo puede crearse mediante {@link NaveFactory}.
 */
public class Carguero extends Nave {

    private static final int COMBUSTIBLE_INICIAL = 100;
    private static final int ENERGIA_INICIAL = 60;
    private static final int DESGASTE_INICIAL = 0;

    /**
     * @post La nave tiene combustible 100, energía 60 y desgaste 0.
     */
    Carguero() {
        super(COMBUSTIBLE_INICIAL, ENERGIA_INICIAL, DESGASTE_INICIAL);
    }

    @Override
    public TipoNave getTipo() {
        return TipoNave.CARGUERO;
    }

    /**
     * Ida del Double Dispatch: le avisa a la enemiga que la enfrenta un Carguero (vuelta).
     */
    @Override
    public ResultadoBatalla enfrentarseCon(Nave enemiga) {
        assert enemiga != null : "La nave enemiga no puede ser nula.";
        return enemiga.serEnfrentadaPorCarguero(this);
    }

    /**
     * Columna "vs. Carguero" de la tabla de enfrentamientos: una Exploradora gana a un Carguero.
     *
     * @post Retorna GANA.
     */
    @Override
    ResultadoBatalla serEnfrentadaPorExploradora(Exploradora propia) {
        assert propia != null : "La nave propia no puede ser nula.";
        return ResultadoBatalla.GANA;
    }

    /**
     * Columna "vs. Carguero" de la tabla de enfrentamientos: mismo tipo: pacto de paz.
     *
     * @post Retorna PACTO_DE_PAZ.
     */
    @Override
    ResultadoBatalla serEnfrentadaPorCarguero(Carguero propia) {
        assert propia != null : "La nave propia no puede ser nula.";
        return ResultadoBatalla.PACTO_DE_PAZ;
    }

    /**
     * Columna "vs. Carguero" de la tabla de enfrentamientos: una nave de Combate pierde contra un Carguero.
     *
     * @post Retorna PIERDE.
     */
    @Override
    ResultadoBatalla serEnfrentadaPorCombate(Combate propia) {
        assert propia != null : "La nave propia no puede ser nula.";
        return ResultadoBatalla.PIERDE;
    }
}
