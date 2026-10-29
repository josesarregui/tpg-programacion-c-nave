package vista.principal.paneldetallesnave;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import modelo.nave.Nave;
import modelo.nave.ObservadorNave;

/**
 * Vista de detalles y telemetría de la nave.
 * Implementa {@link ObservadorNave} (patrón Observer) para reflejar en tiempo real
 * los cambios en los recursos y el estado de la nave operada con un diseño espacial
 * translúcido (glassmorphism) coherente con la sección de Tripulación.
 */
public class VistaDetallesNave extends JPanel implements IVistaDetallesNave, ObservadorNave {

    private final VitrinaNavePanel panelVitrina;
    private final JLabel nombreNave;

    private final JLabel txtId;
    private final JLabel txtTipo;
    private final JLabel txtCombustible;
    private final JLabel txtEnergia;
    private final JLabel txtDesgaste;
    private final JLabel txtMantenimiento;
    private final JLabel txtEstadoMotor;
    private final JLabel txtTripulacion;
    private final JLabel txtEstadoOperativo;

    private final TarjetaRecurso barraCombustible;
    private final TarjetaRecurso barraEnergia;
    private final TarjetaRecurso barraDesgaste;

    public VistaDetallesNave() {
        this.setLayout(new BorderLayout(0, 16));
        this.setOpaque(false);
        this.setBorder(new EmptyBorder(16, 32, 16, 32));

        // ===================================
        // ENCABEZADO SUPERIOR
        // ===================================
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setLayout(new BoxLayout(panelEncabezado, BoxLayout.Y_AXIS));
        panelEncabezado.setOpaque(false);

        JLabel titulo = new JLabel("DETALLES DE LA NAVE", SwingConstants.CENTER);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitulo = new JLabel(
                "Telemetría en tiempo real de recursos, propulsión y disponibilidad operativa",
                SwingConstants.CENTER
        );
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setForeground(new Color(186, 230, 253));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panelEncabezado.add(titulo);
        panelEncabezado.add(Box.createVerticalStrut(4));
        panelEncabezado.add(subtitulo);

        this.add(panelEncabezado, BorderLayout.NORTH);

        // ===================================
        // TARJETA IDENTIFICACIÓN (IZQUIERDA)
        // ===================================
        PanelTranslucido cardIdentidad = new PanelTranslucido(
                18,
                new Color(15, 23, 42, 175),
                new Color(56, 189, 248, 120)
        );
        cardIdentidad.setLayout(new BorderLayout(0, 12));
        cardIdentidad.setBorder(new EmptyBorder(18, 22, 18, 22));

        cardIdentidad.add(crearEncabezadoTarjeta(
                "🚀",
                "Identificación de Unidad",
                "Perfil estructural y disponibilidad general de vuelo"
        ), BorderLayout.NORTH);

        // Vitrina central con badges (ID y Tipo), ilustración escalada y nombre
        this.txtId = new JLabel("ID: -");
        this.txtId.setFont(new Font("Segoe UI", Font.BOLD, 11));
        this.txtId.setForeground(new Color(56, 189, 248));

        this.txtTipo = new JLabel("Tipo: -");
        this.txtTipo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        this.txtTipo.setForeground(new Color(224, 242, 254));

        this.nombreNave = new JLabel("SIN ASIGNAR", SwingConstants.CENTER);
        this.nombreNave.setForeground(Color.WHITE);
        this.nombreNave.setFont(new Font("Segoe UI", Font.BOLD, 20));

        this.panelVitrina = new VitrinaNavePanel(this.txtId, this.txtTipo, this.nombreNave);
        cardIdentidad.add(this.panelVitrina, BorderLayout.CENTER);

        // Tiles inferiores de estado operativo y tripulación
        this.txtEstadoOperativo = new JLabel("Estado: No disponible para operar");
        this.txtTripulacion = new JLabel("Tripulación: No asignada");

        JPanel panelEstadosIzquierda = new JPanel(new GridLayout(2, 1, 0, 10));
        panelEstadosIzquierda.setOpaque(false);

        panelEstadosIzquierda.add(crearTarjetaMetrica(
                "🛡️",
                new Color(16, 185, 129),
                "Capacidad Operativa",
                this.txtEstadoOperativo,
                "Disponibilidad general para iniciar misiones"
        ));

        panelEstadosIzquierda.add(crearTarjetaMetrica(
                "⭐",
                new Color(56, 189, 248),
                "Dotación a Bordo",
                this.txtTripulacion,
                "Requisito mínimo: 1 Capitán y 4 Oficiales"
        ));

        cardIdentidad.add(panelEstadosIzquierda, BorderLayout.SOUTH);

        // ===================================
        // TARJETA TELEMETRÍA (DERECHA)
        // ===================================
        PanelTranslucido cardTelemetria = new PanelTranslucido(
                18,
                new Color(15, 23, 42, 175),
                new Color(56, 189, 248, 120)
        );
        cardTelemetria.setLayout(new BorderLayout(0, 12));
        cardTelemetria.setBorder(new EmptyBorder(18, 22, 18, 22));

        cardTelemetria.add(crearEncabezadoTarjeta(
                "⚡",
                "Recursos y Propulsión",
                "Monitoreo reactivo de reservas energéticas y sistemas"
        ), BorderLayout.NORTH);

        // Tres tarjetas de recursos con barras de progreso reactivas
        this.txtCombustible = new JLabel("Combustible: 0");
        this.txtEnergia = new JLabel("Energía: 0");
        this.txtDesgaste = new JLabel("Desgaste: 0");

        this.barraCombustible = new TarjetaRecurso(
                "⛽",
                "Reserva de Combustible",
                this.txtCombustible,
                new Color(56, 189, 248)
        );
        this.barraEnergia = new TarjetaRecurso(
                "🔋",
                "Núcleo de Energía",
                this.txtEnergia,
                new Color(52, 211, 153)
        );
        this.barraDesgaste = new TarjetaRecurso(
                "🔧",
                "Desgaste Estructural",
                this.txtDesgaste,
                new Color(251, 191, 36)
        );

        JPanel panelBarrasRecursos = new JPanel(new GridLayout(3, 1, 0, 10));
        panelBarrasRecursos.setOpaque(false);
        panelBarrasRecursos.add(this.barraCombustible);
        panelBarrasRecursos.add(this.barraEnergia);
        panelBarrasRecursos.add(this.barraDesgaste);

        cardTelemetria.add(panelBarrasRecursos, BorderLayout.CENTER);

        // Tiles inferiores de Motor Warp y Mantenimiento
        this.txtEstadoMotor = new JLabel("Motor Warp: Disponible");
        this.txtMantenimiento = new JLabel("Mantenimiento: No requerido");

        JPanel panelSistemasDerecha = new JPanel(new GridLayout(2, 1, 0, 10));
        panelSistemasDerecha.setOpaque(false);

        panelSistemasDerecha.add(crearTarjetaMetrica(
                "🌀",
                new Color(192, 132, 252),
                "Propulsión Superlumínica",
                this.txtEstadoMotor,
                "Estado actual del ciclo del Motor Warp"
        ));

        panelSistemasDerecha.add(crearTarjetaMetrica(
                "🛠️",
                new Color(251, 191, 36),
                "Diagnóstico de Casco",
                this.txtMantenimiento,
                "Revisión obligatoria al alcanzar 80 de desgaste"
        ));

        cardTelemetria.add(panelSistemasDerecha, BorderLayout.SOUTH);

        // ===================================
        // PANEL CENTRAL (DOS COLUMNAS)
        // ===================================
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 24, 0));
        panelCentral.setOpaque(false);
        panelCentral.add(cardIdentidad);
        panelCentral.add(cardTelemetria);

        this.add(panelCentral, BorderLayout.CENTER);
    }

    private JPanel crearEncabezadoTarjeta(String icono, String titulo, String subtitulo) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblIcono = new JLabel(icono + " ");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lblIcono.setForeground(new Color(56, 189, 248));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));

        filaTitulo.add(lblIcono);
        filaTitulo.add(lblTitulo);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSub.setForeground(new Color(148, 163, 184));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        header.add(filaTitulo);
        header.add(Box.createVerticalStrut(2));
        header.add(lblSub);
        return header;
    }

    /**
     * Crea un tile con icono y textos reactivos alineado con la estética de Tripulación.
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
        tarjeta.setPreferredSize(new Dimension(0, 72));

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

        JLabel lblIcono = new JLabel(icono, SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblIcono.setForeground(colorAcento);
        panelIcono.add(lblIcono, BorderLayout.CENTER);

        JPanel panelContenido = new JPanel(new GridLayout(3, 1, 0, 1));
        panelContenido.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo.toUpperCase());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitulo.setForeground(new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 220));

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblValor.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(148, 163, 184));

        panelContenido.add(lblTitulo);
        panelContenido.add(lblValor);
        panelContenido.add(lblSub);

        tarjeta.add(panelIcono, BorderLayout.WEST);
        tarjeta.add(panelContenido, BorderLayout.CENTER);

        return tarjeta;
    }

    private BufferedImage cargarImagenRecortada(String rutaImagen) {
        URL ruta = getClass().getResource(rutaImagen);
        if (ruta == null) {
            throw new RuntimeException("No se encontró la imagen: " + rutaImagen);
        }

        ImageIcon iconoOriginal = new ImageIcon(ruta);
        BufferedImage original = new BufferedImage(
                iconoOriginal.getIconWidth(),
                iconoOriginal.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D gOriginal = original.createGraphics();
        gOriginal.drawImage(iconoOriginal.getImage(), 0, 0, null);
        gOriginal.dispose();

        return recortarTransparencia(original);
    }

    private BufferedImage recortarTransparencia(BufferedImage imagen) {
        int minX = imagen.getWidth();
        int minY = imagen.getHeight();
        int maxX = 0;
        int maxY = 0;

        for (int y = 0; y < imagen.getHeight(); y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                int alpha = (imagen.getRGB(x, y) >> 24) & 0xff;
                if (alpha > 10) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (minX > maxX || minY > maxY) {
            return imagen;
        }

        return imagen.getSubimage(
                minX,
                minY,
                maxX - minX + 1,
                maxY - minY + 1
        );
    }

    @Override
    public void mostrarId(int id) {
        this.txtId.setText("ID: " + id);
    }

    @Override
    public void mostrarTipo(String tipo) {
        this.txtTipo.setText("Tipo: " + tipo);
        this.nombreNave.setText("NAVE " + tipo.toUpperCase());

        if (tipo.equalsIgnoreCase("Exploradora")) {
            this.panelVitrina.setDatosVisuales(
                    cargarImagenRecortada("/imagenes/naves/exploradora.png"),
                    new Color(56, 189, 248)
            );
        } else if (tipo.equalsIgnoreCase("Carguero") || tipo.equalsIgnoreCase("Carguera")) {
            this.panelVitrina.setDatosVisuales(
                    cargarImagenRecortada("/imagenes/naves/carguera.png"),
                    new Color(251, 191, 36)
            );
        } else if (tipo.equalsIgnoreCase("Combate")) {
            this.panelVitrina.setDatosVisuales(
                    cargarImagenRecortada("/imagenes/naves/combate.png"),
                    new Color(192, 132, 252)
            );
        }
    }

    @Override
    public void mostrarCombustible(int combustible) {
        this.txtCombustible.setText("Combustible: " + combustible);
        Color color = combustible < 15
                ? new Color(248, 113, 113)
                : (combustible < 30 ? new Color(251, 191, 36) : new Color(56, 189, 248));
        this.barraCombustible.actualizarValor(combustible, combustible + " / 100", color);
    }

    @Override
    public void mostrarEnergia(int energia) {
        this.txtEnergia.setText("Energía: " + energia);
        Color color = energia < 15
                ? new Color(248, 113, 113)
                : (energia < 30 ? new Color(251, 191, 36) : new Color(52, 211, 153));
        this.barraEnergia.actualizarValor(energia, energia + " / 100", color);
    }

    @Override
    public void mostrarDesgaste(int desgaste) {
        this.txtDesgaste.setText("Desgaste: " + desgaste);
        Color color = desgaste >= 80
                ? new Color(248, 113, 113)
                : (desgaste >= 50 ? new Color(251, 191, 36) : new Color(74, 222, 128));
        this.barraDesgaste.actualizarValor(desgaste, desgaste + " / 100", color);
    }

    @Override
    public void mostrarMantenimiento(boolean requiereMantenimiento) {
        if (requiereMantenimiento) {
            this.txtMantenimiento.setText("Mantenimiento: Requerido");
            this.txtMantenimiento.setForeground(new Color(248, 113, 113));
        } else {
            this.txtMantenimiento.setText("Mantenimiento: No requerido");
            this.txtMantenimiento.setForeground(new Color(74, 222, 128));
        }
    }

    @Override
    public void mostrarEstadoMotor(String estado) {
        this.txtEstadoMotor.setText("Motor Warp: " + estado);
        if ("Disponible".equalsIgnoreCase(estado)) {
            this.txtEstadoMotor.setForeground(new Color(74, 222, 128));
        } else if ("En warp".equalsIgnoreCase(estado)) {
            this.txtEstadoMotor.setForeground(new Color(192, 132, 252));
        } else if ("Preparando salto".equalsIgnoreCase(estado)) {
            this.txtEstadoMotor.setForeground(new Color(56, 189, 248));
        } else {
            this.txtEstadoMotor.setForeground(new Color(251, 191, 36));
        }
    }

    @Override
    public void mostrarTripulacion(boolean tieneTripulacion) {
        if (tieneTripulacion) {
            this.txtTripulacion.setText("Tripulación: Asignada");
            this.txtTripulacion.setForeground(new Color(74, 222, 128));
        } else {
            this.txtTripulacion.setText("Tripulación: No asignada");
            this.txtTripulacion.setForeground(new Color(251, 191, 36));
        }
    }

    @Override
    public void mostrarEstadoOperativo(boolean listaParaOperar) {
        if (listaParaOperar) {
            this.txtEstadoOperativo.setText("Estado: Lista para operar");
            this.txtEstadoOperativo.setForeground(new Color(74, 222, 128));
        } else {
            this.txtEstadoOperativo.setText("Estado: No disponible para operar");
            this.txtEstadoOperativo.setForeground(new Color(251, 191, 36));
        }
    }

    // ===================
    // PATRÓN OBSERVER
    // ===================

    @Override
    public void notificarCambio(Nave nave) {
        if (nave != null) {
            // Se captura una instantánea inmutable en el hilo invocante antes de despachar al EDT
            // para evitar condiciones de carrera cuando la misión modifique la nave en otro hilo.
            final int combustible = nave.getCombustible();
            final int energia = nave.getEnergia();
            final int desgaste = nave.getDesgaste();
            final boolean requiereMantenimiento = nave.requiereMantenimiento();
            final String estadoMotor = nave.getMotor().getEstado().toString();
            final boolean tieneTripulacion = nave.getTripulacion() != null;
            final boolean listaParaOperar = nave.estaListaParaOperar();

            Runnable actualizacion = () -> actualizarComponentes(
                    combustible,
                    energia,
                    desgaste,
                    requiereMantenimiento,
                    estadoMotor,
                    tieneTripulacion,
                    listaParaOperar
            );

            if (SwingUtilities.isEventDispatchThread()) {
                actualizacion.run();
            } else {
                SwingUtilities.invokeLater(actualizacion);
            }
        }
    }

    private void actualizarComponentes(
            int combustible,
            int energia,
            int desgaste,
            boolean requiereMantenimiento,
            String estadoMotor,
            boolean tieneTripulacion,
            boolean listaParaOperar
    ) {
        mostrarCombustible(combustible);
        mostrarEnergia(energia);
        mostrarDesgaste(desgaste);
        mostrarMantenimiento(requiereMantenimiento);
        mostrarEstadoMotor(estadoMotor);
        mostrarTripulacion(tieneTripulacion);
        mostrarEstadoOperativo(listaParaOperar);
    }

    // ========================================================
    // GETTERS PARA VERIFICACIÓN Y PRUEBAS UNITARIAS
    // ========================================================

    String getTextoCombustible() {
        return this.txtCombustible.getText();
    }

    String getTextoEnergia() {
        return this.txtEnergia.getText();
    }

    String getTextoDesgaste() {
        return this.txtDesgaste.getText();
    }

    String getTextoMantenimiento() {
        return this.txtMantenimiento.getText();
    }

    String getTextoEstadoMotor() {
        return this.txtEstadoMotor.getText();
    }

    String getTextoTripulacion() {
        return this.txtTripulacion.getText();
    }

    String getTextoEstadoOperativo() {
        return this.txtEstadoOperativo.getText();
    }

    // =========================================================================
    // COMPONENTES PERSONALIZADOS CON TRANSLUCIDEZ (GLASSMORPHISM)
    // =========================================================================

    /**
     * Vitrina central para la unidad activa: muestra las insignias de ID y Tipo,
     * dibuja la ilustración de la nave centrada y escalada proporcionalmente, y el título inferior.
     */
    private static class VitrinaNavePanel extends JPanel {
        private BufferedImage imagen;
        private Color acento = new Color(56, 189, 248);

        VitrinaNavePanel(JLabel lblId, JLabel lblTipo, JLabel lblNombre) {
            super(new BorderLayout(0, 6));
            setOpaque(false);
            setBorder(new EmptyBorder(12, 14, 12, 14));

            JPanel barraSuperior = new JPanel(new BorderLayout());
            barraSuperior.setOpaque(false);
            barraSuperior.add(crearPillComponente(lblId, new Color(56, 189, 248)), BorderLayout.WEST);
            barraSuperior.add(crearPillComponente(lblTipo, new Color(148, 163, 184)), BorderLayout.EAST);

            JPanel lienzoImagen = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (imagen == null) {
                        return;
                    }
                    int w = getWidth();
                    int h = getHeight();
                    if (w <= 20 || h <= 20) {
                        return;
                    }
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    double escala = Math.min((double) (w - 24) / imagen.getWidth(), (double) (h - 12) / imagen.getHeight());
                    int drawW = Math.max(1, (int) Math.round(imagen.getWidth() * escala));
                    int drawH = Math.max(1, (int) Math.round(imagen.getHeight() * escala));
                    int drawX = (w - drawW) / 2;
                    int drawY = (h - drawH) / 2;

                    g2.drawImage(imagen, drawX, drawY, drawW, drawH, null);
                    g2.dispose();
                }
            };
            lienzoImagen.setOpaque(false);

            add(barraSuperior, BorderLayout.NORTH);
            add(lienzoImagen, BorderLayout.CENTER);
            add(lblNombre, BorderLayout.SOUTH);
        }

        void setDatosVisuales(BufferedImage imagen, Color acento) {
            this.imagen = imagen;
            this.acento = acento;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(8, 15, 30, 105));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

            g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 85));
            g2.setStroke(new BasicStroke(1.1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

            g2.dispose();
            super.paintComponent(g);
        }

        private static JPanel crearPillComponente(JLabel contenido, Color colorBorde) {
            JPanel pill = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 3)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(15, 23, 42, 190));
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    g2.setColor(new Color(colorBorde.getRed(), colorBorde.getGreen(), colorBorde.getBlue(), 120));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            pill.setOpaque(false);
            pill.add(contenido);
            return pill;
        }
    }

    /**
     * Tarjeta reactiva para un recurso (Combustible, Energía o Desgaste) con barra de progreso.
     */
    private static class TarjetaRecurso extends JPanel {
        private final JLabel lblBadge;
        private int valor = 0;
        private Color colorAcento;

        TarjetaRecurso(String icono, String subtituloCategoria, JLabel lblPrincipal, Color colorInicial) {
            super(new BorderLayout(14, 0));
            this.colorAcento = colorInicial;
            setOpaque(false);
            setBorder(new EmptyBorder(10, 14, 10, 14));

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

            JLabel lblIcono = new JLabel(icono, SwingConstants.CENTER);
            lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            panelIcono.add(lblIcono, BorderLayout.CENTER);

            JPanel panelDerecho = new JPanel(new BorderLayout(0, 6));
            panelDerecho.setOpaque(false);

            JPanel filaTextos = new JPanel(new BorderLayout(8, 0));
            filaTextos.setOpaque(false);

            JPanel bloqueTitulos = new JPanel(new GridLayout(2, 1, 0, 1));
            bloqueTitulos.setOpaque(false);

            JLabel lblCategoria = new JLabel(subtituloCategoria.toUpperCase());
            lblCategoria.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblCategoria.setForeground(new Color(148, 163, 184));

            lblPrincipal.setFont(new Font("Segoe UI", Font.BOLD, 15));
            lblPrincipal.setForeground(Color.WHITE);

            bloqueTitulos.add(lblCategoria);
            bloqueTitulos.add(lblPrincipal);

            this.lblBadge = new JLabel("0 / 100", SwingConstants.RIGHT);
            this.lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
            this.lblBadge.setForeground(colorInicial);

            filaTextos.add(bloqueTitulos, BorderLayout.CENTER);
            filaTextos.add(this.lblBadge, BorderLayout.EAST);

            JPanel lienzoBarra = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = 8;
                    int y = (getHeight() - h) / 2;

                    g2.setColor(new Color(255, 255, 255, 24));
                    g2.fillRoundRect(0, y, w, h, 8, 8);

                    int fillW = Math.max(0, Math.min(w, (int) Math.round((valor / 100.0) * w)));
                    if (fillW > 0) {
                        g2.setColor(colorAcento);
                        g2.fillRoundRect(0, y, fillW, h, 8, 8);
                    }

                    g2.dispose();
                }
            };
            lienzoBarra.setOpaque(false);
            lienzoBarra.setPreferredSize(new Dimension(0, 12));

            panelDerecho.add(filaTextos, BorderLayout.CENTER);
            panelDerecho.add(lienzoBarra, BorderLayout.SOUTH);

            add(panelIcono, BorderLayout.WEST);
            add(panelDerecho, BorderLayout.CENTER);
        }

        void actualizarValor(int nuevoValor, String textoBadge, Color nuevoColor) {
            this.valor = Math.max(0, Math.min(100, nuevoValor));
            this.colorAcento = nuevoColor;
            this.lblBadge.setText(textoBadge);
            this.lblBadge.setForeground(nuevoColor);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 255, 255, 12));
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

            g2.setColor(new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 75));
            g2.setStroke(new BasicStroke(1.1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Panel contenedor translúcido con bordes redondeados (glassmorphism).
     */
    private static class PanelTranslucido extends JPanel {
        private final int radio;
        private final Color colorFondo;
        private final Color colorBorde;

        PanelTranslucido(int radio, Color colorFondo, Color colorBorde) {
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
}
