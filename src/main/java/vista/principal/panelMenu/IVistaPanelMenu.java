/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package vista.principal.panelMenu;

import controlador.principal.ControladorPrincipal;

/**
 *
 * @author Damia
 */
public interface IVistaPanelMenu {
    public String CREAR_NAVE = "CREAR_NAVE";
    public String TRIPULACION = "TRIPULACION";
    public String TRIPULANTES = "TRIPULANTES";
    public String CONTROL_NAVE = "CONTROL_NAVE";
    public String VISOR = "VISOR";
    public String CERRAR_SESION = "CERRAR_SESION";

    
    
    public void setControlador(ControladorPrincipal controlador);

    public void habilitarTripulacion(boolean habilitar);

    public void habilitarTripulantes(boolean habilitar);

    public void habilitarControlNave(boolean habilitar);
}