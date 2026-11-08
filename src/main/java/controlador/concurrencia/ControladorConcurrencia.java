package controlador.concurrencia;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import modelo.concurrencia.GeneradoraDeEnergia;
import modelo.concurrencia.NaveConsumidora;
import modelo.concurrencia.ObservadorConcurrencia;
import modelo.concurrencia.PlataformaDeCarga;
import vista.concurrencia.IVistaConcurrencia;

/**
 * Controlador del módulo de Concurrencia (Visor del Universo Paralelo). Sigue
 * el mismo lineamiento que {@link controlador.nave.ControladorDetallesNave}:
 * trabaja contra la interfaz {@link IVistaConcurrencia} y suscribe la vista
 * como observadora si implementa {@link ObservadorConcurrencia}.
 *
 * Implementa {@link ActionListener} para capturar los eventos del usuario
 * (MVC), instanciar las tareas {@link Runnable} y ejecutarlas en nuevos hilos
 * {@link Thread}. Permite además detener hilos individuales al interactuar con
 * las entidades (mediante doble clic sobre la lista de generadoras o naves). El
 * controlador captura cualquier excepción y delega en la vista la presentación
 * del mensaje de error (sin propagar excepciones al entorno visual).
 */
public class ControladorConcurrencia implements ActionListener {
    private final IVistaConcurrencia vista;
    private final PlataformaDeCarga plataforma;

    private final List<GeneradoraDeEnergia> generadoras;
    private final List<NaveConsumidora> naves;
    private final List<Thread> hilos;

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
        this.generadoras = new ArrayList<>();
        this.naves = new ArrayList<>();
        this.hilos = new ArrayList<>();
        this.contadorGeneradoras = 1;
        this.contadorNaves = 1;

        // Patron Observer: el Modelo notifica directamente a la Vista
        this.plataforma.agregarObservador(this.vista);

        this.vista.setControlador(this);
        this.vista.setOyenteDetenerGeneradora(this::detenerGeneradora);
        this.vista.setOyenteDetenerNave(this::detenerNave);

        this.vista.actualizarBaterias(this.plataforma.getBaterias(), this.plataforma.getCapacidadMaxima());
        this.vista.actualizarEntidades(0, 0);
    }

    public void desuscribir() {
        if (this.plataforma != null) {
            this.plataforma.eliminarObservador(this.vista);
        }
    }

    public int getGeneradorasActivas() {
        int activas = 0;
        for (GeneradoraDeEnergia g : this.generadoras) {
            if (g.isActivo()) {
                activas++;
            }
        }
        return activas;
    }

    public int getNavesActivas() {
        int activas = 0;
        for (NaveConsumidora n : this.naves) {
            if (n.isActivo()) {
                activas++;
            }
        }
        return activas;
    }

    /**
     * Detiene todas las tareas Runnable y los hilos en ejecución.
     */
    public void detenerTodo() {
        try {
            for (GeneradoraDeEnergia generador : this.generadoras) {
                generador.detener();
            }
            for (NaveConsumidora nave : this.naves) {
                nave.detener();
            }
            for (Thread hilo : this.hilos) {
                hilo.interrupt();
            }
            this.generadoras.clear();
            this.naves.clear();
            this.hilos.clear();

            this.desuscribir();
            this.vista.limpiarEntidades();
            this.vista.actualizarEntidades(0, 0);
        } catch (Exception ex) {
            this.vista.mostrarError(ex.getMessage() != null ? ex.getMessage() : "Error al detener los procesos concurrentes.");
        }
    }

    public PlataformaDeCarga getPlataforma() {
        return this.plataforma;
    }

    public List<GeneradoraDeEnergia> getGeneradoras() {
        return this.generadoras;
    }

    public List<NaveConsumidora> getNaves() {
        return this.naves;
    }

    public List<Thread> getHilos() {
        return this.hilos;
    }

    public synchronized GeneradoraDeEnergia crearGeneradora() {
        try {
            String id = "G-" + this.contadorGeneradoras++;
            GeneradoraDeEnergia generadora = new GeneradoraDeEnergia(id, this.plataforma);
            Thread hilo = new Thread(generadora, "Thread-" + id);

            this.generadoras.add(generadora);
            this.hilos.add(hilo);

            this.plataforma.notificarIngreso("Generadora de Energía", id);
            hilo.start();

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
            Thread hilo = new Thread(nave, "Thread-" + id);

            this.naves.add(nave);
            this.hilos.add(hilo);

            this.plataforma.notificarIngreso("Nave Consumidora", id);
            hilo.start();

            this.vista.agregarNaveConsumidora(id);
            this.vista.actualizarEntidades(getGeneradorasActivas(), getNavesActivas());
            return nave;
        } catch (Exception ex) {
            this.vista.mostrarError(ex.getMessage() != null ? ex.getMessage() : "Error al crear la nave.");
            return null;
        }
    }

    /**
     * Detiene el hilo y la tarea de una generadora de energía específica (por
     * ejemplo al hacer doble clic).
     *
     * @param idGeneradora identificador de la generadora a detener.
     * @return true si la generadora existía y fue detenida, false en caso
     * contrario.
     */
    public synchronized boolean detenerGeneradora(String idGeneradora) {
        if (idGeneradora == null) {
            return false;
        } else {
            for (GeneradoraDeEnergia g : this.generadoras) {
                if (g.getIdGeneradora().equalsIgnoreCase(idGeneradora.trim())) {
                    g.detener();
                    this.generadoras.remove(g);

                    for (Thread h : this.hilos) {
                        if (h.getName().endsWith(g.getIdGeneradora())) {
                            h.interrupt();
                            this.hilos.remove(h);
                            break;
                        }
                    }

                    this.plataforma.notificarEgreso("Generadora de Energía", g.getIdGeneradora());
                    this.vista.removerGeneradora(g.getIdGeneradora());
                    this.vista.actualizarEntidades(getGeneradorasActivas(), getNavesActivas());
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Detiene el hilo y la tarea de una nave consumidora especifica.
     *
     * @param idNave identificador de la nave a detener.
     * @return true si la nave existía y fue detenida, false en caso contrario.
     */
    public synchronized boolean detenerNave(String idNave) {
        if (idNave == null) {
            return false;
        } else {
            for (NaveConsumidora n : this.naves) {
                if (n.getIdNave().equalsIgnoreCase(idNave.trim())) {
                    n.detener();
                    this.naves.remove(n);

                    for (Thread h : this.hilos) {
                        if (h.getName().endsWith(n.getIdNave())) {
                            h.interrupt();
                            this.hilos.remove(h);
                            break;
                        }
                    }

                    this.plataforma.notificarEgreso("Nave Consumidora", n.getIdNave());
                    this.vista.removerNaveConsumidora(n.getIdNave());
                    this.vista.actualizarEntidades(getGeneradorasActivas(), getNavesActivas());
                    return true;
                }
            }
            return false;
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
}
