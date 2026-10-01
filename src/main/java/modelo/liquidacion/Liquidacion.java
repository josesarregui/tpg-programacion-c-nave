package modelo.liquidacion;

import modelo.tripulacion.Origen;

/**
 * Interfaz común implementada tanto por el componente concreto (Tripulante)
 * como por el decorador abstracto (Decorator).
 */
public interface Liquidacion {
    double calcularSueldo();
    int getAntiguedad();
    double getAdicionalCargo();
    Origen getOrigen();

    /**
     * Devuelve el sueldo base nominal para cimentar cálculos porcentuales inalterados.
     */
    double getSUELDOBASE();
}
