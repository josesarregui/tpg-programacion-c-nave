package modelo.tripulacion;

/**
 * Clase Hija de Tripulante correspondiente al cargo Consejero.
 */
public class Consejero extends Tripulante {
    private static final double SUELDOBASECONSEJERO = 600.0;
    private static final double ADICIONALCONSEJERO = 0.05;

    private int cantConsejos;

    public Consejero(String nombre, int antiguedad, Origen origen) {
        super(nombre, antiguedad, origen);
        this.cantConsejos = 0; // Estado inicial válido[cite: 3]
        assert invarianteConsejero() : "Fallo invariante en la clase Consejero"; //
    }

    /**
     * Registra un consejo. Imprescindible para que ConsejosDecorator liquide 2 PG.
     */
    public void registrarConsejo() {
        // Blindaje tradicional por si la JVM tiene los asserts desactivados en producción
        if (this.cantConsejos < 0) {
            throw new excepcion.TripulacionInvalidaException("Estado inconsistente: consejos negativos.");
        }

        this.cantConsejos++;
        assert invarianteConsejero() : "Fallo invariante en mutación de Consejero"; //
    }

    private boolean invarianteConsejero() {
        return this.cantConsejos >= 0;
    }

    @Override public double calcularSueldo() { return SUELDOBASECONSEJERO; }
    @Override public double getSUELDOBASE() { return SUELDOBASECONSEJERO; }
    @Override public double getAdicionalCargo() { return ADICIONALCONSEJERO; }
    @Override public int getConsejos() { return this.cantConsejos; }
}