package controlador.concurrencia;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import modelo.concurrencia.GeneradoraDeEnergia;
import modelo.concurrencia.NaveConsumidora;
import modelo.concurrencia.PlataformaDeCarga;
import vista.concurrencia.IVistaConcurrencia;

/**
 * Controlador del módulo de Concurrencia (Visor del Universo Paralelo). Sigue
 * el mismo lineamiento que {@link controlador.nave.ControladorDetallesNave}:
 * trabaja contra la interfaz {@link IVistaConcurrencia} y le entrega la
 * plataforma que debe observar; la suscripción la hace la propia vista
 * (patrón Observer: el observador se vincula con el observado).
 *
 * Implementa {@link ActionListener} para capturar los eventos del usuario
 * (MVC). Crea en tiempo de ejecución las tareas {@link Runnable}
 * ({@link GeneradoraDeEnergia}, {@link NaveConsumidora}), todas con la misma
 * {@link PlataformaDeCarga}, y lanza cada una en su propio {@link Thread} con
 * start(). Asocia el identificador de cada entidad con su hilo, de modo que
 * detenerla (doble clic sobre la lista, o al cerrar sesión) no depende del
 * nombre del hilo.
 *
 * Detener una entidad es interrumpir su hilo y esperar con join (con tiempo
 * límite) a que salga de run(); recién entonces se publica su egreso. Los hilos
 * nunca toman el candado de este controlador y la vista sólo encola con
 * invokeLater, por eso esperar desde el hilo de eventos no produce un bloqueo
 * mutuo. El controlador captura las excepciones de creación y de detención
 * total y delega en la vista la presentación del mensaje.
 */
public class ControladorConcurrencia implements ActionListener {
    /** Tiempo máximo que se espera a que un hilo interrumpido termine antes de informar su egreso. */
    public static final long ESPERA_MAXIMA_DETENCION_MS = 2000;

    private static final String TIPO_GENERADORA = "Generadora de Energía";
    private static final String TIPO_NAVE = "Nave Consumidora";

    private final IVistaConcurrencia vista;
    private final PlataformaDeCarga plataforma;

    /** Identificador de cada generadora activa y el hilo que la ejecuta, en orden de creación. */
    private final Map<String, Thread> generadoras;
    /** Identificador de cada nave activa y el hilo que la ejecuta, en orden de creación. */
    private final Map<String, Thread> naves;

    private int contadorGeneradoras;
    private int contadorNaves;

    public ControladorConcurrencia(IVistaConcurrencia vista) {
        this(vista, new PlataformaDeCarga());
    }

    public ControladorConcurrencia(IVistaConcurrencia vista, PlataformaDeCarga plataforma) {
        if (vista == null) {
            throw new IllegalArgumentException("La vista de concurrencia no puede ser nula.");
        }
        if (plataforma == null) {
            throw new IllegalArgumentException("La plataforma de carga no puede ser nula.");
        }
        this.vista = vista;
        this.plataforma = plataforma;
        this.generadoras = new LinkedHashMap<>();
        this.naves = new LinkedHashMap<>();
        this.contadorGeneradoras = 1;
        this.contadorNaves = 1;

        // Patron Observer: la vista se suscribe a la plataforma antes de que exista algún hilo.
        this.vista.observar(this.plataforma);

        this.vista.setControlador(this);
        this.vista.setOyenteDetenerGeneradora(this::detenerGeneradora);
        this.vista.setOyenteDetenerNave(this::detenerNave);

        this.vista.actualizarBaterias(this.plataforma.getBaterias(), this.plataforma.getCapacidadMaxima());
        this.vista.actualizarEntidades(0, 0);
    }

    /**
     * La vista deja de observar la plataforma.
     */
    public void desuscribir() {
        this.vista.dejarDeObservar();
    }

    public synchronized int getGeneradorasActivas() {
        return this.generadoras.size();
    }

    public synchronized int getNavesActivas() {
        return this.naves.size();
    }

    public PlataformaDeCarga getPlataforma() {
        return this.plataforma;
    }

    /**
     * Hilo que ejecuta la entidad activa con ese identificador, o null si no hay ninguna. Sólo para las pruebas
     * del paquete (verificar que el hilo terminó).
     */
    synchronized Thread getHilo(String id) {
        Thread hilo = this.generadoras.get(id);
        return hilo != null ? hilo : this.naves.get(id);
    }

    public synchronized GeneradoraDeEnergia crearGeneradora() {
        try {
            String id = "G-" + this.contadorGeneradoras++;
            GeneradoraDeEnergia generadora = new GeneradoraDeEnergia(id, this.plataforma);
            lanzar(this.generadoras, TIPO_GENERADORA, id, generadora);

            this.vista.agregarGeneradora(id);
            this.vista.actualizarEntidades(getGeneradorasActivas(), getNavesActivas());
            return generadora;
        } catch (Exception ex) {
            this.vista.mostrarError(ex.getMessage() != null ? ex.getMessage() : "Error al crear la generadora.");
            return null;
        }
    }

