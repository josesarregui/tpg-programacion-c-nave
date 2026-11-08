package controlador.control;

import controlador.principal.ControladorPrincipal;
import modelo.asistente.Asistente;
import modelo.mision.Mision;
import vista.principal.panelcontrol.IVistaControl;
import vista.principal.panelcontrol.panelselectmision.IVistaSelectMision;

/**
 * Controlador coordinador del panel de Control de Nave y ciclo de Misiones.
 * Administra las transiciones entre la seleccion de misiones (SelectMision),
 * la mision en curso y la presentacion de resultados.
 */
public class ControladorControl {
    private final IVistaControl vistaControl;
    private final Asistente asistente;
    private final ControladorPrincipal controladorPrincipal;

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

        inicializarControladores();
    }

    private void inicializarControladores() {
        if (this.vistaControl == null) {
            return;
        }

        IVistaSelectMision vistaSelect = this.vistaControl.getVistaSelectMision();
        if (vistaSelect != null && this.asistente != null) {
            this.controladorSelectMision = new ControladorSelectMision(vistaSelect, this.asistente, this);
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
}
