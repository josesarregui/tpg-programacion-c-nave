package vista.concurrencia;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.DefaultCaret;
import modelo.concurrencia.ObservadorConcurrencia;
import modelo.concurrencia.PlataformaDeCarga;

/**
 * Vista de la pantalla de Concurrencia ("Universo Paralelo"). 1. Generadora de
 * Energia (productores, con boton Agregar y soporte para detener hilo con doble
 * clic) 2. Eventos (consola central informa en tiempo real) 3. Naves
 * Consumidoras (consumidores, con boton Agregar y soporte para detener hilo con
 * doble clic)
 *
 * Implementa {@link IVistaConcurrencia} (que extiende
 * {@link ObservadorConcurrencia}), recibiendo directamente las notificaciones
 * de la {@link PlataformaDeCarga} y despachandolas.
 */
public class VistaConcurrencia extends JPanel implements IVistaConcurrencia {

    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final JLabel lblBaterias;
    private final JLabel lblGeneradoras;
    private final JLabel lblNaves;

    private final JButton btnCrearGeneradora;
    private final JButton btnCrearNave;
    private final JButton btnLimpiarConsola;

    private final JTextArea areaConsola;

    private final DefaultListModel<String> modeloGeneradoras;
    private final JList<String> listaGeneradoras;

    private final DefaultListModel<String> modeloNaves;
    private final JList<String> listaNaves;

    private final Image imagenFondo;
    private int capacidadMaxima = PlataformaDeCarga.CAPACIDAD_POR_DEFECTO;

    private Consumer<String> oyenteDetenerGeneradora;
    private Consumer<String> oyenteDetenerNave;

