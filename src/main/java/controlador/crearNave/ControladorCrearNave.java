
package controlador.crearNave;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controlador.principal.ControladorPrincipal;
import modelo.nave.Nave;
import modelo.nave.NaveFactory;
import modelo.nave.TipoNave;
import vista.principal.PanelCrearNave.IVistaCrearNave;

/**
 *
 */
public class ControladorCrearNave implements ActionListener{
    private IVistaCrearNave vista;
    private ControladorPrincipal controladorPrincipal;
    
    private NaveFactory fabrica;
    private Nave nave;
    
    
    public ControladorCrearNave(IVistaCrearNave vista, ControladorPrincipal controladorPrincipal) {
        this.vista = vista;
        this.vista.setControlador(this);
        
        this.fabrica = new NaveFactory();
        this.controladorPrincipal = controladorPrincipal;
    }

    
    private void crearNave() {
        TipoNave tipo = vista.getTipoSeleccionado();

        if (tipo == null) {
            this.vista.mostrarError("Debe seleccionar una nave.");
        }else{
            this.nave = fabrica.crearNave(tipo);
            this.vista.mostrarMensaje("Nave creada correctamente.");
            
            this.vista.bloquearSeleccion();

            if (this.controladorPrincipal != null) {
                this.controladorPrincipal.naveCreada();
            }
        }
    }

    public Nave getNave() {
        return this.nave;
    }
    
    
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        if (IVistaCrearNave.SELECCIONAR_EXPLORADORA.equals(comando)) {
            this.vista.setTipoSeleccionado(TipoNave.EXPLORADORA);

        } else if (IVistaCrearNave.SELECCIONAR_CARGUERA.equals(comando)) {
            this.vista.setTipoSeleccionado(TipoNave.CARGUERO);

        } else if (IVistaCrearNave.SELECCIONAR_COMBATE.equals(comando)) {
            this.vista.setTipoSeleccionado(TipoNave.COMBATE);

        } else if (IVistaCrearNave.ACEPTAR.equals(comando)) {
            this.crearNave();
        }
    }
    
    
}