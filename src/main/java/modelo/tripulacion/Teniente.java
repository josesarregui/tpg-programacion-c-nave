package modelo.tripulacion;

/**
 * Clase Hija de Tripulante correspondiente al cargo Teniente.
 */
public class Teniente extends Tripulante {

    // Remuneración según E1-08: 400 PG base y 3% adicional por año de antigüedad.
    private static final double SUELDOBASETENIENTE = 400.0;
    private static final double ADICIONALTENIENTE = 0.03;

    public Teniente(String nombre, int antiguedad, Origen origen) {
        super(nombre, antiguedad, origen);
    }

    @Override
    public double calcularSueldo() {
        return SUELDOBASETENIENTE;
    }

    @Override
    public double getSUELDOBASE() {
        return SUELDOBASETENIENTE;
    }

    @Override
    public double getAdicionalCargo() {
        return ADICIONALTENIENTE;
    }
}