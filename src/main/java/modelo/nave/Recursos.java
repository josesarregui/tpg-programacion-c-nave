package modelo.nave;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
import excepcion.RecursoInsuficienteException;

/**
 * Recursos de una nave: combustible, energía, desgaste y necesidad de mantenimiento
 * (E1-09 y Ficha de Inicio, punto 2).
 *
 * La clase no es pública: sólo la usa {@link Nave}, que delega en ella las operaciones
 * sobre recursos. Así nadie puede modificar los recursos sin pasar por la nave.
 *
 * Invariante:
 *  - 0 <= combustible <= CAPACIDAD_MAXIMA_COMBUSTIBLE;
 *  - 0 <= energia <= CAPACIDAD_MAXIMA_ENERGIA;
 *  - 0 <= desgaste <= DESGASTE_MAXIMO.
 *
 * Toda operación que violaría el invariante se rechaza con una excepción antes de modificar
 * cualquier valor, de modo que nunca quedan cambios parciales.
 */
class Recursos {

    static final int CAPACIDAD_MAXIMA_COMBUSTIBLE = 100;
    static final int CAPACIDAD_MAXIMA_ENERGIA = 100;
    static final int DESGASTE_MAXIMO = 100;
    static final int UMBRAL_MANTENIMIENTO = 80;

    private int combustible;
    private int energia;
    private int desgaste;

    /**
     * Crea los recursos con su configuración inicial.
     * Los valores los fija cada tipo de nave con sus constantes, por eso un valor fuera de rango
     * sólo puede deberse a un error de programación y se verifica con aserciones.
     *
     * @pre 0 <= combustibleInicial <= CAPACIDAD_MAXIMA_COMBUSTIBLE.
     * @pre 0 <= energiaInicial <= CAPACIDAD_MAXIMA_ENERGIA.
     * @pre 0 <= desgasteInicial <= DESGASTE_MAXIMO.
     * @post Los recursos tienen los valores indicados y cumplen el invariante.
     */
    Recursos(int combustibleInicial, int energiaInicial, int desgasteInicial) {
        assert combustibleInicial >= 0 && combustibleInicial <= CAPACIDAD_MAXIMA_COMBUSTIBLE : "Combustible inicial fuera de rango.";
        assert energiaInicial >= 0 && energiaInicial <= CAPACIDAD_MAXIMA_ENERGIA : "Energía inicial fuera de rango.";
        assert desgasteInicial >= 0 && desgasteInicial <= DESGASTE_MAXIMO : "Desgaste inicial fuera de rango.";

        this.combustible = combustibleInicial;
        this.energia = energiaInicial;
        this.desgaste = desgasteInicial;

        assert invariante() : "Fallo invariante tras crear los recursos.";
    }

    int getCombustible() {
        return combustible;
    }

    int getEnergia() {
        return energia;
    }

    int getDesgaste() {
        return desgaste;
    }

    /**
     * @post Retorna true si desgaste >= UMBRAL_MANTENIMIENTO.
     */
    boolean requiereMantenimiento() {
        return desgaste >= UMBRAL_MANTENIMIENTO;
    }

    /**
     * Carga combustible.
     *
     * @param cantidad combustible a cargar.
     * @throws CantidadInvalidaException si la cantidad es negativa.
     * @throws CapacidadExcedidaException si la carga supera CAPACIDAD_MAXIMA_COMBUSTIBLE.
     * @post combustible == combustible anterior + cantidad. Si se lanza una excepción, nada cambia.
     */
    void cargarCombustible(int cantidad) throws CantidadInvalidaException, CapacidadExcedidaException {
        validarCantidad(TipoRecurso.COMBUSTIBLE, cantidad);
        // Se compara contra el espacio libre (máximo - actual) y no contra (actual + cantidad):
        // con cantidades muy grandes la suma de dos int se desborda y da un número negativo.
        if (cantidad > CAPACIDAD_MAXIMA_COMBUSTIBLE - combustible) {
            throw new CapacidadExcedidaException(TipoRecurso.COMBUSTIBLE, combustible, cantidad, CAPACIDAD_MAXIMA_COMBUSTIBLE);
        }
        int combustibleAnterior = combustible;
        combustible = combustible + cantidad;
        assert combustible == combustibleAnterior + cantidad : "Fallo postcondición al cargar combustible.";
        assert invariante() : "Fallo invariante tras cargar combustible.";
    }

