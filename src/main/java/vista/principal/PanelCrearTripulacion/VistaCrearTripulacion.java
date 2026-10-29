package vista.principal.panelcreartripulacion;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import modelo.tripulacion.Cargo;
import modelo.tripulacion.Origen;

/**
 * Vista para la creación y gestión de la tripulación de la nave.
 * Posee un diseño espacial translúcido (efecto glassmorphism) para que
 * se aprecie la animación del fondo del panel contenedor.
 */
public class VistaCrearTripulacion extends JPanel implements IVistaCrearTripulacion {

    private JTextField txtNombre;
    private JTextField txtAntiguedad;

    private JComboBox<Cargo> cbCargo;
    private JComboBox<Origen> cbOrigen;

    private JButton bAgregar;
    private JButton bAceptar;

    private DefaultListModel<String> modeloLista;
    private JList<String> listaTripulantes;
    private JLabel lblContador;
    private OyenteCamposTripulacion oyenteCampos;

    public VistaCrearTripulacion() {
        this.setLayout(new BorderLayout(0, 16));
        this.setOpaque(false);
        this.setBorder(new EmptyBorder(16, 32, 16, 32));

        // =========================
        // TITULO Y SUBTITULO
        // =========================
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setLayout(new BoxLayout(panelEncabezado, BoxLayout.Y_AXIS));
        panelEncabezado.setOpaque(false);

        JLabel titulo = new JLabel("ARMADO DE TRIPULACIÓN", SwingConstants.CENTER);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitulo = new JLabel("Recluta y asigna a los miembros de la tripulación a bordo", SwingConstants.CENTER);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setForeground(new Color(186, 230, 253));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panelEncabezado.add(titulo);
        panelEncabezado.add(Box.createVerticalStrut(4));
        panelEncabezado.add(subtitulo);

        this.add(panelEncabezado, BorderLayout.NORTH);

        // ===================================
        // TARJETA FORMULARIO (IZQUIERDA)
        // ===================================
        PanelTranslucido cardFormulario = new PanelTranslucido(
                18,
                new Color(15, 23, 42, 175),
                new Color(56, 189, 248, 120)
        );
        cardFormulario.setLayout(new BorderLayout(0, 14));
        cardFormulario.setBorder(new EmptyBorder(18, 22, 18, 22));

        // Encabezado de la tarjeta formulario
        JPanel headerFormulario = new JPanel();
        headerFormulario.setLayout(new BoxLayout(headerFormulario, BoxLayout.Y_AXIS));
        headerFormulario.setOpaque(false);

        JLabel tituloForm = new JLabel("📋 Nuevo Tripulante");
        tituloForm.setForeground(Color.WHITE);
        tituloForm.setFont(new Font("Segoe UI", Font.BOLD, 17));

        JLabel subForm = new JLabel("Completa los datos del nuevo oficial");
        subForm.setForeground(new Color(148, 163, 184));
        subForm.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        headerFormulario.add(tituloForm);
        headerFormulario.add(Box.createVerticalStrut(2));
        headerFormulario.add(subForm);

        cardFormulario.add(headerFormulario, BorderLayout.NORTH);

        // Campos de entrada
        this.txtNombre = new CampoTextoEstilizado(15);
        this.txtAntiguedad = new CampoTextoEstilizado(5);

        this.cbCargo = new ComboBoxEstilizado<>();
        this.cbCargo.addItem(Cargo.CAPITAN);
        this.cbCargo.addItem(Cargo.TENIENTE);
        this.cbCargo.addItem(Cargo.ALFEREZ);
        this.cbCargo.addItem(Cargo.CONSEJERO);

        this.cbOrigen = new ComboBoxEstilizado<>();
        for (Origen origen : Origen.values()) {
            this.cbOrigen.addItem(origen);
        }

        this.bAgregar = new BotonEstilizado(
                "➕  Agregar Tripulante",
                new Color(14, 116, 144, 210),
                new Color(6, 182, 212, 240),
                new Color(56, 189, 248, 210),
                12
        );
        this.bAgregar.setPreferredSize(new Dimension(100, 40));
        this.bAgregar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        this.bAgregar.setActionCommand(IVistaCrearTripulacion.AGREGAR);
        this.bAgregar.setEnabled(false);

        this.oyenteCampos = new OyenteCamposTripulacion(this);
        this.txtNombre.addKeyListener(this.oyenteCampos);
        this.txtAntiguedad.addKeyListener(this.oyenteCampos);

        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new BoxLayout(panelCampos, BoxLayout.Y_AXIS));
        panelCampos.setOpaque(false);