    public VistaConcurrencia() {
        this.setLayout(new BorderLayout(0, 16));
        this.setOpaque(false);
        this.setBorder(new EmptyBorder(16, 28, 20, 28));

        // Fondo espacial
        URL rutaFondo = getClass().getResource("/imagenes/FondoEspacio.gif");
        if (rutaFondo != null) {
            this.imagenFondo = new ImageIcon(rutaFondo).getImage();
        } else {
            this.imagenFondo = null;
        }

        // =====================================================================
        // ENCABEZADO SUPERIOR: "Universo Paralelo"
        // =====================================================================
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setLayout(new BoxLayout(panelEncabezado, BoxLayout.Y_AXIS));
        panelEncabezado.setOpaque(false);

        JLabel titulo = new JLabel("Universo Paralelo", SwingConstants.CENTER);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel subtitulo = new JLabel(
                "Monitoreo de Concurrencia • Productor - Monitor - Consumidor",
                SwingConstants.CENTER);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setForeground(new Color(186, 230, 253));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panelEncabezado.add(titulo);
        panelEncabezado.add(Box.createVerticalStrut(4));
        panelEncabezado.add(subtitulo);

        this.add(panelEncabezado, BorderLayout.NORTH);

        // =====================================================================
        // CUERPO CENTRAL: TRES COLUMNAS CON EVENTOS PROTAGÓNICO Y AMPLIADO
        // =====================================================================
        JPanel panelTresColumnas = new JPanel(new GridBagLayout());
        panelTresColumnas.setOpaque(false);

        // ---------------------------------------------------------------------
        // COLUMNA 1: Generadora de Energia
        // ---------------------------------------------------------------------
        this.lblGeneradoras = new JLabel("0 activas");
        this.lblGeneradoras.setFont(new Font("Segoe UI", Font.BOLD, 12));
        this.lblGeneradoras.setForeground(new Color(251, 191, 36));

        this.btnCrearGeneradora = crearBotonEstilizado(
                "Agregar",
                new Color(245, 158, 11, 200),
                new Color(217, 119, 6, 230),
                new Color(251, 191, 36),
                10);
        this.btnCrearGeneradora.setActionCommand(COMANDO_CREAR_GENERADORA);
        this.btnCrearGeneradora.setPreferredSize(new Dimension(105, 36));

        this.modeloGeneradoras = new DefaultListModel<>();
        this.listaGeneradoras = new JList<>(this.modeloGeneradoras);
        configurarLista(this.listaGeneradoras, "⚡", new Color(251, 191, 36));

        // Doble clic para detener hilo de generadora
        this.listaGeneradoras.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = listaGeneradoras.locationToIndex(e.getPoint());
                    if (index >= 0 && index < modeloGeneradoras.getSize()) {
                        String id = extraerId(modeloGeneradoras.getElementAt(index));
                        if (id != null && oyenteDetenerGeneradora != null) {
                            oyenteDetenerGeneradora.accept(id);
                        }
                    }
                }
            }
        });

        JPanel colGeneradoras = crearColumna(
                "Generadora de Energia",
                this.btnCrearGeneradora,
                this.lblGeneradoras,
                this.listaGeneradoras,
                new Color(245, 158, 11, 140));

        // ---------------------------------------------------------------------
        // COLUMNA 2: Eventos (Espacio ampliado)
        // ---------------------------------------------------------------------
        this.lblBaterias = new JLabel("0 / " + this.capacidadMaxima);
        this.lblBaterias.setFont(new Font("Segoe UI", Font.BOLD, 12));
        this.lblBaterias.setForeground(new Color(52, 211, 153));

        this.btnLimpiarConsola = crearBotonEstilizado(
                "Limpiar",
                new Color(71, 85, 105, 190),
                new Color(100, 116, 139, 230),
                new Color(148, 163, 184),
                10);
        this.btnLimpiarConsola.setActionCommand(COMANDO_LIMPIAR_CONSOLA);
        this.btnLimpiarConsola.setPreferredSize(new Dimension(90, 36));

        this.areaConsola = new JTextArea();
        this.areaConsola.setEditable(false);
        this.areaConsola.setFont(new Font("Consolas", Font.PLAIN, 13));
        this.areaConsola.setBackground(new Color(3, 7, 18, 235));
        this.areaConsola.setForeground(new Color(186, 230, 253));
        this.areaConsola.setCaretColor(new Color(56, 189, 248));
        this.areaConsola.setLineWrap(true);
        this.areaConsola.setWrapStyleWord(true);
        this.areaConsola.setBorder(new EmptyBorder(10, 12, 10, 12));
        ((DefaultCaret) this.areaConsola.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JPanel colEventos = crearColumnaEventos(
                "Eventos",
                this.btnLimpiarConsola,
                this.lblBaterias,
                this.areaConsola,
                new Color(56, 189, 248, 150));

        // ---------------------------------------------------------------------
        // COLUMNA 3: Naves Consumidoras
        // ---------------------------------------------------------------------
        this.lblNaves = new JLabel("0 activas");
        this.lblNaves.setFont(new Font("Segoe UI", Font.BOLD, 12));
        this.lblNaves.setForeground(new Color(56, 189, 248));

        this.btnCrearNave = crearBotonEstilizado(
                "Agregar",
                new Color(14, 116, 144, 210),
                new Color(6, 182, 212, 230),
                new Color(56, 189, 248),
                10);
        this.btnCrearNave.setActionCommand(COMANDO_CREAR_NAVE);
        this.btnCrearNave.setPreferredSize(new Dimension(105, 36));

        this.modeloNaves = new DefaultListModel<>();
        this.listaNaves = new JList<>(this.modeloNaves);
        configurarLista(this.listaNaves, "🛸", new Color(56, 189, 248));

        // Doble clic para detener hilo de nave consumidora
        this.listaNaves.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = listaNaves.locationToIndex(e.getPoint());
                    if (index >= 0 && index < modeloNaves.getSize()) {
                        String id = extraerId(modeloNaves.getElementAt(index));
                        if (id != null && oyenteDetenerNave != null) {
                            oyenteDetenerNave.accept(id);
                        }
                    }
                }
            }
        });

        JPanel colNaves = crearColumna(
                "Naves Consumidoras",
                this.btnCrearNave,
                this.lblNaves,
                this.listaNaves,
                new Color(6, 182, 212, 140));

        // Disposición con GridBagLayout:
        // Columna 1 (Generadoras): weightx = 0.25
        // Columna 2 (Eventos): weightx = 0.50 (el doble de ancha)
        // Columna 3 (Naves): weightx = 0.25
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.weightx = 0.25;
        gbc.insets = new Insets(0, 0, 0, 14);
        panelTresColumnas.add(colGeneradoras, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.50;
        gbc.insets = new Insets(0, 0, 0, 14);
        panelTresColumnas.add(colEventos, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.25;
        gbc.insets = new Insets(0, 0, 0, 0);
        panelTresColumnas.add(colNaves, gbc);

        this.add(panelTresColumnas, BorderLayout.CENTER);
    }

    // =========================================================================
    // CREACIÓN DE LAS COLUMNAS BASADAS EN EL DIAGRAMA
    // =========================================================================
    private JPanel crearColumna(
            String titulo,
            JButton btnAgregar,
            JLabel lblContador,
            JList<String> lista,
            Color colorAcento) {
        JPanel columna = new JPanel(new BorderLayout(0, 10));
        columna.setOpaque(false);

        // Cabecera: Título y Botón Agregar (conforme al diagrama)
        JPanel header = new JPanel(new BorderLayout(8, 6));
        header.setOpaque(false);

        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));

        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(2));
        panelTitulo.add(lblContador);

        header.add(panelTitulo, BorderLayout.CENTER);
        header.add(btnAgregar, BorderLayout.EAST);

        columna.add(header, BorderLayout.NORTH);

        // Caja contenedora (rectángulo grande del diagrama)
        JPanel cardCaja = crearPanelTranslucido(16, new Color(15, 23, 42, 185), colorAcento);
        cardCaja.setLayout(new BorderLayout(0, 6));
        cardCaja.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        estilizarScrollBar(scroll);

        JLabel lblAyuda = new JLabel("Doble clic en un ítem para detener su hilo", SwingConstants.CENTER);
        lblAyuda.setForeground(new Color(148, 163, 184));
        lblAyuda.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAyuda.setBorder(new EmptyBorder(4, 0, 2, 0));

        cardCaja.add(scroll, BorderLayout.CENTER);
        cardCaja.add(lblAyuda, BorderLayout.SOUTH);
        columna.add(cardCaja, BorderLayout.CENTER);

        return columna;
    }

    private JPanel crearColumnaEventos(
            String titulo,
            JButton btnLimpiar,
            JLabel lblBaterias,
            JTextArea areaTexto,
            Color colorAcento) {
        JPanel columna = new JPanel(new BorderLayout(0, 10));
        columna.setOpaque(false);

        // Cabecera: Título "Eventos" y estado de baterías
        JPanel header = new JPanel(new BorderLayout(8, 6));
        header.setOpaque(false);

        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel panelSub = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelSub.setOpaque(false);
        JLabel lblPrefijo = new JLabel("Baterías en Plataforma:");
        lblPrefijo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPrefijo.setForeground(new Color(148, 163, 184));
        panelSub.add(lblPrefijo);
        panelSub.add(lblBaterias);

        panelTitulo.add(lblTitulo);
        panelTitulo.add(Box.createVerticalStrut(2));
        panelTitulo.add(panelSub);

        header.add(panelTitulo, BorderLayout.CENTER);
        header.add(btnLimpiar, BorderLayout.EAST);

        columna.add(header, BorderLayout.NORTH);

        // Caja contenedora de eventos (rectángulo central del diagrama, amplio)
        JPanel cardCaja = crearPanelTranslucido(16, new Color(3, 7, 18, 225), colorAcento);
        cardCaja.setLayout(new BorderLayout());
        cardCaja.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        estilizarScrollBar(scroll);

        cardCaja.add(scroll, BorderLayout.CENTER);
        columna.add(cardCaja, BorderLayout.CENTER);

        return columna;
    }

    private void configurarLista(JList<String> lista, String icono, Color colorAcento) {
        lista.setOpaque(false);
        lista.setFixedCellHeight(44);
        lista.setSelectionBackground(new Color(14, 116, 144, 180));
        lista.setSelectionForeground(Color.WHITE);
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> l,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                JPanel item = new JPanel(new BorderLayout(8, 0)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        if (isSelected) {
                            g2.setColor(new Color(14, 116, 144, 200));
                        } else {
                            g2.setColor(index % 2 == 0 ? new Color(255, 255, 255, 14) : new Color(255, 255, 255, 6));
                        }
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                        g2.setColor(new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(),
                                isSelected ? 200 : 40));
                        g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 10, 10);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                item.setOpaque(false);
                item.setBorder(new EmptyBorder(6, 10, 6, 10));

                JLabel lblIcono = new JLabel(icono);
                lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));

                JLabel lblTexto = new JLabel(String.valueOf(value));
                lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblTexto.setForeground(Color.WHITE);

                item.add(lblIcono, BorderLayout.WEST);
                item.add(lblTexto, BorderLayout.CENTER);

                return item;
            }
        });
    }

    private String extraerId(String valor) {
        if (valor == null) {
            return null;
        }
        int idx = valor.indexOf(" •");
        return (idx != -1 ? valor.substring(0, idx) : valor).trim();
    }

    // =========================================================================
    // DIBUJO DEL FONDO
    // =========================================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.imagenFondo != null) {
            g.drawImage(this.imagenFondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(10, 15, 30));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    // =========================================================================
    // IMPLEMENTACIÓN IVISTACONCURRENCIA / OBSERVADOR
    // =========================================================================
    @Override
    public void setControlador(ActionListener controlador) {
        this.btnCrearGeneradora.addActionListener(controlador);
        this.btnCrearNave.addActionListener(controlador);
        this.btnLimpiarConsola.addActionListener(controlador);
    }

    @Override
    public void setOyenteDetenerGeneradora(Consumer<String> oyente) {
        this.oyenteDetenerGeneradora = oyente;
    }

    @Override
    public void setOyenteDetenerNave(Consumer<String> oyente) {
        this.oyenteDetenerNave = oyente;
    }

    @Override
    public void agregarEvento(String evento) {
        String timestamp = LocalTime.now().format(HORA_FORMATTER);
        this.areaConsola.append("[" + timestamp + "] " + evento + "\n");
    }

    @Override
    public void limpiarConsola() {
        this.areaConsola.setText("");
    }

    @Override
    public void notificarEvento(String mensaje, int bateriasActuales) {
        SwingUtilities.invokeLater(() -> {
            agregarEvento(mensaje);
            actualizarBaterias(bateriasActuales, this.capacidadMaxima);
        });
    }

    @Override
    public void actualizarBaterias(int baterias, int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        this.lblBaterias.setText(baterias + " / " + capacidadMaxima);
    }

    @Override
    public void actualizarEntidades(int generadorasActivas, int navesActivas) {
        this.lblGeneradoras.setText(generadorasActivas == 1 ? "1 activa" : generadorasActivas + " activas");
        this.lblNaves.setText(navesActivas == 1 ? "1 activa" : navesActivas + " activas");
    }

    @Override
    public void agregarGeneradora(String idGeneradora) {
        Runnable r = () -> this.modeloGeneradoras.addElement(idGeneradora + " • Productora");
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    @Override
    public void agregarNaveConsumidora(String idNave) {
        Runnable r = () -> this.modeloNaves.addElement(idNave + " • Consumidora");
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    @Override
    public void removerGeneradora(String idGeneradora) {
        Runnable r = () -> {
            for (int i = 0; i < this.modeloGeneradoras.getSize(); i++) {
                if (this.modeloGeneradoras.getElementAt(i).startsWith(idGeneradora)) {
                    this.modeloGeneradoras.remove(i);
                    break;
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    @Override
    public void removerNaveConsumidora(String idNave) {
        Runnable r = () -> {
            for (int i = 0; i < this.modeloNaves.getSize(); i++) {
                if (this.modeloNaves.getElementAt(i).startsWith(idNave)) {
                    this.modeloNaves.remove(i);
                    break;
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    @Override
    public void limpiarEntidades() {
        Runnable r = () -> {
            this.modeloGeneradoras.clear();
            this.modeloNaves.clear();
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error en Universo Paralelo",
                JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================================
    // COMPONENTES AUXILIARES Y ESTILOS
    // =========================================================================
    private void estilizarScrollBar(JScrollPane scroll) {
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(56, 189, 248, 120);
                this.trackColor = new Color(15, 23, 42, 80);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return crearBotonInvisible();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return crearBotonInvisible();
            }

            private JButton crearBotonInvisible() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                return b;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 8,
                        8);
                g2.dispose();
            }
        });
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
    }

    private JPanel crearPanelTranslucido(int radio, Color fondo, Color borde) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(fondo);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 1, true),
                new EmptyBorder(6, 6, 6, 6)));
        return panel;
    }

    private JButton crearBotonEstilizado(String texto, Color normal, Color hover, Color borde, int radio) {
        JButton btn = new JButton(texto);
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 1, true),
                new EmptyBorder(8, 14, 8, 14)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(normal);
            }
        });
        return btn;
    }

    // =========================================================================
    // GETTERS PARA PRUEBAS Y CONTROLADOR
    // =========================================================================
    public JLabel getLblBaterias() {
        return this.lblBaterias;
    }

    public JLabel getLblGeneradoras() {
        return this.lblGeneradoras;
    }

    public JLabel getLblNaves() {
        return this.lblNaves;
    }

    public JButton getBtnCrearGeneradora() {
        return this.btnCrearGeneradora;
    }

    public JButton getBtnCrearNave() {
        return this.btnCrearNave;
    }

    public JButton getBtnLimpiarConsola() {
        return this.btnLimpiarConsola;
    }

    public JTextArea getAreaConsola() {
        return this.areaConsola;
    }

    public DefaultListModel<String> getModeloGeneradoras() {
        return this.modeloGeneradoras;
    }

    public DefaultListModel<String> getModeloNaves() {
        return this.modeloNaves;
    }

    public JList<String> getListaGeneradoras() {
        return this.listaGeneradoras;
    }

    public JList<String> getListaNaves() {
        return this.listaNaves;
    }
}
