package excepcion;

/**
 * Excepción lanzada cuando una misión no puede prepararse porque la nave no está lista para operar
 * (Aclaración, R4: antes de actuar, una misión debe verificar que la nave esté disponible).
 *
 * La nave no está lista si está destruida (Enunciado E2), si no tiene tripulación, si requiere mantenimiento o
 * si su Motor Warp no está en Disponible. La excepción guarda esos cuatro datos, con sus getters, para que el
 * invocante sepa qué falta resolver (asignar tripulación, hacer mantenimiento o esperar al motor; una nave
 * destruida ya no puede operar) y lo registre en la Bitácora.
 */
public class NaveNoDisponibleException extends Exception {

    private final String codigoMision;
    private final boolean naveDestruida;
    private final boolean tieneTripulacion;
    private final boolean requiereMantenimiento;
    private final String estadoMotor;

    public NaveNoDisponibleException(String codigoMision, boolean naveDestruida, boolean tieneTripulacion,
                                     boolean requiereMantenimiento, String estadoMotor) {
        super("La nave no está lista para operar la misión " + codigoMision
                + " (destruida: " + (naveDestruida ? "sí" : "no")
                + ", tripulación asignada: " + (tieneTripulacion ? "sí" : "no")
                + ", requiere mantenimiento: " + (requiereMantenimiento ? "sí" : "no")
                + ", motor: " + estadoMotor + ").");
        this.codigoMision = codigoMision;
        this.naveDestruida = naveDestruida;
        this.tieneTripulacion = tieneTripulacion;
        this.requiereMantenimiento = requiereMantenimiento;
        this.estadoMotor = estadoMotor;
    }

    public String getCodigoMision() {
        return codigoMision;
    }

    public boolean isNaveDestruida() {
        return naveDestruida;
    }

    public boolean tieneTripulacion() {
        return tieneTripulacion;
    }

    public boolean requiereMantenimiento() {
        return requiereMantenimiento;
    }

    public String getEstadoMotor() {
        return estadoMotor;
    }
}
