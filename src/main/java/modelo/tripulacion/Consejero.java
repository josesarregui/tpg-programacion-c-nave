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
        this.cantConsejos = 0;
        assert invarianteConsejero() : "Fallo invariante en la clase Consejero";
    }

    public void registrarConsejo() {
        if (this.cantConsejos < 0) {
            throw new excepcion.TripulacionInvalidaException("Estado inconsistente: consejos negativos.");
        }
        this.cantConsejos++;
        assert invarianteConsejero() : "Fallo invariante en mutacion de Consejero";
    }

    private boolean invarianteConsejero() {
        return this.cantConsejos >= 0;
    }

    @Override
    public double calcularSueldo() {
        // LÓGICA TRASPASADA: Ahora el Consejero suma sus propios bonos por consejo
        double premioConsejos = this.cantConsejos * 2.0;
        return SUELDOBASECONSEJERO + premioConsejos;
    }

    @Override public double getSUELDOBASE() { return SUELDOBASECONSEJERO; }
    @Override public double getAdicionalCargo() { return ADICIONALCONSEJERO; }

    // Ya no lleva @Override porque se quitó de la interfaz Liquidacion,
    // pero sigue siendo un metodo útil y propio de esta clase.
    public int getConsejos() { return this.cantConsejos; }
}