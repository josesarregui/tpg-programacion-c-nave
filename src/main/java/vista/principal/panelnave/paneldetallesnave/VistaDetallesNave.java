package vista.principal.panelnave.paneldetallesnave;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import modelo.nave.Nave;
import modelo.nave.ObservadorNave;

/**
 * Vista de detalles y telemetría de la nave.
 * Implementa {@link ObservadorNave} (patrón Observer) para reflejar en tiempo real
 * los cambios en los recursos y el estado de la nave operada.
 * Todo el diseño y las tareas visuales se resuelven en la misma clase sin subclases internas.
 */
public class VistaDetallesNave extends JPanel implements IVistaDetallesNave {
    private final JPanel panelVitrina;
    private JLabel lblImagenVitrina;
    private Color colorAcentoVitrina = new Color(56, 189, 248);

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

    private final JPanel tarjetaCombustible;
    private final JProgressBar barraCombustible;
    private final JLabel lblBadgeCombustible;

    private final JPanel tarjetaEnergia;
    private final JProgressBar barraEnergia;
    private final JLabel lblBadgeEnergia;

    private final JPanel tarjetaDesgaste;
    private final JProgressBar barraDesgaste;
    private final JLabel lblBadgeDesgaste;

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
        JPanel cardIdentidad = crearPanelTranslucido(
                new Color(15, 23, 42, 175),
                new Color(56, 189, 248, 120)
        );
        cardIdentidad.setLayout(new BorderLayout(0, 12));

        cardIdentidad.add(crearEncabezadoTarjeta(
                "🚀",
                "Identificación de Unidad",
                "Perfil estructural y disponibilidad general de vuelo"
        ), BorderLayout.NORTH);

        // Vitrina central con badges (ID y Tipo), ilustración y nombre
        this.txtId = new JLabel("ID: -");
        this.txtId.setFont(new Font("Segoe UI", Font.BOLD, 11));
        this.txtId.setForeground(new Color(56, 189, 248));

        this.txtTipo = new JLabel("Tipo: -");
        this.txtTipo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        this.txtTipo.setForeground(new Color(224, 242, 254));

        this.nombreNave = new JLabel("SIN ASIGNAR", SwingConstants.CENTER);
        this.nombreNave.setForeground(Color.WHITE);
        this.nombreNave.setFont(new Font("Segoe UI", Font.BOLD, 20));

        this.panelVitrina = crearPanelVitrina(this.txtId, this.txtTipo, this.nombreNave);
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
        JPanel cardTelemetria = crearPanelTranslucido(
                new Color(15, 23, 42, 175),
                new Color(56, 189, 248, 120)
        );
        cardTelemetria.setLayout(new BorderLayout(0, 12));

        cardTelemetria.add(crearEncabezadoTarjeta(
                "⚡",
                "Recursos y Propulsión",
                "Monitoreo reactivo de reservas energéticas y sistemas"
        ), BorderLayout.NORTH);

        // Tres tarjetas de recursos con barras de progreso reactivas
        this.txtCombustible = new JLabel("Combustible: 0");
        this.barraCombustible = new JProgressBar(0, 100);
        this.lblBadgeCombustible = new JLabel("0 / 100", SwingConstants.RIGHT);
        this.tarjetaCombustible = crearTarjetaRecurso(
                "⛽",
                "Reserva de Combustible",
                this.txtCombustible,
                new Color(56, 189, 248),
                this.barraCombustible,
                this.lblBadgeCombustible
        );

        this.txtEnergia = new JLabel("Energía: 0");
        this.barraEnergia = new JProgressBar(0, 100);
        this.lblBadgeEnergia = new JLabel("0 / 100", SwingConstants.RIGHT);
        this.tarjetaEnergia = crearTarjetaRecurso(
                "🔋",
                "Núcleo de Energía",
                this.txtEnergia,
                new Color(52, 211, 153),
                this.barraEnergia,
                this.lblBadgeEnergia
        );

        this.txtDesgaste = new JLabel("Desgaste: 0");
        this.barraDesgaste = new JProgressBar(0, 100);
        this.lblBadgeDesgaste = new JLabel("0 / 100", SwingConstants.RIGHT);
        this.tarjetaDesgaste = crearTarjetaRecurso(
                "🔧",
                "Desgaste Estructural",
                this.txtDesgaste,
                new Color(251, 191, 36),
                this.barraDesgaste,
                this.lblBadgeDesgaste
        );

