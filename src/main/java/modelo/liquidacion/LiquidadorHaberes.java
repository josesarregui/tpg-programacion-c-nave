package modelo.liquidacion;

import excepcion.LiquidacionInvalidaException;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Liquida los haberes mensuales componiendo los decoradores sobre cada tripulante.
 * Centraliza el armado de la cadena para que ningún cliente olvide un concepto ni lo aplique dos veces.
 * Agregar un concepto nuevo implica crear un decorador y sumarlo aquí, sin modificar los existentes.
 */
public class LiquidadorHaberes {

    /**
     * Liquida el haber de un tripulante: conceptos propios del cargo + antigüedad + origen.
     *
     * @throws LiquidacionInvalidaException si el tripulante o el período son nulos.
     * @post El recibo retornado corresponde al tripulante y al período indicados.
     */
    public ReciboHaberes liquidar(Tripulante tripulante, YearMonth periodo) throws LiquidacionInvalidaException {
        if (tripulante == null) {
            throw new LiquidacionInvalidaException("No se puede liquidar un tripulante nulo.", null);
        }
        if (periodo == null) {
            throw new LiquidacionInvalidaException("El período a liquidar debe estar informado.", null);
        }
        Liquidacion liquidacion = new OrigenDecorator(new AntiguedadDecorator(tripulante));
        return new ReciboHaberes(tripulante, periodo,
                liquidacion.calcularConceptos(periodo), liquidacion.calcularHaberTotal(periodo));
    }

    /**
     * Liquida a todos los integrantes de la tripulación para el período indicado.
     *
     * @throws LiquidacionInvalidaException si la tripulación o el período son nulos.
     * @post Se genera un recibo por cada integrante de la tripulación.
     */
    public LiquidacionTripulacion liquidar(Tripulacion tripulacion, YearMonth periodo) throws LiquidacionInvalidaException {
        if (tripulacion == null) {
            throw new LiquidacionInvalidaException("No se puede liquidar una tripulación nula.", null);
        }
        List<ReciboHaberes> recibos = new ArrayList<>();
        for (Tripulante tripulante : tripulacion.getTripulantes()) {
            recibos.add(liquidar(tripulante, periodo));
        }
        assert recibos.size() == tripulacion.getCantidad() : "Fallo postcondición: falta el recibo de algún tripulante.";
        return new LiquidacionTripulacion(periodo, recibos);
    }
}
