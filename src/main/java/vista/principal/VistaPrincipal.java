
package vista.principal;

import controlador.principal.ControladorPrincipal;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import vista.principal.PanelCrearNave.VistaCrearNave;
import vista.principal.panelMenu.VistaPanelMenu;

/**
 *
 */
public class VistaPrincipal extends JFrame implements IVistaPrincipal{
    private VistaPanelMenu panelMenu;

    private JPanel panelContenido;
    private CardLayout cardLayout;

    private JPanel panelInicio;
    private VistaCrearNave panelCrearNave;
    private JPanel panelTripulacion;
    private JPanel panelTripulantes;
    private JPanel panelControlNave;
    private JPanel panelVisor;

    
    
    public VistaPrincipal() {
        super("Viaje Interestelar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setLayout(new BorderLayout());

        // =========================
        // MENU LATERAL
        // =========================

        this.panelMenu = new VistaPanelMenu();
        this.add(panelMenu, BorderLayout.WEST);


        // =========================
        // PANEL DERECHO
        // =========================

        this.cardLayout = new CardLayout();
        this.panelContenido = new JPanel(cardLayout);


        // =========================
        // PANEL INICIO
        // =========================

        this.panelInicio = crearPanelTemporal("Bienvenido a Viaje Interestelar");


        // =========================
        // PANEL CREAR NAVE
        // =========================

        this.panelCrearNave = new VistaCrearNave();


        // =========================
        // PANEL TRIPULACION
        // =========================

        this.panelTripulacion = crearPanelTemporal("Creacion de Tripulacion");


        // =========================
        // PANEL TRIPULANTES
        // =========================

        this.panelTripulantes = crearPanelTemporal("Listado de Tripulantes");


        // =========================
        // PANEL CONTROL NAVE
        // =========================

        this.panelControlNave = crearPanelTemporal("Control de Nave");


        // =========================
        // PANEL VISOR
        // =========================

        this.panelVisor = crearPanelTemporal("Visor del Universo Paralelo");


        // =========================
        // AGREGAR CARDS
        // =========================

        this.panelContenido.add(this.panelInicio, IVistaPrincipal.PANEL_INICIO);
        this.panelContenido.add(this.panelCrearNave, IVistaPrincipal.PANEL_CREAR_NAVE);
        this.panelContenido.add(this.panelTripulacion, IVistaPrincipal.PANEL_TRIPULACION);
        this.panelContenido.add(this.panelTripulantes, IVistaPrincipal.PANEL_TRIPULANTES);
        this.panelContenido.add(this.panelControlNave, IVistaPrincipal.PANEL_CONTROL_NAVE);
        this.panelContenido.add(this.panelVisor, VistaPrincipal.PANEL_VISOR);

        this.add(this.panelContenido, BorderLayout.CENTER);


        // =========================
        // PANEL INICIAL
        // =========================

        this.cardLayout.show(panelContenido, IVistaPrincipal.PANEL_INICIO);
    }


    private JPanel crearPanelTemporal(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(10, 15, 30));

        JLabel label = new JLabel(titulo,SwingConstants.CENTER);
        label.setForeground(Color.WHITE);

        panel.add(label,BorderLayout.CENTER);
        return panel;
    }
    
    
    
    @Override
    public VistaCrearNave getPanelCreacionNave() {
        return this.panelCrearNave;
    }
    
    @Override
    public VistaPanelMenu getPanelMenu() {
        return this.panelMenu;
    }
    
    @Override
    public void habilitarTripulacion(boolean habilitar) {
        this.panelMenu.habilitarTripulacion(habilitar);
    }

    @Override
    public void setControlador(ControladorPrincipal controlador) {
        this.panelMenu.setControlador(controlador);
    }

    @Override
    public void mostrarPanel(String nombrePanel) {
        cardLayout.show(panelContenido,nombrePanel);
    }

    @Override
    public void arrancar() {
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void cerrar() {
        dispose();
    }
}