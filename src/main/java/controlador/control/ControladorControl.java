package controlador.control;

import controlador.principal.ControladorPrincipal;
import modelo.asistente.Asistente;
import modelo.mision.Mision;
import modelo.universo.Destino;
import modelo.universo.FabricaDeUniverso;
import modelo.universo.Tablero;
import vista.principal.panelcontrol.IVistaControl;
import vista.principal.panelcontrol.panelselectmision.IVistaSelectMision;

/**
 * Controlador coordinador del panel de Control de Nave y ciclo de Misiones.
 * Administra las transiciones entre la seleccion de misiones (SelectMision),
 * la mision en curso y la presentacion de resultados.
 *
 * Crea el universo sobre el que navegan las misiones: el tablero de 20 x 20 ({@link FabricaDeUniverso}) y el
 * {@link Destino} asociado a ese mismo tablero, que entrega a las misiones al crearlas.
 */
public class ControladorControl {
    private final IVistaControl vistaControl;
    private final Asistente asistente;
    private final ControladorPrincipal controladorPrincipal;
    private final Tablero tablero;
    private final Destino destino;

    private ControladorSelectMision controladorSelectMision;
    private ControladorCursoMision controladorCursoMision;
    private ControladorResultadosMision controladorResultadosMisio;

    public ControladorControl(IVistaControl vistaControl, Asistente asistente) {
        this(vistaControl, asistente, null);
    }

    public ControladorControl(IVistaControl vistaControl, Asistente asistente,
            ControladorPrincipal controladorPrincipal) {
        this.vistaControl = vistaControl;
        this.asistente = asistente;
        this.controladorPrincipal = controladorPrincipal;
        this.tablero = new FabricaDeUniverso().crearTablero();
        this.destino = new Destino(this.tablero);

        inicializarControladores();
    }

    private void inicializarControladores() {
        if (this.vistaControl == null) {
            return;
        }

        IVistaSelectMision vistaSelect = this.vistaControl.getVistaSelectMision();
        if (vistaSelect != null && this.asistente != null) {
            this.controladorSelectMision = new ControladorSelectMision(vistaSelect, this.asistente, this,
                    this.tablero, this.destino);
        }
    }

    public void actualizarVista() {
        if (this.controladorSelectMision != null) {
            this.controladorSelectMision.actualizarVista();
        }
        if (this.vistaControl != null) {
            this.vistaControl.mostrarSelectMision();
        }
    }

    public void misionIniciada(Mision mision) {
        if (this.vistaControl != null) {
            this.vistaControl.mostrarMisionCurso();
        }
    }

    public void mostrarSelectMision() {
        if (this.controladorSelectMision != null) {
            this.controladorSelectMision.actualizarVista();
        }
        if (this.vistaControl != null) {
            this.vistaControl.mostrarSelectMision();
        }
    }

    public void mostrarMisionCurso() {
        if (this.vistaControl != null) {
            this.vistaControl.mostrarMisionCurso();
        }
    }

    public void mostrarResultadosMision() {
        if (this.vistaControl != null) {
            this.vistaControl.mostrarResultadosMision();
        }
    }

    public ControladorSelectMision getControladorSelectMision() {
        return this.controladorSelectMision;
    }

    public Asistente getAsistente() {
        return this.asistente;
    }

    /**
     * @return el tablero sobre el que navegan las misiones (para que la vista lo observe).
     */
    public Tablero getTablero() {
        return this.tablero;
    }
}
