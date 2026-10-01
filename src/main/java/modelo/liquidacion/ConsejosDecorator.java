package modelo.liquidacion;

/**
 * Decorador Concreto para otorgar premio extra al cargo de Consejero.
 */
public class ConsejosDecorator extends Decorator {
    public ConsejosDecorator(Liquidacion trip) {
        super(trip);
    }

    @Override
    public double calcularSueldo() {
        // El consejero recibirá 2 PG por cada consejo registrado durante el período
        double premioConsejos = trip.getConsejos() * 2.0;
        return trip.calcularSueldo() + premioConsejos;
    }
}
