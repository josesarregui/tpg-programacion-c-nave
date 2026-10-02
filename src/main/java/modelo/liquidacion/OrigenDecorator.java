package modelo.liquidacion;

import excepcion.LiquidacionInvalidaException;

/**
 * Decorador concreto: agrega al haber el subsidio mensual por planeta de origen.
 * El importe lo provee el enum Origen, por lo que no hace falta un switch.
 */
public class OrigenDecorator extends Decorator {

    /**
     * @throws LiquidacionInvalidaException si decorado es null o ya tiene aplicado el subsidio por origen.
     */
    public OrigenDecorator(Liquidacion decorado) throws LiquidacionInvalidaException {
        super(decorado, TipoConcepto.ORIGEN);
    }

    @Override
    protected String describirConcepto() {
        return "Subsidio por origen (" + getOrigen() + ")";
    }

    /**
     * @post El valor retornado es igual a getOrigen().getSubsidioMensual().
     */
    @Override
    protected double calcularImporte() {
        return getOrigen().getSubsidioMensual();
    }
}
