package vista.principal.panelcrearnave;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import modelo.nave.TipoNave;

/**
 * Vista para la selección y creación de la nave interestelar.
 * Emplea tarjetas translúcidas (glassmorphism) coherentes con el diseño visual
 * de los paneles de Tripulación.
 */
public class VistaCrearNave extends JPanel implements IVistaCrearNave {

    private final TarjetaNave bExploradora;
    private final TarjetaNave bCarguera;
    private final TarjetaNave bCombate;
    private final BotonEstilizado bAceptar;

    private final JLabel txtSeleccion;

    private TipoNave tipoSeleccionado;

    public VistaCrearNave() {
        this.setLayout(new BorderLayout(0, 14));
        this.setOpaque(false);
        this.setBorder(new EmptyBorder(16, 32, 16, 32));

        // ===================================
        // ENCABEZADO SUPERIOR
        // ===================================
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setLayout(new BoxLayout(panelEncabezado, BoxLayout.Y_AXIS));
        panelEncabezado.setOpaque(false);

        JLabel titulo = new JLabel("SELECCIÓN DE NAVE", SwingConstants.CENTER);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitulo = new JLabel(
                "Elige la embarcación interestelar que comandarás durante la travesía",
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
        // TARJETAS INTERACTIVAS DE NAVES
        // ===================================
        BufferedImage imgExploradora = cargarImagenRecortada("/imagenes/naves/exploradora.png");
        BufferedImage imgCarguera = cargarImagenRecortada("/imagenes/naves/carguera.png");
        BufferedImage imgCombate = cargarImagenRecortada("/imagenes/naves/combate.png");

        this.bExploradora = new TarjetaNave(
                "Exploradora",
                "RECONOCIMIENTO",
                "Sensores de largo alcance y equilibrio energético",
                imgExploradora,
                new Color(56, 189, 248),
                60,
                80,
                0
        );
        this.bExploradora.setActionCommand(IVistaCrearNave.SELECCIONAR_EXPLORADORA);

        this.bCarguera = new TarjetaNave(
                "Carguero",
                "LOGÍSTICA PESADA",
                "Máxima autonomía inicial de combustible en ruta",
                imgCarguera,
                new Color(251, 191, 36),
                100,
                60,
                0
        );
        this.bCarguera.setActionCommand(IVistaCrearNave.SELECCIONAR_CARGUERA);

        this.bCombate = new TarjetaNave(
                "Combate",
                "DEFENSA TÁCTICA",
                "Núcleo energético de máxima potencia táctica",
                imgCombate,
                new Color(192, 132, 252),
                80,
                100,
                0
        );
        this.bCombate.setActionCommand(IVistaCrearNave.SELECCIONAR_COMBATE);

        JPanel panelNaves = new JPanel(new GridLayout(1, 3, 20, 0));
        panelNaves.setOpaque(false);
        panelNaves.add(this.bExploradora);
        panelNaves.add(this.bCarguera);
        panelNaves.add(this.bCombate);

        this.add(panelNaves, BorderLayout.CENTER);

        // ===================================
        // PANEL INFERIOR (ESTADO + ACEPTAR)
        // ===================================
        this.txtSeleccion = new JLabel("Ninguna nave seleccionada", SwingConstants.CENTER);
        this.txtSeleccion.setForeground(new Color(186, 230, 253));
        this.txtSeleccion.setFont(new Font("Segoe UI", Font.BOLD, 13));

        PanelTranslucido badgeSeleccion = new PanelTranslucido(
                14,
                new Color(15, 23, 42, 180),
                new Color(56, 189, 248, 95)
        );
        badgeSeleccion.setLayout(new FlowLayout(FlowLayout.CENTER, 16, 6));
        badgeSeleccion.add(this.txtSeleccion);

        JPanel filaSeleccion = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        filaSeleccion.setOpaque(false);
        filaSeleccion.add(badgeSeleccion);

        this.bAceptar = new BotonEstilizado(
                "Confirmar y Crear Nave",
                new Color(16, 185, 129, 215),
                new Color(34, 197, 94, 245),
                new Color(74, 222, 128, 220),
                14
        );
        this.bAceptar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        this.bAceptar.setPreferredSize(new Dimension(320, 44));
        this.bAceptar.setActionCommand(IVistaCrearNave.ACEPTAR);

        JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        filaBoton.setOpaque(false);
        filaBoton.add(this.bAceptar);

        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));
        panelInferior.setOpaque(false);
        panelInferior.add(filaSeleccion);
        panelInferior.add(Box.createVerticalStrut(8));
        panelInferior.add(filaBoton);

