package vista.principal.panelcontrol;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Componentes con el estilo visual común de la sección Control de la Nave (tarjetas translúcidas, encabezados y
 * botones). Sólo arma componentes Swing: no conoce el modelo ni los controladores. Se usa desde el hilo de eventos
 * de Swing.
 */
public final class EstiloControl {

    public static final Color FONDO_TARJETA = new Color(15, 23, 42, 185);
    public static final Color BORDE_TARJETA = new Color(56, 189, 248, 120);
    public static final Color CELESTE = new Color(56, 189, 248);
    public static final Color GRIS = new Color(148, 163, 184);
    public static final Color VERDE = new Color(74, 222, 128);
    public static final Color AMARILLO = new Color(251, 191, 36);
    public static final Color ROJO = new Color(248, 113, 113);

    private EstiloControl() {
    }

    public static JPanel crearPanelTranslucido(Color colorFondo, Color colorBorde) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(colorFondo);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBorde, 1, true),
                new EmptyBorder(14, 16, 14, 16)));
        return panel;
    }

    public static JPanel crearEncabezadoTarjeta(String icono, String titulo, String subtitulo) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblIcono = new JLabel(icono + " ");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lblIcono.setForeground(CELESTE);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));

        filaTitulo.add(lblIcono);
        filaTitulo.add(lblTitulo);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSub.setForeground(GRIS);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        header.add(filaTitulo);
        header.add(Box.createVerticalStrut(2));
        header.add(lblSub);
        return header;
    }

    public static JButton crearBotonEstilizado(String texto, Color normal, Color hover, Color borde) {
        JButton btn = new JButton(texto);
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 1, true),
                new EmptyBorder(6, 12, 6, 12)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(hover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(normal);
                }
            }
        });
        return btn;
    }

    /**
     * @return una etiqueta con la fuente y el color indicados.
     */
    public static JLabel crearEtiqueta(String texto, int estilo, int tamanio, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Segoe UI", estilo, tamanio));
        etiqueta.setForeground(color);
        return etiqueta;
    }
}
