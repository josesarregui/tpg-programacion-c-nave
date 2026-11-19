package controlador.principal;

import controlador.concurrencia.ControladorConcurrencia;
import controlador.control.ControladorControl;
import controlador.nave.ControladorNave;
import controlador.tripulacion.ControladorTripulacion;
import controlador.usuario.ControladorLogin;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.asistente.Asistente;
import modelo.universo.CentroDeControl;
import modelo.usuario.ServicioUsuarios;
import modelo.usuario.Usuario;
import vista.FabricaDeVistas;
import vista.principal.IVistaPrincipal;
import vista.principal.panelmenu.IVistaPanelMenu;
import vista.usuario.IVistaLogin;

/**
 * Controlador de la ventana principal. Gestiona la navegación entre las secciones operativas (Nave, Tripulación,
 * Control de la Nave y Visor Recóndito), coordina los subcontroladores y administra el cierre de sesión.
 *
 * Cierre de sesión: detiene el hilo de la misión en curso y los hilos del Visor, todas las vistas dejan de observar
 * sus sujetos (asistente, tablero y plataforma), cierra la sesión en {@link ServicioUsuarios}, cierra la ventana y
 * vuelve al login, que pide a la {@link FabricaDeVistas}.
 */
public class ControladorPrincipal implements ActionListener {
    private final IVistaPrincipal vista;
    private final Usuario usuario;
    private final ServicioUsuarios servicioUsuarios;
    private final CentroDeControl centroDeControl;
    private final FabricaDeVistas fabricaDeVistas;

    private final ControladorNave controladorNave;
    private final ControladorConcurrencia controladorConcurrencia;
    private ControladorTripulacion controladorTripulacion;
    private ControladorControl controladorControl;
    private Asistente asistente;

    /**
     * @pre Ningún parámetro es nulo, usuario es el usuario en sesión y la vista provee sus cuatro secciones.
     * @post La vista muestra al operador y la sección Nave; el Visor ya observa su plataforma.
     */
    public ControladorPrincipal(
            IVistaPrincipal vista,
            Usuario usuario,
            ServicioUsuarios servicioUsuarios,
            CentroDeControl centroDeControl,
            FabricaDeVistas fabricaDeVistas) {
        assert vista != null && usuario != null && servicioUsuarios != null && centroDeControl != null
                && fabricaDeVistas != null : "La ventana principal necesita la vista, el usuario, los servicios y la fábrica.";
        this.vista = vista;
        this.usuario = usuario;
        this.servicioUsuarios = servicioUsuarios;
        this.centroDeControl = centroDeControl;
        this.fabricaDeVistas = fabricaDeVistas;

        this.asistente = null;
        this.controladorTripulacion = null;
        this.controladorControl = null;

        this.vista.mostrarOperador(this.usuario.getNombreUsuario());
        this.vista.setControlador(this);
        this.controladorNave = new ControladorNave(this.vista.getPanelNave(), this, this.centroDeControl);
        this.controladorConcurrencia = new ControladorConcurrencia(this.vista.getPanelVisor());
    }

    // =========================
    // NAVE
    // =========================

    /**
     * @pre asistente != null es el asistente de la nave recién creada y registrada en el centro de control.
     * @post Se habilita la sección Tripulación con un controlador para esa nave.
     */
    public void naveCreada(Asistente asistente) {
        assert asistente != null : "La nave creada debe tener su asistente.";
        this.asistente = asistente;
        this.controladorTripulacion = new ControladorTripulacion(this.vista.getPanelTripulacion(), this.asistente,
                this);
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

    /**
     * Habilita la sección Control de la Nave. Su controlador (con el tablero y las suscripciones) se crea una sola
     * vez por nave.
     *
     * @pre tieneAsistente() y el asistente tiene tripulación.
     */
    public void tripulacionCreada() {
        assert tieneAsistente() : "La tripulación se asigna a una nave creada.";
        this.vista.habilitarControlNave(true);
        crearControlSiFalta();
    }

    public ControladorTripulacion getControladorTripulacion() {
        return this.controladorTripulacion;
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
        } else if (this.controladorControl == null && !this.asistente.tieneTripulacion()) {
            this.vista.mostrarError("Primero debe configurar una tripulación.");
        } else {
            crearControlSiFalta();
            this.controladorControl.actualizarVista();
            this.vista.mostrarPanel(IVistaPrincipal.PANEL_CONTROL_NAVE);
        }
    }

    private void crearControlSiFalta() {
        if (this.controladorControl == null) {
            this.controladorControl = new ControladorControl(this.vista.getPanelControl(), this.asistente);
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

    public ControladorConcurrencia getControladorConcurrencia() {
        return this.controladorConcurrencia;
    }

    // =========================
    // CERRAR SESION
    // =========================

    /**
     * Primero detiene los hilos (misión y Visor) y, recién cuando terminaron, las vistas dejan de observar; después
     * cierra la sesión y la ventana y abre el login.
     *
     * @post No queda ningún hilo de la sesión vivo ni ninguna vista suscripta (salvo que un hilo no termine en su
     *       tiempo límite, lo que se informa en la vista), no hay sesión iniciada y el login quedó visible.
     */
    private void cerrarSesion() {
        if (this.controladorControl != null) {
            this.controladorControl.detener();
        }
        this.controladorNave.desuscribir();
        this.controladorConcurrencia.detenerTodo();
        this.servicioUsuarios.cerrarSesion();

        this.vista.cerrar();

        IVistaLogin vistaLogin = this.fabricaDeVistas.crearVistaLogin();
        new ControladorLogin(vistaLogin, this.servicioUsuarios, this.centroDeControl, this.fabricaDeVistas);
        vistaLogin.arrancar();
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