        panelCampos.add(crearSeccionCampo("Nombre", this.txtNombre));
        panelCampos.add(Box.createVerticalStrut(10));
        panelCampos.add(crearSeccionCampo("Rango / Cargo", this.cbCargo));
        panelCampos.add(Box.createVerticalStrut(10));
        panelCampos.add(crearSeccionCampo("Planeta de Origen", this.cbOrigen));
        panelCampos.add(Box.createVerticalStrut(10));
        panelCampos.add(crearSeccionCampo("Antigüedad (años)", this.txtAntiguedad));
        panelCampos.add(Box.createVerticalStrut(16));
        panelCampos.add(this.bAgregar);

        cardFormulario.add(panelCampos, BorderLayout.CENTER);

        // ===================================
        // TARJETA LISTA (DERECHA)
        // ===================================
        PanelTranslucido cardLista = new PanelTranslucido(
                18,
                new Color(15, 23, 42, 175),
                new Color(56, 189, 248, 120)
        );
        cardLista.setLayout(new BorderLayout(0, 12));
        cardLista.setBorder(new EmptyBorder(18, 22, 18, 22));

        // Encabezado de la lista
        JPanel headerLista = new JPanel(new BorderLayout(10, 0));
        headerLista.setOpaque(false);

        JLabel tituloLista = new JLabel("👥 Tripulación Actual");
        tituloLista.setForeground(Color.WHITE);
        tituloLista.setFont(new Font("Segoe UI", Font.BOLD, 17));

        this.lblContador = new JLabel("Sin tripulantes registrados");
        this.lblContador.setForeground(new Color(56, 189, 248));
        this.lblContador.setFont(new Font("Segoe UI", Font.BOLD, 12));
        this.lblContador.setHorizontalAlignment(SwingConstants.RIGHT);

        headerLista.add(tituloLista, BorderLayout.WEST);
        headerLista.add(this.lblContador, BorderLayout.EAST);

        cardLista.add(headerLista, BorderLayout.NORTH);

        // Componentes de la lista
        this.modeloLista = new DefaultListModel<>();
        this.listaTripulantes = new JList<>(this.modeloLista);
        this.listaTripulantes.setOpaque(false);
        this.listaTripulantes.setSelectionBackground(new Color(14, 116, 144, 200));
        this.listaTripulantes.setSelectionForeground(Color.WHITE);
        this.listaTripulantes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        this.listaTripulantes.setCellRenderer(crearRendererLista());

        JScrollPane scroll = new JScrollPane(this.listaTripulantes);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        estilizarScrollBar(scroll);

        cardLista.add(scroll, BorderLayout.CENTER);

