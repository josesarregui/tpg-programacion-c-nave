package modelo.liquidacion;

/**
 * Línea inmutable de una liquidación: qué concepto se pagó y por qué importe.
 * Permite "mantener identificable el aporte de cada concepto" (E1-08).
 *
 * Invariante: tipo no nulo, descripción no vacía e importe mayor o igual a 0.
 */
public class ConceptoHaber {

    private final TipoConcepto tipo;
    private final String descripcion;
    private final double importe;

    /**
     * Los importes los calcula el propio modelo, por lo que un importe inválido sólo puede
     * deberse a un error de programación: se verifica con aserciones.
     *
     * @pre tipo != null.
     * @pre descripcion != null y no está en blanco.
     * @pre importe >= 0.
     */
    public ConceptoHaber(TipoConcepto tipo, String descripcion, double importe) {
        assert tipo != null : "El tipo de concepto no puede ser nulo.";
        assert descripcion != null && !descripcion.isBlank() : "La descripción del concepto no puede estar vacía.";
        assert importe >= 0 : "Importe inválido para el concepto " + tipo + ": " + importe;

        this.tipo = tipo;
        this.descripcion = descripcion;
        this.importe = importe;

        assert invariante() : "Fallo invariante del concepto de haber.";
    }

    public TipoConcepto getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getImporte() {
        return importe;
    }

    private boolean invariante() {
        return tipo != null && descripcion != null && !descripcion.isBlank() && importe >= 0;
    }

    @Override
    public String toString() {
        return descripcion + ": " + importe + " PG";
    }
}
