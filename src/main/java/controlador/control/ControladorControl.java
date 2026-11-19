package controlador.control;

import modelo.asistente.Asistente;
import modelo.mision.Mision;
import modelo.universo.Destino;
import modelo.universo.FabricaDeUniverso;
import modelo.universo.Tablero;
import vista.principal.panelcontrol.IVistaControl;
import vista.principal.panelcontrol.panelrecursos.IVistaRecursos;

/**
 * Controlador coordinador de la sección Control de la Nave (Enunciado E2, Vistas, punto 4). Administra las
 * transiciones entre la selección de misión ({@link ControladorSelectMision}), la misión en curso
 * ({@link ControladorCursoMision}, dueño del hilo de la misión) y el historial de informes
 * ({@link ControladorResultadosMision}).
 *
 * Crea el universo sobre el que navegan las misiones: el tablero de 20 x 20 ({@link FabricaDeUniverso}) y el
 * {@link Destino} asociado a ese mismo tablero. Al crearse, cada vista de la sección pasa a observar al asistente y
 * la vista del tablero pasa a observar el tablero, antes de que exista cualquier hilo de misión.
 *
 * Concurrencia: mientras hay una misión en curso, el modelo sólo se consulta desde el hilo que notifica (las vistas
 * observadoras); este controlador no lo lee desde el hilo de eventos de Swing.
 */
public class ControladorControl {
    private final IVistaControl vistaControl;
    private final Asistente asistente;
    private final Tablero tablero;

    private final ControladorSelectMision controladorSelectMision;
    private final ControladorCursoMision controladorCursoMision;
    private final ControladorResultadosMision controladorResultadosMision;

    /**
     * Control de la nave sobre un universo nuevo de 20 x 20, con la pausa por turno del Enunciado.
     *
     * @pre vistaControl != null y asistente != null.
     */
    public ControladorControl(IVistaControl vistaControl, Asistente asistente) {
        this(vistaControl, asistente, new FabricaDeUniverso().crearTablero(), Mision.PAUSA_POR_TURNO_MS);
    }

    private ControladorControl(IVistaControl vistaControl, Asistente asistente, Tablero tablero,
            int pausaPorTurnoMs) {
        this(vistaControl, asistente, tablero, new Destino(tablero), pausaPorTurnoMs);
    }

    /**
     * Control de la nave sobre el tablero y el Destino recibidos (lo usan las pruebas para que las misiones sean
     * deterministas).
     *
     * @pre vistaControl != null, asistente != null, tablero != null, destino != null creado sobre ese tablero y
     *      pausaPorTurnoMs >= 0.
     * @post Todas las vistas de la sección observan al asistente, la vista del tablero observa el tablero y se
     *       muestra la selección de misión.
     */
    public ControladorControl(IVistaControl vistaControl, Asistente asistente, Tablero tablero, Destino destino,
            int pausaPorTurnoMs) {
        assert vistaControl != null && asistente != null && tablero != null && destino != null
                : "El control de la nave necesita la vista, el asistente, el tablero y el Destino.";
        assert pausaPorTurnoMs >= 0 : "La pausa por turno no puede ser negativa.";
        this.vistaControl = vistaControl;
        this.asistente = asistente;
        this.tablero = tablero;

        IVistaRecursos vistaRecursos = vistaControl.getVistaRecursos();
        vistaRecursos.observar(asistente);
        this.controladorCursoMision = new ControladorCursoMision(vistaControl.getVistaCursoMision(), asistente,
                tablero, this);
        this.controladorResultadosMision = new ControladorResultadosMision(vistaControl.getVistaResultadosMision(),
                asistente, this);
        this.controladorSelectMision = new ControladorSelectMision(vistaControl.getVistaSelectMision(), asistente,
                this, tablero, destino, pausaPorTurnoMs);

        mostrarRecursos();
        vistaControl.mostrarSelectMision();
    }

    /**
     * Al entrar en la sección: si hay una misión en curso muestra su avance, sin consultar el modelo; si no, refresca
     * los recursos y la selección de misión.
     */
    public void actualizarVista() {
        if (hayMisionEnCurso()) {
            vistaControl.mostrarMisionCurso();
        } else {
            mostrarRecursos();
            mostrarSelectMision();
        }
    }

    /**
     * @pre !hayMisionEnCurso().
     */
    public void mostrarSelectMision() {
        assert !hayMisionEnCurso() : "No se vuelve a la selección mientras hay una misión en curso.";
        controladorSelectMision.actualizarVista();
        vistaControl.mostrarSelectMision();
    }

    public void mostrarMisionCurso() {
        vistaControl.mostrarMisionCurso();
    }

    public void mostrarResultadosMision() {
        vistaControl.mostrarResultadosMision();
    }

    /**
     * Lanza la misión ya encomendada al asistente en su propio hilo y muestra su avance.
     *
     * @pre !hayMisionEnCurso() y asistente.tieneMisionPendiente().
     * @post hayMisionEnCurso() (hasta que el hilo termine) y se muestra la misión en curso.
     */
    void lanzarMision() {
        controladorCursoMision.lanzarMision();
        vistaControl.mostrarMisionCurso();
    }

    /**
     * @return true si hay una misión ejecutándose en su hilo.
     */
    public boolean hayMisionEnCurso() {
        return controladorCursoMision.hayMisionEnCurso();
    }

    /**
     * Cancela la misión en curso, si la hay: interrumpe su hilo y espera, con tiempo límite, a que termine.
     *
     * @return true si no queda ninguna misión en curso.
     */
    public boolean cancelarMision() {
        return controladorCursoMision.cancelarMision();
    }

    /**
     * Cierre de sesión: cancela la misión en curso y, recién cuando su hilo terminó, todas las vistas de la sección
     * dejan de observar al asistente y al tablero.
     *
     * @return true si el hilo de la misión terminó; si no terminó en el tiempo límite, las vistas siguen
     *         suscriptas (quitarlas mientras el hilo notifica produciría una carrera sobre el tablero) y el error
     *         ya se informó en la vista.
     */
    public boolean detener() {
        boolean termino = cancelarMision();
        if (termino) {
            vistaControl.getVistaRecursos().dejarDeObservar();
            controladorSelectMision.desuscribir();
            controladorCursoMision.desuscribir();
            controladorResultadosMision.desuscribir();
        }
        return termino;
    }

    public ControladorSelectMision getControladorSelectMision() {
        return controladorSelectMision;
    }

    public ControladorCursoMision getControladorCursoMision() {
        return controladorCursoMision;
    }

    public Asistente getAsistente() {
        return asistente;
    }

    /**
     * @return el tablero sobre el que navegan las misiones.
     */
    public Tablero getTablero() {
        return tablero;
    }

    /**
     * Muestra el estado de la nave en la franja de recursos (sólo sin misión en curso).
     */
    private void mostrarRecursos() {
        vistaControl.getVistaRecursos().mostrarRecursos(asistente.getCombustible(), asistente.getEnergia(),
                asistente.getDesgaste(), asistente.getGemas(), asistente.getEstadoMotor(),
                asistente.naveListaParaOperar(), asistente.naveDestruida());
    }
}
