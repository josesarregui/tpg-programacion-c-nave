package modelo.concurrencia;

import modelo.universo.Observable;
import modelo.universo.Observador;

/**
 * Recurso compartido del problema Productor-Consumidor, resuelto como monitor (Enunciado, Especificación Técnica de
 * Concurrencia, punto 3; Clase 10 - Concurrencia, estructura de Mi_Monitor). Encapsula el stock de baterías y la
 * capacidad máxima, que sólo se tocan desde sus métodos.
 *
 * Toda la lógica de sincronización está acá: las generadoras y las naves sólo piden depositar o retirar una batería.
 * Los métodos del monitor son synchronized (exclusión mutua: una sola entidad opera a la vez sobre la plataforma);
 * el que no puede seguir evalúa su condición de guarda en un while, espera con wait() (que libera el monitor) y, al
 * cambiar el estado, despierta a todos los que esperan con notifyAll(). Los hilos despertados no reciben el monitor:
 * compiten por él y vuelven a evaluar el while. Las condiciones de guarda son los métodos privados
 * {@link #estaLlena()} y {@link #estaVacia()}.
 *
 * Es un sujeto observable (patrón Observer, Paso 4 de la E2): extiende {@link Observable} de la cátedra y publica
 * cada producción, solicitud, espera, depósito, retiro y alta o baja de entidades con un mensaje descriptivo (modelo
 * push, con los mensajes de ejemplo del Enunciado). El stock no viaja en el mensaje: el observador lo consulta con
 * {@link #getBaterias()} (modelo pull). La plataforma no conoce las vistas ni Swing.
 *
 * Las notificaciones salen de los hilos de las generadoras y de las naves, mientras la vista puede suscribirse o
 * quitarse desde el hilo de eventos de Swing. Como la lista de observadores de la cátedra no admite accesos
 * concurrentes, agregar, quitar y notificar se redefinen como synchronized (el mismo monitor que ya tienen tomado
 * depositarBateria y retirarBateria al notificar), delegando en Observable sin cambiar su lógica.
 *
 * Invariante: capacidadMaxima > 0 && 0 <= baterias && baterias <= capacidadMaxima.
 */
public class PlataformaDeCarga extends Observable {
    public static final int CAPACIDAD_POR_DEFECTO = 5;

    private final int capacidadMaxima;
    private int baterias;

    public PlataformaDeCarga() {
        this(CAPACIDAD_POR_DEFECTO);
    }

