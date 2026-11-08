package controlador.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import modelo.asistente.Asistente;
import modelo.mision.Mision;
import modelo.mision.MisionIntercepcion;
import modelo.mision.MisionRecoleccion;
import modelo.mision.MisionRetorno;
import vista.principal.panelselectmision.IVistaSelectMision;

/**
 * Controlador para la pantalla de seleccion de mision y bitacora
 * (VistaSelectMision).
 * Administra la carga de eventos en la bitacora, la inspeccion de misiones
 * disponibles
 * y el paso a la fase de ejecucion de la mision encomendada.
 */
public class ControladorSelectMision implements ActionListener {
    private final IVistaSelectMision vista;
    private final Asistente asistente;
    private final ControladorControl controladorControl;

    private Mision misionSeleccionada;

    public ControladorSelectMision(
            IVistaSelectMision vista,
            Asistente asistente,
            ControladorControl controladorControl) {
        this.vista = vista;
        this.asistente = asistente;
        this.controladorControl = controladorControl;

        inicializarVista();
    }

    private void inicializarVista() {
        if (this.vista == null) {
            return;
        }

        // Catálogo de misiones disponibles según las especificaciones del sistema
        List<Mision> misiones = List.of(
                new MisionIntercepcion(),
                new MisionRecoleccion(),
                new MisionRetorno());
        this.vista.mostrarMisionesDisponibles(misiones);
        this.vista.setControlador(this);

        actualizarVista();
    }

    public void actualizarVista() {
        if (this.vista == null || this.asistente == null) {
            return;
        }

        // 1. Cargar bitacora cronologica
        this.vista.mostrarBitacora(this.asistente.getEventos());

        // 2. Reflejar detalles y factibilidad de la mision seleccionada
        seleccionarMision();
    }

    private void seleccionarMision() {
        if (this.vista == null || this.asistente == null) {
            return;
        }
        String codigo = this.vista.getMisionSeleccionada();
        this.misionSeleccionada = crearMisionPorCodigo(codigo);
        if (this.misionSeleccionada != null) {
            this.vista.mostrarDetallesMision(
                    this.misionSeleccionada,
                    this.asistente.naveListaParaOperar(),
                    this.asistente.getCombustible(),
                    this.asistente.getEnergia());
        }
    }

    private void iniciarMision() {
        if (this.vista == null || this.asistente == null) {
            return;
        }

        if (this.misionSeleccionada == null) {
            this.vista.mostrarError("Debe seleccionar una directiva de misión válida.");
            return;
        }

        if (!this.asistente.naveListaParaOperar()) {
            this.vista.mostrarError("La nave no se encuentra lista para operar.\n"
                    + "Verifique el estado del Motor Warp, el mantenimiento estructural y la asignación de tripulación.");
            return;
        }

        try {
            if (this.asistente.tieneMisionPendiente()) {
                this.asistente.cancelarMision();
            }
            this.asistente.encomendarMision(this.misionSeleccionada);

            if (this.controladorControl != null) {
                this.controladorControl.misionIniciada(this.misionSeleccionada);
            }
        } catch (Exception ex) {
            this.vista.mostrarError("No fue posible encomendar la misión: " + ex.getMessage());
        }
    }

    private Mision crearMisionPorCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        switch (codigo.trim().toUpperCase()) {
            case "M-01":
                return new MisionIntercepcion();
            case "M-02":
                return new MisionRecoleccion();
            case "M-03":
                return new MisionRetorno();
            default:
                return null;
        }
    }

    public Mision getMisionSeleccionada() {
        return this.misionSeleccionada;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        if (IVistaSelectMision.COMANDO_SELECCIONAR_MISION.equals(comando)) {
            this.seleccionarMision();
        } else if (IVistaSelectMision.COMANDO_ACTUALIZAR_BITACORA.equals(comando)) {
            this.actualizarVista();
        } else if (IVistaSelectMision.COMANDO_INICIAR_MISION.equals(comando)) {
            this.iniciarMision();
        }
    }
}
