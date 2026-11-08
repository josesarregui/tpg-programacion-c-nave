package controlador.principal;

import controlador.concurrencia.ControladorConcurrencia;
import controlador.control.ControladorControl;
import controlador.nave.ControladorNave;
import controlador.tripulacion.ControladorTripulacion;
import controlador.usuario.ControladorLogin;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.asistente.Asistente;
import modelo.universo.CentroDeControl;
import modelo.usuario.ServicioUsuarios;
import modelo.usuario.Usuario;
import vista.concurrencia.IVistaConcurrencia;
import vista.principal.IVistaPrincipal;
import vista.principal.panelcontrol.IVistaControl;
import vista.principal.panelmenu.IVistaPanelMenu;
import vista.principal.panelnave.IVistaNave;
import vista.principal.paneltripulacion.IVistaTripulacion;
import vista.usuario.IVistaLogin;
import vista.usuario.VistaLogin;

/**
 * Controlador de la ventana principal. Gestiona la navegacion entre las
 * secciones principales, coordina los subcontroladores y administra el cierre
 * de sesion.
 */
public class ControladorPrincipal implements ActionListener {
    private final IVistaPrincipal vista;
    private final Usuario usuario;
    private final ServicioUsuarios servicioUsuarios;
    private final CentroDeControl centroDeControl;

    private final ControladorNave controladorNave;
    private final ControladorConcurrencia controladorConcurrencia;
    private ControladorTripulacion controladorTripulacion;
    private ControladorControl controladorControl;
    private Asistente asistente;

    public ControladorPrincipal(IVistaPrincipal vista, Usuario usuario) {
        this(vista, usuario, null, new CentroDeControl());
    }

    public ControladorPrincipal(
            IVistaPrincipal vista,
            Usuario usuario,
            ServicioUsuarios servicioUsuarios,
            CentroDeControl centroDeControl) {

        this.vista = vista;
        this.usuario = usuario;
        this.servicioUsuarios = servicioUsuarios;
        this.centroDeControl = centroDeControl != null ? centroDeControl : new CentroDeControl();

        this.asistente = null;
        this.controladorTripulacion = null;
        this.controladorControl = null;

        if (this.usuario != null) {
            this.vista.mostrarOperador(this.usuario.getNombreUsuario());
        }

        this.vista.setControlador(this);

        IVistaNave panelNave = this.vista.getPanelNave();
        if (panelNave != null) {
            this.controladorNave = new ControladorNave(panelNave, this, this.centroDeControl);
        } else {
            this.controladorNave = null;
        }

        IVistaConcurrencia panelVisor = this.vista.getPanelVisor();
        if (panelVisor != null) {
            ControladorConcurrencia ctrlConcurrencia = null;
            try {
                ctrlConcurrencia = new ControladorConcurrencia(panelVisor);
            } catch (Exception e) {
                this.vista.mostrarError(e.getMessage());
            }
            this.controladorConcurrencia = ctrlConcurrencia;
        } else {
            this.controladorConcurrencia = null;
        }
    }

    // =========================
    // NAVE
    // =========================
    public void naveCreada(Asistente asistente) {
        this.asistente = asistente;

        IVistaTripulacion vistaTripulacion = this.vista.getPanelTripulacion();
        if (vistaTripulacion != null) {
            this.controladorTripulacion = new ControladorTripulacion(
                    vistaTripulacion,
                    this.asistente,
                    this);
        }

        this.vista.habilitarTripulacion(true);
        this.vista.mostrarPanel(IVistaPrincipal.PANEL_NAVE);
    }

    public Asistente getAsistente() {
        return this.asistente;
    }

    public boolean tieneAsistente() {
        return this.asistente != null;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    // =========================
    // TRIPULACION
    // =========================
    public void tripulacionCreada() {
        this.vista.habilitarControlNave(true);
        IVistaControl vistaControl = this.vista.getPanelControl();
        if (vistaControl != null && this.asistente != null) {
            this.controladorControl = new ControladorControl(vistaControl, this.asistente, this);
        }
    }

    // =========================
    // NAVEGACION DE SECCIONES
    // =========================
    private void mostrarNave() {
        this.vista.mostrarPanel(IVistaPrincipal.PANEL_NAVE);
    }

    private void mostrarTripulacion() {
        if (!tieneAsistente()) {
            this.vista.mostrarError("Primero debe crear una nave.");
        } else {
            this.vista.mostrarPanel(IVistaPrincipal.PANEL_TRIPULACION);
        }
    }

    private void mostrarControlNave() {
        if (!tieneAsistente()) {
            this.vista.mostrarError("Primero debe crear una nave.");
        } else if (!this.asistente.tieneTripulacion()) {
            this.vista.mostrarError("Primero debe configurar una tripulación.");
        } else {
            if (this.controladorControl == null && this.vista.getPanelControl() != null) {
                this.controladorControl = new ControladorControl(this.vista.getPanelControl(), this.asistente, this);
            }
            if (this.controladorControl != null) {
                this.controladorControl.actualizarVista();
            }
            this.vista.mostrarPanel(IVistaPrincipal.PANEL_CONTROL_NAVE);
        }
    }

    public ControladorControl getControladorControl() {
        return this.controladorControl;
    }

    // =========================
    // VISOR
    // =========================
    private void abrirVisor() {
        this.vista.mostrarPanel(IVistaPrincipal.PANEL_VISOR);
    }

    // =========================
    // CERRAR SESION
    // =========================
    private void cerrarSesion() {
        if (this.controladorNave != null) {
            this.controladorNave.desuscribir();
        }
        if (this.controladorConcurrencia != null) {
            this.controladorConcurrencia.detenerTodo();
        }
        if (this.servicioUsuarios != null) {
            this.servicioUsuarios.cerrarSesion();
        }

        this.vista.cerrar();

        if (this.servicioUsuarios != null && !GraphicsEnvironment.isHeadless()) {
            IVistaLogin vistaLogin = new VistaLogin();
            new ControladorLogin(vistaLogin, this.servicioUsuarios, this.centroDeControl);
            vistaLogin.arrancar();
        }
    }

    public ControladorConcurrencia getControladorConcurrencia() {
        return this.controladorConcurrencia;
    }

    // =========================
    // EVENTOS MENU
    // =========================
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        if (IVistaPanelMenu.NAVE.equals(comando)) {
            this.mostrarNave();
        } else if (IVistaPanelMenu.TRIPULACION.equals(comando)) {
            this.mostrarTripulacion();
        } else if (IVistaPanelMenu.CONTROL_NAVE.equals(comando)) {
            this.mostrarControlNave();
        } else if (IVistaPanelMenu.VISOR.equals(comando)) {
            this.abrirVisor();
        } else if (IVistaPanelMenu.CERRAR_SESION.equals(comando)) {
            this.cerrarSesion();
        }
    }
}
