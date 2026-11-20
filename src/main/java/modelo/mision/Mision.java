package modelo.mision;

import excepcion.CantidadInvalidaException;
import excepcion.CapacidadExcedidaException;
import excepcion.EstadoMotorInvalidoException;
import excepcion.NaveNoDisponibleException;
import excepcion.RecursoInsuficienteException;
import modelo.asistente.Asistente;
import modelo.bitacora.TipoEvento;
import modelo.nave.TipoRecurso;
import modelo.universo.Destino;
import modelo.universo.ElementoDelUniverso;
import modelo.universo.Posicion;
import modelo.universo.Tablero;
import modelo.warp.MotorWarp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Misión de la Entrega 2, resuelta con el patrón Template Method (E1-06; Enunciado E2, "Ciclo de Vida de una
 * Misión").
 *
 * El método plantilla {@link #realizar()} es final y fija el ciclo, siempre en el mismo orden:
 * preparar -> ejecutar -> evaluar -> cerrar. Los cuatro pasos son privados y contienen lo común a toda misión:
 * <ul>
 *   <li><b>preparar</b> (Fase 1): verifica que la nave esté lista y tenga los recursos mínimos, fija el objetivo
 *       y calcula la ruta con el radar de largo alcance ({@link Tablero#verTablero()}).</li>
 *   <li><b>ejecutar</b> (Fase 2): cobra el consumo de despegue y recorre el tablero por turnos mientras la nave
 *       siga con vida y no haya llegado. Turno de la nave: inspección de la posición e interacción con el
 *       elemento, escaneo con el radar de corto alcance ({@link Tablero#observarAlrededores()}), planificación y
 *       salto a través del asistente (Anexo). Turno del tablero: {@link Destino#turnoDestino()}. Una pausa
 *       configurable por turno ({@code Thread.sleep}). Al llegar, verifica en destino y realiza la acción.</li>
 *   <li><b>evaluar</b> (Fase 3): determina el resultado y confecciona el {@link InformeMision} con el
 *       {@link BalanceRecursos}.</li>
 *   <li><b>cerrar</b> (Fase 4): si la misión fue exitosa, aplica a la nave lo ganado o entregado.</li>
 * </ul>
 * Cada misión concreta redefine sólo sus pasos particulares: las operaciones primitivas (objetivo, energía
 * inicial, acción al llegar y condición de éxito) y, si los necesita, los ganchos con implementación por
 * defecto (recursos a entregar, gemas obtenidas y cierre). Agregar una misión nueva no requiere modificar las
 * existentes (Aclaración, Pedidos del diseño).
 *
 * Costos (Anexo; Tabla de Recursos, IV.2 y IV.5): el despegue cobra el costo base del Anexo
 * ({@link #COMBUSTIBLE_DESPEGUE} de combustible y {@link #DESGASTE_DESPEGUE} de desgaste), la energía inicial de la
 * misión ({@link #getEnergiaInicial()}) y el escaneo de largo alcance ({@link #ENERGIA_ESCANEO_LARGO}); cada turno
 * cuesta un escaneo de corto alcance ({@link #ENERGIA_ESCANEO_CORTO}) por cada casilla que se avanza; cada recálculo,
 * {@link #ENERGIA_RECALCULO}; y cada salto, lo que cobra el Motor Warp (Anexo) más {@link #DESGASTE_POR_CELDA} por
 * casilla. La acción final no tiene costo: el Anexo pone la energía de cada misión al comenzar.
 *
 * Responsabilidades (Guía 4.2 y Aclaración, R2): la misión es el sistema de navegación. Lee los radares del
 * {@link Tablero}, decide con el {@link Navegador}, mueve la nave en el tablero y hace avanzar al {@link Destino}.
 * No conoce a la nave: toda consulta u orden pasa por el asistente al que fue encomendada, que además registra en
 * su Bitácora los acontecimientos. Cada objeto misión se encomienda a un único asistente y se realiza una vez.
 *
 * Invariante:
 *  - codigo y nombre no nulos ni vacíos; tablero, destino, etapa, acciones, recorrido y rutaPlanificada no nulos;
 *  - pausaPorTurno >= 0 y los acumuladores del balance >= 0;
 *  - la misión tiene informe si y sólo si está evaluada o cerrada.
 */
public abstract class Mision {

    /** Combustible del costo base obligatorio al poner en marcha cualquier misión (Anexo, consumo inicial). */
    public static final int COMBUSTIBLE_DESPEGUE = 4;
    /** Desgaste del costo base obligatorio al poner en marcha cualquier misión (Anexo, consumo inicial). */
    public static final int DESGASTE_DESPEGUE = 4;
    /** Escaneo de largo alcance para calcular la ruta; se cobra con el despegue (Tabla de Recursos, IV.2). */
    public static final int ENERGIA_ESCANEO_LARGO = 5;
    /** Escaneo de corto alcance, uno por cada casilla que se avanza (Tabla de Recursos, IV.2). */
    public static final int ENERGIA_ESCANEO_CORTO = 1;
    /** Recalcular la ruta cuando la próxima casilla es una amenaza (Tabla de Recursos, IV.2 y IV.3). */
    public static final int ENERGIA_RECALCULO = 2;
    /** Desgaste por casilla avanzada (Tabla de Recursos, IV.2; el Anexo no fija desgaste para el salto). */
    public static final int DESGASTE_POR_CELDA = 1;
    /** Pausa por defecto de cada turno, para que la vista muestre el avance (Enunciado E2, Nota técnica). */
    public static final int PAUSA_POR_TURNO_MS = 400;
    /**
     * Energía que se necesita, después del escaneo del turno, para completar una maniobra warp sin que sus propios
     * consumos destruyan la nave (Anexo y Tabla de Recursos, IV.2): el salto warp y un escaneo de corto alcance por
     * cada turno restante de preparación, por el turno del salto, por su segunda casilla y por el turno de
     * enfriamiento. La maniobra se inicia sólo con más energía que este valor, para que no quede en 0.
     */
    private static final int ENERGIA_MANIOBRA_WARP = MotorWarp.ENERGIA_SALTO_WARP
            + (MotorWarp.TURNOS_PREPARACION + 2) * ENERGIA_ESCANEO_CORTO;

    private final String codigo;
    private final String nombre;
    private final Tablero tablero;
    private final Destino destino;
    private final Navegador navegador;
    private final List<String> acciones = new ArrayList<>();
    private final List<Posicion> recorrido = new ArrayList<>();
    private LinkedList<Posicion> rutaPlanificada = new LinkedList<>();
    private Asistente asistente;
    private EtapaMision etapa;
    private int pausaPorTurno;
    private Posicion objetivo;
    private boolean llegoAlObjetivo;
    private boolean verificadaEnDestino;
    private boolean travesiaInterrumpida;
    private boolean exitosa;
    private InformeMision informe;

    // Acumuladores del balance (Fase 3).
    private int combustibleConsumido;
    private int energiaConsumida;
    private int desgasteProducido;
    private int combustiblePerdido;
    private int energiaPerdida;
    private int combustibleGanado;
    private int energiaGanada;

    /**
     * @pre codigo y nombre no nulos ni vacíos (los fija cada subclase con valores constantes).
     * @pre tablero != null y destino != null, y destino fue creado sobre ese tablero (Documentación de Destino y
     *      Tablero: el tablero mantiene el estado y Destino aplica los movimientos sobre él). Destino no expone su
     *      tablero, por eso esta precondición no puede verificarse.
     * @post La misión queda en etapa CREADA, sin asistente, sin objetivo, sin informe y con la pausa por defecto.
     */
    protected Mision(String codigo, String nombre, Tablero tablero, Destino destino) {
        assert codigo != null && !codigo.isBlank() : "La misión debe tener un código.";
        assert nombre != null && !nombre.isBlank() : "La misión debe tener un nombre.";
        assert tablero != null : "La misión necesita el tablero sobre el que navega.";
        assert destino != null : "La misión necesita el Destino que mueve los elementos del tablero.";
        this.codigo = codigo;
        this.nombre = nombre;
        this.tablero = tablero;
        this.destino = destino;
        this.navegador = new Navegador();
        this.etapa = EtapaMision.CREADA;
        this.pausaPorTurno = PAUSA_POR_TURNO_MS;
        assert invariante() : "Fallo invariante tras crear la misión.";
    }

    // --- Operaciones primitivas: lo que redefine cada misión concreta ---

    /**
     * Fija la coordenada objetivo de la misión (Enunciado E2, Fase 1).
     *
     * @pre tablero != null.
     * @post El valor retornado no es nulo y está dentro del tablero.
     */
    protected abstract Posicion fijarObjetivo(Tablero tablero);

    /**
     * @return energía específica que la misión consume al ponerse en marcha, además del costo base (Anexo, consumo
     *         inicial: M-01 5, M-02 10, M-03 0). Mayor o igual a 0.
     */
    protected abstract int getEnergiaInicial();

    /**
     * Realiza la acción propia de la misión al llegar al objetivo, después de superar la verificación en destino.
     * Debe dejar constancia de lo hecho con {@link #registrarAccion(String)}. Lo que gane o entregue la nave se
     * aplica recién en el cierre (Enunciado E2, Fases 2 y 4).
     */
    protected abstract void realizarAccion();

    /**
     * Es pública para que la vista describa cada misión por polimorfismo, sin condicionales de tipo.
     *
     * @return texto de la condición de éxito, para las observaciones del informe y la selección de misión.
     */
    public abstract String getCondicionDeExito();

    // --- Ganchos (hooks): implementación por defecto que algunas misiones redefinen ---

    /**
     * @return combustible que la misión debe entregar al llegar (M-01). Por defecto, 0. Mayor o igual a 0.
     */
    protected int getCombustibleAEntregar() {
        return 0;
    }

    /**
     * @return energía que la misión debe entregar al llegar (M-01). Por defecto, 0. Mayor o igual a 0.
     */
    protected int getEnergiaAEntregar() {
        return 0;
    }

    /**
     * @return gemas obtenidas al llegar (M-02), para el balance del informe. Por defecto, 0. Mayor o igual a 0.
     */
    protected int getGemasObtenidas() {
        return 0;
    }

    /**
     * Sólo se consulta en la evaluación de una misión exitosa, antes del cierre.
     *
     * @return combustible que el cierre recarga en la nave (M-03), para el balance del informe. Por defecto, 0.
     *         Mayor o igual a 0.
     */
    protected int getCombustibleARecargar() {
        return 0;
    }

    /**
     * Sólo se consulta en la evaluación de una misión exitosa, antes del cierre.
     *
     * @return energía que el cierre recarga en la nave (M-03), para el balance del informe. Por defecto, 0.
     *         Mayor o igual a 0.
     */
    protected int getEnergiaARecargar() {
        return 0;
    }

    /**
     * Sólo se consulta en la evaluación de una misión exitosa, antes del cierre.
     *
     * @return desgaste que el cierre repara en la nave (M-03), para el balance del informe. Por defecto, 0.
     *         Mayor o igual a 0.
     */
    protected int getDesgasteAReparar() {
        return 0;
    }

    /**
     * Aplica a la nave, a través del asistente, lo ganado o entregado al completar la misión (Enunciado E2,
     * Fase 4). Sólo se invoca si la misión fue exitosa. Por defecto no hace nada.
     *
     * @pre La misión fue exitosa y la nave no está destruida.
     */
    protected void aplicarCierre() {
    }

    // --- Asignación y configuración ---

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

    /**
     * Configura la pausa de cada turno de la travesía (Enunciado E2, Nota técnica). Las pruebas usan 0 para no
     * esperar; la vista, una pausa breve para mostrar el avance.
     *
     * @pre milisegundos >= 0 y la misión todavía no se realizó (etapa CREADA).
     * @post getPausaPorTurno() == milisegundos.
     */
    public void configurarPausaPorTurno(int milisegundos) {
        assert milisegundos >= 0 : "La pausa por turno no puede ser negativa.";
        assert etapa == EtapaMision.CREADA : "La pausa sólo puede configurarse antes de realizar la misión.";
        this.pausaPorTurno = milisegundos;
        assert this.pausaPorTurno == milisegundos : "Fallo postcondición al configurar la pausa.";
    }

    // --- Template Method ---

    /**
     * Método plantilla: realiza el ciclo completo de la misión en el orden fijo
     * preparar -> ejecutar -> evaluar -> cerrar. Es final para que ninguna subclase altere el orden.
     *
     * Si la nave no está disponible o no tiene los recursos mínimos, la misión se rechaza en la preparación, antes
     * de modificar nada: la nave y el tablero no quedan con cambios y la misión sigue en etapa CREADA, de modo que
     * puede intentarse otra vez después de resolver el problema (Escenario B).
     *
     * Sólo la invoca {@link Asistente#ejecutarMision()}: así el asistente registra los rechazos y lleva la cuenta
     * de sus misiones (Aclaración, R2). Puede tardar varios turnos: quien la lanza debe hacerlo en un hilo propio,
     * fuera del hilo de eventos de Swing (en la aplicación, el controlador de la misión en curso).
     *
     * @pre La misión fue encomendada a un asistente y está en etapa CREADA (no se realizó antes).
     * @pre Su asistente la está ejecutando: asistente.estaEjecutando(this).
     * @post Si no se lanza excepción: getEtapa() == CERRADA y getInforme() != null.
     * @return el informe de la misión.
     * @throws NaveNoDisponibleException si la nave no está lista para operar.
     * @throws RecursoInsuficienteException si no alcanzan el combustible o la energía mínimos de la misión.
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
     * Fase 1: verifica que la nave esté lista y tenga los recursos mínimos (Aclaración, R4), fija el objetivo y
     * calcula la ruta con el radar de largo alcance. El costo del escaneo se cobra como consumo de despegue al
     * comenzar la ejecución, para que un rechazo no deje cambios en la nave.
     *
     * Recursos mínimos: en energía, el despegue (escaneo de largo alcance y energía inicial), un escaneo corto y los
     * suministros a entregar; en combustible, el despegue, un salto normal y los suministros a entregar.
     *
     * @pre etapa == CREADA.
     * @post etapa == PREPARADA, getObjetivo() está dentro del tablero y la ruta planificada termina en él.
     *       Si se lanza excepción, nada cambia.
     */
    private void preparar() throws NaveNoDisponibleException, RecursoInsuficienteException {
        assert etapa == EtapaMision.CREADA : "La misión sólo puede prepararse una vez.";
        assert getEnergiaInicial() >= 0 && getCombustibleAEntregar() >= 0 && getEnergiaAEntregar() >= 0
                : "Los recursos de la misión no pueden ser negativos.";

        if (!asistente.naveListaParaOperar()) {
            throw new NaveNoDisponibleException(codigo, asistente.naveDestruida(), asistente.tieneTripulacion(),
                    asistente.requiereMantenimiento(), asistente.getEstadoMotor());
        }
        int combustibleMinimo = getCombustibleMinimo();
        int energiaMinima = getEnergiaMinima();
        if (asistente.getCombustible() < combustibleMinimo) {
            throw new RecursoInsuficienteException(TipoRecurso.COMBUSTIBLE, asistente.getCombustible(), combustibleMinimo);
        }
        if (asistente.getEnergia() < energiaMinima) {
            throw new RecursoInsuficienteException(TipoRecurso.ENERGIA, asistente.getEnergia(), energiaMinima);
        }

        objetivo = fijarObjetivo(tablero);
        assert objetivo != null && estaDentro(objetivo) : "El objetivo de la misión debe estar dentro del tablero.";
        Posicion partida = tablero.getPosicionNave();
        recorrido.add(partida);
        rutaPlanificada = new LinkedList<>(navegador.calcularRuta(tablero.verTablero(), partida, objetivo));

        etapa = EtapaMision.PREPARADA;
        registrarAccion("Preparación: nave disponible con los recursos mínimos (combustible " + combustibleMinimo
                + ", energía " + energiaMinima + "). Objetivo " + texto(objetivo) + "; ruta de "
                + rutaPlanificada.size() + " casillas calculada con el radar de largo alcance.");
        assert invariante() : "Fallo invariante tras preparar la misión.";
    }

    /**
     * Fase 2: cobra el consumo de despegue y recorre el tablero por turnos (ver la descripción de la clase). La
     * travesía termina cuando la nave llega al objetivo, cuando queda destruida (la misión se aborta de inmediato),
     * cuando no tiene recursos para avanzar, cuando se interrumpe el hilo o cuando se supera el máximo de turnos.
     * En los dos últimos casos, si hay una maniobra warp en curso, primero se completa (una vez iniciada, la maniobra
     * se completa), para que el motor quede Disponible y la nave pueda realizar otra misión.
     * Si la nave llegó y la travesía no se interrumpió, verifica que la nave conserve los recursos de la entrega
     * (sin descontarlos, eso se hace en el cierre) y, si los conserva, realiza la acción final. Una travesía
     * interrumpida no realiza la acción aunque la nave haya llegado en ese turno.
     *
     * @pre etapa == PREPARADA (Observaciones: no se ejecuta sin preparación previa).
     * @post etapa == EJECUTADA. Cada movimiento de la nave en el tablero fue a una casilla dentro del tablero y
     *       vecina de la anterior, o a dos casillas en la dirección de la ruta con un salto warp. Si la nave no fue
     *       destruida, el motor quedó Disponible.
     */
    private void ejecutar() {
        assert etapa == EtapaMision.PREPARADA : "La misión no puede ejecutarse sin preparación previa.";

        // La preparación verificó los mínimos y la nave lista para operar (desgaste < 80): el despegue no la destruye.
        int energiaDespegue = ENERGIA_ESCANEO_LARGO + getEnergiaInicial();
        consumir(COMBUSTIBLE_DESPEGUE, energiaDespegue, DESGASTE_DESPEGUE);
        registrarAccion("Despegue: puesta en marcha (" + COMBUSTIBLE_DESPEGUE + " de combustible, " + getEnergiaInicial()
                + " de energía y " + DESGASTE_DESPEGUE + " de desgaste) y escaneo de largo alcance ("
                + ENERGIA_ESCANEO_LARGO + " de energía).");
        assert !asistente.naveDestruida() : "Fallo: el despegue no puede destruir la nave.";

        int turno = 0;
        int maximoTurnos = tablero.getX() * tablero.getY();
        boolean puedeAvanzar = true;
        llegoAlObjetivo = tablero.getPosicionNave().equals(objetivo);
        // Sin recursos para avanzar sólo se queda la nave con el motor Disponible: puedeAvanzar no corta una maniobra.
        while (!asistente.naveDestruida() && !llegoAlObjetivo
                && (!asistente.motorDisponible() || (puedeAvanzar && !travesiaInterrumpida && turno < maximoTurnos))) {
            turno++;
            puedeAvanzar = turnoDeLaNave(turno);
            if (!asistente.naveDestruida()) {
                destino.turnoDestino();
                pausar();
            }
            llegoAlObjetivo = tablero.getPosicionNave().equals(objetivo);
        }

        if (llegoAlObjetivo && !asistente.naveDestruida() && !asistente.motorDisponible()) {
            completarManiobraWarp();
        }
        if (asistente.naveDestruida()) {
            registrarAccion("Ejecución abortada: la nave fue destruida y su tripulación murió.");
        } else if (travesiaInterrumpida) {
            registrarAccion("Ejecución abortada: la travesía se interrumpió.");
        } else if (llegoAlObjetivo) {
            verificarEnDestino();
        } else if (!puedeAvanzar) {
            registrarAccion("Ejecución abortada: la nave no tiene recursos para avanzar sin destruirse.");
        } else {
            registrarAccion("Ejecución abortada: se superó el máximo de " + maximoTurnos + " turnos.");
        }
        assert asistente.naveDestruida() || asistente.motorDisponible()
                : "Fallo postcondición: la travesía no puede terminar con una maniobra warp en curso.";

        etapa = EtapaMision.EJECUTADA;
        assert invariante() : "Fallo invariante tras ejecutar la misión.";
    }

    /**
     * Turno de la nave (Enunciado E2, Fase 2, A): inspección de la posición e interacción, escaneo de corto
     * alcance y planificación y salto.
     *
     * Con el motor Disponible, si el escaneo dejaría la energía en 0 (y destruiría la nave), la nave no escanea ni
     * avanza. Durante una maniobra warp siempre escanea: la maniobra sólo se inicia con energía para completarla.
     *
     * @pre !asistente.naveDestruida().
     * @return false si la nave no puede avanzar sin destruirse (sin energía para el escaneo, o sin combustible para
     *         un salto normal ni energía para completar la maniobra warp); true en otro caso.
     */
    private boolean turnoDeLaNave(int turno) {
        ElementoDelUniverso elemento = tablero.inspeccionarPosicion();
        if (elemento != null) {
            interactuarCon(elemento, turno);
        }
        boolean puedeAvanzar = true;
        if (!asistente.naveDestruida()) {
            if (asistente.motorDisponible() && asistente.getEnergia() <= ENERGIA_ESCANEO_CORTO) {
                puedeAvanzar = false;
            } else {
                ElementoDelUniverso[][] alrededores = escanearCortoAlcance();
                if (!asistente.naveDestruida()) {
                    puedeAvanzar = planificarYSaltar(alrededores, turno);
                }
            }
        }
        return puedeAvanzar;
    }

    /**
     * La nave coincide con un elemento: se resuelve por polimorfismo y la diferencia de recursos queda en el
     * balance como perdida o ganada.
     */
    private void interactuarCon(ElementoDelUniverso elemento, int turno) {
        registrarAccion("Turno " + turno + ": encuentro con " + elemento.getNombre() + " en "
                + texto(tablero.getPosicionNave()) + ".");
        int combustibleAntes = asistente.getCombustible();
        int energiaAntes = asistente.getEnergia();
        elemento.interactuarCon(asistente);
        int diferenciaCombustible = asistente.getCombustible() - combustibleAntes;
        int diferenciaEnergia = asistente.getEnergia() - energiaAntes;
        combustibleGanado += Math.max(diferenciaCombustible, 0);
        combustiblePerdido += Math.max(-diferenciaCombustible, 0);
        energiaGanada += Math.max(diferenciaEnergia, 0);
        energiaPerdida += Math.max(-diferenciaEnergia, 0);
    }

    /**
     * Radar de corto alcance (Tabla de Recursos, IV.2).
     *
     * @return la matriz de 3 x 3 centrada en la nave; null en casillas vacías o fuera del tablero.
     */
    private ElementoDelUniverso[][] escanearCortoAlcance() {
        consumir(0, ENERGIA_ESCANEO_CORTO, 0);
        return tablero.observarAlrededores();
    }

    /**
     * Elige el salto y mueve la nave (Anexo, "Navegación y Motor Warp"). Con el motor Disponible:
     * <ul>
     *   <li>si el radar de corto alcance detecta un obstáculo en la siguiente posición de la ruta y la energía alcanza
     *       para completar la maniobra warp sin destruir la nave ({@link #ENERGIA_MANIOBRA_WARP}), la inicia para
     *       esquivarlo;</li>
     *   <li>si no, salto normal si al consumir el combustible la nave no queda destruida (con un obstáculo y sin
     *       energía para el warp, antes recalcula la ruta: Tabla de Recursos, IV.3);</li>
     *   <li>sin combustible para el salto normal, inicia la maniobra warp si la energía alcanza para completarla.</li>
     * </ul>
     * Con una maniobra en curso, la continúa: sólo en el turno del salto la nave avanza dos casillas.
     *
     * @pre La nave no está en el objetivo y la ruta planificada parte de su posición.
     * @return false si no puede avanzar sin destruirse; true en otro caso.
     */
    private boolean planificarYSaltar(ElementoDelUniverso[][] alrededores, int turno) {
        assert !rutaPlanificada.isEmpty() && Navegador.sonVecinas(tablero.getPosicionNave(), rutaPlanificada.getFirst())
                : "La ruta planificada debe partir de la posición de la nave.";
        boolean puedeAvanzar = true;
        if (!asistente.motorDisponible()) {
            int casilleros = cobrarSalto(true);
            registrarAccion("Turno " + turno + ": maniobra warp (" + asistente.getEstadoMotor() + ").");
            avanzar(casilleros, alrededores, turno);
        } else if (hayAmenaza(alrededores, tablero.getPosicionNave(), rutaPlanificada.getFirst())
                && asistente.getEnergia() > ENERGIA_MANIOBRA_WARP) {
            registrarAccion("Turno " + turno + ": obstáculo detectado en " + texto(rutaPlanificada.getFirst())
                    + "; inicia la maniobra warp para esquivarlo.");
            cobrarSalto(true);
        } else if (asistente.getCombustible() > MotorWarp.COMBUSTIBLE_SALTO_NORMAL) {
            Posicion siguiente = proximoPaso(alrededores, turno);
            int casilleros = cobrarSalto(false);
            assert casilleros == MotorWarp.CASILLEROS_SALTO_NORMAL && siguiente != null;
            moverA(siguiente, turno, "salto normal");
        } else if (asistente.getEnergia() > ENERGIA_MANIOBRA_WARP) {
            cobrarSalto(true);
            registrarAccion("Turno " + turno + ": sin combustible para un salto normal; inicia la maniobra warp.");
        } else {
            puedeAvanzar = false;
        }
        return puedeAvanzar;
    }

    /**
     * Ordena el salto al asistente y lleva al balance lo que cobró el Motor Warp (Anexo).
     *
     * @return casilleros que debe avanzar la nave.
     */
    private int cobrarSalto(boolean warp) {
        int combustibleAntes = asistente.getCombustible();
        int energiaAntes = asistente.getEnergia();
        int desgasteAntes = asistente.getDesgaste();
        int casilleros;
        if (warp) {
            casilleros = asistente.saltarWarp();
        } else {
            try {
                casilleros = asistente.saltar();
            } catch (EstadoMotorInvalidoException | RecursoInsuficienteException e) {
                // No puede ocurrir: se verificó que el motor está Disponible y que el combustible alcanza.
                throw new IllegalStateException("Error de programación al ordenar el salto normal en " + codigo + ".", e);
            }
        }
        combustibleConsumido += combustibleAntes - asistente.getCombustible();
        energiaConsumida += energiaAntes - asistente.getEnergia();
        desgasteProducido += asistente.getDesgaste() - desgasteAntes;
        return casilleros;
    }

    /**
     * Avanza la nave los casilleros del salto warp. La primera casilla es la siguiente de la ruta y se pasa por
     * encima sin recalcular: así el salto esquiva el obstáculo que lo motivó (Anexo), y la nave sólo interactúa con
     * el elemento de la casilla en la que termina el turno. Cada casilla conserva su costo de escaneo y movimiento
     * (Tabla de Recursos, IV.2): la segunda se escanea antes de entrar y, si hay una amenaza, se recalcula. Si el
     * segundo avance no puede hacerse (la nave llegó o quedó destruida), no se cobra.
     */
    private void avanzar(int casilleros, ElementoDelUniverso[][] alrededores, int turno) {
        for (int i = 0; i < casilleros && !asistente.naveDestruida() && !tablero.getPosicionNave().equals(objetivo); i++) {
            if (i == 0) {
                moverA(rutaPlanificada.getFirst(), turno, "salto warp");
            } else {
                ElementoDelUniverso[][] lectura = escanearCortoAlcance();
                if (!asistente.naveDestruida()) {
                    moverA(proximoPaso(lectura, turno), turno, "salto warp");
                }
            }
        }
    }

    /**
     * Decide la próxima casilla según la ruta planificada. Si el radar de corto alcance detecta una amenaza en el
     * próximo paso, recalcula la ruta con el radar de largo alcance (Tabla de Recursos, IV.3), siempre que la
     * energía alcance sin destruir la nave; si no alcanza, avanza igual por la ruta.
     *
     * @pre La nave no está en el objetivo. La ruta planificada no está vacía y su primer paso es vecino de la nave
     *      (sólo la misión mueve la nave, y cada movimiento consume el primer paso de la ruta).
     * @post El valor retornado está dentro del tablero y es vecino de la posición de la nave.
     */
    private Posicion proximoPaso(ElementoDelUniverso[][] alrededores, int turno) {
        Posicion actual = tablero.getPosicionNave();
        assert !actual.equals(objetivo) : "La nave ya está en el objetivo.";
        assert !rutaPlanificada.isEmpty() && Navegador.sonVecinas(actual, rutaPlanificada.getFirst())
                : "La ruta planificada debe partir de la posición de la nave.";
        if (hayAmenaza(alrededores, actual, rutaPlanificada.getFirst())) {
            if (asistente.getEnergia() > ENERGIA_RECALCULO) {
                recalcularRuta(turno, "amenaza detectada en " + texto(rutaPlanificada.getFirst()));
            } else {
                registrarAccion("Turno " + turno + ": amenaza en la ruta, sin energía para recalcular.");
            }
        }
        Posicion siguiente = rutaPlanificada.getFirst();
        assert estaDentro(siguiente) && Navegador.sonVecinas(actual, siguiente)
                : "Fallo postcondición: la próxima casilla debe estar dentro del tablero y ser vecina.";
        return siguiente;
    }

    private boolean hayAmenaza(ElementoDelUniverso[][] alrededores, Posicion actual, Posicion siguiente) {
        ElementoDelUniverso elemento = alrededores[1 + siguiente.getX() - actual.getX()][1 + siguiente.getY() - actual.getY()];
        return elemento != null && elemento.esAmenaza() && !siguiente.equals(objetivo);
    }

    private void recalcularRuta(int turno, String motivo) {
        consumir(0, ENERGIA_RECALCULO, 0);
        rutaPlanificada = new LinkedList<>(navegador.calcularRuta(tablero.verTablero(), tablero.getPosicionNave(), objetivo));
        registrarAccion("Turno " + turno + ": recálculo de la ruta (" + motivo + "); quedan "
                + rutaPlanificada.size() + " casillas.");
    }

    /**
     * Mueve la nave en el tablero a la casilla indicada y cobra el desgaste del avance.
     *
     * @pre siguiente está dentro del tablero y es vecina de la posición de la nave (Documentación de Destino y
     *      Tablero: moverNave no comprueba los límites, la posición debe ser válida).
     * @post tablero.getPosicionNave().equals(siguiente) y la casilla quedó al final del recorrido.
     */
    private void moverA(Posicion siguiente, int turno, String tipoDeSalto) {
        assert estaDentro(siguiente) && Navegador.sonVecinas(tablero.getPosicionNave(), siguiente)
                : "La nave sólo puede moverse a una casilla vecina dentro del tablero.";
        tablero.moverNave(siguiente);
        recorrido.add(siguiente);
        rutaPlanificada.removeFirst();
        registrarAccion("Turno " + turno + ": " + tipoDeSalto + " a " + texto(siguiente) + ".");
        consumir(0, 0, DESGASTE_POR_CELDA);
        assert tablero.getPosicionNave().equals(siguiente) : "Fallo postcondición al mover la nave.";
    }

    /**
     * Si la nave llegó con el salto warp, el motor quedó en Enfriamiento: se completa la maniobra con el turno de
     * enfriamiento (Anexo), para que la nave termine con el motor Disponible y pueda iniciar otra misión.
     *
     * @pre La nave llegó al objetivo, no está destruida y el motor no está Disponible (sólo puede estar en
     *      Enfriamiento: la nave avanza únicamente con el motor Disponible o En warp, que pasa a Enfriamiento).
     * @post asistente.motorDisponible(), salvo que el desgaste del enfriamiento haya destruido la nave.
     */
    private void completarManiobraWarp() {
        int casilleros = cobrarSalto(true);
        assert casilleros == 0 : "El turno de enfriamiento no hace avanzar a la nave.";
        registrarAccion("Arribo: enfriamiento del Motor Warp (" + MotorWarp.DESGASTE_ENFRIAMIENTO + " de desgaste).");
        assert asistente.motorDisponible() || asistente.naveDestruida()
                : "Fallo postcondición: el motor debe quedar Disponible tras el enfriamiento.";
    }

    /**
     * Verificación en destino (Enunciado E2, Fase 2, punto 3): la nave debe conservar los recursos a entregar, sin
     * quedar destruida al entregarlos. Si los conserva, realiza la acción final (sin costo: la energía de la misión
     * se cobró al despegar, Anexo); si no, la misión resultará fallida. La entrega no se descuenta aquí, sino en el
     * cierre.
     */
    private void verificarEnDestino() {
        verificadaEnDestino = asistente.getCombustible() > getCombustibleAEntregar()
                && asistente.getEnergia() > getEnergiaAEntregar();
        if (verificadaEnDestino) {
            realizarAccion();
        } else {
            registrarAccion("Verificación en destino: la nave ya no conserva los recursos para la entrega"
                    + " (combustible " + getCombustibleAEntregar() + ", energía " + getEnergiaAEntregar() + ").");
        }
    }

    /**
     * Pausa del turno (Enunciado E2, Nota técnica). Si el hilo se interrumpe, restaura el estado de interrupción
     * para que lo vea quien lanzó la misión y marca la travesía como interrumpida: termina en cuanto el motor esté
     * Disponible (los turnos que faltan para completar una maniobra warp ya no duermen) y la misión resultará
     * fallida (ver {@link #ejecutar()}).
     */
    private void pausar() {
        try {
            Thread.sleep(pausaPorTurno);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            travesiaInterrumpida = true;
        }
    }

    /**
     * Aplica un costo de la misión a través del asistente y lo lleva al balance.
     *
     * @pre Las cantidades son constantes no negativas y se verificó antes que el recurso alcanza; el desgaste se
     *      cobra de a una casilla, de modo que llegar a 100 destruye la nave sin superar el máximo, salvo el del
     *      despegue, que se cobra con la nave lista para operar (desgaste menor que 80).
     */
    private void consumir(int combustible, int energia, int desgaste) {
        try {
            asistente.consumirRecursos(combustible, energia, desgaste);
        } catch (CantidadInvalidaException | RecursoInsuficienteException | CapacidadExcedidaException e) {
            // No puede ocurrir si se cumple la precondición: sería un error de programación
            // (apunte de Excepciones: RuntimeException).
            throw new IllegalStateException("Error de programación al consumir recursos en la misión " + codigo + ".", e);
        }
        combustibleConsumido += combustible;
        energiaConsumida += energia;
        desgasteProducido += desgaste;
    }

    /**
     * Fase 3: determina el resultado y confecciona el informe final con el balance de recursos. Las gemas, los
     * suministros y la recarga y reparación en la base sólo figuran si la misión fue exitosa, porque sólo entonces
     * el cierre los aplica.
     *
     * @pre etapa == EJECUTADA.
     * @post etapa == EVALUADA y getInforme() != null; es exitosa sólo si la nave no está destruida, llegó al
     *       objetivo y superó la verificación en destino.
     */
    private void evaluar() {
        assert etapa == EtapaMision.EJECUTADA : "La misión no puede evaluarse sin haberse ejecutado.";

        exitosa = !asistente.naveDestruida() && llegoAlObjetivo && verificadaEnDestino;
        registrarAccion("Evaluación: " + (exitosa ? "objetivo cumplido." : "objetivo no cumplido."));

        BalanceRecursos balance = new BalanceRecursos(combustibleConsumido, energiaConsumida, desgasteProducido,
                combustiblePerdido, energiaPerdida, combustibleGanado, energiaGanada,
                exitosa ? getGemasObtenidas() : 0,
                exitosa ? getCombustibleAEntregar() : 0,
                exitosa ? getEnergiaAEntregar() : 0,
                exitosa ? getCombustibleARecargar() : 0,
                exitosa ? getEnergiaARecargar() : 0,
                exitosa ? getDesgasteAReparar() : 0);
        String observaciones = "Condición de éxito: " + getCondicionDeExito()
                + (exitosa ? " (cumplida)." : " (no cumplida).") + " Balance: " + balance + ".";
        informe = new InformeMision(codigo + " — " + nombre, exitosa, acciones, objetivo, recorrido, balance,
                asistente.getCombustible(), asistente.getEnergia(), asistente.getDesgaste(),
                asistente.getEstadoMotor(), asistente.naveListaParaOperar(), observaciones);

        etapa = EtapaMision.EVALUADA;
        assert informe != null : "Fallo postcondición: la evaluación debe confeccionar el informe.";
        assert invariante() : "Fallo invariante tras evaluar la misión.";
    }

    /**
     * Fase 4: si la misión fue exitosa, aplica a la nave lo ganado o entregado (gancho {@link #aplicarCierre()});
     * deja constancia del cierre en la Bitácora del asistente.
     *
     * @pre etapa == EVALUADA (Observaciones: no se cierra sin resultado).
     * @post etapa == CERRADA.
     */
    private void cerrar() {
        assert etapa == EtapaMision.EVALUADA : "La misión no puede cerrarse sin resultado.";

        if (exitosa) {
            aplicarCierre();
        }
        etapa = EtapaMision.CERRADA;
        asistente.registrarEvento(TipoEvento.MISION, "Misión " + codigo + " cerrada. Resultado: "
                + (exitosa ? "exitosa." : "no exitosa."));
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
     * Combustible mínimo para iniciar la misión (Fase 1): el despegue y un salto normal (Anexo) más los suministros a
     * entregar. Permite que la vista muestre la factibilidad sin repetir el cálculo.
     *
     * @post El valor retornado es mayor que 0.
     */
    public int getCombustibleMinimo() {
        return COMBUSTIBLE_DESPEGUE + MotorWarp.COMBUSTIBLE_SALTO_NORMAL + getCombustibleAEntregar();
    }

    /**
     * Energía mínima para iniciar la misión (Fase 1): el despegue (escaneo de largo alcance y energía inicial de la
     * misión, Anexo), un escaneo de corto alcance y los suministros a entregar (Tabla de Recursos, IV).
     *
     * @post El valor retornado es mayor que 0.
     */
    public int getEnergiaMinima() {
        return ENERGIA_ESCANEO_LARGO + getEnergiaInicial() + ENERGIA_ESCANEO_CORTO + getEnergiaAEntregar();
    }

    /**
     * @return pausa de cada turno de la travesía, en milisegundos.
     */
    public int getPausaPorTurno() {
        return pausaPorTurno;
    }

    /**
     * @return coordenada objetivo, o null si la misión todavía no se preparó.
     */
    public Posicion getObjetivo() {
        return objetivo;
    }

    /**
     * @return posiciones que ocupó la nave durante la misión, desde la de partida (lista de sólo lectura).
     */
    public List<Posicion> getRuta() {
        return Collections.unmodifiableList(recorrido);
    }

    /**
     * @return el asistente al que fue encomendada, o null si todavía no se encomendó.
     */
    public Asistente getAsistente() {
        return asistente;
    }

    /**
     * @return el informe de la misión, o null si todavía no se evaluó.
     */
    public InformeMision getInforme() {
        return informe;
    }

    private boolean estaDentro(Posicion posicion) {
        return Navegador.estaDentro(posicion, tablero.getX(), tablero.getY());
    }

    private static String texto(Posicion posicion) {
        return "(" + posicion.getX() + ", " + posicion.getY() + ")";
    }

    private boolean invariante() {
        boolean conInforme = etapa == EtapaMision.EVALUADA || etapa == EtapaMision.CERRADA;
        return codigo != null && !codigo.isBlank() && nombre != null && !nombre.isBlank()
                && tablero != null && destino != null && etapa != null
                && acciones != null && recorrido != null && rutaPlanificada != null && pausaPorTurno >= 0
                && combustibleConsumido >= 0 && energiaConsumida >= 0 && desgasteProducido >= 0
                && combustiblePerdido >= 0 && energiaPerdida >= 0 && combustibleGanado >= 0 && energiaGanada >= 0
                && conInforme == (informe != null);
    }

    @Override
    public String toString() {
        return codigo + " — " + nombre + " [" + etapa + "]";
    }
}
