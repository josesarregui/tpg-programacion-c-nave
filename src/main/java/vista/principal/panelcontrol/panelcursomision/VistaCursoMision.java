package vista.principal.panelcontrol.panelcursomision;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import modelo.asistente.Asistente;
import vista.principal.panelcontrol.EstiloControl;

/**
 * Misión en Curso: tablero de 20 × 20 en tiempo real, consola con los eventos del asistente y controles para
 * cancelar la misión, ver los informes o volver a la selección.
 *
 * Observa al asistente (patrón Observer, Versión 2 de la cátedra): la consola agrega cada mensaje recibido (modelo
 * push). Los componentes se modifican siempre en el hilo de eventos de Swing: las notificaciones y los avisos de fin
 * o de error, que llegan desde el hilo de la misión, se delegan con invokeLater.
 */
public class VistaCursoMision extends JPanel implements IVistaCursoMision {

    private final VistaTablero vistaTablero;
    private final JLabel lblMision;
    private final JLabel lblEstado;
    private final JTextArea areaConsola;
    private final JButton btnCancelar;
    private final JButton btnVerResultados;
    private final JButton btnVolver;

    /**
     * Asistente observado. Se asigna antes de suscribirse y se anula después de quitarse; el hilo que notifica lo ve
     * actualizado porque el asistente sincroniza la suscripción y la notificación.
     */
    private Asistente asistente;

    public VistaCursoMision() {
        this.setOpaque(false);
        this.setLayout(new BorderLayout(0, 12));
        this.setBorder(new EmptyBorder(12, 24, 16, 24));

        // Encabezado: misión y estado
        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 2));
        encabezado.setOpaque(false);
        this.lblMision = EstiloControl.crearEtiqueta("Sin misión en curso", Font.BOLD, 20, Color.WHITE);
        this.lblEstado = EstiloControl.crearEtiqueta("Seleccione una misión para comenzar.", Font.PLAIN, 13,
                new Color(186, 230, 253));
        encabezado.add(this.lblMision);
        encabezado.add(this.lblEstado);
        this.add(encabezado, BorderLayout.NORTH);

        // Tablero
        JPanel tarjetaTablero = EstiloControl.crearPanelTranslucido(EstiloControl.FONDO_TARJETA,
                EstiloControl.BORDE_TARJETA);
        tarjetaTablero.setLayout(new BorderLayout(0, 8));
        tarjetaTablero.add(EstiloControl.crearEncabezadoTarjeta("🛰️", "Tablero en tiempo real",
                "Universo de 20 x 20 actualizado en cada turno"), BorderLayout.NORTH);
        this.vistaTablero = new VistaTablero();
        tarjetaTablero.add(this.vistaTablero, BorderLayout.CENTER);

        // Consola de eventos
        JPanel tarjetaConsola = EstiloControl.crearPanelTranslucido(EstiloControl.FONDO_TARJETA,
                EstiloControl.BORDE_TARJETA);
        tarjetaConsola.setLayout(new BorderLayout(0, 8));
        tarjetaConsola.add(EstiloControl.crearEncabezadoTarjeta("📡", "Eventos de la misión",
                "Bitácora en vivo del asistente de comando"), BorderLayout.NORTH);
        this.areaConsola = new JTextArea();
        this.areaConsola.setEditable(false);
        this.areaConsola.setLineWrap(true);
        this.areaConsola.setWrapStyleWord(true);
        this.areaConsola.setBackground(new Color(8, 15, 30));
        this.areaConsola.setForeground(new Color(226, 232, 240));
        this.areaConsola.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(this.areaConsola);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(56, 189, 248, 60), 1, true));
        tarjetaConsola.add(scroll, BorderLayout.CENTER);

        JPanel centro = new JPanel(new GridLayout(1, 2, 18, 0));
        centro.setOpaque(false);
        centro.add(tarjetaTablero);
        centro.add(tarjetaConsola);
        this.add(centro, BorderLayout.CENTER);

        // Controles
        this.btnCancelar = EstiloControl.crearBotonEstilizado("⛔ Cancelar misión", new Color(153, 27, 27, 220),
                new Color(220, 38, 38, 240), EstiloControl.ROJO);
        this.btnCancelar.setActionCommand(COMANDO_CANCELAR_MISION);
        this.btnVerResultados = EstiloControl.crearBotonEstilizado("📋 Ver informes", new Color(14, 116, 144, 220),
                new Color(6, 182, 212, 240), EstiloControl.CELESTE);
        this.btnVerResultados.setActionCommand(COMANDO_VER_RESULTADOS);
        this.btnVolver = EstiloControl.crearBotonEstilizado("↩ Volver a misiones", new Color(30, 58, 138, 200),
                new Color(37, 99, 235, 230), EstiloControl.CELESTE);
        this.btnVolver.setActionCommand(COMANDO_VOLVER);

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controles.setOpaque(false);
        controles.add(this.btnCancelar);
        controles.add(this.btnVerResultados);
        controles.add(this.btnVolver);
        this.add(controles, BorderLayout.SOUTH);

        habilitarControles(false);
    }

    @Override
    public void setControlador(ActionListener controlador) {
        this.btnCancelar.addActionListener(controlador);
        this.btnVerResultados.addActionListener(controlador);
        this.btnVolver.addActionListener(controlador);
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
     * Agrega a la consola el mensaje recibido (modelo push), en el hilo de eventos de Swing.
     */
    @Override
    public void actualizar(String mensaje) {
        SwingUtilities.invokeLater(() -> agregarLinea(mensaje));
    }

    @Override
    public IVistaTablero getVistaTablero() {
        return this.vistaTablero;
    }

    @Override
    public void misionIniciada(String codigo, String nombre) {
        this.areaConsola.setText("");
        this.lblMision.setText(codigo + " — " + nombre);
        this.lblEstado.setText("Misión en curso: la nave avanza turno a turno.");
        this.lblEstado.setForeground(EstiloControl.CELESTE);
        habilitarControles(true);
    }

    @Override
    public void misionFinalizada(String resumen) {
        SwingUtilities.invokeLater(() -> {
            this.lblEstado.setText(resumen);
            this.lblEstado.setForeground(EstiloControl.AMARILLO);
            agregarLinea(">> " + resumen);
            habilitarControles(false);
        });
    }

    @Override
    public void mostrarError(String mensaje) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, mensaje, "Misión",
                JOptionPane.ERROR_MESSAGE));
    }

    /**
     * Con la misión en curso sólo se puede cancelar; terminada, sólo ver los informes o volver.
     */
    private void habilitarControles(boolean enCurso) {
        this.btnCancelar.setEnabled(enCurso);
        this.btnVerResultados.setEnabled(!enCurso);
        this.btnVolver.setEnabled(!enCurso);
    }

    private void agregarLinea(String linea) {
        this.areaConsola.append(linea + "\n");
        this.areaConsola.setCaretPosition(this.areaConsola.getDocument().getLength());
    }

    // Getters para las pruebas del paquete (se invocan en el hilo de eventos)

    String getTextoConsola() {
        return this.areaConsola.getText();
    }

    String getTextoEstado() {
        return this.lblEstado.getText();
    }

    JButton getBtnCancelar() {
        return this.btnCancelar;
    }

    JButton getBtnVolver() {
        return this.btnVolver;
    }
}
