package vista.principal.paneldetallestripulacion;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import modelo.liquidacion.ConceptoHaber;
import modelo.liquidacion.ReciboHaberes;
import modelo.tripulacion.Tripulante;

/**
 * Vista de detalles de la tripulación de la nave.
 * Implementa {@link IVistaDetallesTripulacion} con un diseño espacial translúcido
 * (glassmorphism) coherente con el lenguaje visual de la cabina y el armado de tripulación.
 */
public class VistaDetallesTripulacion extends JPanel implements IVistaDetallesTripulacion {

    private final JLabel lblCantidad;
    private final JLabel lblCapitan;
    private final JLabel lblEstadoOperativo;
    private final JLabel lblCostoNomina;
    private final JLabel lblContadorLista;
    private final JButton btnVerRecibo;

    private final DefaultListModel<String> modeloLista;
    private final JList<String> listaTripulantes;
    private final Map<Integer, ReciboHaberes> recibosPorId = new HashMap<>();

    public VistaDetallesTripulacion() {
        this.setLayout(new BorderLayout(0, 16));
        this.setOpaque(false);
        this.setBorder(new EmptyBorder(16, 32, 16, 32));

        // ===================================
        // ENCABEZADO SUPERIOR
        // ===================================
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setLayout(new BoxLayout(panelEncabezado, BoxLayout.Y_AXIS));
        panelEncabezado.setOpaque(false);

        JLabel titulo = new JLabel("DETALLES DE LA TRIPULACIÓN", SwingConstants.CENTER);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitulo = new JLabel("Estado del personal y dotación activa a bordo", SwingConstants.CENTER);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setForeground(new Color(186, 230, 253));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panelEncabezado.add(titulo);
        panelEncabezado.add(Box.createVerticalStrut(4));
        panelEncabezado.add(subtitulo);

        this.add(panelEncabezado, BorderLayout.NORTH);

        // ===================================
        // TARJETA RESUMEN DE MANDO (IZQUIERDA)
        // ===================================
        JPanel cardResumen = crearPanelTranslucido(
                new Color(15, 23, 42, 175),
                new Color(56, 189, 248, 120)
        );
        cardResumen.setLayout(new BorderLayout(0, 14));
        cardResumen.setBorder(new EmptyBorder(18, 22, 18, 22));

        // Cabecera de la tarjeta resumen
        JPanel headerResumen = new JPanel();
        headerResumen.setLayout(new BoxLayout(headerResumen, BoxLayout.Y_AXIS));
        headerResumen.setOpaque(false);

        JLabel tituloResumen = new JLabel("🎖️ Resumen de Dotación");
        tituloResumen.setForeground(Color.WHITE);
        tituloResumen.setFont(new Font("Segoe UI", Font.BOLD, 17));

        JLabel subResumen = new JLabel("Jerarquía y estado general de la tripulación");
        subResumen.setForeground(new Color(148, 163, 184));
        subResumen.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        headerResumen.add(tituloResumen);
        headerResumen.add(Box.createVerticalStrut(2));
        headerResumen.add(subResumen);

        cardResumen.add(headerResumen, BorderLayout.NORTH);

        // Labels reactivos para los indicadores
        this.lblCapitan = new JLabel("Sin Asignar");
        this.lblCantidad = new JLabel("0 Tripulantes");
        this.lblEstadoOperativo = new JLabel("Pendiente de Asignación");
        this.lblCostoNomina = new JLabel("0.00 PG");

        // Panel de métricas e indicadores
        JPanel panelContenidoResumen = new JPanel();
        panelContenidoResumen.setLayout(new BoxLayout(panelContenidoResumen, BoxLayout.Y_AXIS));
        panelContenidoResumen.setOpaque(false);

        JPanel tileCapitan = crearTarjetaMetrica(
                "⭐",
                new Color(251, 191, 36),
                "Oficial al Mando",
                this.lblCapitan,
                "Comandante responsable del puente"
        );

        JPanel tileCantidad = crearTarjetaMetrica(
                "👥",
                new Color(56, 189, 248),
                "Total de Oficiales",
                this.lblCantidad,
                "Dotación registrada en el manifiesto"
        );

        JPanel tileEstado = crearTarjetaMetrica(
                "🛡️",
                new Color(16, 185, 129),
                "Estado de la Misión",
                this.lblEstadoOperativo,
                "Disponibilidad para maniobras interestelares"
        );

        JPanel tileNomina = crearTarjetaMetrica(
                "💰",
                new Color(168, 85, 247),
                "Presupuesto de Haberes",
                this.lblCostoNomina,
                "Costo mensual total de la dotación"
        );

        panelContenidoResumen.add(tileCapitan);
        panelContenidoResumen.add(Box.createVerticalStrut(8));
        panelContenidoResumen.add(tileCantidad);
        panelContenidoResumen.add(Box.createVerticalStrut(8));
        panelContenidoResumen.add(tileEstado);
        panelContenidoResumen.add(Box.createVerticalStrut(8));
        panelContenidoResumen.add(tileNomina);
        panelContenidoResumen.add(Box.createVerticalStrut(10));
        panelContenidoResumen.add(crearPanelGuiaMando());
        panelContenidoResumen.add(Box.createVerticalGlue());

        cardResumen.add(panelContenidoResumen, BorderLayout.CENTER);

        // ===================================
        // TARJETA LISTA DE TRIPULANTES (DERECHA)
        // ===================================
        JPanel cardLista = crearPanelTranslucido(
                new Color(15, 23, 42, 175),
                new Color(56, 189, 248, 120)
        );
        cardLista.setLayout(new BorderLayout(0, 12));
        cardLista.setBorder(new EmptyBorder(18, 22, 18, 22));

        // Cabecera de la lista
        JPanel headerLista = new JPanel(new BorderLayout(10, 0));
        headerLista.setOpaque(false);

        JLabel tituloLista = new JLabel("📋 Nómina Oficial de Servicio");
        tituloLista.setForeground(Color.WHITE);
        tituloLista.setFont(new Font("Segoe UI", Font.BOLD, 17));

        this.lblContadorLista = new JLabel("Sin oficiales a bordo");
        this.lblContadorLista.setForeground(new Color(56, 189, 248));
        this.lblContadorLista.setFont(new Font("Segoe UI", Font.BOLD, 12));
        this.lblContadorLista.setHorizontalAlignment(SwingConstants.RIGHT);

        headerLista.add(tituloLista, BorderLayout.WEST);
        headerLista.add(this.lblContadorLista, BorderLayout.EAST);

        cardLista.add(headerLista, BorderLayout.NORTH);

        // Lista estilizada
        this.modeloLista = new DefaultListModel<>();
        this.listaTripulantes = new JList<>(this.modeloLista);
        this.listaTripulantes.setOpaque(false);
        this.listaTripulantes.setFixedCellHeight(54);
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

        // Footer con botón para ver el recibo y ayuda
        JPanel footerLista = new JPanel(new BorderLayout(10, 0));
        footerLista.setOpaque(false);
        footerLista.setBorder(new EmptyBorder(10, 0, 0, 0));

        this.btnVerRecibo = crearBotonEstilizado(
                "📄 Ver Recibo de Haberes",
                new Color(14, 116, 144, 200),
                new Color(6, 182, 212, 230),
                new Color(56, 189, 248)
        );
        this.btnVerRecibo.setPreferredSize(new Dimension(210, 36));
        this.btnVerRecibo.addActionListener(e -> abrirReciboSeleccionado());

        JLabel lblDobleClic = new JLabel("Doble clic en un oficial para ver recibo", SwingConstants.RIGHT);
        lblDobleClic.setForeground(new Color(148, 163, 184));
        lblDobleClic.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        footerLista.add(this.btnVerRecibo, BorderLayout.WEST);
        footerLista.add(lblDobleClic, BorderLayout.EAST);

        cardLista.add(footerLista, BorderLayout.SOUTH);

        // Listener para abrir con doble clic
        this.listaTripulantes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirReciboSeleccionado();
                }
            }
        });

        // ===================================
        // PANEL CENTRAL (DOS COLUMNAS)
        // ===================================
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 24, 0));
        panelCentral.setOpaque(false);
        panelCentral.add(cardResumen);
        panelCentral.add(cardLista);

        this.add(panelCentral, BorderLayout.CENTER);
    }

    /**
     * Crea un tile con métricas para la tarjeta de resumen.
     */
    private JPanel crearTarjetaMetrica(
            String icono,
            Color colorAcento,
            String titulo,
            JLabel lblValor,
            String subtitulo
    ) {
        JPanel tarjeta = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

                g2.setColor(new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 70));
                g2.setStroke(new BasicStroke(1.1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(10, 14, 10, 14));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        // Icono izquierdo en contenedor con tinte
        JPanel panelIcono = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 35));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 120));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panelIcono.setOpaque(false);
        panelIcono.setPreferredSize(new Dimension(42, 42));
        panelIcono.setMaximumSize(new Dimension(42, 42));

        JLabel lblIcono = new JLabel(icono, SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcono.setForeground(colorAcento);
        panelIcono.add(lblIcono, BorderLayout.CENTER);

        // Textos del indicador
        JPanel panelContenido = new JPanel();
        panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
        panelContenido.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo.toUpperCase());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitulo.setForeground(new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 220));

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblValor.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(148, 163, 184));

        panelContenido.add(lblTitulo);
        panelContenido.add(Box.createVerticalStrut(2));
        panelContenido.add(lblValor);
        panelContenido.add(Box.createVerticalStrut(1));
        panelContenido.add(lblSub);

        tarjeta.add(panelIcono, BorderLayout.WEST);
        tarjeta.add(panelContenido, BorderLayout.CENTER);

        return tarjeta;
    }

    /**
     * Panel informativo con la guía y descripción de la jerarquía de oficiales.
     */
    private JPanel crearPanelGuiaMando() {
        JPanel panelGuia = new JPanel(new BorderLayout(0, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(15, 23, 42, 110));
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.setColor(new Color(56, 189, 248, 55));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panelGuia.setOpaque(false);
        panelGuia.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel lblGuiaTitulo = new JLabel("🛰️ Estructura de Mando Estelar");
        lblGuiaTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblGuiaTitulo.setForeground(new Color(186, 230, 253));

        JPanel panelItemsGuia = new JPanel(new GridLayout(4, 1, 0, 4));
        panelItemsGuia.setOpaque(false);

        panelItemsGuia.add(crearFilaGuia("⭐", "Capitán", "Comando absoluto de la nave y tripulación"));
        panelItemsGuia.add(crearFilaGuia("🛡️", "Teniente", "Coordinación táctica y defensa de sistemas"));
        panelItemsGuia.add(crearFilaGuia("🚀", "Alférez", "Navegación espacial e ingeniería técnica"));
        panelItemsGuia.add(crearFilaGuia("🔮", "Consejero", "Asesoría diplomática y bienestar a bordo"));

        panelGuia.add(lblGuiaTitulo, BorderLayout.NORTH);
        panelGuia.add(panelItemsGuia, BorderLayout.CENTER);

        return panelGuia;
    }

    private JPanel crearFilaGuia(String icono, String cargo, String descripcion) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        fila.setOpaque(false);

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        JLabel lblCargo = new JLabel(cargo + ":");
        lblCargo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCargo.setForeground(new Color(224, 242, 254));

        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(148, 163, 184));

        fila.add(lblIcono);
        fila.add(lblCargo);
        fila.add(lblDesc);
        return fila;
    }

    /**
     * Renderer con diseño moderno para cada oficial de la nómina.
     */
    private ListCellRenderer<String> crearRendererLista() {
        return new ListCellRenderer<String>() {
            @Override
            public Component getListCellRendererComponent(
                    JList<? extends String> list,
                    String value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                String idTexto = "ID: -";
                String nombreTexto = value != null ? value : "";
                String cargoTexto = "Oficial";
                String sueldoTexto = "";

                if (value != null) {
                    if (value.contains("|")) {
                        String[] partes = value.split("\\s*\\|\\s*");
                        if (partes.length >= 4) {
                            idTexto = partes[0].trim();
                            nombreTexto = partes[1].trim();
                            cargoTexto = partes[2].trim();
                            sueldoTexto = partes[3].trim();
                        } else if (partes.length >= 3) {
                            idTexto = partes[0].trim();
                            nombreTexto = partes[1].trim();
                            cargoTexto = partes[2].trim();
                        } else if (partes.length == 2) {
                            nombreTexto = partes[0].trim();
                            cargoTexto = partes[1].trim();
                        }
                    } else if (value.startsWith("#")) {
                        int primerEspacio = value.indexOf(' ');
                        int parentesisAbre = value.indexOf('(');
                        if (primerEspacio > 0 && parentesisAbre > primerEspacio) {
                            idTexto = "ID: " + value.substring(1, primerEspacio).trim();
                            nombreTexto = value.substring(primerEspacio + 1, parentesisAbre).trim();
                            String dentroParentesis = value.substring(parentesisAbre + 1);
                            if (dentroParentesis.contains(",")) {
                                cargoTexto = dentroParentesis.split(",")[0].trim();
                            }
                        }
                    }
                }

                // Determinación del color y badge según el cargo
                final Color colorAcento;
                final Color pillFondo;
                final Color pillBorde;
                final String icono;

                String cargoLower = cargoTexto.toLowerCase();
                if (cargoLower.contains("capit")) {
                    icono = "⭐";
                    colorAcento = new Color(251, 191, 36);
                    pillFondo = new Color(245, 158, 11, 40);
                    pillBorde = new Color(245, 158, 11, 160);
                } else if (cargoLower.contains("consej")) {
                    icono = "🔮";
                    colorAcento = new Color(192, 132, 252);
                    pillFondo = new Color(168, 85, 247, 40);
                    pillBorde = new Color(168, 85, 247, 160);
                } else if (cargoLower.contains("tenient")) {
                    icono = "🛡️";
                    colorAcento = new Color(56, 189, 248);
                    pillFondo = new Color(6, 182, 212, 40);
                    pillBorde = new Color(6, 182, 212, 160);
                } else if (cargoLower.contains("alfer") || cargoLower.contains("alfér")) {
                    icono = "🚀";
                    colorAcento = new Color(52, 211, 153);
                    pillFondo = new Color(16, 185, 129, 40);
                    pillBorde = new Color(16, 185, 129, 160);
                } else {
                    icono = "👤";
                    colorAcento = new Color(148, 163, 184);
                    pillFondo = new Color(148, 163, 184, 40);
                    pillBorde = new Color(148, 163, 184, 160);
                }

                JPanel itemPanel = new JPanel(new BorderLayout(12, 0)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        if (isSelected) {
                            g2.setColor(new Color(14, 116, 144, 220));
                        } else {
                            if (index % 2 == 0) {
                                g2.setColor(new Color(255, 255, 255, 18));
                            } else {
                                g2.setColor(new Color(255, 255, 255, 8));
                            }
                        }
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);

                        if (isSelected) {
                            g2.setColor(new Color(56, 189, 248, 220));
                            g2.setStroke(new BasicStroke(1.2f));
                            g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 12, 12);
                        } else {
                            g2.setColor(new Color(255, 255, 255, 25));
                            g2.setStroke(new BasicStroke(0.8f));
                            g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 12, 12);
                        }
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                itemPanel.setOpaque(false);
                itemPanel.setBorder(new EmptyBorder(6, 12, 6, 12));

                // Caja del icono
                JPanel iconBox = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(pillFondo);
                        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                        g2.setColor(pillBorde);
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                iconBox.setOpaque(false);
                iconBox.setPreferredSize(new Dimension(36, 36));
                JLabel lblIcono = new JLabel(icono, SwingConstants.CENTER);
                lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                lblIcono.setForeground(colorAcento);
                iconBox.add(lblIcono, BorderLayout.CENTER);

                // Textos centrales
                JPanel textPanel = new JPanel();
                textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                textPanel.setOpaque(false);

                JLabel lblNombre = new JLabel(nombreTexto);
                lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lblNombre.setForeground(Color.WHITE);

                String subDetalle = idTexto;
                if (!sueldoTexto.isBlank()) {
                    subDetalle += "   •   💰 " + sueldoTexto;
                }

                JLabel lblId = new JLabel(subDetalle);
                lblId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                lblId.setForeground(new Color(148, 163, 184));

                textPanel.add(lblNombre);
                textPanel.add(Box.createVerticalStrut(2));
                textPanel.add(lblId);

                // Badge de cargo (derecha)
                final String textoBadge = cargoTexto.toUpperCase();
                JPanel pillCargo = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(pillFondo);
                        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                        g2.setColor(pillBorde);
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                        g2.dispose();
                        super.paintComponent(g);
                    }
                };
                pillCargo.setOpaque(false);
                JLabel lblCargoPill = new JLabel(textoBadge);
                lblCargoPill.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lblCargoPill.setForeground(colorAcento);
                pillCargo.add(lblCargoPill);

                JPanel pillWrapper = new JPanel(new GridBagLayout());
                pillWrapper.setOpaque(false);
                pillWrapper.add(pillCargo);

                itemPanel.add(iconBox, BorderLayout.WEST);
                itemPanel.add(textPanel, BorderLayout.CENTER);
                itemPanel.add(pillWrapper, BorderLayout.EAST);

                return itemPanel;
            }
        };
    }

    /**
     * Aplica estilo translúcido a la barra de scroll.
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

    @Override
    public void mostrarCantidad(int cantidad) {
        this.lblCantidad.setText(cantidad == 1 ? "1 Tripulante" : cantidad + " Tripulantes");
        this.lblContadorLista.setText(
                cantidad == 0 ? "Sin oficiales a bordo"
                        : (cantidad == 1 ? "1 oficial a bordo" : cantidad + " oficiales a bordo")
        );
        if (cantidad >= 5) {
            this.lblEstadoOperativo.setText("Dotación Lista para Zarpar");
            this.lblEstadoOperativo.setForeground(new Color(74, 222, 128));
        } else if (cantidad > 0) {
            this.lblEstadoOperativo.setText("Dotación Incompleta (mín. 5)");
            this.lblEstadoOperativo.setForeground(new Color(251, 191, 36));
        } else {
            this.lblEstadoOperativo.setText("Pendiente de Asignación");
            this.lblEstadoOperativo.setForeground(new Color(251, 191, 36));
        }
    }

    @Override
    public void mostrarCapitan(String nombre) {
        if (nombre != null && !nombre.isBlank() && !nombre.equals("-")) {
            this.lblCapitan.setText(nombre);
            this.lblCapitan.setForeground(new Color(251, 191, 36));
        } else {
            this.lblCapitan.setText("Sin Asignar");
            this.lblCapitan.setForeground(new Color(148, 163, 184));
        }
    }

    @Override
    public void mostrarCostoNomina(double totalNomina) {
        this.lblCostoNomina.setText(String.format(Locale.US, "%.2f PG", totalNomina));
    }

    @Override
    public void limpiarTripulantes() {
        this.modeloLista.clear();
        this.recibosPorId.clear();
        this.lblContadorLista.setText("Sin oficiales a bordo");
        this.lblCostoNomina.setText("0.00 PG");
    }

    @Override
    public void agregarTripulante(int id, String nombre, String cargo) {
        agregarTripulante(id, nombre, cargo, 0.0, null);
    }

    @Override
    public void agregarTripulante(int id, String nombre, String cargo, double sueldoTotal, ReciboHaberes recibo) {
        if (recibo != null) {
            this.recibosPorId.put(id, recibo);
        }
        String elemento = "ID: " + id + "   |   " + nombre + "   |   " + cargo;
        if (sueldoTotal > 0) {
            elemento += "   |   " + String.format(Locale.US, "%.2f PG", sueldoTotal);
        }
        this.modeloLista.addElement(elemento);
        int total = this.modeloLista.getSize();
        this.lblContadorLista.setText(
                total == 1 ? "1 oficial a bordo" : total + " oficiales a bordo"
        );
    }

    @Override
    public void mostrarReciboDetallado(ReciboHaberes recibo) {
        if (recibo == null) {
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner instanceof Frame ? (Frame) owner : null, "Recibo Oficial de Haberes", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);

        // Contenedor principal estilizado oscuro
        JPanel contenedor = new JPanel(new BorderLayout(0, 16));
        contenedor.setBackground(new Color(15, 23, 42));
        contenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(56, 189, 248, 180), 1),
                new EmptyBorder(20, 24, 20, 24)
        ));

        // 1. Cabecera del recibo
        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setOpaque(false);

        JLabel lblTitulo = new JLabel("📜 RECIBO DE HABERES");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Liquidación reglamentaria de la dotación • Período: " + recibo.getPeriodo());
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(186, 230, 253));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelHeader.add(lblTitulo);
        panelHeader.add(Box.createVerticalStrut(4));
        panelHeader.add(lblSubtitulo);

        // 2. Ficha del tripulante
        Tripulante tripulante = recibo.getTripulante();
        JPanel panelOficial = new JPanel(new GridLayout(2, 2, 10, 6));
        panelOficial.setOpaque(false);
        panelOficial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(56, 189, 248, 70), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));

        panelOficial.add(crearEtiquetaDato("Oficial:", tripulante.getNombre()));
        panelOficial.add(crearEtiquetaDato("Rango:", tripulante.getCargo().toString()));
        panelOficial.add(crearEtiquetaDato("Antigüedad:", tripulante.getAntiguedad() + (tripulante.getAntiguedad() == 1 ? " año" : " años")));
        panelOficial.add(crearEtiquetaDato("Planeta Origen:", tripulante.getOrigen().toString()));

        // 3. Desglose de conceptos (Patrón Decorator)
        JPanel panelCuerpo = new JPanel();
        panelCuerpo.setLayout(new BoxLayout(panelCuerpo, BoxLayout.Y_AXIS));
        panelCuerpo.setOpaque(false);
        panelCuerpo.add(panelOficial);
        panelCuerpo.add(Box.createVerticalStrut(14));

        JLabel lblDesglose = new JLabel("DETALLE DE CONCEPTOS LIQUIDADOS");
        lblDesglose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDesglose.setForeground(new Color(56, 189, 248));
        panelCuerpo.add(lblDesglose);
        panelCuerpo.add(Box.createVerticalStrut(8));

        JPanel panelConceptos = new JPanel();
        panelConceptos.setLayout(new BoxLayout(panelConceptos, BoxLayout.Y_AXIS));
        panelConceptos.setOpaque(false);

        List<ConceptoHaber> conceptos = recibo.getConceptos();
        for (ConceptoHaber concepto : conceptos) {
            JPanel fila = new JPanel(new BorderLayout(12, 0));
            fila.setOpaque(false);
            fila.setBorder(new EmptyBorder(4, 6, 4, 6));

            JLabel lblDesc = new JLabel("• " + concepto.getDescripcion());
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblDesc.setForeground(new Color(224, 242, 254));

            JLabel lblImporte = new JLabel(String.format(Locale.US, "+ $%.2f PG", concepto.getImporte()));
            lblImporte.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblImporte.setForeground(new Color(52, 211, 153));

            fila.add(lblDesc, BorderLayout.WEST);
            fila.add(lblImporte, BorderLayout.EAST);
            panelConceptos.add(fila);
        }

        panelCuerpo.add(panelConceptos);
        panelCuerpo.add(Box.createVerticalStrut(12));

        // Total
        JPanel panelTotal = new JPanel(new BorderLayout());
        panelTotal.setOpaque(false);
        panelTotal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(56, 189, 248, 120)),
                new EmptyBorder(10, 6, 4, 6)
        ));

        JLabel lblTextoTotal = new JLabel("TOTAL LIQUIDADO:");
        lblTextoTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTextoTotal.setForeground(Color.WHITE);

        JLabel lblValorTotal = new JLabel(String.format(Locale.US, "$%.2f PG", recibo.getTotal()));
        lblValorTotal.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblValorTotal.setForeground(new Color(251, 191, 36));

        panelTotal.add(lblTextoTotal, BorderLayout.WEST);
        panelTotal.add(lblValorTotal, BorderLayout.EAST);
        panelCuerpo.add(panelTotal);

        // 4. Botón de cierre
        JButton btnCerrar = crearBotonEstilizado(
                "Entendido",
                new Color(14, 116, 144, 200),
                new Color(6, 182, 212, 230),
                new Color(56, 189, 248)
        );
        btnCerrar.setPreferredSize(new Dimension(130, 36));
        btnCerrar.addActionListener(e -> dialog.dispose());

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panelBoton.setOpaque(false);
        panelBoton.add(btnCerrar);

        contenedor.add(panelHeader, BorderLayout.NORTH);
        contenedor.add(panelCuerpo, BorderLayout.CENTER);
        contenedor.add(panelBoton, BorderLayout.SOUTH);

        dialog.setContentPane(contenedor);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(460, 400));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private void abrirReciboSeleccionado() {
        int index = this.listaTripulantes.getSelectedIndex();
        if (index < 0 || index >= this.modeloLista.getSize()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, seleccione un oficial de la nómina para ver su recibo de haberes.",
                    "Recibo de Haberes",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String elemento = this.modeloLista.getElementAt(index);
        Integer id = extraerIdDeCadena(elemento);
        ReciboHaberes recibo = id != null ? this.recibosPorId.get(id) : null;

        if (recibo != null) {
            mostrarReciboDetallado(recibo);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay un recibo de haberes liquidado disponible para el oficial seleccionado.",
                    "Recibo de Haberes",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private Integer extraerIdDeCadena(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            if (texto.contains("|")) {
                String[] partes = texto.split("\\s*\\|\\s*");
                if (partes.length > 0) {
                    String idStr = partes[0].replace("ID:", "").trim();
                    return Integer.parseInt(idStr);
                }
            } else if (texto.startsWith("#")) {
                int primerEspacio = texto.indexOf(' ');
                if (primerEspacio > 1) {
                    return Integer.parseInt(texto.substring(1, primerEspacio).trim());
                }
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    private JPanel crearEtiquetaDato(String etiqueta, String valor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel lblE = new JLabel(etiqueta);
        lblE.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblE.setForeground(new Color(148, 163, 184));
        JLabel lblV = new JLabel(valor);
        lblV.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblV.setForeground(Color.WHITE);
        p.add(lblE);
        p.add(lblV);
        return p;
    }

    // =========================================================================
    // GETTERS PARA INSPECCIÓN Y TESTS UNITARIOS
    // =========================================================================

    public JLabel getLblCantidad() {
        return lblCantidad;
    }

    public JLabel getLblCapitan() {
        return lblCapitan;
    }

    public JLabel getLblEstadoOperativo() {
        return lblEstadoOperativo;
    }

    public JLabel getLblCostoNomina() {
        return lblCostoNomina;
    }

    public JLabel getLblContadorLista() {
        return lblContadorLista;
    }

    public JButton getBtnVerRecibo() {
        return btnVerRecibo;
    }

    public DefaultListModel<String> getModeloLista() {
        return modeloLista;
    }

    public JList<String> getListaTripulantes() {
        return listaTripulantes;
    }

    public Map<Integer, ReciboHaberes> getRecibosPorId() {
        return Collections.unmodifiableMap(recibosPorId);
    }

    // =========================================================================
    // MÉTODOS AUXILIARES VISUALES (SIN SUBCLASES INTERNAS)
    // =========================================================================

    private JPanel crearPanelTranslucido(Color colorFondo, Color colorBorde) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(colorFondo);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBorde, 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
        return panel;
    }

    private JButton crearBotonEstilizado(String texto, Color normal, Color hover, Color borde) {
        JButton btn = new JButton(texto);
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
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
}