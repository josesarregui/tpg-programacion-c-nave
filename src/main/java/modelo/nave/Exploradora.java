package modelo.nave;

/**
 * Nave exploradora (E1-01). Se diferencia de las demás por su configuración inicial de recursos
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
}
