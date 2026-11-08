package vista.principal.panelcontrol;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import vista.principal.panelcontrol.panelselectmision.IVistaSelectMision;
import vista.principal.panelcontrol.panelselectmision.VistaSelectMision;

/**
 * Panel contenedor principal para la seccion Control de Nave y Ejecucion de Misiones.
 * Administra el flujo del ciclo de misiones.
 * 1. Selección de Mision y Bitacora (SELECT_MISION)
 * 2. Mision en Curso (MISION_CURSO)
 * 3. Resultados de la Mision (RESULTADOS_MISION)
 */
public class VistaControl extends JPanel implements IVistaControl {

    private final CardLayout cardLayout;
    private final VistaSelectMision vistaSelectMision;
    private final JPanel panelMisionCurso;
    private final JPanel panelResultadosMision;

    private final ImageIcon fondoAnimado;
    private final Image imagenFondo;

    public VistaControl() {
        URL rutaFondo = getClass().getResource("/imagenes/Fondo-control-nave.gif");
        if (rutaFondo == null) {
            rutaFondo = getClass().getResource("/imagenes/FondoEspacio.gif");
        }
        if (rutaFondo != null) {
            this.fondoAnimado = new ImageIcon(rutaFondo);
            this.imagenFondo = this.fondoAnimado.getImage();
        } else {
            this.fondoAnimado = null;
            this.imagenFondo = null;
        }

        this.cardLayout = new CardLayout();
        this.setLayout(this.cardLayout);

        this.vistaSelectMision = new VistaSelectMision();
        this.vistaSelectMision.setOpaque(false);

        this.panelMisionCurso = new JPanel();
        this.panelMisionCurso.setOpaque(false);

        this.panelResultadosMision = new JPanel();
        this.panelResultadosMision.setOpaque(false);

        this.add(this.vistaSelectMision, IVistaControl.SELECT_MISION);
        this.add(this.panelMisionCurso, IVistaControl.MISION_CURSO);
        this.add(this.panelResultadosMision, IVistaControl.RESULTADOS_MISION);

        this.mostrarSelectMision();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(8, 15, 30));
        g.fillRect(0, 0, getWidth(), getHeight());

        if (this.imagenFondo != null) {
            g.drawImage(this.imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }

        g.setColor(new Color(8, 15, 30, 90));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void mostrarSelectMision() {
        this.cardLayout.show(this, IVistaControl.SELECT_MISION);
    }

    @Override
    public void mostrarMisionCurso() {
        this.cardLayout.show(this, IVistaControl.MISION_CURSO);
    }

    @Override
    public void mostrarResultadosMision() {
        this.cardLayout.show(this, IVistaControl.RESULTADOS_MISION);
    }

    @Override
    public IVistaSelectMision getVistaSelectMision() {
        return this.vistaSelectMision;
    }

    public JPanel getPanelMisionCurso() {
        return this.panelMisionCurso;
    }

    public JPanel getPanelResultadosMision() {
        return this.panelResultadosMision;
    }
}
