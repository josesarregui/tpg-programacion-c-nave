package modelo.liquidacion;

import excepcion.TripulacionInvalidaException;

/**
 * Decorador Concreto para asignar subsidio por origen planetario.
 */
public class OrigenDecorator extends Decorator {

    // Buenas prácticas: Extracción de "Magic Numbers" a constantes.
    private static final double SUBSIDIO_TERRICOLA = 20.0;
    private static final double SUBSIDIO_VULCANO = 30.0;
    private static final double SUBSIDIO_MARCIANO = 18.0;

    public OrigenDecorator(Liquidacion trip) {
        super(trip);
    }

    @Override
    public double calcularSueldo() {
        // Bloque switch exhaustivo. Lanza excepción de dominio ante fallo.
        double subsidio = switch (trip.getOrigen()) {
            case TERRICOLA -> SUBSIDIO_TERRICOLA;
            case VULCANO -> SUBSIDIO_VULCANO;
            case MARCIANO -> SUBSIDIO_MARCIANO;
            default -> throw new TripulacionInvalidaException("Origen planetario desconocido o inválido.");
        };

        return trip.calcularSueldo() + subsidio;
    }
}