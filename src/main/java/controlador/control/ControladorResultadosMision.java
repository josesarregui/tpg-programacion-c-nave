package controlador.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import modelo.asistente.Asistente;
import modelo.mision.InformeMision;
import modelo.mision.Mision;
import vista.principal.panelcontrol.panelresultadosmision.IVistaResultadosMision;

/**
 * Controlador del historial de informes finales de misiones. Le entrega a la vista el asistente que debe observar:
 * cada informe nuevo aparece solo (patrón Observer). Muestra los informes que ya existían y atiende el regreso a la
 * selección de misión.
 */
public class ControladorResultadosMision implements ActionListener {
    private final IVistaResultadosMision vista;
    private final Asistente asistente;
    private final ControladorControl controladorControl;

    /**
     * @pre Ningún parámetro es nulo y no hay misión en curso.
     * @post La vista observa al asistente y muestra los informes de sus misiones realizadas.
     */
    public ControladorResultadosMision(IVistaResultadosMision vista, Asistente asistente,
            ControladorControl controladorControl) {
        assert vista != null && asistente != null && controladorControl != null
                : "El historial de informes necesita la vista, el asistente y el control.";
        this.vista = vista;
        this.asistente = asistente;
        this.controladorControl = controladorControl;

        this.vista.observar(asistente);
        this.vista.setControlador(this);
        this.vista.mostrarInformes(informes());
    }

    /**
     * La vista deja de observar al asistente.
     */
    public void desuscribir() {
        vista.dejarDeObservar();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (IVistaResultadosMision.COMANDO_VOLVER.equals(e.getActionCommand())) {
            if (controladorControl.hayMisionEnCurso()) {
                controladorControl.mostrarMisionCurso();
            } else {
                controladorControl.mostrarSelectMision();
            }
        }
    }

    private List<InformeMision> informes() {
        List<InformeMision> informes = new ArrayList<>();
        for (Mision mision : asistente.getMisionesRealizadas()) {
            informes.add(mision.getInforme());
        }
        return informes;
    }
}
