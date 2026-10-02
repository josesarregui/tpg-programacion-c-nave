package modelo.tripulacion;

import excepcion.TripulanteInvalidoException;

/**
 * Tripulante con cargo Teniente.
 * Remuneración (E1-08): 400 PG y 3 % de adicional por cada año de antigüedad.
 */
public class Teniente extends Tripulante {

    private static final double REMUNERACION_CARGO = 400.0;
    private static final double PORCENTAJE_ANTIGUEDAD_ANUAL = 0.03;

    /**
     * @throws TripulanteInvalidoException si el nombre es nulo o vacío, la antigüedad es negativa o el origen es nulo.
     */
    public Teniente(String nombre, int antiguedad, Origen origen) throws TripulanteInvalidoException {
        super(nombre, antiguedad, origen);
    }

    @Override
    public Cargo getCargo() {
        return Cargo.TENIENTE;
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
