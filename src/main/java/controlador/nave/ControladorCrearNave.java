package controlador.nave;

import excepcion.NaveInexistenteException;
import excepcion.NaveYaRegistradaException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.asistente.Asistente;
import modelo.asistente.AsistenteComando;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import modelo.universo.CentroDeControl;
import vista.principal.panelcrearnave.IVistaCrearNave;

/**
 * Controlador de la vista de creación de nave.
 * Solicita la creación a {@link NaveFactory}, envuelve la nave con su {@link Asistente}
 * y la registra como nave en uso en el {@link CentroDeControl}.
 */
public class ControladorCrearNave implements ActionListener {
    private final IVistaCrearNave vistaCrearNave;
    private final ControladorNave controladorNave;
    private final NaveFactory fabrica;
    private final CentroDeControl centroDeControl;

    public ControladorCrearNave(
            IVistaCrearNave vistaCrearNave,
            ControladorNave controladorNave) {
        this(vistaCrearNave, controladorNave, null);
    }

    public ControladorCrearNave(
            IVistaCrearNave vistaCrearNave,
            ControladorNave controladorNave,
            CentroDeControl centroDeControl) {

        this.vistaCrearNave = vistaCrearNave;
        this.controladorNave = controladorNave;
        this.centroDeControl = centroDeControl;
        this.fabrica = new NaveFactory();

        this.vistaCrearNave.setControlador(this);
    }

    private void crearNave() {
        TipoNave tipo = this.vistaCrearNave.getTipoSeleccionado();

        if (tipo == null) {
            this.vistaCrearNave.mostrarError("Debe seleccionar una nave.");
            return;
        }

        try {
            Nave nave = this.fabrica.crearNave(tipo);
            Asistente asistente = new AsistenteComando(nave);

            if (this.centroDeControl != null) {
                this.centroDeControl.registrar(asistente);
                this.centroDeControl.seleccionar(asistente.getIdNave());
            }

            if (this.controladorNave != null) {
                this.controladorNave.naveCreada(asistente);
            }
        } catch (NaveYaRegistradaException | NaveInexistenteException e) {
            this.vistaCrearNave.mostrarError(e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        if (IVistaCrearNave.SELECCIONAR_EXPLORADORA.equals(comando)) {
            this.vistaCrearNave.setTipoSeleccionado(TipoNave.EXPLORADORA);
        } else if (IVistaCrearNave.SELECCIONAR_CARGUERA.equals(comando)) {
            this.vistaCrearNave.setTipoSeleccionado(TipoNave.CARGUERO);
        } else if (IVistaCrearNave.SELECCIONAR_COMBATE.equals(comando)) {
            this.vistaCrearNave.setTipoSeleccionado(TipoNave.COMBATE);
        } else if (IVistaCrearNave.ACEPTAR.equals(comando)) {
            crearNave();
        }
    }
}
