package modelo.concurrencia;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Recurso compartido del problema Productor-Consumidor (Patrón Monitor).
 * Coordina la exclusión mutua y la sincronización entre generadoras de energía
 * y naves consumidoras mediante métodos synchronized, bucles while de guarda,
 * wait() y notifyAll().
 *
 * Invariante: capacidadMaxima > 0 && 0 <= baterias && baterias <= capacidadMaxima.
 */
public class PlataformaDeCarga {
    public static final int CAPACIDAD_POR_DEFECTO = 5;

    private final int capacidadMaxima;
    private int baterias;
    private final List<ObservadorConcurrencia> observadores;

    public PlataformaDeCarga() {
        this(CAPACIDAD_POR_DEFECTO);
    }

    public PlataformaDeCarga(int capacidadMaxima) {
        if (capacidadMaxima <= 0) {
            throw new IllegalArgumentException("La capacidad maxima de la plataforma debe ser mayor a 0.");
        }
        this.capacidadMaxima = capacidadMaxima;
        this.baterias = 0;
        this.observadores = new CopyOnWriteArrayList<>();
        assert invariante() : "Fallo invariante al inicializar PlataformaDeCarga.";
    }
    
    private boolean invariante() {
        return this.capacidadMaxima > 0 && this.baterias >= 0 && this.baterias <= this.capacidadMaxima;
    }

    
    
    
    

    public void agregarObservador(ObservadorConcurrencia observador) {
        if (observador != null && !this.observadores.contains(observador)) {
            this.observadores.add(observador);
        }
    }

    public void eliminarObservador(ObservadorConcurrencia observador) {
        if (observador != null) {
            this.observadores.remove(observador);
        }
    }

    private void notificar(String mensaje) {
        for (ObservadorConcurrencia obs : this.observadores) {
            obs.notificarEvento(mensaje, this.baterias);
        }
    }
    
    
    
    

    public int getCapacidadMaxima() {
        return this.capacidadMaxima;
    }

    
    
    /**
     * Informa el ingreso de una nueva entidad al universo paralelo.
     */
    public synchronized void notificarIngreso(String tipo, String id) {
        notificar("¡Ingreso una nueva " + tipo + " [" + id + "] al universo paralelo!");
    }

    /**
     * Informa la detención y egreso de una entidad del universo paralelo.
     */
    public synchronized void notificarEgreso(String tipo, String id) {
        notificar("⛔ Hilo detenido: " + tipo + " [" + id + "] ha finalizado su ejecución.");
    }

    public synchronized int getBaterias() {
        return this.baterias;
    }

    /**
     * Permite a una generadora de energía depositar una batería.
     * Si la plataforma está llena, el hilo se suspende con wait() hasta que se libere espacio.
     *
     * @param idGeneradora identificador de la generadora.
     * @throws InterruptedException si el hilo es interrumpido durante la espera.
     */
    public synchronized void depositar(String idGeneradora) throws InterruptedException {
        notificar("[" + idGeneradora + "] solicitando acceso a la Plataforma.");

        while (this.baterias >= this.capacidadMaxima) {
            notificar("[" + idGeneradora + "] esperando a que se libere espacio en la Plataforma.");
            wait();
        }

        this.baterias++;
        notificar("Bateria producida y depositada por [" + idGeneradora + "] (" + this.baterias + "/" + this.capacidadMaxima + ").");
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
        notificar("[" + idNave + "] solicitando batería.");

        while (this.baterias == 0) {
            notificar("[" + idNave + "] esperando por falta de baterias en la Plataforma.");
            wait();
        }

        this.baterias--;
        notificar("Bateria retirada y utilizada por [" + idNave + "] (" + this.baterias + "/" + this.capacidadMaxima + ").");
        notifyAll();

        assert invariante() : "Fallo invariante tras retirar de la plataforma.";
    }
}
