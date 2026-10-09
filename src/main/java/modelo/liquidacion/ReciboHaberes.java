package modelo.liquidacion;

import modelo.tripulacion.Tripulante;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado de liquidar el haber de un tripulante en un período: el detalle de cada concepto y el total.
 * Devuelve datos (no texto formateado), para que puedan mostrarse por consola o en una vista Swing.
 * Sólo lo crea {@link LiquidadorHaberes} (constructor de paquete): nadie puede fabricar un recibo desde afuera.
 *
 * Invariante: el total es igual a la suma de los importes de los conceptos.
 */
public class ReciboHaberes {

    private static final double TOLERANCIA = 0.0001;

    private final Tripulante tripulante;
    private final YearMonth periodo;
    private final List<ConceptoHaber> conceptos;
    private final double total;

    /**
     * @pre tripulante != null, periodo != null, conceptos != null y no vacía.
     * @pre total es igual a la suma de los importes de conceptos.
     */
    ReciboHaberes(Tripulante tripulante, YearMonth periodo, List<ConceptoHaber> conceptos, double total) {
        assert tripulante != null : "El recibo debe pertenecer a un tripulante.";
        assert periodo != null : "El recibo debe indicar el período liquidado.";
        assert conceptos != null && !conceptos.isEmpty() : "El recibo debe tener al menos un concepto.";

        this.tripulante = tripulante;
        this.periodo = periodo;
        this.conceptos = new ArrayList<>(conceptos);
        this.total = total;

        assert invariante() : "Fallo invariante: el total del recibo no coincide con la suma de sus conceptos.";
    }

    public Tripulante getTripulante() {
        return tripulante;
    }

    public YearMonth getPeriodo() {
        return periodo;
    }

    /**
     * @return lista de solo lectura con el detalle de cada concepto.
     */
    public List<ConceptoHaber> getConceptos() {
        return Collections.unmodifiableList(conceptos);
    }

    public double getTotal() {
        return total;
    }

    private boolean invariante() {
        double sumaConceptos = 0;
        // Invariante de ciclo: sumaConceptos es la suma de los importes de los conceptos ya recorridos.
        for (ConceptoHaber concepto : conceptos) {
            sumaConceptos += concepto.getImporte();
        }
        return Math.abs(sumaConceptos - total) < TOLERANCIA;
    }
}