    /**
     * Carga energía.
     *
     * @param cantidad energía a cargar.
     * @throws CantidadInvalidaException si la cantidad es negativa.
     * @throws CapacidadExcedidaException si la carga supera CAPACIDAD_MAXIMA_ENERGIA.
     * @post energia == energia anterior + cantidad. Si se lanza una excepción, nada cambia.
     */
    void cargarEnergia(int cantidad) throws CantidadInvalidaException, CapacidadExcedidaException {
        validarCantidad(TipoRecurso.ENERGIA, cantidad);
        if (cantidad > CAPACIDAD_MAXIMA_ENERGIA - energia) {
            throw new CapacidadExcedidaException(TipoRecurso.ENERGIA, energia, cantidad, CAPACIDAD_MAXIMA_ENERGIA);
        }
        int energiaAnterior = energia;
        energia = energia + cantidad;
        assert energia == energiaAnterior + cantidad : "Fallo postcondición al cargar energía.";
        assert invariante() : "Fallo invariante tras cargar energía.";
    }

    /**
     * Aplica el costo de una operación: consume combustible y energía y suma desgaste.
     * Primero verifica las tres condiciones y recién después modifica los valores, de modo que
     * una operación rechazada no deja cambios parciales (Ficha de Inicio, punto 2; Escenario B).
     *
     * @param combustibleConsumido combustible que consume la operación.
     * @param energiaConsumida energía que consume la operación.
     * @param desgasteProducido desgaste que produce la operación.
     * @throws CantidadInvalidaException si alguna cantidad es negativa.
     * @throws RecursoInsuficienteException si no hay combustible o energía suficientes.
     * @throws CapacidadExcedidaException si el desgaste superaría DESGASTE_MAXIMO.
     * @post combustible y energia disminuyen y desgaste aumenta en las cantidades indicadas.
     *       Si se lanza una excepción, nada cambia.
     */
    void consumir(int combustibleConsumido, int energiaConsumida, int desgasteProducido)
            throws CantidadInvalidaException, RecursoInsuficienteException, CapacidadExcedidaException {
        validarCantidad(TipoRecurso.COMBUSTIBLE, combustibleConsumido);
        validarCantidad(TipoRecurso.ENERGIA, energiaConsumida);
        validarCantidad(TipoRecurso.DESGASTE, desgasteProducido);
        if (combustibleConsumido > combustible) {
            throw new RecursoInsuficienteException(TipoRecurso.COMBUSTIBLE, combustible, combustibleConsumido);
        }
        if (energiaConsumida > energia) {
            throw new RecursoInsuficienteException(TipoRecurso.ENERGIA, energia, energiaConsumida);
        }
        if (desgasteProducido > DESGASTE_MAXIMO - desgaste) {
            throw new CapacidadExcedidaException(TipoRecurso.DESGASTE, desgaste, desgasteProducido, DESGASTE_MAXIMO);
        }

        int combustibleAnterior = combustible;
        int energiaAnterior = energia;
        int desgasteAnterior = desgaste;
        combustible = combustible - combustibleConsumido;
        energia = energia - energiaConsumida;
        desgaste = desgaste + desgasteProducido;

        assert combustible == combustibleAnterior - combustibleConsumido : "Fallo postcondición al consumir combustible.";
        assert energia == energiaAnterior - energiaConsumida : "Fallo postcondición al consumir energía.";
        assert desgaste == desgasteAnterior + desgasteProducido : "Fallo postcondición al aumentar el desgaste.";
        assert invariante() : "Fallo invariante tras consumir recursos.";
    }

    /**
     * Suma desgaste producido por el recorrido (por ejemplo, el enfriamiento del Motor Warp). A diferencia de
     * {@link #consumir(int, int, int)}, no se rechaza si supera el máximo: el desgaste queda en DESGASTE_MAXIMO,
     * porque alcanzarlo es una consecuencia del recorrido (la nave se destruye, Enunciado E2) y no una
     * operación que pueda evitarse.
     *
     * @pre cantidad >= 0.
     * @post desgaste == min(desgaste anterior + cantidad, DESGASTE_MAXIMO).
     */
    void sumarDesgaste(int cantidad) {
        assert cantidad >= 0 : "El desgaste a sumar no puede ser negativo.";
        // Se compara contra el espacio libre, como en las cargas, para que la suma no se desborde.
        boolean alcanzaElMaximo = cantidad >= DESGASTE_MAXIMO - desgaste;
        int desgasteAnterior = desgaste;
        desgaste = alcanzaElMaximo ? DESGASTE_MAXIMO : desgaste + cantidad;
        assert alcanzaElMaximo ? desgaste == DESGASTE_MAXIMO : desgaste == desgasteAnterior + cantidad
                : "Fallo postcondición al sumar desgaste.";
        assert invariante() : "Fallo invariante tras sumar desgaste.";
    }

