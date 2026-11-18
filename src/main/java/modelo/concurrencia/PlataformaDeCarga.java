package modelo.concurrencia;

import modelo.universo.Observable;
import modelo.universo.Observador;

/**
 * Recurso compartido del problema Productor-Consumidor (Patrón Monitor).
 * Coordina la exclusión mutua y la sincronización entre generadoras de energía
 * y naves consumidoras mediante métodos synchronized, bucles while de guarda,
 * wait() y notifyAll().
 *
 * Es un sujeto observable (patrón Observer, Paso 4 de la E2): extiende {@link Observable} de la cátedra y
 * publica cada evento de producción, espera, retiro y alta o baja de entidades con un mensaje descriptivo
 * (modelo push, con los mensajes de ejemplo del Enunciado). El stock no viaja en el mensaje: el observador lo
 * consulta con {@link #getBaterias()} (modelo pull). La plataforma no conoce las vistas ni Swing.
 *
 * Las notificaciones salen de los hilos de las generadoras y de las naves, mientras la vista puede suscribirse
 * o quitarse desde el hilo de eventos de Swing. Como la lista de observadores de la cátedra no admite accesos
 * concurrentes, agregar, quitar y notificar se redefinen como synchronized (el mismo monitor que ya tienen
 * tomado depositar y retirar al notificar), delegando en Observable sin cambiar su lógica.
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

    public PlataformaDeCarga(int capacidadMaxima) {
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad maxima de la plataforma debe ser mayor a 0.");
        }
        this.capacidadMaxima = capacidadMaxima;
        this.baterias = 0;
        assert invariante() : "Fallo invariante al inicializar PlataformaDeCarga.";
    }

    private boolean invariante() {
        return this.capacidadMaxima > 0 && this.baterias >= 0 && this.baterias <= this.capacidadMaxima;
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

    // --- Monitor (Productor-Consumidor) ---

    /**
     * Permite a una generadora de energía depositar una batería.
     * Si la plataforma está llena, el hilo se suspende con wait() hasta que se libere espacio.
     *
     * @param idGeneradora identificador de la generadora.
     * @throws InterruptedException si el hilo es interrumpido durante la espera.
     */
    public synchronized void depositar(String idGeneradora) throws InterruptedException {
        notificarObservadores("[" + idGeneradora + "] solicitando acceso a la Plataforma.");

        while (this.baterias >= this.capacidadMaxima) {
            notificarObservadores("[" + idGeneradora + "] esperando a que se libere espacio en la Plataforma.");
            wait();
        }

        this.baterias++;
        notificarObservadores("Batería producida por [" + idGeneradora + "].");
        notifyAll();

        assert invariante() : "Fallo invariante tras depositar en la plataforma.";
    }

    /**
     * Permite a una nave consumidora retirar una batería.
     * Si la plataforma está vacía, el hilo se suspende con wait() hasta que haya baterías disponibles.
     *
     * @param idNave identificador de la nave consumidora.
     * @throws InterruptedException si el hilo es interrumpido durante la espera.
     */
    public synchronized void retirar(String idNave) throws InterruptedException {
        notificarObservadores("[" + idNave + "] solicitando batería.");

        while (this.baterias == 0) {
            notificarObservadores("[" + idNave + "] esperando por falta de baterías en la Plataforma.");
            wait();
        }

        this.baterias--;
        notificarObservadores("Batería retirada y utilizada por [" + idNave + "].");
        notifyAll();

        assert invariante() : "Fallo invariante tras retirar de la plataforma.";
    }
}