        this.add(panelInferior, BorderLayout.SOUTH);
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
    public TipoNave getTipoSeleccionado() {
        return this.tipoSeleccionado;
    }

    @Override
    public void setTipoSeleccionado(TipoNave tipo) {
        this.tipoSeleccionado = tipo;

        boolean haySeleccion = tipo != null;
        this.bExploradora.actualizarEstado(tipo == TipoNave.EXPLORADORA, haySeleccion && tipo != TipoNave.EXPLORADORA);
        this.bCarguera.actualizarEstado(tipo == TipoNave.CARGUERO, haySeleccion && tipo != TipoNave.CARGUERO);
        this.bCombate.actualizarEstado(tipo == TipoNave.COMBATE, haySeleccion && tipo != TipoNave.COMBATE);

        if (tipo == TipoNave.EXPLORADORA) {
            this.txtSeleccion.setText("Nave seleccionada: Exploradora");
            this.txtSeleccion.setForeground(new Color(56, 189, 248));
        } else if (tipo == TipoNave.CARGUERO) {
            this.txtSeleccion.setText("Nave seleccionada: Carguero");
            this.txtSeleccion.setForeground(new Color(251, 191, 36));
        } else if (tipo == TipoNave.COMBATE) {
            this.txtSeleccion.setText("Nave seleccionada: Combate");
            this.txtSeleccion.setForeground(new Color(192, 132, 252));
        } else {
            this.txtSeleccion.setText("Ninguna nave seleccionada");
            this.txtSeleccion.setForeground(new Color(186, 230, 253));
        }

        this.revalidate();
        this.repaint();
    }

    @Override
    public void setControlador(ActionListener controlador) {
        this.bExploradora.addActionListener(controlador);
        this.bCarguera.addActionListener(controlador);
        this.bCombate.addActionListener(controlador);
        this.bAceptar.addActionListener(controlador);
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

    // =========================================================================
    // GETTERS DE PAQUETE PARA PRUEBAS UNITARIAS
    // =========================================================================

    JButton getBotonExploradora() {
        return this.bExploradora;
    }

    JButton getBotonCarguera() {
        return this.bCarguera;
    }

    JButton getBotonCombate() {
        return this.bCombate;
    }

    JButton getBotonAceptar() {
        return this.bAceptar;
    }

    JLabel getLblSeleccion() {
        return this.txtSeleccion;
    }

    // =========================================================================
    // COMPONENTES PERSONALIZADOS CON TRANSLUCIDEZ (GLASSMORPHISM)
    // =========================================================================

    /**
     * Tarjeta interactiva de selección de nave con ilustración escalada proporcionalmente,
     * insignia de rol e indicadores de configuración inicial.
     */
    private static class TarjetaNave extends JButton {
        private final String nombre;
        private final String rol;
        private final String descripcion;
        private final BufferedImage imagen;
        private final Color acento;
        private final int combustible;
        private final int energia;
        private final int desgaste;

        private boolean seleccionada = false;
        private boolean atenuada = false;
        private boolean hover = false;

        TarjetaNave(
                String nombre,
                String rol,
                String descripcion,
                BufferedImage imagen,
                Color acento,
                int combustible,
                int energia,
                int desgaste
        ) {
            this.nombre = nombre;
            this.rol = rol;
            this.descripcion = descripcion;
            this.imagen = imagen;
            this.acento = acento;
            this.combustible = combustible;
            this.energia = energia;
            this.desgaste = desgaste;

            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        void actualizarEstado(boolean seleccionada, boolean atenuada) {
            this.seleccionada = seleccionada;
            this.atenuada = atenuada;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth();
            int h = getHeight();
            int padX = 18;
            int padY = 16;

            // 1. Fondo y borde translúcido de la tarjeta
            Color fondoTarjeta;
            if (seleccionada) {
                fondoTarjeta = new Color(18, 30, 54, 210);
            } else if (hover) {
                fondoTarjeta = new Color(20, 32, 56, 195);
            } else if (atenuada) {
                fondoTarjeta = new Color(15, 23, 42, 145);
            } else {
                fondoTarjeta = new Color(15, 23, 42, 175);
            }
            g2.setColor(fondoTarjeta);
            g2.fillRoundRect(1, 1, w - 2, h - 2, 18, 18);

            if (seleccionada) {
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 235));
                g2.setStroke(new BasicStroke(2.2f));
            } else if (hover) {
                g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 180));
                g2.setStroke(new BasicStroke(1.5f));
            } else if (atenuada) {
                g2.setColor(new Color(100, 116, 139, 75));
                g2.setStroke(new BasicStroke(1.0f));
            } else {
                g2.setColor(new Color(56, 189, 248, 120));
                g2.setStroke(new BasicStroke(1.2f));
            }
            g2.drawRoundRect(1, 1, w - 3, h - 3, 18, 18);

