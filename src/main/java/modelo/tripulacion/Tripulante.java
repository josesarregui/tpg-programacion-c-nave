package modelo.tripulacion;

import excepcion.TripulanteInvalidoException;
import modelo.liquidacion.ConceptoHaber;
import modelo.liquidacion.Liquidacion;
import modelo.liquidacion.TipoConcepto;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Miembro de la tripulación con identidad, cargo, planeta de origen y antigüedad (E1-04).
 *
 * Es el Componente Concreto del patrón Decorator de la liquidación de haberes: los decoradores
 * (AntiguedadDecorator, OrigenDecorator) envuelven a un tripulante para agregarle conceptos.
 * Cada subclase define la remuneración de su cargo y calcula su propio adicional por antigüedad,
 * de modo que la interfaz Liquidacion sólo declara operaciones que todo tripulante puede cumplir
 * (principio de segregación de interfaces).
 *
 * Invariante: id > 0, nombre no nulo ni vacío, antigüedad >= 0 y origen no nulo.
 */
public abstract class Tripulante implements Liquidacion {

    private static int ultimoIdAsignado = 0;

    private final int id;
    private final String nombre;
    private int antiguedad;
    private final Origen origen;

    /**
     * Crea un tripulante con un id único autogenerado.
     *
     * @param nombre     nombre del tripulante.
     * @param antiguedad años de antigüedad.
     * @param origen     planeta de origen.
     * @throws TripulanteInvalidoException si el nombre es nulo o vacío, la antigüedad es negativa o el origen es nulo.
     * @post Se crea el tripulante con los datos recibidos y un id mayor que todos los anteriores.
     */
    protected Tripulante(String nombre, int antiguedad, Origen origen) throws TripulanteInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new TripulanteInvalidoException("El nombre del tripulante no puede ser nulo ni vacío.", nombre, antiguedad, origen);
        }
        if (antiguedad < 0) {
            throw new TripulanteInvalidoException("La antigüedad del tripulante no puede ser negativa.", nombre, antiguedad, origen);
        }
        if (origen == null) {
            throw new TripulanteInvalidoException("El origen planetario del tripulante debe estar informado.", nombre, antiguedad, origen);
        }

        this.id = siguienteId();
        this.nombre = nombre.trim();
        this.antiguedad = antiguedad;
        this.origen = origen;

        assert invariante() : "Fallo invariante: el tripulante quedó en un estado inconsistente tras su creación.";
    }

    /**
     * @post El id retornado es mayor que cualquier id generado previamente.
     */
    private static int siguienteId() {
        ultimoIdAsignado++;
        return ultimoIdAsignado;
    }

    /**
     * Cada subclase indica su cargo; así no puede existir un tripulante sin cargo.
     */
    public abstract Cargo getCargo();

    /**
     * @post El valor retornado es mayor que 0.
     * @return remuneración mensual correspondiente al cargo, en PG.
     */
    protected abstract double getRemuneracionCargo();

    /**
     * Calcula el adicional por antigüedad según el porcentaje propio de cada cargo,
     * aplicado sobre la remuneración del cargo por cada año de antigüedad.
     *
     * @post El valor retornado es mayor o igual a 0.
     */
    @Override
    public abstract double calcularAdicionalAntiguedad();

    /**
     * Devuelve los conceptos propios del tripulante: la remuneración de su cargo.
     * Las subclases con conceptos particulares (Consejero) agregan los suyos redefiniendo este método.
     *
     * @pre periodo != null.
     * @post Retorna una lista nueva cuyo primer elemento es el concepto CARGO.
     */
    @Override
    public List<ConceptoHaber> calcularConceptos(YearMonth periodo) {
        assert periodo != null : "El período liquidado no puede ser nulo.";
        List<ConceptoHaber> conceptos = new ArrayList<>();
        conceptos.add(new ConceptoHaber(TipoConcepto.CARGO, "Remuneración por cargo (" + getCargo() + ")", getRemuneracionCargo()));
        return conceptos;
    }

    /**
     * @pre periodo != null.
     * @post El valor retornado es igual a la suma de los importes de calcularConceptos(periodo).
     */
    @Override
    public double calcularHaberTotal(YearMonth periodo) {
        double total = 0;
        for (ConceptoHaber concepto : calcularConceptos(periodo)) {
            total += concepto.getImporte();
        }
        return total;
    }

    /**
     * Suma un año a la antigüedad del tripulante.
     *
     * @post antiguedad == antiguedad anterior + 1.
     */
    public void incrementarAntiguedad() {
        int antiguedadAnterior = this.antiguedad;
        this.antiguedad++;
        assert this.antiguedad == antiguedadAnterior + 1 : "Fallo postcondición: la antigüedad no aumentó en 1.";
        assert invariante() : "Fallo invariante tras incrementar la antigüedad.";
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getAntiguedad() {
        return antiguedad;
    }

    @Override
    public Origen getOrigen() {
        return origen;
    }

    private boolean invariante() {
        return id > 0 && nombre != null && !nombre.isBlank() && antiguedad >= 0 && origen != null;
    }

    @Override
    public String toString() {
        return "#" + id + " " + nombre + " (" + getCargo() + ", " + origen + ", " + antiguedad + (antiguedad == 1 ? " año)" : " años)");
    }
}
