package modelo.warp;

import excepcion.EstadoMotorInvalidoException;
import excepcion.RecursoInsuficienteException;

/**
 * Motor Warp de la nave: contexto del patrón State (E1-02; Anexo, "Navegación y Motor Warp").
 *
 * Hacia el exterior expone sólo dos operaciones, como pide el Anexo: {@link #saltar()} y {@link #saltarWarp()}.
 * Las demás operaciones públicas son consultas que no cambian el motor ({@link #estaDisponible()},
 * {@link #getEstado()}). Cada operación se delega en el estado actual, que decide qué hacer, cobra el costo
 * y, si corresponde, cambia el estado: el motor no tiene condicionales sobre el estado en el que se encuentra.
 *
 * Ciclo de la maniobra warp (una llamada a saltarWarp() por turno):
 * Disponible -> Preparando salto (2 turnos, con 5 de energía) -> En warp (salta 2 casilleros y consume 5 de
 * energía) -> Enfriamiento (1 turno, suma 3 de desgaste) -> Disponible.
 *
 * Invariante: estado != null y suministro != null. El motor se crea en estado Disponible (Ficha de Inicio, punto 2).
 */
public class MotorWarp {

    /** Combustible que consume un salto normal (Anexo). */
    public static final int COMBUSTIBLE_SALTO_NORMAL = 2;
    /** Energía que consume el salto warp; también se verifica en cada turno de preparación (Anexo). */
    public static final int ENERGIA_SALTO_WARP = 5;
    /** Desgaste que genera el turno de enfriamiento posterior al salto warp (Anexo). */
    public static final int DESGASTE_ENFRIAMIENTO = 3;
    /** Turnos de preparación necesarios antes del salto warp (Anexo). */
    public static final int TURNOS_PREPARACION = 2;
    /** Casilleros que avanza un salto normal. */
    public static final int CASILLEROS_SALTO_NORMAL = 1;
    /** Casilleros que avanza un salto warp. */
    public static final int CASILLEROS_SALTO_WARP = 2;

    private final SuministroMotor suministro;
    private State estado;

    /**
     * @pre suministro != null.
     * @post estaDisponible() == true.
     */
    public MotorWarp(SuministroMotor suministro) {
        assert suministro != null : "El motor necesita acceso a los recursos de la nave.";
        this.suministro = suministro;
        this.estado = new DisponibleState(this);
        assert estaDisponible() : "Fallo postcondición: el motor debe crearse en Disponible.";
        assert invariante() : "Fallo invariante tras crear el motor.";
    }

    /**
     * Salto normal: si el motor está Disponible y hay combustible, consume {@link #COMBUSTIBLE_SALTO_NORMAL}
     * y la nave avanza {@link #CASILLEROS_SALTO_NORMAL} casillero. El motor sigue Disponible.
     *
     * @return casilleros que avanza la nave.
     * @throws EstadoMotorInvalidoException si el motor está en medio de una maniobra warp; nada cambia.
     * @throws RecursoInsuficienteException si no alcanza el combustible; nada cambia.
     */
    public int saltar() throws EstadoMotorInvalidoException, RecursoInsuficienteException {
        int casilleros = estado.saltar();
        assert casilleros == CASILLEROS_SALTO_NORMAL : "Fallo postcondición: el salto normal avanza un casillero.";
        assert invariante() : "Fallo invariante tras el salto normal.";
        return casilleros;
    }

    /**
     * Avanza un turno de la maniobra warp. El estado actual decide qué paso corresponde:
     *  - Disponible: comienza la preparación (primer turno de preparación);
     *  - Preparando salto: cuenta el turno si hay {@link #ENERGIA_SALTO_WARP} de energía; si no, sigue preparando;
     *  - En warp: si hay energía la consume y la nave avanza {@link #CASILLEROS_SALTO_WARP} casilleros;
     *    si ya no alcanza, el salto se aborta y el motor vuelve a preparar;
     *  - Enfriamiento: suma {@link #DESGASTE_ENFRIAMIENTO} de desgaste y el motor vuelve a Disponible.
     *
     * @return casilleros que avanza la nave en este turno (0 o 2).
     */
    public int saltarWarp() {
        int casilleros = estado.saltarWarp();
        assert casilleros == 0 || casilleros == CASILLEROS_SALTO_WARP : "Fallo postcondición: avance warp inválido.";
        assert invariante() : "Fallo invariante tras el turno de la maniobra warp.";
        return casilleros;
    }

    /**
     * Consulta delegada al estado actual: el motor no necesita preguntar en qué estado está.
     *
     * @return true si el motor está en Disponible y puede iniciar una nueva operación.
     */
    public boolean estaDisponible() {
        return estado.estaDisponible();
    }

    /**
     * Devuelve el nombre del estado actual ("Disponible", "Preparando salto", "En warp" o "Enfriamiento").
     * Devuelve un texto y no el objeto estado, para que nadie pueda operar el estado directamente y saltear
     * el ciclo.
     *
     * @return nombre del estado actual.
     */
    public String getEstado() {
        return estado.toString();
    }

    /**
     * Cambia el estado del motor. Es de paquete: sólo los estados lo invocan al completar un paso de la
     * maniobra, de modo que ningún cliente puede saltear el ciclo.
     *
     * @pre nuevoEstado != null.
     * @post el estado actual es nuevoEstado.
     */
    void setEstado(State nuevoEstado) {
        assert nuevoEstado != null : "El nuevo estado del motor no puede ser nulo.";
        estado = nuevoEstado;
        assert invariante() : "Fallo invariante tras cambiar el estado del motor.";
    }

    /**
     * @return los recursos de la nave; sólo los usan los estados (mismo paquete).
     */
    SuministroMotor getSuministro() {
        return suministro;
    }

    private boolean invariante() {
        return estado != null && suministro != null;
    }

    @Override
    public String toString() {
        return "MotorWarp [estado=" + estado + "]";
    }
}
