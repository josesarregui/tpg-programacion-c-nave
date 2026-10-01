package modelo.tripulacion;

/**
 * Clase Hija de Tripulante correspondiente al cargo Alférez.
 */
public class Alferez extends Tripulante {

    // Remuneración según E1-08: 200 PG base y 0.5% adicional por año de antigüedad.
    private static final double SUELDOBASEALFEREZ = 200.0;
    private static final double ADICIONALALFEREZ = 0.005;

    public Alferez(String nombre, int antiguedad, Origen origen) {
        super(nombre, antiguedad, origen);
    }

    @Override
    public double calcularSueldo() {
        return SUELDOBASEALFEREZ;
    }

    @Override
    public double getSUELDOBASE() {
        return SUELDOBASEALFEREZ;
    }

    @Override
    public double getAdicionalCargo() {
        return ADICIONALALFEREZ;
    }
}
