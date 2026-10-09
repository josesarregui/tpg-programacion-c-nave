package modelo.mision;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
import excepcion.EstadoMotorInvalidoException;
import excepcion.NaveNoDisponibleException;
import excepcion.RecursoInsuficienteException;
import modelo.asistente.Asistente;
import modelo.bitacora.TipoEvento;
import modelo.nave.TipoRecurso;

import java.util.ArrayList;
import java.util.List;

/**
 * Misión simplificada de la Entrega 1 (E1-06), resuelta con el patrón Template Method.
 *
 * El método plantilla {@link #realizar()} es final y fija el ciclo, siempre en el mismo orden:
 * preparar -> ejecutar -> evaluar -> cerrar. Los cuatro pasos son privados y contienen todo lo que
 * es común a las misiones (verificar la nave, consumir los recursos, ordenar el salto y armar el informe).
 * Cada misión concreta sólo redefine sus pasos particulares (operaciones primitivas):
 * la energía adicional que consume, la acción que realiza y su condición de éxito (Ficha de Inicio, puntos 4 y 5).
 * Así, agregar una misión nueva no requiere modificar las existentes (Aclaración, Pedidos del diseño).
 *
 * La misión no conoce a la nave: toda consulta u orden pasa por el asistente al que fue encomendada
 * (Aclaración, R2 y R4), que además registra en su Bitácora los acontecimientos de la misión.
 * Cada objeto misión se encomienda a un único asistente y se realiza una sola vez: si dos naves hacen
 * el mismo tipo de misión, son dos misiones distintas (Aclaración, R4).
 *
 * Invariante:
 *  - codigo y nombre no nulos ni vacíos; etapa != null; acciones != null;
 *  - la misión tiene informe si y sólo si está cerrada.
 */
public abstract class Mision {

    /** Costo común de la operación controlada de toda misión (Ficha de Inicio, punto 5). */
    public static final int COMBUSTIBLE_CONSUMIDO = 4;
    public static final int DESGASTE_PRODUCIDO = 4;

    private final String codigo;
    private final String nombre;
    private final List<String> acciones = new ArrayList<>();
    private Asistente asistente;
    private EtapaMision etapa;
    private boolean exitosa;
    private InformeMision informe;

    /**
     * @pre codigo y nombre no nulos ni vacíos (los fija cada subclase con valores constantes).
     * @post La misión queda en etapa CREADA, sin asistente y sin informe.
     */
    protected Mision(String codigo, String nombre) {
        assert codigo != null && !codigo.isBlank() : "La misión debe tener un código.";
        assert nombre != null && !nombre.isBlank() : "La misión debe tener un nombre.";
        this.codigo = codigo;
        this.nombre = nombre;
        this.etapa = EtapaMision.CREADA;
        assert invariante() : "Fallo invariante tras crear la misión.";
    }

    // --- Operaciones primitivas: lo único que redefine cada misión concreta ---

    /**
     * @return energía que consume la misión al completar su objetivo (Ficha de Inicio, punto 5). Mayor o igual a 0.
     */
    protected abstract int getEnergiaAdicional();

    /**
     * Realiza la acción simplificada propia de la misión (Ficha de Inicio, punto 4).
     * Debe dejar constancia de lo hecho con {@link #registrarAccion(String)}.
     */
    protected abstract void realizarAccion();

    /**
     * @return true si se cumple la condición de éxito de la misión (Ficha de Inicio, punto 4).
     */
    protected abstract boolean objetivoCumplido();

    /**
     * @return texto de la condición de éxito, para las observaciones del informe.
     */
    protected abstract String getCondicionDeExito();

    // --- Asignación ---

    /**
     * Encomienda la misión a un asistente. Sólo la invoca {@link Asistente#encomendarMision(Mision)},
     * que antes registra esta misión como su misión pendiente (Aclaración, R2: toda orden pasa por el asistente).
     *
     * @pre asistente != null.
     * @pre La misión no fue encomendada antes (Aclaración, R4: una misión se encomienda a un asistente en particular).
     * @pre asistente.getMisionPendiente() == this (se encomienda a través del asistente).
     * @post getAsistente() == asistente.
     */
    public void asignarAsistente(Asistente asistente) {
        assert asistente != null : "La misión debe encomendarse a un asistente.";
        assert this.asistente == null : "La misión " + codigo + " ya fue encomendada a un asistente.";
        assert asistente.getMisionPendiente() == this
                : "La misión " + codigo + " debe encomendarse a través del asistente (encomendarMision).";
        this.asistente = asistente;
        assert this.asistente == asistente : "Fallo postcondición al asignar el asistente.";
        assert invariante() : "Fallo invariante tras asignar el asistente.";
    }

