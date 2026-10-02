package modelo.liquidacion;

import modelo.tripulacion.Origen;

import java.time.YearMonth;
import java.util.List;

/**
 * Componente del patrón Decorator: define el contrato común del tripulante (componente concreto)
 * y de los decoradores que le agregan conceptos, para que el cliente los trate por igual.
 *
 * Sólo declara operaciones que todo tripulante puede cumplir (principio de segregación de interfaces):
 * los conceptos particulares de un cargo, como los consejos del consejero, no forman parte del contrato.
 */
public interface Liquidacion {

    /**
     * @pre periodo != null.
     * @post Retorna una lista nueva con los conceptos en el orden en que se aplicaron.
     * @return conceptos que componen el haber del período.
     */
    public List<ConceptoHaber> calcularConceptos(YearMonth periodo);

    /**
     * @pre periodo != null.
     * @post El valor retornado es igual a la suma de los importes de calcularConceptos(periodo).
     * @return haber total del período en PG.
     */
    public double calcularHaberTotal(YearMonth periodo);

    /**
     * @post El valor retornado es mayor o igual a 0.
     * @return adicional por antigüedad calculado según el cargo del tripulante.
     */
    public double calcularAdicionalAntiguedad();

    /**
     * @post El valor retornado es distinto de null.
     * @return planeta de origen del tripulante.
     */
    public Origen getOrigen();
}
