package modelo.warp;

import excepcion.RecursoInsuficienteException;
import modelo.nave.TipoRecurso;

/**
 * Estado inicial del motor: no hay ninguna maniobra en curso.
 * Permite el salto normal y comenzar la preparación del salto warp.
 */
public class DisponibleState implements State {

    private static final String NOMBRE = "Disponible";

    private final MotorWarp motor;

    /**
     * Constructor de paquete: sólo lo usan el motor y los demás estados al completar un paso de la maniobra.
     * Así ningún cliente puede crear un estado para forzar un cambio que saltee el ciclo.
     *
     * @pre motor != null.
     */
    DisponibleState(MotorWarp motor) {
        assert motor != null : "El estado debe pertenecer a un motor.";
        this.motor = motor;
    }

    /**
     * Salto normal: si hay combustible, lo consume y la nave avanza un casillero. El motor sigue Disponible.
     *
     * @throws RecursoInsuficienteException si no alcanza el combustible; nada cambia.
     */
    @Override
    public int saltar() throws RecursoInsuficienteException {
        SuministroMotor suministro = motor.getSuministro();
        if (suministro.getCombustible() < MotorWarp.COMBUSTIBLE_SALTO_NORMAL) {
            throw new RecursoInsuficienteException(TipoRecurso.COMBUSTIBLE, suministro.getCombustible(),
                    MotorWarp.COMBUSTIBLE_SALTO_NORMAL);
        }
        suministro.consumirCombustible(MotorWarp.COMBUSTIBLE_SALTO_NORMAL);
        return MotorWarp.CASILLEROS_SALTO_NORMAL;
    }

    /**
     * Comienza la maniobra warp: el motor pasa a Preparando salto y este turno ya cuenta como el primero
     * de preparación (si hay energía suficiente).
     */
    @Override
    public int saltarWarp() {
        PreparandoSaltoState preparando = new PreparandoSaltoState(motor);
        motor.setEstado(preparando);
        preparando.prepararTurno();
        return 0;
    }

    @Override
    public boolean estaDisponible() {
        return true;
    }

    @Override
    public String toString() {
        return NOMBRE;
    }
}