        // =========================
        // PANEL CENTRAL
        // =========================
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 24, 0));
        panelCentral.setOpaque(false);
        panelCentral.add(cardFormulario);
        panelCentral.add(cardLista);

        this.add(panelCentral, BorderLayout.CENTER);

        // =========================
        // BOTON ACEPTAR (INFERIOR)
        // =========================
        this.bAceptar = new BotonEstilizado(
                "✓  Aceptar Tripulación",
                new Color(16, 185, 129, 215),
                new Color(34, 197, 94, 245),
                new Color(74, 222, 128, 220),
                14
        );
        this.bAceptar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        this.bAceptar.setPreferredSize(new Dimension(320, 44));
        this.bAceptar.setActionCommand(IVistaCrearTripulacion.ACEPTAR);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 4));
        panelInferior.setOpaque(false);
        panelInferior.add(this.bAceptar);

        this.add(panelInferior, BorderLayout.SOUTH);
    }

    /**
     * Crea un contenedor para un campo de entrada con su etiqueta estilizada encima.
     */
    private JPanel crearSeccionCampo(String textoEtiqueta, JComponent campo) {
        JPanel seccion = new JPanel(new BorderLayout(0, 4));
        seccion.setOpaque(false);
        seccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel etiqueta = new JLabel(textoEtiqueta);
        etiqueta.setForeground(new Color(224, 242, 254));
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 13));

        seccion.add(etiqueta, BorderLayout.NORTH);
        seccion.add(campo, BorderLayout.CENTER);
        return seccion;
    }

    /**
     * Renderer estilizado para cada elemento de la lista de tripulantes.
     */
    private ListCellRenderer<String> crearRendererLista() {
        return new ListCellRenderer<String>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JPanel itemPanel = new JPanel(new BorderLayout(10, 0)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        if (isSelected) {
                            g2.setColor(new Color(14, 116, 144, 220));
                        } else {
                            if (index % 2 == 0) {
                                g2.setColor(new Color(255, 255, 255, 22));
                            } else {
                                g2.setColor(new Color(255, 255, 255, 10));
                            }
                        }
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);

                        if (isSelected) {
                            g2.setColor(new Color(56, 189, 248, 220));
                            g2.setStroke(new BasicStroke(1.2f));
                            g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
                        }
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                itemPanel.setOpaque(false);
                itemPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

                String valStr = value != null ? value.toString() : "";
                JLabel lblTexto = new JLabel(valStr);
                lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lblTexto.setForeground(Color.WHITE);

                JLabel lblIcono = new JLabel();
                if (valStr.contains("Capitán")) {
                    lblIcono.setText("⭐");
                } else if (valStr.contains("Teniente")) {
                    lblIcono.setText("🛡️");
                } else if (valStr.contains("Alférez")) {
                    lblIcono.setText("🚀");
                } else if (valStr.contains("Consejero")) {
                    lblIcono.setText("🔮");
                } else {
                    lblIcono.setText("👤");
                }
                lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
                lblIcono.setForeground(new Color(56, 189, 248));

                itemPanel.add(lblIcono, BorderLayout.WEST);
                itemPanel.add(lblTexto, BorderLayout.CENTER);

                return itemPanel;
            }
        };
    }

    /**
     * Aplica estilo translúcido y moderno a la barra de desplazamiento.
     */
    private void estilizarScrollBar(JScrollPane scroll) {
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(56, 189, 248, 130);
                this.trackColor = new Color(0, 0, 0, 30);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return crearBotonInvisible();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return crearBotonInvisible();
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.width - 2, thumbBounds.height - 2, 8, 8);
                g2.dispose();
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(trackColor);
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.dispose();
            }

            private JButton crearBotonInvisible() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                btn.setMinimumSize(new Dimension(0, 0));
                btn.setMaximumSize(new Dimension(0, 0));
                return btn;
            }
        });
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroll.getVerticalScrollBar().setOpaque(false);
    }

    /**
     * Actualiza el texto del contador de tripulantes registrados.
     */
    private void actualizarContador() {
        int cantidad = this.modeloLista.getSize();
        if (cantidad == 0) {
            this.lblContador.setText("Sin tripulantes registrados");
        } else if (cantidad == 1) {
            this.lblContador.setText("1 tripulante registrado");
        } else {
            this.lblContador.setText(cantidad + " tripulantes registrados");
        }
    }

    @Override
    public String getNombre() {
        return this.txtNombre.getText().trim();
    }

    @Override
    public Cargo getCargo() {
        return (Cargo) this.cbCargo.getSelectedItem();
    }

    @Override
    public Origen getOrigen() {
        return (Origen) this.cbOrigen.getSelectedItem();
    }

    @Override
    public String getTextoAntiguedad() {
        return this.txtAntiguedad.getText().trim();
    }

    @Override
    public void agregarTripulanteLista(String descripcion) {
        this.modeloLista.addElement(descripcion);
        actualizarContador();
    }

    @Override
    public void limpiarCampos() {
        this.txtNombre.setText("");
        this.txtAntiguedad.setText("");
        this.cbCargo.setSelectedIndex(0);
        this.cbOrigen.setSelectedIndex(0);
        this.oyenteCampos.verificarCampos();
    }

    JTextField getCampoNombre() {
        return this.txtNombre;
    }

    JTextField getCampoAntiguedad() {
        return this.txtAntiguedad;
    }

    JButton getBotonAgregar() {
        return this.bAgregar;
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
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje
        );
    }

    @Override
    public void setControlador(ActionListener controlador) {
        this.bAgregar.addActionListener(controlador);
        this.bAceptar.addActionListener(controlador);
    }

    // =========================================================================
    // COMPONENTES PERSONALIZADOS CON TRANSLUCIDEZ (GLASSMORPHISM)
    // =========================================================================

    /**
     * Panel con fondo translúcido y bordes redondeados con resplandor suave.
     */
    private static class PanelTranslucido extends JPanel {
        private final int radio;
        private final Color colorFondo;
        private final Color colorBorde;

        public PanelTranslucido(int radio, Color colorFondo, Color colorBorde) {
            this.radio = radio;
            this.colorFondo = colorFondo;
            this.colorBorde = colorBorde;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(colorFondo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);

            if (colorBorde != null) {
                g2.setColor(colorBorde);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Campo de texto con fondo translúcido, borde suave y realce con foco.
     */
    private static class CampoTextoEstilizado extends JTextField {
        private final int radio = 10;
        private boolean enfocado = false;

        public CampoTextoEstilizado(int columnas) {
            super(columnas);
            setOpaque(false);
            setForeground(Color.WHITE);
            setCaretColor(new Color(56, 189, 248));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
            setPreferredSize(new Dimension(getPreferredSize().width, 38));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    enfocado = true;
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    enfocado = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (enfocado) {
                g2.setColor(new Color(30, 41, 59, 215));
            } else {
                g2.setColor(new Color(15, 23, 42, 160));
            }
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, radio, radio);

            if (enfocado) {
                g2.setColor(new Color(56, 189, 248, 230));
                g2.setStroke(new BasicStroke(1.8f));
            } else {
                g2.setColor(new Color(100, 116, 139, 130));
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radio, radio);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * ComboBox con fondo translúcido y flecha personalizada.
     */
    private static class ComboBoxEstilizado<E> extends JComboBox<E> {
        private final int radio = 10;
        private boolean enfocado = false;

        public ComboBoxEstilizado() {
            super();
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(Color.WHITE);
            setPreferredSize(new Dimension(getPreferredSize().width, 38));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

            setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    l.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                    l.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                    if (index == -1) {
                        l.setOpaque(false);
                        l.setForeground(Color.WHITE);
                    } else {
                        l.setOpaque(true);
                        if (isSelected) {
                            l.setBackground(new Color(14, 116, 144, 230));
                            l.setForeground(Color.WHITE);
                        } else {
                            l.setBackground(new Color(15, 23, 42, 235));
                            l.setForeground(new Color(226, 232, 240));
                        }
                    }
                    return l;
                }
            });

            setUI(new BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    JButton btn = new JButton("▾");
                    btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    btn.setForeground(new Color(56, 189, 248));
                    btn.setContentAreaFilled(false);
                    btn.setBorderPainted(false);
                    btn.setFocusPainted(false);
                    btn.setOpaque(false);
                    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    return btn;
                }

                @Override
                public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                    // Evita pintar fondo opaco por defecto
                }
            });

            setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 8));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    enfocado = true;
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    enfocado = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (enfocado) {
                g2.setColor(new Color(30, 41, 59, 215));
            } else {
                g2.setColor(new Color(15, 23, 42, 160));
            }
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, radio, radio);

            if (enfocado) {
                g2.setColor(new Color(56, 189, 248, 230));
                g2.setStroke(new BasicStroke(1.8f));
            } else {
                g2.setColor(new Color(100, 116, 139, 130));
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radio, radio);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Botón con bordes redondeados, colores translúcidos y animación de hover.
     */
    private static class BotonEstilizado extends JButton {
        private final Color colorNormal;
        private final Color colorHover;
        private final Color colorBorde;
        private final int radio;
        private boolean hover = false;

        public BotonEstilizado(String texto, Color normal, Color hover, Color borde, int radio) {
            super(texto);
            this.colorNormal = normal;
            this.colorHover = hover;
            this.colorBorde = borde;
            this.radio = radio;

            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    BotonEstilizado.this.hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    BotonEstilizado.this.hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fondo = !isEnabled()
                    ? new Color(51, 65, 85, 140)
                    : (hover ? colorHover : colorNormal);
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);

            if (colorBorde != null) {
                g2.setColor(!isEnabled()
                        ? new Color(100, 116, 139, 90)
                        : (hover ? colorBorde.brighter() : colorBorde));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}