    // --- Template Method ---

    /**
     * Método plantilla: realiza el ciclo completo de la misión en el orden fijo
     * preparar -> ejecutar -> evaluar -> cerrar. Es final para que ninguna subclase altere el orden.
     *
     * Si la nave no está disponible o no tiene recursos suficientes, la misión se rechaza en la preparación,
     * antes de modificar nada: la nave no queda con cambios parciales y la misión sigue en etapa CREADA,
     * de modo que puede intentarse otra vez después de resolver el problema (Escenario B).
     *
     * Sólo la invoca {@link Asistente#ejecutarMision()}: así el asistente registra los rechazos y lleva
     * la cuenta de sus misiones (Aclaración, R2: toda orden pasa por el asistente).
     *
     * @pre La misión fue encomendada a un asistente y está en etapa CREADA (no se realizó antes).
     * @pre Su asistente la está ejecutando: asistente.estaEjecutando(this) (se realiza a través del asistente).
     * @post Si no se lanza excepción: getEtapa() == CERRADA y getInforme() != null.
     * @return el informe de la misión.
     * @throws NaveNoDisponibleException si la nave no está lista para operar.
     * @throws RecursoInsuficienteException si no alcanza el combustible o la energía que requiere la misión.
     */
    public final InformeMision realizar() throws NaveNoDisponibleException, RecursoInsuficienteException {
        assert asistente != null : "La misión " + codigo + " debe encomendarse a un asistente antes de realizarse.";
        assert asistente.estaEjecutando(this)
                : "La misión " + codigo + " debe realizarse a través de su asistente (ejecutarMision).";
        assert etapa == EtapaMision.CREADA : "La misión " + codigo + " ya fue realizada: cada misión se realiza una sola vez.";

        preparar();
        ejecutar();
        evaluar();
        cerrar();

        assert etapa == EtapaMision.CERRADA && informe != null : "Fallo postcondición: la misión no se cerró con su informe.";
        return informe;
    }

    /**
     * Paso 1: verifica que la nave esté lista para operar y que tenga los recursos que requiere la misión
     * (Aclaración, R4). No modifica la nave.
     *
     * @pre etapa == CREADA.
     * @post etapa == PREPARADA. Si se lanza excepción, nada cambia.
     */
    private void preparar() throws NaveNoDisponibleException, RecursoInsuficienteException {
        assert etapa == EtapaMision.CREADA : "La misión sólo puede prepararse una vez.";
        assert getEnergiaAdicional() >= 0 : "La energía adicional de la misión no puede ser negativa.";

        if (!asistente.naveListaParaOperar()) {
            throw new NaveNoDisponibleException(codigo, asistente.tieneTripulacion(),
                    asistente.requiereMantenimiento(), asistente.getEstadoMotor());
        }
        if (asistente.getCombustible() < COMBUSTIBLE_CONSUMIDO) {
            throw new RecursoInsuficienteException(TipoRecurso.COMBUSTIBLE, asistente.getCombustible(), COMBUSTIBLE_CONSUMIDO);
        }
        if (asistente.getEnergia() < getEnergiaAdicional()) {
            throw new RecursoInsuficienteException(TipoRecurso.ENERGIA, asistente.getEnergia(), getEnergiaAdicional());
        }

        etapa = EtapaMision.PREPARADA;
        registrarAccion("Preparación: la nave está disponible y tiene los recursos necesarios (combustible "
                + COMBUSTIBLE_CONSUMIDO + ", energía " + getEnergiaAdicional() + ").");
        assert invariante() : "Fallo invariante tras preparar la misión.";
    }

    /**
     * Paso 2: aplica el costo de la misión en un solo paso y realiza la acción propia de la misión.
     *
     * @pre etapa == PREPARADA (Observaciones: no se ejecuta sin preparación previa).
     * @post etapa == EJECUTADA; la nave consumió el combustible y la energía de la misión y sumó su desgaste.
     */
    private void ejecutar() throws RecursoInsuficienteException {
        assert etapa == EtapaMision.PREPARADA : "La misión no puede ejecutarse sin preparación previa.";

        try {
            asistente.consumirRecursos(COMBUSTIBLE_CONSUMIDO, getEnergiaAdicional(), DESGASTE_PRODUCIDO);
        } catch (CantidadInvalidaException | CapacidadExcedidaException e) {
            // No puede ocurrir: las cantidades son constantes positivas y preparar() verificó que la nave no
            // requiere mantenimiento (desgaste < 80), así que sumar 4 no supera el máximo de 100.
            // Si ocurriera, sería un error de programación (apunte de Excepciones: RuntimeException).
            throw new IllegalStateException("Error de programación al consumir los recursos de la misión " + codigo + ".", e);
        }
        realizarAccion();

        etapa = EtapaMision.EJECUTADA;
        assert invariante() : "Fallo invariante tras ejecutar la misión.";
    }