    /**
     * @pre capacidadMaxima > 0 (si no, IllegalArgumentException).
     * @post La plataforma está vacía y tiene la capacidad indicada.
     */
    public PlataformaDeCarga(int capacidadMaxima) {
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad maxima de la plataforma debe ser mayor a 0.");
        }
        this.capacidadMaxima = capacidadMaxima;
        this.baterias = 0;
        assert invariante() : "Fallo invariante al inicializar PlataformaDeCarga.";
    }

    // --- Métodos privados del monitor ---

    private boolean invariante() {
        return this.capacidadMaxima > 0 && this.baterias >= 0 && this.baterias <= this.capacidadMaxima;
    }

    /** Condición de guarda del productor. Se invoca con el monitor tomado. */
    private boolean estaLlena() {
        return this.baterias == this.capacidadMaxima;
    }

    /** Condición de guarda del consumidor. Se invoca con el monitor tomado. */
    private boolean estaVacia() {
        return this.baterias == 0;
    }

    private static boolean identificadorValido(String id) {
        return id != null && !id.isBlank();
    }

    // --- Patrón Observer (E2, Paso 4) ---

    /**
     * @pre observador != null.
     * @post observador queda registrado una sola vez.
     */
    @Override
    public synchronized void agregarObservador(Observador observador) {
        assert observador != null : "El observador no puede ser nulo.";
        super.agregarObservador(observador);
    }

    @Override
    public synchronized void quitarObservador(Observador observador) {
        super.quitarObservador(observador);
    }

    /**
     * Notifica a los observadores con exclusión mutua respecto de las suscripciones. Cada observador debe
     * resolver rápido su actualización (una vista Swing la delega con invokeLater): se ejecuta con el monitor
     * de la plataforma tomado.
     */
    @Override
    public synchronized void notificarObservadores(String mensaje) {
        super.notificarObservadores(mensaje);
    }

    // --- Consultas (modelo pull) ---

    public int getCapacidadMaxima() {
        return this.capacidadMaxima;
    }

    public synchronized int getBaterias() {
        return this.baterias;
    }

    // --- Alta y baja de entidades ---

    /**
     * Informa el ingreso de una nueva entidad al universo paralelo.
     *
     * @pre tipo e id no son nulos.
     * @post Cada observador recibió "¡Ingresó una nueva tipo [id] al universo paralelo!".
     */
    public synchronized void notificarIngreso(String tipo, String id) {
        assert tipo != null && id != null : "El tipo y el identificador de la entidad no pueden ser nulos.";
        notificarObservadores("¡Ingresó una nueva " + tipo + " [" + id + "] al universo paralelo!");
    }

    /**
     * Informa la detención y egreso de una entidad del universo paralelo.
     *
     * @pre tipo e id no son nulos.
     */
    public synchronized void notificarEgreso(String tipo, String id) {
        assert tipo != null && id != null : "El tipo y el identificador de la entidad no pueden ser nulos.";
        notificarObservadores("⛔ Hilo detenido: " + tipo + " [" + id + "] ha finalizado su ejecución.");
    }

    // --- Métodos del monitor (Productor-Consumidor) ---

    /**
     * Ciclo del productor (Enunciado, punto 1): la generadora llega con la batería producida, solicita acceso, espera
     * mientras la plataforma esté llena, deposita y avisa a los demás hilos que el estado cambió.
     *
     * Mensajes, en orden: "Batería producida por [id].", "[id] solicitando acceso a la Plataforma.", "[id] esperando
     * a que se libere espacio en la Plataforma." (en cada evaluación del while que encuentra la plataforma llena) y
     * "Batería depositada por [id] en la Plataforma.".
     *
     * @param idGeneradora identificador de la generadora.
     * @pre idGeneradora no es nulo ni vacío.
     * @post baterias == stock al salir de la espera + 1.
     * @throws InterruptedException si el hilo es interrumpido mientras espera. El stock no cambia.
     */
    public synchronized void depositarBateria(String idGeneradora) throws InterruptedException {
        assert identificadorValido(idGeneradora) : "El identificador de la generadora no puede ser vacío.";
        notificarObservadores("Batería producida por [" + idGeneradora + "].");
        notificarObservadores("[" + idGeneradora + "] solicitando acceso a la Plataforma.");

        while (estaLlena()) {
            notificarObservadores("[" + idGeneradora + "] esperando a que se libere espacio en la Plataforma.");
            wait();
        }

        int previo = this.baterias;
        this.baterias++;
        notificarObservadores("Batería depositada por [" + idGeneradora + "] en la Plataforma.");
        notifyAll();

        assert this.baterias == previo + 1 : "Fallo postcondición: depositar debe sumar una batería.";
        assert invariante() : "Fallo invariante tras depositar en la plataforma.";
    }

    /**
     * Ciclo del consumidor (Enunciado, punto 2): la nave solicita una batería, espera mientras no haya, la retira
     * para consumirla y avisa a los demás hilos que el estado cambió.
     *
     * Mensajes, en orden: "[id] solicitando batería.", "[id] esperando por falta de baterías en la Plataforma." (en
     * cada evaluación del while que encuentra la plataforma vacía) y "Batería retirada y utilizada por [id].".
     *
     * @param idNave identificador de la nave consumidora.
     * @pre idNave no es nulo ni vacío.
     * @post baterias == stock al salir de la espera - 1.
     * @throws InterruptedException si el hilo es interrumpido mientras espera. El stock no cambia.
     */
    public synchronized void retirarBateria(String idNave) throws InterruptedException {
        assert identificadorValido(idNave) : "El identificador de la nave no puede ser vacío.";
        notificarObservadores("[" + idNave + "] solicitando batería.");

        while (estaVacia()) {
            notificarObservadores("[" + idNave + "] esperando por falta de baterías en la Plataforma.");
            wait();
        }

        int previo = this.baterias;
        this.baterias--;
        notificarObservadores("Batería retirada y utilizada por [" + idNave + "].");
        notifyAll();

        assert this.baterias == previo - 1 : "Fallo postcondición: retirar debe restar una batería.";
        assert invariante() : "Fallo invariante tras retirar de la plataforma.";
    }
}
