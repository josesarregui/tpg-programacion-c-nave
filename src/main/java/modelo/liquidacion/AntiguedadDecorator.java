package modelo.liquidacion;

/**
 * Decorador Concreto para calcular el adicional por años de antigüedad.
 */
public class AntiguedadDecorator extends Decorator {
    public AntiguedadDecorator(Liquidacion trip) {
        super(trip);
    }

    @Override
    public double calcularSueldo() {
        // Adicional calculado estrictamente sobre la remuneración base del cargo.
        double adicional = trip.getSUELDOBASE() * trip.getAdicionalCargo() * trip.getAntiguedad();
        return trip.calcularSueldo() + adicional;
    }
}
