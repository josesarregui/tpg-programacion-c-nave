package vista.principal;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import vista.principal.panelmenu.IVistaPanelMenu;
import vista.principal.panelmenu.VistaPanelMenu;
import vista.principal.panelnave.IVistaNave;
import vista.principal.panelnave.VistaNave;
import vista.principal.paneltripulacion.IVistaTripulacion;
import vista.principal.paneltripulacion.VistaTripulacion;

/**
 * Ventana principal del sistema tras iniciar sesión.
 */
public class VistaPrincipal extends JFrame implements IVistaPrincipal {
    private final IVistaPanelMenu panelMenu;

    private final JPanel panelContenido;
    private final CardLayout cardLayout;

    private final VistaNave panelNave;
    private final VistaTripulacion panelTripulacion;
    private final JPanel panelControlNave;

    public VistaPrincipal() {
        super("Viaje Interestelar");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(new BorderLayout());

        // MENU LATERAL
        VistaPanelMenu menuLateral = new VistaPanelMenu();
        this.panelMenu = menuLateral;

        this.add(menuLateral, BorderLayout.WEST);

        // CARD LAYOUT PRINCIPAL
        this.cardLayout = new CardLayout();
        this.panelContenido = new JPanel(this.cardLayout);

        // Después se reemplaza panelControlNave por su vista específica.
        this.panelNave = new VistaNave();
        this.panelTripulacion = new VistaTripulacion();
        this.panelControlNave = new JPanel();

        this.panelContenido.add(this.panelNave, IVistaPrincipal.PANEL_NAVE);
        this.panelContenido.add(this.panelTripulacion, IVistaPrincipal.PANEL_TRIPULACION);
        this.panelContenido.add(this.panelControlNave, IVistaPrincipal.PANEL_CONTROL_NAVE);

        this.add(this.panelContenido, BorderLayout.CENTER);

        // Pantalla inicial
        this.cardLayout.show(this.panelContenido, IVistaPrincipal.PANEL_NAVE);
    }

    @Override
    public IVistaNave getPanelNave() {
        return this.panelNave;
    }

    @Override
    public IVistaTripulacion getPanelTripulacion() {
        return this.panelTripulacion;
    }

    @Override
    public void mostrarOperador(String nombreUsuario) {
        if (nombreUsuario != null && !nombreUsuario.isBlank()) {
            this.setTitle("Viaje Interestelar — Operador: " + nombreUsuario);
        }
    }

    @Override
    public void setControlador(ActionListener controlador) {
        this.panelMenu.setControlador(controlador);
    }

    @Override
    public void mostrarPanel(String nombrePanel) {
        this.cardLayout.show(this.panelContenido, nombrePanel);
    }

    @Override
    public void habilitarTripulacion(boolean habilitar) {
        this.panelMenu.habilitarTripulacion(habilitar);
    }

    @Override
    public void habilitarControlNave(boolean habilitar) {
        this.panelMenu.habilitarControlNave(habilitar);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void arrancar() {
        this.setSize(1200, 700);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void cerrar() {
        this.dispose();
    }
}
