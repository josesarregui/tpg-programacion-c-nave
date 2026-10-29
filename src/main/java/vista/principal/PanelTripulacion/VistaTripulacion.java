package vista.principal.paneltripulacion;

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import vista.principal.panelcreartripulacion.IVistaCrearTripulacion;
import vista.principal.panelcreartripulacion.VistaCrearTripulacion;
import vista.principal.paneldetallestripulacion.IVistaDetallesTripulacion;
import vista.principal.paneldetallestripulacion.VistaDetallesTripulacion;

/**
 * Panel contenedor de la sección Tripulación: alterna entre el armado y los detalles de la dotación.
 */
public class VistaTripulacion extends JPanel implements IVistaTripulacion {

    private final CardLayout cardLayout;

    private final VistaCrearTripulacion vistaCrearTripulacion;
    private final VistaDetallesTripulacion vistaDetallesTripulacion;

    private final ImageIcon fondoAnimado;
    private final Image imagenFondo;

    public VistaTripulacion() {
        // =========================
        // FONDO GIF
        // =========================
        URL rutaFondo = getClass().getResource("/imagenes/Fondo-tripulacion.gif");

        if (rutaFondo == null) {
            throw new RuntimeException("No se encontró el fondo de Tripulación.");
        }

        this.fondoAnimado = new ImageIcon(rutaFondo);
        this.imagenFondo = this.fondoAnimado.getImage();

        // =========================
        // CARD LAYOUT
        // =========================
        this.cardLayout = new CardLayout();
        this.setLayout(this.cardLayout);

        this.vistaCrearTripulacion = new VistaCrearTripulacion();
        this.vistaDetallesTripulacion = new VistaDetallesTripulacion();

        // Muy importante:
        // las vistas internas deben ser transparentes
        this.vistaCrearTripulacion.setOpaque(false);
        this.vistaDetallesTripulacion.setOpaque(false);

        this.add(this.vistaCrearTripulacion, IVistaTripulacion.CREAR_TRIPULACION);
        this.add(this.vistaDetallesTripulacion, IVistaTripulacion.DETALLES_TRIPULACION);

        this.mostrarCrearTripulacion();
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
    public void mostrarCrearTripulacion() {
        this.cardLayout.show(this, IVistaTripulacion.CREAR_TRIPULACION);
    }

    @Override
    public void mostrarDetalleTripulacion() {
        this.cardLayout.show(this, IVistaTripulacion.DETALLES_TRIPULACION);
    }

    @Override
    public IVistaCrearTripulacion getVistaCrearTripulacion() {
        return this.vistaCrearTripulacion;
    }

    @Override
    public IVistaDetallesTripulacion getVistaDetallesTripulacion() {
        return this.vistaDetallesTripulacion;
    }
}
