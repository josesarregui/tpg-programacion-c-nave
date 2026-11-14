package modelo.warp;

import excepcion.EstadoMotorInvalidoException;

/**
 * La preparación terminó y el motor está en condiciones de materializar el salto warp (Anexo).
 * En el turno siguiente, si la nave aún tiene {@link MotorWarp#ENERGIA_SALTO_WARP} de energía, la consume y
 * avanza {@link MotorWarp#CASILLEROS_SALTO_WARP} casilleros; luego el motor pasa a Enfriamiento.
 * Si los recursos ya no están disponibles, el salto se aborta y el motor vuelve a preparar el salto.
 */
public class EnWarpState implements State {

    private static final String NOMBRE = "En warp";

    private final MotorWarp motor;

    /**
     * Constructor de paquete: sólo lo usan el motor y los demás estados al completar un paso de la maniobra.
     *
     * @pre motor != null.
     */
    EnWarpState(MotorWarp motor) {
        assert motor != null : "El estado debe pertenecer a un motor.";
        this.motor = motor;
    }

    /**
     * Un salto normal no es válido con el salto warp listo para ejecutarse.
     *
     * @throws EstadoMotorInvalidoException siempre; el motor y la nave no cambian.
     */
    @Override
    public int saltar() throws EstadoMotorInvalidoException {
        throw new EstadoMotorInvalidoException(
                "Transición inválida: no se puede hacer un salto normal con el salto warp en curso.",
                NOMBRE);
    }

    /**
     * Ejecuta el salto warp si hay energía; si no, lo aborta y vuelve a preparar.
     *
     * @return CASILLEROS_SALTO_WARP si saltó; 0 si se abortó.
     */
    @Override
    public int saltarWarp() {
        SuministroMotor suministro = motor.getSuministro();
        int casilleros;
        if (suministro.getEnergia() >= MotorWarp.ENERGIA_SALTO_WARP) {
            suministro.consumirEnergia(MotorWarp.ENERGIA_SALTO_WARP);
            motor.setEstado(new EnfriamientoState(motor));
            casilleros = MotorWarp.CASILLEROS_SALTO_WARP;
        } else {
            motor.setEstado(new PreparandoSaltoState(motor));
            casilleros = 0;
        }
        return casilleros;
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