    /**
     * Descuenta energía por una consecuencia del recorrido (impacto de un asteroide, Enunciado E2). A diferencia
     * de {@link #consumir(int, int, int)}, no se rechaza si no alcanza: la energía queda en 0 (nave inoperativa).
     *
     * @pre cantidad >= 0.
     * @post energia == max(energia anterior - cantidad, 0).
     */
    void descontarEnergia(int cantidad) {
        assert cantidad >= 0 : "La energía a descontar no puede ser negativa.";
        int energiaAnterior = energia;
        energia = cantidad >= energia ? 0 : energia - cantidad;
        assert energia == Math.max(energiaAnterior - cantidad, 0) : "Fallo postcondición al descontar energía.";
        assert invariante() : "Fallo invariante tras descontar energía.";
    }

    /**
     * Reabastece combustible y energía (estación de carga, Enunciado E2). A diferencia de las cargas, no se
     * rechaza si supera la capacidad: cada recurso queda, como máximo, en su capacidad máxima.
     *
     * @pre cantidadCombustible >= 0 y cantidadEnergia >= 0.
     * @post combustible == min(combustible anterior + cantidadCombustible, CAPACIDAD_MAXIMA_COMBUSTIBLE) y
     *       energia == min(energia anterior + cantidadEnergia, CAPACIDAD_MAXIMA_ENERGIA).
     */
    void reabastecer(int cantidadCombustible, int cantidadEnergia) {
        assert cantidadCombustible >= 0 : "El combustible a reabastecer no puede ser negativo.";
        assert cantidadEnergia >= 0 : "La energía a reabastecer no puede ser negativa.";
        int combustibleAnterior = combustible;
        int energiaAnterior = energia;
        // Se compara contra el espacio libre, como en las cargas, para que la suma no se desborde.
        combustible = cantidadCombustible >= CAPACIDAD_MAXIMA_COMBUSTIBLE - combustible
                ? CAPACIDAD_MAXIMA_COMBUSTIBLE : combustible + cantidadCombustible;
        energia = cantidadEnergia >= CAPACIDAD_MAXIMA_ENERGIA - energia
                ? CAPACIDAD_MAXIMA_ENERGIA : energia + cantidadEnergia;
        assert combustible == (int) Math.min((long) combustibleAnterior + cantidadCombustible, CAPACIDAD_MAXIMA_COMBUSTIBLE)
                : "Fallo postcondición al reabastecer combustible.";
        assert energia == (int) Math.min((long) energiaAnterior + cantidadEnergia, CAPACIDAD_MAXIMA_ENERGIA)
                : "Fallo postcondición al reabastecer energía.";
        assert invariante() : "Fallo invariante tras reabastecer.";
    }

    /**
     * Realiza el mantenimiento: lleva el desgaste a 0 (Ficha de Inicio, punto 2).
     * Puede hacerse aunque la nave todavía no lo requiera.
     *
     * @post desgaste == 0 y requiereMantenimiento() == false.
     */
    void realizarMantenimiento() {
        desgaste = 0;
        assert desgaste == 0 && !requiereMantenimiento() : "Fallo postcondición al realizar el mantenimiento.";
        assert invariante() : "Fallo invariante tras realizar el mantenimiento.";
    }

    private void validarCantidad(TipoRecurso recurso, int cantidad) throws CantidadInvalidaException {
        if (cantidad < 0) {
            throw new CantidadInvalidaException(recurso, cantidad);
        }
    }

    private boolean invariante() {
        return combustible >= 0 && combustible <= CAPACIDAD_MAXIMA_COMBUSTIBLE
                && energia >= 0 && energia <= CAPACIDAD_MAXIMA_ENERGIA
                && desgaste >= 0 && desgaste <= DESGASTE_MAXIMO;
    }

    @Override
    public String toString() {
        return "combustible=" + combustible + ", energía=" + energia + ", desgaste=" + desgaste;
    }
}