    /**
     * Paso 3: evalúa el resultado según la condición de éxito de la misión concreta.
     *
     * @pre etapa == EJECUTADA.
     * @post etapa == EVALUADA y el resultado quedó determinado.
     */
    private void evaluar() {
        assert etapa == EtapaMision.EJECUTADA : "La misión no puede evaluarse sin haberse ejecutado.";

        exitosa = objetivoCumplido();

        etapa = EtapaMision.EVALUADA;
        registrarAccion("Evaluación: " + (exitosa ? "objetivo cumplido." : "objetivo no cumplido."));
        assert invariante() : "Fallo invariante tras evaluar la misión.";
    }

    /**
     * Paso 4: si la misión fue exitosa, ordena el salto de la nave (Aclaración, R4); luego genera el informe.
     *
     * @pre etapa == EVALUADA (Observaciones: no se cierra sin resultado).
     * @post etapa == CERRADA y getInforme() != null.
     */
    private void cerrar() {
        assert etapa == EtapaMision.EVALUADA : "La misión no puede cerrarse sin resultado.";

        if (exitosa) {
            try {
                asistente.prepararSalto();
                asistente.saltar();
            } catch (EstadoMotorInvalidoException e) {
                // No puede ocurrir: preparar() verificó que la nave estuviera lista para operar (motor en Disponible)
                // y ejecutar() y evaluar() no cambian el motor. Si ocurriera, sería un error de programación
                // (apunte de Excepciones: RuntimeException), igual que en ejecutar().
                throw new IllegalStateException("Error de programación al ordenar el salto de la misión " + codigo + ".", e);
            }
            registrarAccion("Cierre: la nave preparó su salto y saltó.");
        } else {
            registrarAccion("Cierre: no se ordena el salto porque la misión no fue exitosa.");
        }

        String observaciones = "Condición de éxito: " + getCondicionDeExito()
                + (exitosa ? " (cumplida)." : " (no cumplida).");
        informe = new InformeMision(codigo + " — " + nombre, exitosa, acciones,
                COMBUSTIBLE_CONSUMIDO, getEnergiaAdicional(), DESGASTE_PRODUCIDO,
                asistente.getCombustible(), asistente.getEnergia(), asistente.getDesgaste(),
                asistente.getEstadoMotor(), asistente.naveListaParaOperar(), observaciones);

        etapa = EtapaMision.CERRADA;
        asistente.registrarEvento(TipoEvento.MISION, "Misión " + codigo + " cerrada. Resultado: "
                + (exitosa ? "exitosa." : "no exitosa."));
        assert informe != null : "Fallo postcondición: la misión no puede cerrarse sin informe.";
        assert invariante() : "Fallo invariante tras cerrar la misión.";
    }

    /**
     * Agrega una acción principal al informe y la registra en la Bitácora del asistente.
     * La usan los pasos comunes y las misiones concretas en {@link #realizarAccion()}.
     *
     * @pre descripcion no nula ni vacía.
     * @post La acción se agregó al final de las acciones de la misión.
     */
    protected final void registrarAccion(String descripcion) {
        assert descripcion != null && !descripcion.isBlank() : "La acción debe tener una descripción.";
        int cantidadAnterior = acciones.size();
        acciones.add(descripcion);
        asistente.registrarEvento(TipoEvento.MISION, codigo + " - " + descripcion);
        assert acciones.size() == cantidadAnterior + 1 : "Fallo postcondición al registrar la acción.";
    }

    // --- Consultas ---

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public EtapaMision getEtapa() {
        return etapa;
    }

    /**
     * @return el asistente al que fue encomendada, o null si todavía no se encomendó.
     */
    public Asistente getAsistente() {
        return asistente;
    }

    /**
     * @return el informe de la misión, o null si todavía no se cerró.
     */
    public InformeMision getInforme() {
        return informe;
    }

    private boolean invariante() {
        return codigo != null && !codigo.isBlank()
                && nombre != null && !nombre.isBlank()
                && etapa != null && acciones != null
                && (etapa == EtapaMision.CERRADA) == (informe != null);
    }

    @Override
    public String toString() {
        return codigo + " — " + nombre + " [" + etapa + "]";
    }
}