            // 2. Fila superior: Badge de rol (izquierda) y estado (derecha)
            dibujarPill(
                    g2,
                    padX,
                    padY,
                    rol,
                    new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), seleccionada ? 55 : 35),
                    new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), seleccionada ? 200 : 120),
                    acento,
                    false,
                    false
            );

            String textoEstado = seleccionada ? "ACTIVA" : (hover ? "SELECCIONAR" : "DISPONIBLE");
            Color colorTextoEstado = seleccionada
                    ? new Color(74, 222, 128)
                    : (hover ? Color.WHITE : new Color(148, 163, 184));
            Color fondoEstado = seleccionada
                    ? new Color(16, 185, 129, 45)
                    : new Color(255, 255, 255, hover ? 25 : 12);
            Color bordeEstado = seleccionada
                    ? new Color(74, 222, 128, 180)
                    : new Color(148, 163, 184, hover ? 130 : 60);
            dibujarPill(g2, w - padX, padY, textoEstado, fondoEstado, bordeEstado, colorTextoEstado, true, seleccionada);

            // 3. Título y descripción de la clase de nave
            g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
            g2.setColor(atenuada && !hover ? new Color(203, 213, 225) : Color.WHITE);
            g2.drawString(nombre.toUpperCase(), padX, padY + 46);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(new Color(148, 163, 184));
            g2.drawString(recortarTexto(g2, descripcion, w - 2 * padX), padX, padY + 65);

            // 4. Caja inferior de configuración inicial (Ficha de Inicio)
            int teleH = 118;
            int teleY = h - padY - teleH;
            int teleW = w - 2 * padX;

            g2.setColor(new Color(255, 255, 255, seleccionada ? 16 : 10));
            g2.fillRoundRect(padX, teleY, teleW, teleH, 12, 12);
            g2.setColor(new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), seleccionada ? 95 : 45));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(padX, teleY, teleW, teleH, 12, 12);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(new Color(186, 230, 253));
            g2.drawString("CONFIGURACIÓN INICIAL", padX + 12, teleY + 18);

            String textoMotor = "Warp: Disponible";
            FontMetrics fmHeader = g2.getFontMetrics();
            g2.setColor(new Color(74, 222, 128));
            g2.drawString(textoMotor, padX + teleW - 12 - fmHeader.stringWidth(textoMotor), teleY + 18);

            dibujarBarraInicial(
                    g2, padX + 12, teleY + 34, teleW - 24,
                    "Combustible", combustible, new Color(56, 189, 248)
            );
            dibujarBarraInicial(
                    g2, padX + 12, teleY + 62, teleW - 24,
                    "Energía", energia, new Color(52, 211, 153)
            );
            dibujarBarraInicial(
                    g2, padX + 12, teleY + 90, teleW - 24,
                    "Desgaste", desgaste, new Color(251, 191, 36)
            );

            // 5. Vitrina central con la ilustración de la nave
            int showY = padY + 76;
            int showH = Math.max(70, teleY - 12 - showY);
            int showW = w - 2 * padX;

            g2.setColor(seleccionada
                    ? new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 24)
                    : new Color(8, 15, 30, 95));
            g2.fillRoundRect(padX, showY, showW, showH, 14, 14);

            g2.setColor(seleccionada
                    ? new Color(acento.getRed(), acento.getGreen(), acento.getBlue(), 110)
                    : new Color(255, 255, 255, 20));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(padX, showY, showW, showH, 14, 14);

            if (imagen != null && showW > 24 && showH > 24) {
                int maxImgW = showW - 28;
                int maxImgH = showH - 20;
                double factorEscala = seleccionarEscala(imagen.getWidth(), imagen.getHeight(), maxImgW, maxImgH);
                if (atenuada && !hover) {
                    factorEscala *= 0.90;
                }
                int drawW = Math.max(1, (int) Math.round(imagen.getWidth() * factorEscala));
                int drawH = Math.max(1, (int) Math.round(imagen.getHeight() * factorEscala));
                int drawX = padX + (showW - drawW) / 2;
                int drawY = showY + (showH - drawH) / 2;

                Composite compOriginal = g2.getComposite();
                float opacidad = (atenuada && !hover) ? 0.45f : (atenuada ? 0.80f : 1.0f);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));
                g2.drawImage(imagen, drawX, drawY, drawW, drawH, null);
                g2.setComposite(compOriginal);
            }

            g2.dispose();
        }

        private static double seleccionarEscala(int imgW, int imgH, int cajaW, int cajaH) {
            return Math.min((double) cajaW / imgW, (double) cajaH / imgH);
        }

        private static void dibujarPill(
                Graphics2D g2,
                int xRef,
                int y,
                String texto,
                Color fondo,
                Color borde,
                Color colorTexto,
                boolean alinearDerecha,
                boolean conPuntoActivo
        ) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            FontMetrics fm = g2.getFontMetrics();
            int extraPunto = conPuntoActivo ? 11 : 0;
            int pillW = fm.stringWidth(texto) + 16 + extraPunto;
            int pillH = 20;
            int x = alinearDerecha ? (xRef - pillW) : xRef;

            g2.setColor(fondo);
            g2.fillRoundRect(x, y, pillW, pillH, 8, 8);
            g2.setColor(borde);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(x, y, pillW, pillH, 8, 8);

            g2.setColor(colorTexto);
            int textX = x + 8;
            if (conPuntoActivo) {
                g2.fillOval(textX, y + 7, 6, 6);
                textX += 11;
            }
            int textY = y + ((pillH - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(texto, textX, textY);
        }

        private static void dibujarBarraInicial(
                Graphics2D g2,
                int x,
                int y,
                int ancho,
                String etiqueta,
                int valor,
                Color colorBarra
        ) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(new Color(226, 232, 240));
            g2.drawString(etiqueta, x, y);

            String txtValor = valor + " / 100";
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(colorBarra);
            g2.drawString(txtValor, x + ancho - fm.stringWidth(txtValor), y);

            int barY = y + 5;
            int barH = 6;
            g2.setColor(new Color(255, 255, 255, 24));
            g2.fillRoundRect(x, barY, ancho, barH, 6, 6);

            int fillW = Math.max(0, Math.min(ancho, (int) Math.round((valor / 100.0) * ancho)));
            if (fillW > 0) {
                g2.setColor(colorBarra);
                g2.fillRoundRect(x, barY, fillW, barH, 6, 6);
            }
        }

        private static String recortarTexto(Graphics2D g2, String texto, int anchoMaximo) {
            FontMetrics fm = g2.getFontMetrics();
            if (fm.stringWidth(texto) <= anchoMaximo) {
                return texto;
            }
            String sufijo = "...";
            int len = texto.length();
            while (len > 0 && fm.stringWidth(texto.substring(0, len) + sufijo) > anchoMaximo) {
                len--;
            }
            return texto.substring(0, Math.max(0, len)) + sufijo;
        }
    }

    /**
     * Panel con fondo translúcido y bordes redondeados.
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

    /**
     * Botón estilizado con bordes redondeados y efecto hover translúcido.
     */
    private static class BotonEstilizado extends JButton {
        private final Color colorNormal;
        private final Color colorHover;
        private final Color colorBorde;
        private final int radio;
        private boolean hover = false;

        BotonEstilizado(String texto, Color normal, Color hover, Color borde, int radio) {
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
