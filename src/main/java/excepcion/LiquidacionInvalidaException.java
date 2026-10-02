package excepcion;

import modelo.liquidacion.TipoConcepto;

/**
 * Excepción lanzada cuando no puede armarse una liquidación de haberes válida (E1-08):
 * conceptos nulos, un mismo concepto aplicado dos veces, o tripulante o período no informados.
 * Guarda el concepto involucrado (puede ser null si el error no corresponde a un concepto).
 */
public class LiquidacionInvalidaException extends Exception {

    private final TipoConcepto conceptoRechazado;

    public LiquidacionInvalidaException(String mensaje, TipoConcepto conceptoRechazado) {
        super(mensaje);
        this.conceptoRechazado = conceptoRechazado;
    }

    public TipoConcepto getConceptoRechazado() {
        return conceptoRechazado;
    }
}
