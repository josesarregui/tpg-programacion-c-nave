package vista.principal.panelcontrol.panelrecursos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import modelo.asistente.Asistente;
import vista.principal.panelcontrol.EstiloControl;

/**
 * Franja con el estado actual de los recursos de la nave en la sección Control de la Nave (Enunciado E2, Vistas,
 * punto 4).
 *
 * Observa al asistente (patrón Observer, Versión 2 de la cátedra): en {@link #actualizar(String)} consulta sus
 * getters (modelo pull) en el hilo que notifica, para que los valores correspondan al evento notificado, y modifica
 * las barras después en el hilo de eventos de Swing con invokeLater.
 */
public class VistaRecursos extends JPanel implements IVistaRecursos {

    private final JProgressBar barraCombustible;
    private final JProgressBar barraEnergia;
    private final JProgressBar barraDesgaste;
    private final JLabel lblGemas;
    private final JLabel lblMotor;
    private final JLabel lblEstado;

    /**
     * Asistente observado. Se asigna antes de suscribirse y se anula después de quitarse; el hilo que notifica lo ve
     * actualizado porque el asistente sincroniza la suscripción y la notificación.
     */
    private Asistente asistente;

    public VistaRecursos() {
        this.setOpaque(false);
        this.setLayout(new BorderLayout());

        JPanel tarjeta = EstiloControl.crearPanelTranslucido(EstiloControl.FONDO_TARJETA, EstiloControl.BORDE_TARJETA);
        tarjeta.setLayout(new GridLayout(1, 6, 14, 0));

        this.barraCombustible = crearBarra(new Color(56, 189, 248));
        this.barraEnergia = crearBarra(new Color(52, 211, 153));
        this.barraDesgaste = crearBarra(new Color(251, 146, 60));
        this.lblGemas = EstiloControl.crearEtiqueta("0", Font.BOLD, 15, Color.WHITE);
        this.lblMotor = EstiloControl.crearEtiqueta("-", Font.BOLD, 13, Color.WHITE);
        this.lblEstado = EstiloControl.crearEtiqueta("-", Font.BOLD, 13, EstiloControl.GRIS);

        tarjeta.add(crearCelda("⛽ Combustible", this.barraCombustible));
        tarjeta.add(crearCelda("⚡ Energía", this.barraEnergia));
        tarjeta.add(crearCelda("🔧 Desgaste", this.barraDesgaste));
        tarjeta.add(crearCelda("💎 Gemas", this.lblGemas));
        tarjeta.add(crearCelda("🌀 Motor Warp", this.lblMotor));
        tarjeta.add(crearCelda("🛡️ Estado", this.lblEstado));

        this.add(tarjeta, BorderLayout.CENTER);
    }

    @Override
    public void observar(Asistente asistente) {
        assert asistente != null : "La vista debe observar un asistente.";
        dejarDeObservar();
        this.asistente = asistente;
        this.asistente.agregarObservador(this);
    }

    @Override
    public void dejarDeObservar() {
        Asistente observado = this.asistente;
        if (observado != null) {
            observado.quitarObservador(this);
            this.asistente = null;
        }
    }

    /**
     * @pre La vista observa un asistente (sólo ese asistente la notifica).
     */
    @Override
    public void actualizar(String mensaje) {
        Asistente observado = this.asistente;
        assert observado != null : "La vista recibió una notificación sin observar un asistente.";
        final int combustible = observado.getCombustible();
        final int energia = observado.getEnergia();
        final int desgaste = observado.getDesgaste();
        final int gemas = observado.getGemas();
        final String estadoMotor = observado.getEstadoMotor();
        final boolean listaParaOperar = observado.naveListaParaOperar();
        final boolean destruida = observado.naveDestruida();
        SwingUtilities.invokeLater(() -> mostrarRecursos(combustible, energia, desgaste, gemas, estadoMotor,
                listaParaOperar, destruida));
    }

    @Override
    public void mostrarRecursos(int combustible, int energia, int desgaste, int gemas, String estadoMotor,
            boolean listaParaOperar, boolean destruida) {
        actualizarBarra(this.barraCombustible, combustible);
        actualizarBarra(this.barraEnergia, energia);
        actualizarBarra(this.barraDesgaste, desgaste);
        this.lblGemas.setText(String.valueOf(gemas));
        this.lblMotor.setText(estadoMotor);
        if (destruida) {
            this.lblEstado.setText("Destruida");
            this.lblEstado.setForeground(EstiloControl.ROJO);
        } else if (listaParaOperar) {
            this.lblEstado.setText("Operativa");
            this.lblEstado.setForeground(EstiloControl.VERDE);
        } else {
            this.lblEstado.setText("No disponible");
            this.lblEstado.setForeground(EstiloControl.AMARILLO);
        }
    }

    private JProgressBar crearBarra(Color color) {
        JProgressBar barra = new JProgressBar(0, 100);
        barra.setStringPainted(true);
        barra.setForeground(color);
        barra.setBackground(new Color(30, 41, 59));
        barra.setFont(new Font("Segoe UI", Font.BOLD, 11));
        barra.setPreferredSize(new Dimension(110, 18));
        return barra;
    }

    private void actualizarBarra(JProgressBar barra, int valor) {
        barra.setValue(valor);
        barra.setString(String.valueOf(valor));
    }

    private JPanel crearCelda(String titulo, javax.swing.JComponent valor) {
        JPanel celda = new JPanel(new BorderLayout(0, 4));
        celda.setOpaque(false);
        celda.add(EstiloControl.crearEtiqueta(titulo, Font.PLAIN, 11, EstiloControl.GRIS), BorderLayout.NORTH);
        celda.add(valor, BorderLayout.CENTER);
        return celda;
    }

    // Getters para las pruebas del paquete

    String getTextoCombustible() {
        return this.barraCombustible.getString();
    }

    String getTextoEnergia() {
        return this.barraEnergia.getString();
    }

    String getTextoEstado() {
        return this.lblEstado.getText();
    }
}
