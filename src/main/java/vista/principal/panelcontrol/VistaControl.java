package vista.principal.panelcontrol;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import vista.principal.panelcontrol.panelcursomision.IVistaCursoMision;
import vista.principal.panelcontrol.panelcursomision.VistaCursoMision;
import vista.principal.panelcontrol.panelrecursos.IVistaRecursos;
import vista.principal.panelcontrol.panelrecursos.VistaRecursos;
import vista.principal.panelcontrol.panelresultadosmision.IVistaResultadosMision;
import vista.principal.panelcontrol.panelresultadosmision.VistaResultadosMision;
import vista.principal.panelcontrol.panelselectmision.IVistaSelectMision;
import vista.principal.panelcontrol.panelselectmision.VistaSelectMision;

/**
 * Panel contenedor de la sección Control de la Nave (Enunciado E2, Vistas, punto 4).
 * Arriba muestra siempre los recursos de la nave; debajo alterna las etapas del ciclo de misión:
 * 1. Selección de Misión y Bitácora (SELECT_MISION)
 * 2. Misión en Curso, con el tablero en tiempo real (MISION_CURSO)
 * 3. Historial de informes finales (RESULTADOS_MISION)
 */
public class VistaControl extends JPanel implements IVistaControl {

    private final CardLayout cardLayout;
    private final JPanel panelEtapas;
    private final VistaRecursos vistaRecursos;
    private final VistaSelectMision vistaSelectMision;
    private final VistaCursoMision vistaCursoMision;
    private final VistaResultadosMision vistaResultadosMision;

    private final Image imagenFondo;

    public VistaControl() {
        URL rutaFondo = VistaControl.class.getResource("/imagenes/Fondo-control-nave.gif");
        if (rutaFondo == null) {
            rutaFondo = VistaControl.class.getResource("/imagenes/FondoEspacio.gif");
        }
        this.imagenFondo = rutaFondo != null ? new ImageIcon(rutaFondo).getImage() : null;

        this.setLayout(new BorderLayout());

        this.vistaRecursos = new VistaRecursos();
        this.vistaRecursos.setBorder(new EmptyBorder(12, 24, 0, 24));
        this.add(this.vistaRecursos, BorderLayout.NORTH);

        this.cardLayout = new CardLayout();
        this.panelEtapas = new JPanel(this.cardLayout);
        this.panelEtapas.setOpaque(false);

        this.vistaSelectMision = new VistaSelectMision();
        this.vistaSelectMision.setOpaque(false);
        this.vistaCursoMision = new VistaCursoMision();
        this.vistaResultadosMision = new VistaResultadosMision();

        this.panelEtapas.add(this.vistaSelectMision, IVistaControl.SELECT_MISION);
        this.panelEtapas.add(this.vistaCursoMision, IVistaControl.MISION_CURSO);
        this.panelEtapas.add(this.vistaResultadosMision, IVistaControl.RESULTADOS_MISION);
        this.add(this.panelEtapas, BorderLayout.CENTER);

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
        this.cardLayout.show(this.panelEtapas, IVistaControl.SELECT_MISION);
    }

    @Override
    public void mostrarMisionCurso() {
        this.cardLayout.show(this.panelEtapas, IVistaControl.MISION_CURSO);
    }

    @Override
    public void mostrarResultadosMision() {
        this.cardLayout.show(this.panelEtapas, IVistaControl.RESULTADOS_MISION);
    }

    @Override
    public IVistaRecursos getVistaRecursos() {
        return this.vistaRecursos;
    }

    @Override
    public IVistaSelectMision getVistaSelectMision() {
        return this.vistaSelectMision;
    }

    @Override
    public IVistaCursoMision getVistaCursoMision() {
        return this.vistaCursoMision;
    }

    @Override
    public IVistaResultadosMision getVistaResultadosMision() {
        return this.vistaResultadosMision;
    }
}
