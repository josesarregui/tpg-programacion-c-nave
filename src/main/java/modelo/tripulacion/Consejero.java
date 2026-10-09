package modelo.tripulacion;

import excepcion.LiquidacionInvalidaException;
import excepcion.TripulanteInvalidoException;
import modelo.liquidacion.ConceptoHaber;
import modelo.liquidacion.TipoConcepto;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Tripulante con cargo Consejero.
 * Remuneración (E1-08): 600 PG, 5 % de adicional por cada año de antigüedad
 * y 2 PG por cada consejo registrado durante el período liquidado.
 *
 * El adicional por consejos es responsabilidad exclusiva de esta clase: ni la interfaz
 * Liquidacion ni el resto de los tripulantes necesitan conocerlo.
 *
 * Invariante: cada consejo registrado tiene su período informado (no hay períodos nulos).
 */
public class Consejero extends Tripulante {

    private static final double REMUNERACION_CARGO = 600.0;
    private static final double PORCENTAJE_ANTIGUEDAD_ANUAL = 0.05;
    private static final double IMPORTE_POR_CONSEJO = 2.0;

    // Período (mes) de cada consejo registrado: así cada liquidación cobra sólo los consejos de su mes.
    private final List<YearMonth> periodosDeConsejos = new ArrayList<>();

    /**
     * @throws TripulanteInvalidoException si el nombre es nulo o vacío, la antigüedad es negativa o el origen es nulo.
     * @post El consejero no tiene consejos registrados en ningún período.
     */
    public Consejero(String nombre, int antiguedad, Origen origen) throws TripulanteInvalidoException {
        super(nombre, antiguedad, origen);
        assert invarianteConsejos() : "Fallo invariante del consejero tras su creación.";
    }

    @Override
    public Cargo getCargo() {
        return Cargo.CONSEJERO;
    }

    @Override
    protected double getRemuneracionCargo() {
        return REMUNERACION_CARGO;
    }

    /**
     * @post El valor retornado es igual a REMUNERACION_CARGO * PORCENTAJE_ANTIGUEDAD_ANUAL * antigüedad.
     */
    @Override
    public double calcularAdicionalAntiguedad() {
        return REMUNERACION_CARGO * PORCENTAJE_ANTIGUEDAD_ANUAL * getAntiguedad();
    }

    /**
     * Registra un consejo brindado durante el período indicado.
     * El período llega desde fuera del modelo (el Asistente o, en la E2, un controlador),
     * por eso se valida con una excepción y no sólo con una aserción.
     *
     * @throws LiquidacionInvalidaException si el período es nulo; el concepto rechazado es CONSEJOS.
     * @post getCantidadConsejos(periodo) == cantidad anterior + 1.
     */
    public void registrarConsejo(YearMonth periodo) throws LiquidacionInvalidaException {
        if (periodo == null) {
            throw new LiquidacionInvalidaException("El período del consejo debe estar informado.", TipoConcepto.CONSEJOS);
        }
        int cantidadAnterior = getCantidadConsejos(periodo);
        periodosDeConsejos.add(periodo);
        assert getCantidadConsejos(periodo) == cantidadAnterior + 1 : "Fallo postcondición: el consejo no quedó registrado.";
        assert invarianteConsejos() : "Fallo invariante tras registrar un consejo.";
    }

    /**
     * @pre periodo != null.
     * @post El valor retornado es mayor o igual a 0 (0 si no hubo consejos en el período).
     */
    public int getCantidadConsejos(YearMonth periodo) {
        assert periodo != null : "El período consultado no puede ser nulo.";
        int cantidad = 0;
        // Invariante de ciclo: cantidad es la cantidad de consejos del período entre los ya recorridos.
        for (YearMonth periodoDelConsejo : periodosDeConsejos) {
            if (periodoDelConsejo.equals(periodo)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    /**
     * @pre periodo != null.
     * @post El valor retornado es igual a IMPORTE_POR_CONSEJO * getCantidadConsejos(periodo).
     */
    public double calcularAdicionalConsejos(YearMonth periodo) {
        return IMPORTE_POR_CONSEJO * getCantidadConsejos(periodo);
    }

    /**
     * Agrega a los conceptos del cargo el adicional por los consejos del período.
     *
     * @pre periodo != null.
     * @post La lista retornada contiene los conceptos de Tripulante más el concepto CONSEJOS.
     */
    @Override
    public List<ConceptoHaber> calcularConceptos(YearMonth periodo) {
        List<ConceptoHaber> conceptos = super.calcularConceptos(periodo);
        conceptos.add(new ConceptoHaber(TipoConcepto.CONSEJOS,
                "Adicional por consejos (" + getCantidadConsejos(periodo) + " en " + periodo + ")",
                calcularAdicionalConsejos(periodo)));
        return conceptos;
    }

    private boolean invarianteConsejos() {
        boolean periodosValidos = true;
        // Invariante de ciclo: ninguno de los períodos ya recorridos es nulo.
        for (YearMonth periodo : periodosDeConsejos) {
            periodosValidos = periodosValidos && periodo != null;
        }
        return periodosValidos;
    }
}
