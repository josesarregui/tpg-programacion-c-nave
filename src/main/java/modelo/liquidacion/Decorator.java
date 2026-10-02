package modelo.liquidacion;

import excepcion.LiquidacionInvalidaException;
import modelo.tripulacion.Origen;

import java.time.YearMonth;
import java.util.List;

/**
 * Decorador abstracto del patrón Decorator.
 * Implementa la misma interfaz que el componente para poder sustituirlo (principio de sustitución
 * de Liskov) y mantiene una referencia al objeto decorado, inyectada en el constructor.
 * Cada decorador concreto agrega exactamente un concepto y propaga el resto de los mensajes
 * al objeto que decora.
 *
 * Invariante: el objeto decorado y el concepto agregado no son nulos.
 */
public abstract class Decorator implements Liquidacion {

    private final Liquidacion decorado;
    private final TipoConcepto conceptoAgregado;

    /**
     * @param decorado         tripulante u otro decorador a envolver.
     * @param conceptoAgregado concepto que agrega este decorador.
     * @pre conceptoAgregado != null (lo indica cada decorador concreto).
     * @throws LiquidacionInvalidaException si decorado es null o si ya tiene aplicado el mismo concepto.
     * @post El decorador envuelve a decorado.
     */
    protected Decorator(Liquidacion decorado, TipoConcepto conceptoAgregado) throws LiquidacionInvalidaException {
        assert conceptoAgregado != null : "Todo decorador debe indicar el concepto que agrega.";
        if (decorado == null) {
            throw new LiquidacionInvalidaException("El cálculo de haberes no acepta conceptos nulos.", conceptoAgregado);
        }
        // Una de las desventajas del patrón es que permite combinaciones que no funcionan:
        // por ejemplo, cobrar dos veces la antigüedad. Se rechazan explícitamente.
        if (conceptoYaAplicado(decorado, conceptoAgregado)) {
            throw new LiquidacionInvalidaException("El concepto " + conceptoAgregado + " ya fue aplicado a esta liquidación.", conceptoAgregado);
        }
        this.decorado = decorado;
        this.conceptoAgregado = conceptoAgregado;
        assert invariante() : "Fallo invariante: el decorador quedó sin objeto decorado.";
    }

    /**
     * @return texto que identifica el concepto en el detalle de la liquidación.
     */
    protected abstract String describirConcepto();

    /**
     * @post El valor retornado es mayor o igual a 0.
     * @return importe del concepto que agrega este decorador.
     */
    protected abstract double calcularImporte();

    protected Liquidacion getDecorado() {
        return decorado;
    }

    /**
     * Obtiene los conceptos del objeto decorado y les agrega el concepto propio.
     *
     * @pre periodo != null.
     * @post La lista retornada tiene un concepto más que la del objeto decorado.
     */
    @Override
    public List<ConceptoHaber> calcularConceptos(YearMonth periodo) {
        List<ConceptoHaber> conceptos = decorado.calcularConceptos(periodo);
        int cantidadAnterior = conceptos.size();
        conceptos.add(new ConceptoHaber(conceptoAgregado, describirConcepto(), calcularImporte()));
        assert conceptos.size() == cantidadAnterior + 1 : "Fallo postcondición: el decorador no agregó su concepto.";
        return conceptos;
    }

    /**
     * Delega en el objeto decorado y suma el importe propio.
     *
     * @pre periodo != null.
     * @post El valor retornado es igual al haber del objeto decorado más calcularImporte().
     */
    @Override
    public double calcularHaberTotal(YearMonth periodo) {
        double haberDecorado = decorado.calcularHaberTotal(periodo);
        double importe = calcularImporte();
        assert importe >= 0 : "El importe del concepto " + conceptoAgregado + " no puede ser negativo.";
        double haberTotal = haberDecorado + importe;
        assert haberTotal >= haberDecorado : "Fallo postcondición: un decorador no puede disminuir el haber.";
        return haberTotal;
    }

    @Override
    public double calcularAdicionalAntiguedad() {
        return decorado.calcularAdicionalAntiguedad();
    }

    @Override
    public Origen getOrigen() {
        return decorado.getOrigen();
    }

    /**
     * Recorre las capas de la cadena de decoradores buscando el concepto indicado.
     */
    private static boolean conceptoYaAplicado(Liquidacion liquidacion, TipoConcepto concepto) {
        Liquidacion capaActual = liquidacion;
        boolean encontrado = false;
        // Invariante de ciclo: ninguna de las capas ya recorridas agrega el concepto buscado.
        while (!encontrado && capaActual instanceof Decorator) {
            Decorator decorador = (Decorator) capaActual;
            encontrado = decorador.conceptoAgregado == concepto;
            capaActual = decorador.decorado;
        }
        return encontrado;
    }

    private boolean invariante() {
        return decorado != null && conceptoAgregado != null;
    }
}
