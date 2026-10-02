package modelo.tripulacion;

import excepcion.TripulanteInvalidoException;

/**
 * Tripulante con cargo Alférez.
 * Remuneración (E1-08): 200 PG y 0,5 % de adicional por cada año de antigüedad.
 */
public class Alferez extends Tripulante {

    private static final double REMUNERACION_CARGO = 200.0;
    private static final double PORCENTAJE_ANTIGUEDAD_ANUAL = 0.005;

    /**
     * @throws TripulanteInvalidoException si el nombre es nulo o vacío, la antigüedad es negativa o el origen es nulo.
     */
    public Alferez(String nombre, int antiguedad, Origen origen) throws TripulanteInvalidoException {
        super(nombre, antiguedad, origen);
    }

    @Override
    public Cargo getCargo() {
        return Cargo.ALFEREZ;
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
