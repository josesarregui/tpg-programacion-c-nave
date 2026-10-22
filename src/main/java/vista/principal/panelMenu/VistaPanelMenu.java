/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista.principal.panelMenu;

import controlador.principal.ControladorPrincipal;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author Damia
 */
public class VistaPanelMenu extends JPanel implements IVistaPanelMenu{
    private JButton bCrearNave;
    private JButton bTripulacion;
    private JButton bTripulantes;
    private JButton bControlNave;
    private JButton bVisor;
    private JButton bCerrarSesion;

    public VistaPanelMenu() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.bCrearNave = new JButton("Crear Nave");
        this.bTripulacion = new JButton("Tripulacion");
        this.bTripulantes = new JButton("Tripulantes");
        this.bControlNave = new JButton("Control Nave");
        this.bVisor = new JButton("Visor");
        this.bCerrarSesion = new JButton("Cerrar sesión");

        this.bCrearNave.setActionCommand(IVistaPanelMenu.CREAR_NAVE);
        this.bTripulacion.setActionCommand(IVistaPanelMenu.TRIPULACION);
        this.bTripulantes.setActionCommand(IVistaPanelMenu.TRIPULANTES);
        this.bControlNave.setActionCommand(IVistaPanelMenu.CONTROL_NAVE);
        this.bVisor.setActionCommand(IVistaPanelMenu.VISOR);
        this.bCerrarSesion.setActionCommand(IVistaPanelMenu.CERRAR_SESION);

        Dimension tamBoton = new Dimension(180, 45);
        JButton[] botones = {
            this.bCrearNave,
            this.bTripulacion,
            this.bTripulantes,
            this.bControlNave,
            this.bVisor,
            this.bCerrarSesion
        };

        for (JButton boton : botones) {
            boton.setMaximumSize(tamBoton);
            boton.setPreferredSize(tamBoton);
            boton.setAlignmentX(CENTER_ALIGNMENT);
        }

        // Al principio todavia no hay nave
        this.bTripulacion.setEnabled(false);
        this.bTripulantes.setEnabled(false);
        this.bControlNave.setEnabled(false);

        this.add(Box.createVerticalStrut(30));

        this.add(bCrearNave);
        this.add(Box.createVerticalStrut(15));

        this.add(bTripulacion);
        this.add(Box.createVerticalStrut(15));

        this.add(bTripulantes);
        this.add(Box.createVerticalStrut(15));

        this.add(bControlNave);
        this.add(Box.createVerticalStrut(15));

        this.add(bVisor);
        this.add(Box.createVerticalGlue());

        this.add(bCerrarSesion);
        this.add(Box.createVerticalStrut(30));
    }

    @Override
    public void setControlador(ControladorPrincipal controlador) {
        this.bCrearNave.addActionListener(controlador);
        this.bTripulacion.addActionListener(controlador);
        this.bTripulantes.addActionListener(controlador);
        this.bControlNave.addActionListener(controlador);
        this.bVisor.addActionListener(controlador);
        this.bCerrarSesion.addActionListener(controlador);
    }

    @Override
    public void habilitarTripulacion(boolean habilitar) {
        bTripulacion.setEnabled(habilitar);
    }

    @Override
    public void habilitarTripulantes(boolean habilitar) {
        bTripulantes.setEnabled(habilitar);
    }

    @Override
    public void habilitarControlNave(boolean habilitar) {
        bControlNave.setEnabled(habilitar);
    }
}