        JPanel panelBarrasRecursos = new JPanel(new GridLayout(3, 1, 0, 10));
        panelBarrasRecursos.setOpaque(false);
        panelBarrasRecursos.add(this.tarjetaCombustible);
        panelBarrasRecursos.add(this.tarjetaEnergia);
        panelBarrasRecursos.add(this.tarjetaDesgaste);

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

    private JPanel crearTarjetaMetrica(
            String icono,
            Color colorAcento,
            String titulo,
            JLabel lblValor,
            String subtitulo
    ) {
        JPanel tarjeta = new JPanel(new BorderLayout(14, 0));
        tarjeta.setOpaque(true);
        tarjeta.setBackground(new Color(15, 23, 42, 160));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 70), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        tarjeta.setPreferredSize(new Dimension(0, 72));

        JPanel panelIcono = new JPanel(new BorderLayout());
        panelIcono.setOpaque(true);
        panelIcono.setBackground(new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 35));
        panelIcono.setBorder(BorderFactory.createLineBorder(new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 120), 1, true));
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
            return null;
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

    // ========================================================
    // TAREAS DE VITRINA Y RECURSOS INTEGRADAS EN LA MISMA CLASE
    // ========================================================

    private JPanel crearPanelVitrina(JLabel lblId, JLabel lblTipo, JLabel lblNombre) {
        JPanel vitrina = new JPanel(new BorderLayout(0, 6));
        vitrina.setOpaque(true);
        vitrina.setBackground(new Color(8, 15, 30, 105));
        vitrina.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(this.colorAcentoVitrina.getRed(), this.colorAcentoVitrina.getGreen(), this.colorAcentoVitrina.getBlue(), 85), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);
        barraSuperior.add(crearPillComponente(lblId, new Color(56, 189, 248)), BorderLayout.WEST);
        barraSuperior.add(crearPillComponente(lblTipo, new Color(148, 163, 184)), BorderLayout.EAST);

        this.lblImagenVitrina = new JLabel();
        this.lblImagenVitrina.setHorizontalAlignment(SwingConstants.CENTER);
        this.lblImagenVitrina.setVerticalAlignment(SwingConstants.CENTER);
        this.lblImagenVitrina.setOpaque(false);

        vitrina.add(barraSuperior, BorderLayout.NORTH);
        vitrina.add(this.lblImagenVitrina, BorderLayout.CENTER);
        vitrina.add(lblNombre, BorderLayout.SOUTH);

        return vitrina;
    }

    private void actualizarDatosVisualesVitrina(BufferedImage imagen, Color acento) {
        this.colorAcentoVitrina = acento;
        if (this.panelVitrina != null) {
            this.panelVitrina.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 85), 1, true),
                    new EmptyBorder(12, 14, 12, 14)
            ));
            this.panelVitrina.repaint();
        }
        if (imagen != null && this.lblImagenVitrina != null) {
            int maxW = 300;
            int maxH = 170;
            double escala = Math.min((double) maxW / imagen.getWidth(), (double) maxH / imagen.getHeight());
            int drawW = Math.max(1, (int) Math.round(imagen.getWidth() * escala));
            int drawH = Math.max(1, (int) Math.round(imagen.getHeight() * escala));
            Image escalada = imagen.getScaledInstance(drawW, drawH, Image.SCALE_SMOOTH);
            this.lblImagenVitrina.setIcon(new ImageIcon(escalada));
        }
    }

    private JPanel crearPillComponente(JLabel contenido, Color colorBorde) {
        JPanel pill = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 3));
        pill.setOpaque(true);
        pill.setBackground(new Color(15, 23, 42, 190));
        pill.setBorder(BorderFactory.createLineBorder(new Color(colorBorde.getRed(), colorBorde.getGreen(), colorBorde.getBlue(), 120), 1, true));
        pill.add(contenido);
        return pill;
    }

    private JPanel crearTarjetaRecurso(
            String icono,
            String subtituloCategoria,
            JLabel lblPrincipal,
            Color colorInicial,
            JProgressBar barraProgreso,
            JLabel lblBadge
    ) {
        JPanel tarjeta = new JPanel(new BorderLayout(14, 0));
        tarjeta.setOpaque(true);
        tarjeta.setBackground(new Color(15, 23, 42, 175));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(colorInicial.getRed(), colorInicial.getGreen(), colorInicial.getBlue(), 75), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JPanel panelIcono = new JPanel(new BorderLayout());
        panelIcono.setOpaque(true);
        panelIcono.setBackground(new Color(colorInicial.getRed(), colorInicial.getGreen(), colorInicial.getBlue(), 35));
        panelIcono.setBorder(BorderFactory.createLineBorder(new Color(colorInicial.getRed(), colorInicial.getGreen(), colorInicial.getBlue(), 120), 1, true));
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

        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadge.setForeground(colorInicial);

        filaTextos.add(bloqueTitulos, BorderLayout.CENTER);
        filaTextos.add(lblBadge, BorderLayout.EAST);

        barraProgreso.setPreferredSize(new Dimension(0, 8));
        barraProgreso.setStringPainted(false);
        barraProgreso.setForeground(colorInicial);
        barraProgreso.setBackground(new Color(255, 255, 255, 24));
        barraProgreso.setBorder(BorderFactory.createEmptyBorder());

        panelDerecho.add(filaTextos, BorderLayout.CENTER);
        panelDerecho.add(barraProgreso, BorderLayout.SOUTH);

        tarjeta.add(panelIcono, BorderLayout.WEST);
        tarjeta.add(panelDerecho, BorderLayout.CENTER);

        return tarjeta;
    }

    private void actualizarTarjetaRecurso(
            JPanel tarjeta,
            JProgressBar barra,
            JLabel badge,
            int valor,
            String textoBadge,
            Color nuevoColor
    ) {
        int valorAjustado = Math.max(0, Math.min(100, valor));
        barra.setValue(valorAjustado);
        barra.setForeground(nuevoColor);
        badge.setText(textoBadge);
        badge.setForeground(nuevoColor);
        if (tarjeta != null) {
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(nuevoColor.getRed(), nuevoColor.getGreen(), nuevoColor.getBlue(), 75), 1, true),
                    new EmptyBorder(10, 14, 10, 14)
            ));
            tarjeta.repaint();
        }
    }

    private JPanel crearPanelTranslucido(Color colorFondo, Color colorBorde) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(colorFondo);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBorde, 1, true),
                new EmptyBorder(18, 22, 18, 22)
        ));
        return panel;
    }

    // ========================================================
    // MÉTODOS DE LA INTERFAZ IVISTADETALLESNAVE
    // ========================================================

    @Override
    public void mostrarId(int id) {
        this.txtId.setText("ID: " + id);
    }

    @Override
    public void mostrarTipo(String tipo) {
        this.txtTipo.setText("Tipo: " + tipo);
        this.nombreNave.setText("NAVE " + tipo.toUpperCase());

        if (tipo.equalsIgnoreCase("Exploradora")) {
            actualizarDatosVisualesVitrina(
                    cargarImagenRecortada("/imagenes/naves/exploradora.png"),
                    new Color(56, 189, 248)
            );
        } else if (tipo.equalsIgnoreCase("Carguero") || tipo.equalsIgnoreCase("Carguera")) {
            actualizarDatosVisualesVitrina(
                    cargarImagenRecortada("/imagenes/naves/carguera.png"),
                    new Color(251, 191, 36)
            );
        } else if (tipo.equalsIgnoreCase("Combate")) {
            actualizarDatosVisualesVitrina(
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
        actualizarTarjetaRecurso(this.tarjetaCombustible, this.barraCombustible, this.lblBadgeCombustible, combustible, combustible + " / 100", color);
    }

    @Override
    public void mostrarEnergia(int energia) {
        this.txtEnergia.setText("Energía: " + energia);
        Color color = energia < 15
                ? new Color(248, 113, 113)
                : (energia < 30 ? new Color(251, 191, 36) : new Color(52, 211, 153));
        actualizarTarjetaRecurso(this.tarjetaEnergia, this.barraEnergia, this.lblBadgeEnergia, energia, energia + " / 100", color);
    }

    @Override
    public void mostrarDesgaste(int desgaste) {
        this.txtDesgaste.setText("Desgaste: " + desgaste);
        Color color = desgaste >= 80
                ? new Color(248, 113, 113)
                : (desgaste >= 50 ? new Color(251, 191, 36) : new Color(74, 222, 128));
        actualizarTarjetaRecurso(this.tarjetaDesgaste, this.barraDesgaste, this.lblBadgeDesgaste, desgaste, desgaste + " / 100", color);
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
}
