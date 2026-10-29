package controlador.nave;

import controlador.principal.ControladorPrincipal;
import modelo.asistente.Asistente;
import modelo.universo.CentroDeControl;
import vista.principal.panelcrearnave.IVistaCrearNave;
import vista.principal.paneldetallesnave.IVistaDetallesNave;
import vista.principal.panelnave.IVistaNave;

/**
 * Controlador coordinador de la sección Nave (creación y detalles).
 * Depende exclusivamente de interfaces tanto hacia la vista como hacia el asistente.
 */
public class ControladorNave {
    private final IVistaNave vista;
    private final ControladorPrincipal controladorPrincipal;
    private final CentroDeControl centroDeControl;

    private ControladorCrearNave controladorCrearNave;
    private ControladorDetallesNave controladorDetallesNave;
    private Asistente asistente;

    public ControladorNave(IVistaNave vista, ControladorPrincipal controladorPrincipal) {
        this(vista, controladorPrincipal, new CentroDeControl());
    }

    public ControladorNave(
            IVistaNave vista,
            ControladorPrincipal controladorPrincipal,
            CentroDeControl centroDeControl) {
        this.vista = vista;
        this.controladorPrincipal = controladorPrincipal;
        this.centroDeControl = centroDeControl;

        this.asistente = null;
        this.controladorDetallesNave = null;

        inicializarControladorCrearNave();
    }

    private void inicializarControladorCrearNave() {
        IVistaCrearNave vistaCrear = this.vista.getVistaCrearNave();
        if (vistaCrear != null) {
            this.controladorCrearNave = new ControladorCrearNave(vistaCrear, this, this.centroDeControl);
        }
    }

    public void naveCreada(Asistente asistente) {
        desuscribir();

        this.asistente = asistente;
        IVistaDetallesNave vistaDetalles = this.vista.getVistaDetallesNave();

        if (vistaDetalles != null) {
            this.controladorDetallesNave = new ControladorDetallesNave(vistaDetalles, this.asistente);
            this.controladorDetallesNave.actualizarVista();
        }

        this.vista.mostrarDetalleNave();

        if (this.controladorPrincipal != null) {
            this.controladorPrincipal.naveCreada(this.asistente);
        }
    }

    public void desuscribir() {
        if (this.controladorDetallesNave != null) {
            this.controladorDetallesNave.desuscribir();
        }
    }

    public void mostrarCrearNave() {
        this.vista.mostrarCrearNave();
    }

    public void mostrarDetallesNave() {
        if (this.controladorDetallesNave != null) {
            this.controladorDetallesNave.actualizarVista();
            this.vista.mostrarDetalleNave();
        }
    }

    public Asistente getAsistente() {
        return this.asistente;
    }
}
