package controlador.control;

import excepcion.NaveNoDisponibleException;
import excepcion.RecursoInsuficienteException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.asistente.Asistente;
import modelo.mision.InformeMision;
import modelo.mision.Mision;
import modelo.universo.Tablero;
import vista.principal.panelcontrol.panelcursomision.IVistaCursoMision;

/**
 * Controlador de la Misión en Curso y dueño del hilo de la misión (Plan, Paso 6).
 *
 * {@link Asistente#ejecutarMision()} recorre el tablero turno a turno con una pausa {@code Thread.sleep} por turno,
 * así que se ejecuta en un {@link Thread} dedicado, lanzado con {@code start()}, para no congelar el hilo de eventos
 * de Swing (EDT). Mientras corre, la vista del tablero y la consola se actualizan solas por Observer. No admite dos
 * misiones a la vez. Cancelar (o cerrar sesión) interrumpe el hilo y espera su fin con {@code join} con tiempo
 * límite: la misión ve la interrupción en su pausa, termina la travesía y resulta fallida.
 *
 * Las excepciones del hilo de la misión no se pierden: se informan en la vista, que las muestra desde el EDT.
 *
 * Sincronización: {@code hiloMision} sólo se lee y se escribe con el monitor de este controlador. Ningún método
 * espera ({@code join}) ni duerme con el monitor tomado.
 */
public class ControladorCursoMision implements ActionListener {

    /** Tiempo máximo que se espera a que el hilo de la misión termine después de interrumpirlo. */
    public static final long ESPERA_MAXIMA_FIN_MS = 2000;

    private final IVistaCursoMision vista;
    private final Asistente asistente;
    private final ControladorControl controladorControl;

    /** Hilo de la misión en curso, o null si no hay ninguna. Protegido por el monitor de este controlador. */
    private Thread hiloMision;

    /**
     * @pre Ningún parámetro es nulo y ningún hilo está modificando el tablero.
     * @post La vista observa al asistente, la vista del tablero observa el tablero y no hay misión en curso.
     */
    public ControladorCursoMision(IVistaCursoMision vista, Asistente asistente, Tablero tablero,
            ControladorControl controladorControl) {
        assert vista != null && asistente != null && tablero != null && controladorControl != null
                : "La misión en curso necesita la vista, el asistente, el tablero y el control.";
        this.vista = vista;
        this.asistente = asistente;
        this.controladorControl = controladorControl;
        this.hiloMision = null;

        // Patrón Observer: las vistas se suscriben antes de que exista el hilo de la misión.
        this.vista.observar(asistente);
        this.vista.getVistaTablero().observar(tablero);
        this.vista.setControlador(this);
    }

    /**
     * @return true si hay una misión ejecutándose en su hilo.
     */
    public synchronized boolean hayMisionEnCurso() {
        return hiloMision != null;
    }

    /**
     * Lanza la misión pendiente del asistente en un hilo dedicado. Se invoca desde el hilo de eventos de Swing.
     *
     * @pre !hayMisionEnCurso() y asistente.tieneMisionPendiente().
     * @post hayMisionEnCurso() hasta que el hilo termine; el hilo ejecuta asistente.ejecutarMision() y la vista del
     *       tablero sigue esa misión.
     */
    public synchronized void lanzarMision() {
        assert hiloMision == null : "Ya hay una misión en curso.";
        assert asistente.tieneMisionPendiente() : "No hay una misión encomendada para lanzar.";
        Mision mision = asistente.getMisionPendiente();
        vista.misionIniciada(mision.getCodigo(), mision.getNombre());
        // El tablero muestra el objetivo de esta misión (M-03 no lo registra en el tablero). Se asigna antes de start().
        vista.getVistaTablero().seguirMision(mision);
        hiloMision = new Thread(this::ejecutarMision, "Misión " + mision.getCodigo());
        hiloMision.start();
    }

    /**
     * Interrumpe el hilo de la misión en curso y espera, con tiempo límite, a que termine. La espera se hace sin
     * tener el monitor, para que el hilo pueda marcar su fin. Si no hay misión en curso, no hace nada.
     *
     * @return true si no queda ninguna misión en curso. Si el hilo no terminó a tiempo, o si se interrumpe la espera
     *         (se restaura la interrupción), se informa en la vista y retorna false.
     */
    public boolean cancelarMision() {
        Thread hilo;
        synchronized (this) {
            hilo = hiloMision;
        }
        boolean termino = true;
        if (hilo != null) {
            hilo.interrupt();
            try {
                hilo.join(ESPERA_MAXIMA_FIN_MS);
                termino = !hilo.isAlive();
                if (!termino) {
                    vista.mostrarError("La misión no finalizó en el tiempo esperado.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                termino = false;
                vista.mostrarError("Se interrumpió la espera del fin de la misión.");
            }
        }
        return termino;
    }

    /**
     * Las vistas dejan de observar al asistente y al tablero.
     *
     * @pre !hayMisionEnCurso(): ningún hilo está notificando desde el tablero.
     */
    public void desuscribir() {
        assert !hayMisionEnCurso() : "Las vistas no se desuscriben del tablero con una misión en curso.";
        vista.dejarDeObservar();
        vista.getVistaTablero().dejarDeObservar();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        if (IVistaCursoMision.COMANDO_CANCELAR_MISION.equals(comando)) {
            cancelarMision();
        } else if (IVistaCursoMision.COMANDO_VER_RESULTADOS.equals(comando)) {
            controladorControl.mostrarResultadosMision();
        } else if (IVistaCursoMision.COMANDO_VOLVER.equals(comando)) {
            volverASeleccion();
        }
    }

    private void volverASeleccion() {
        if (hayMisionEnCurso()) {
            vista.mostrarError("La misión sigue en curso: cancélela o espere a que termine.");
        } else {
            controladorControl.mostrarSelectMision();
        }
    }

    /**
     * Cuerpo del hilo de la misión. Al terminar (por completarse, por interrupción o por un rechazo) primero libera
     * la marca de misión en curso, para que se pueda iniciar otra, y después avisa a la vista.
     */
    private void ejecutarMision() {
        String resumen = "La misión terminó por un error inesperado.";
        try {
            InformeMision informe = asistente.ejecutarMision();
            resumen = describir(informe, Thread.currentThread().isInterrupted());
        } catch (NaveNoDisponibleException | RecursoInsuficienteException e) {
            resumen = "Misión rechazada: " + e.getMessage();
            vista.mostrarError(resumen);
        } catch (RuntimeException e) {
            resumen = "Error en la misión: " + e.getMessage();
            vista.mostrarError(resumen);
        } finally {
            synchronized (this) {
                hiloMision = null;
            }
            vista.misionFinalizada(resumen);
        }
    }

    private String describir(InformeMision informe, boolean cancelada) {
        return (cancelada ? "Misión cancelada por el operador. " : "")
                + "Informe de " + informe.getMision() + ": " + (informe.isExitosa() ? "EXITOSA" : "NO EXITOSA")
                + ". Consumo: combustible " + informe.getCombustibleConsumido()
                + ", energía " + informe.getEnergiaConsumida()
                + ", desgaste " + informe.getDesgasteProducido() + ".";
    }
}
