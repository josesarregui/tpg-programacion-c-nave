package excepcion;

/**
 * Excepción lanzada cuando una misión no puede prepararse porque la nave no está lista para operar
 * (Aclaración, R4: antes de actuar, una misión debe verificar que la nave esté disponible).
 *
 * La nave no está lista si no tiene tripulación, si requiere mantenimiento o si su Motor Warp no está
 * en Disponible. La excepción guarda esos tres datos, con sus getters, para que el invocante sepa qué
 * falta resolver (asignar tripulación, hacer mantenimiento o esperar al motor) y lo registre en la Bitácora.
 */
public class NaveNoDisponibleException extends Exception {

    private final String codigoMision;
    private final boolean tieneTripulacion;
    private final boolean requiereMantenimiento;
    private final String estadoMotor;

    public NaveNoDisponibleException(String codigoMision, boolean tieneTripulacion,
                                     boolean requiereMantenimiento, String estadoMotor) {
        super("La nave no está lista para operar la misión " + codigoMision
                + " (tripulación asignada: " + (tieneTripulacion ? "sí" : "no")
                + ", requiere mantenimiento: " + (requiereMantenimiento ? "sí" : "no")
                + ", motor: " + estadoMotor + ").");
        this.codigoMision = codigoMision;
        this.tieneTripulacion = tieneTripulacion;
        this.requiereMantenimiento = requiereMantenimiento;
        this.estadoMotor = estadoMotor;
    }

    public String getCodigoMision() {
        return codigoMision;
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
