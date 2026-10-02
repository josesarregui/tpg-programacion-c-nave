package modelo.liquidacion;

import excepcion.LiquidacionInvalidaException;

/**
 * Decorador concreto: agrega al haber el adicional por antigüedad.
 * El importe lo calcula cada cargo (Capitan, Consejero, Teniente, Alferez) con su propio porcentaje;
 * el decorador sólo lo incorpora a la liquidación.
 */
public class AntiguedadDecorator extends Decorator {

    /**
     * @throws LiquidacionInvalidaException si decorado es null o ya tiene aplicado el adicional por antigüedad.
     */
    public AntiguedadDecorator(Liquidacion decorado) throws LiquidacionInvalidaException {
        super(decorado, TipoConcepto.ANTIGUEDAD);
    }

    @Override
    protected String describirConcepto() {
        return "Adicional por antigüedad";
    }

    /**
     * @post El valor retornado es igual a getDecorado().calcularAdicionalAntiguedad().
     */
    @Override
    protected double calcularImporte() {
        return getDecorado().calcularAdicionalAntiguedad();
    }
}
