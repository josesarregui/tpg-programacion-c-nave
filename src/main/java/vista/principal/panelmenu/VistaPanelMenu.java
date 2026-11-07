
package vista.principal.panelmenu;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 */
public class VistaPanelMenu extends JPanel implements IVistaPanelMenu {
    private JButton bCrearNave;
    private JButton bTripulacion;
    private JButton bControlNave;
    private JButton bVisor;
    private JButton bCerrarSesion;

    private Image imagenFondo;

    public VistaPanelMenu() {

        // =========================
        // FONDO
        // =========================

        URL rutaFondo = getClass().getResource("/imagenes/menu.png");

        if (rutaFondo == null) {
            throw new RuntimeException(
                    "No se encontró la imagen de fondo del menú."
            );
        }

        this.imagenFondo = new ImageIcon(rutaFondo).getImage();

        this.setOpaque(false);

        // =========================
        // LAYOUT
        // =========================

        this.setLayout(
                new BoxLayout(
                        this,
                        BoxLayout.Y_AXIS
                )
        );

        // =========================
        // BOTONES
        // =========================

        this.bCrearNave = crearBotonImagen(
                "/imagenes/botones/btn-nave.png"
        );

        this.bTripulacion = crearBotonImagen(
                "/imagenes/botones/btn-tripulacion.png"
        );

        this.bControlNave = crearBotonImagen(
                "/imagenes/botones/btn-control-nave.png"
        );

        this.bVisor = crearBotonImagen(
                "/imagenes/botones/btn-visor.png"
        );

        this.bCerrarSesion = crearBotonImagen(
                "/imagenes/botones/btn-cerrar-sesion.png"
        );

        // =========================
        // ACTION COMMANDS
        // =========================

        this.bCrearNave.setActionCommand(
                IVistaPanelMenu.NAVE
        );

        this.bTripulacion.setActionCommand(
                IVistaPanelMenu.TRIPULACION
        );

        this.bControlNave.setActionCommand(
                IVistaPanelMenu.CONTROL_NAVE
        );

        this.bVisor.setActionCommand(
                IVistaPanelMenu.VISOR
        );

        this.bCerrarSesion.setActionCommand(
                IVistaPanelMenu.CERRAR_SESION
        );

        // =========================
        // ESTADO INICIAL
        // =========================

        this.bTripulacion.setEnabled(false);
        this.bControlNave.setEnabled(false);

        // =========================
        // AGREGAR BOTONES
        // =========================

        this.add(
                Box.createVerticalStrut(30)
        );

        this.add(this.bCrearNave);

        this.add(
                Box.createVerticalStrut(15)
        );

        this.add(this.bTripulacion);

        this.add(
                Box.createVerticalStrut(15)
        );

        this.add(this.bControlNave);

        this.add(
                Box.createVerticalStrut(15)
        );

        this.add(this.bVisor);

        this.add(
                Box.createVerticalGlue()
        );

        this.add(this.bCerrarSesion);

        this.add(
                Box.createVerticalStrut(30)
        );
    }

    private JButton crearBotonImagen(String rutaImagen) {

        URL ruta = getClass().getResource(rutaImagen);

        if (ruta == null) {
            throw new RuntimeException(
                    "No se encontró la imagen: " + rutaImagen
            );
        }

        ImageIcon iconoOriginal =
                new ImageIcon(ruta);

        Image imagenEscalada =
                iconoOriginal.getImage().getScaledInstance(
                        180,
                        70,
                        Image.SCALE_SMOOTH
                );

        JButton boton =
                new JButton(
                        new ImageIcon(imagenEscalada)
                );

        Dimension tamBoton =
                new Dimension(180, 70);

        boton.setMaximumSize(tamBoton);
        boton.setPreferredSize(tamBoton);
        boton.setMinimumSize(tamBoton);

        boton.setAlignmentX(
                CENTER_ALIGNMENT
        );

        // Sacamos aspecto normal de JButton
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);

        return boton;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.drawImage(
                this.imagenFondo,
                0,
                0,
                getWidth(),
                getHeight(),
                this
        );
    }

    @Override
    public void setControlador(
            ActionListener controlador) {

        this.bCrearNave.addActionListener(
                controlador
        );

        this.bTripulacion.addActionListener(
                controlador
        );

        this.bControlNave.addActionListener(
                controlador
        );

        this.bVisor.addActionListener(
                controlador
        );

        this.bCerrarSesion.addActionListener(
                controlador
        );
    }

    @Override
    public void habilitarTripulacion(
            boolean habilitar) {

        this.bTripulacion.setEnabled(
                habilitar
        );
    }

    @Override
    public void habilitarControlNave(
            boolean habilitar) {

        this.bControlNave.setEnabled(
                habilitar
        );
    }
}