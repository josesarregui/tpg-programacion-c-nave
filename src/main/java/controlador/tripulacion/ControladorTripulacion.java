
package controlador.tripulacion;

import controlador.principal.ControladorPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vista.principal.PanelTripulacion.IVistaTripulacion;

/**
 *
 */
public class ControladorTripulacion implements ActionListener{
    private IVistaTripulacion vista;
    private ControladorPrincipal controladorPrincipal;
    
    
    
    public ControladorTripulacion(IVistaTripulacion vista, ControladorPrincipal controladorPrincipal) {
        this.vista = vista;
        this.vista.setControlador(this);
        
        this.controladorPrincipal = controladorPrincipal;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
