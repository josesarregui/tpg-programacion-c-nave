package modelo.nave;

/**
 * Nave de tipo carguero (E1-01). Se diferencia de las demás por su configuración inicial de recursos
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
}
