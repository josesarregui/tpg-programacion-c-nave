package modelo.tripulacion;

/**
 * Clase Hija de Tripulante correspondiente al cargo Capitán.
 * Define la remuneración base más alta de la flota y su respectivo porcentaje por antigüedad.
 */
public class Capitan extends Tripulante {

    // Remuneración según E1-08: 1000 PG base y 20% adicional por año de antigüedad.
    private static final double SUELDOBASECAPITAN = 1000.0;
    private static final double ADICIONALCAPITAN = 0.20;

    public Capitan(String nombre, int antiguedad, Origen origen) {
        super(nombre, antiguedad, origen);
    }

    @Override
    public double calcularSueldo() {
        return SUELDOBASECAPITAN;
    }

    @Override
    public double getSUELDOBASE() {
        return SUELDOBASECAPITAN;
    }

    @Override
    public double getAdicionalCargo() {
        return ADICIONALCAPITAN;
    }
}