    public synchronized NaveConsumidora crearNave() {
        try {
            String id = "Nave-" + this.contadorNaves++;
            NaveConsumidora nave = new NaveConsumidora(id, this.plataforma);
            lanzar(this.naves, TIPO_NAVE, id, nave);

            this.vista.agregarNaveConsumidora(id);
            this.vista.actualizarEntidades(getGeneradorasActivas(), getNavesActivas());
            return nave;
        } catch (Exception ex) {
            this.vista.mostrarError(ex.getMessage() != null ? ex.getMessage() : "Error al crear la nave.");
            return null;
        }
    }

    /**
     * Detiene el hilo de una generadora de energía específica (por ejemplo al hacer doble clic).
     *
     * @param idGeneradora identificador de la generadora a detener.
     * @return true si la generadora estaba activa y fue detenida, false en caso contrario.
     * @post La generadora ya no está activa y su hilo fue interrumpido; si terminó dentro del tiempo límite, se
     *       publicó su egreso.
     */
    public synchronized boolean detenerGeneradora(String idGeneradora) {
        if (idGeneradora == null) {
            return false;
        }
        String id = idGeneradora.trim();
        Thread hilo = this.generadoras.remove(id);
        if (hilo == null) {
            return false;
        }
        finalizar(hilo, TIPO_GENERADORA, id);
        this.vista.removerGeneradora(id);
        this.vista.actualizarEntidades(getGeneradorasActivas(), getNavesActivas());
        return true;
    }

    /**
     * Detiene el hilo de una nave consumidora específica.
     *
     * @param idNave identificador de la nave a detener.
     * @return true si la nave estaba activa y fue detenida, false en caso contrario.
     * @post La nave ya no está activa y su hilo fue interrumpido; si terminó dentro del tiempo límite, se publicó
     *       su egreso.
     */
    public synchronized boolean detenerNave(String idNave) {
        if (idNave == null) {
            return false;
        }
        String id = idNave.trim();
        Thread hilo = this.naves.remove(id);
        if (hilo == null) {
            return false;
        }
        finalizar(hilo, TIPO_NAVE, id);
        this.vista.removerNaveConsumidora(id);
        this.vista.actualizarEntidades(getGeneradorasActivas(), getNavesActivas());
        return true;
    }

    /**
     * Detiene todas las entidades (se invoca al cerrar sesión): primero interrumpe todos los hilos, para que terminen
     * a la vez, después espera a cada uno y publica su egreso, y por último la vista deja de observar la plataforma.
     *
     * @post No queda ninguna entidad activa.
     */
    public synchronized void detenerTodo() {
        try {
            interrumpirTodos(this.generadoras);
            interrumpirTodos(this.naves);
            finalizarTodos(this.generadoras, TIPO_GENERADORA);
            finalizarTodos(this.naves, TIPO_NAVE);

            this.desuscribir();
            this.vista.limpiarEntidades();
            this.vista.actualizarEntidades(0, 0);
        } catch (Exception ex) {
            this.vista.mostrarError(ex.getMessage() != null ? ex.getMessage() : "Error al detener los procesos concurrentes.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String comando = e.getActionCommand();
            if (IVistaConcurrencia.COMANDO_CREAR_GENERADORA.equals(comando)) {
                crearGeneradora();
            } else if (IVistaConcurrencia.COMANDO_CREAR_NAVE.equals(comando)) {
                crearNave();
            } else if (IVistaConcurrencia.COMANDO_LIMPIAR_CONSOLA.equals(comando)) {
                this.vista.limpiarConsola();
            }
        } catch (Exception ex) {
            this.vista.mostrarError(ex.getMessage() != null ? ex.getMessage() : "Error al procesar la acción.");
        }
    }

    // --- Ciclo de vida de los hilos ---

    /**
     * Publica el ingreso antes de lanzar el hilo, para que preceda a los eventos de la entidad, y registra la entidad
     * sólo si se lanzó. Si la publicación falla, no queda nada registrado ni ningún hilo lanzado.
     */
    private void lanzar(Map<String, Thread> registro, String tipo, String id, Runnable tarea) {
        Thread hilo = new Thread(tarea, tipo + " [" + id + "]");
        this.plataforma.notificarIngreso(tipo, id);
        hilo.start();
        registro.put(id, hilo);
    }

    private void interrumpirTodos(Map<String, Thread> registro) {
        for (Thread hilo : registro.values()) {
            hilo.interrupt();
        }
    }

    private void finalizarTodos(Map<String, Thread> registro, String tipo) {
        for (Map.Entry<String, Thread> entidad : registro.entrySet()) {
            finalizar(entidad.getValue(), tipo, entidad.getKey());
        }
        registro.clear();
    }

    /**
     * Interrumpe el hilo y espera, con tiempo límite, a que salga de run(). El egreso se publica sólo si el hilo
     * terminó; si no, o si se interrumpe la espera, se informa el error en la vista (la interrupción del hilo que
     * espera se restaura).
     */
    private void finalizar(Thread hilo, String tipo, String id) {
        hilo.interrupt();
        try {
            hilo.join(ESPERA_MAXIMA_DETENCION_MS);
            if (hilo.isAlive()) {
                this.vista.mostrarError("La " + tipo + " [" + id + "] no finalizó en el tiempo esperado.");
            } else {
                this.plataforma.notificarEgreso(tipo, id);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.vista.mostrarError("Se interrumpió la espera de la detención de la " + tipo + " [" + id + "].");
        }
    }
}
