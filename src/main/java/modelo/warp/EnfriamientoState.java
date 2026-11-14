package modelo.warp;

import excepcion.EstadoMotorInvalidoException;

/**
 * Después del salto warp, el motor disipa el calor durante un turno (Anexo): esa etapa genera
 * {@link MotorWarp#DESGASTE_ENFRIAMIENTO} de desgaste en la nave y luego el motor vuelve a Disponible.
 */
public class EnfriamientoState implements State {

    private static final String NOMBRE = "Enfriamiento";

    private final MotorWarp motor;

    /**
     * Constructor de paquete: sólo lo usan el motor y los demás estados al completar un paso de la maniobra.
     *
     * @pre motor != null.
     */
    EnfriamientoState(MotorWarp motor) {
        assert motor != null : "El estado debe pertenecer a un motor.";
        this.motor = motor;
    }

    /**
     * Un salto normal no es válido mientras el motor se enfría.
     *
     * @throws EstadoMotorInvalidoException siempre; el motor y la nave no cambian.
     */
    @Override
    public int saltar() throws EstadoMotorInvalidoException {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: no se puede hacer un salto normal mientras el motor se enfría.",
                NOMBRE);
    }

    /**
     * Turno de enfriamiento: suma el desgaste y el motor vuelve a Disponible.
     *
     * @return 0: en este turno la nave no avanza.
     */
    @Override
    public int saltarWarp() {
        motor.getSuministro().sumarDesgaste(MotorWarp.DESGASTE_ENFRIAMIENTO);
        motor.setEstado(new DisponibleState(motor));
        return 0;
    }

    @Override
    public boolean estaDisponible() {
        return false;
    }

    @Override
    public String toString() {
        return NOMBRE;
    }
}
