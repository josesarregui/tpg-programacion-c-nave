package vista.principal.panelnave;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import vista.principal.panelcrearnave.IVistaCrearNave;
import vista.principal.panelcrearnave.VistaCrearNave;
import vista.principal.paneldetallesnave.IVistaDetallesNave;
import vista.principal.paneldetallesnave.VistaDetallesNave;

/**
 * Panel contenedor de la sección Nave: alterna entre la creación y los detalles de la nave
 * conservando el fondo espacial animado compartido por ambas pantallas.
 */
public class VistaNave extends JPanel implements IVistaNave {

    private final CardLayout cardLayout;

    private final VistaCrearNave vistaCrearNave;
    private final VistaDetallesNave vistaDetallesNave;

    private final ImageIcon fondoAnimado;
    private final Image imagenFondo;

    public VistaNave() {
        URL rutaFondo = getClass().getResource("/imagenes/Fondo-nave.gif");

        if (rutaFondo == null) {
            throw new RuntimeException("No se encontró la imagen de fondo de Nave.");
        }

        this.fondoAnimado = new ImageIcon(rutaFondo);
        this.imagenFondo = this.fondoAnimado.getImage();

        this.cardLayout = new CardLayout();
        this.setLayout(this.cardLayout);

        this.vistaCrearNave = new VistaCrearNave();
        this.vistaDetallesNave = new VistaDetallesNave();

        this.vistaCrearNave.setOpaque(false);
        this.vistaDetallesNave.setOpaque(false);

        this.add(this.vistaCrearNave, IVistaNave.CREAR_NAVE);
        this.add(this.vistaDetallesNave, IVistaNave.DETALLE_NAVE);

        this.mostrarCrearNave();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(8, 15, 30));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.drawImage(
                this.imagenFondo,
                0,
                0,
                getWidth(),
                getHeight(),
                this
        );

        g.setColor(new Color(8, 15, 30, 55));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void mostrarCrearNave() {
        this.cardLayout.show(this, IVistaNave.CREAR_NAVE);
    }

    @Override
    public void mostrarDetalleNave() {
        this.cardLayout.show(this, IVistaNave.DETALLE_NAVE);
    }

    @Override
    public IVistaCrearNave getVistaCrearNave() {
        return this.vistaCrearNave;
    }

    @Override
    public IVistaDetallesNave getVistaDetallesNave() {
        return this.vistaDetallesNave;
    }
}
