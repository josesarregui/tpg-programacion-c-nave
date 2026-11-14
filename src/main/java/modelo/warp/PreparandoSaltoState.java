package modelo.warp;

import excepcion.EstadoMotorInvalidoException;

/**
 * El motor prepara el salto warp (Anexo): necesita {@link MotorWarp#TURNOS_PREPARACION} turnos de preparación.
 * En cada turno verifica que la nave tenga {@link MotorWarp#ENERGIA_SALTO_WARP} de energía: si la tiene, el
 * turno cuenta; si no, la maniobra no puede continuar y el motor sigue preparando.
 * Completada la preparación, el motor pasa a En warp, en condiciones de saltar.
 *
 * Invariante: 0 <= turnosCompletados < TURNOS_PREPARACION (al completarlos, el motor ya cambió de estado).
 */
public class PreparandoSaltoState implements State {

    private static final String NOMBRE = "Preparando salto";

    private final MotorWarp motor;
    private int turnosCompletados;

    /**
     * Constructor de paquete: sólo lo usan el motor y los demás estados al completar un paso de la maniobra.
     *
     * @pre motor != null.
     * @post la preparación comienza sin turnos completados.
     */
    PreparandoSaltoState(MotorWarp motor) {
        assert motor != null : "El estado debe pertenecer a un motor.";
        this.motor = motor;
        this.turnosCompletados = 0;
    }

    /**
     * Un salto normal no es válido mientras se prepara el salto warp: la maniobra ya está en curso.
     *
     * @throws EstadoMotorInvalidoException siempre; el motor y la nave no cambian.
     */
    @Override
    public int saltar() throws EstadoMotorInvalidoException {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: no se puede hacer un salto normal mientras se prepara el salto warp.",
                NOMBRE);
    }

    @Override
    public int saltarWarp() {
        prepararTurno();
        return 0;
    }

    /**
     * Realiza un turno de preparación. Es de paquete porque también lo usa Disponible, cuyo turno de inicio
     * de la maniobra cuenta como el primero de preparación.
     *
     * @post si había energía suficiente, el turno se contó; al completar los turnos, el motor pasó a En warp.
     *       Si no había energía, nada cambia.
     */
    void prepararTurno() {
        if (motor.getSuministro().getEnergia() >= MotorWarp.ENERGIA_SALTO_WARP) {
            turnosCompletados++;
            if (turnosCompletados == MotorWarp.TURNOS_PREPARACION) {
                motor.setEstado(new EnWarpState(motor));
            }
        }
        assert turnosCompletados <= MotorWarp.TURNOS_PREPARACION : "Fallo invariante: turnos de preparación excedidos.";
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
