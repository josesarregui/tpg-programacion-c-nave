package modelo.tripulacion;

import excepcion.TripulanteInvalidoException;

/**
 * Tripulante con cargo Capitán. Conserva la autoridad general sobre la operación.
 * Remuneración (E1-08): 1000 PG y 20 % de adicional por cada año de antigüedad.
 */
public class Capitan extends Tripulante {

    private static final double REMUNERACION_CARGO = 1000.0;
    private static final double PORCENTAJE_ANTIGUEDAD_ANUAL = 0.20;

    /**
     * @throws TripulanteInvalidoException si el nombre es nulo o vacío, la antigüedad es negativa o el origen es nulo.
     */
    public Capitan(String nombre, int antiguedad, Origen origen) throws TripulanteInvalidoException {
        super(nombre, antiguedad, origen);
    }

    @Override
    public Cargo getCargo() {
        return Cargo.CAPITAN;
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
}
