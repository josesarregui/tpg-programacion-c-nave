package vista.principal.panelnave.panelcrearnave;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import modelo.nave.TipoNave;

/**
 * Vista para la selección y creación de la nave interestelar.
 * Emplea un diseño espacial translúcido y tarjetas interactivas de naves.
 * Todo el diseño y las tareas visuales se resuelven en la misma clase sin
 * subclases internas.
 */
public class VistaCrearNave extends JPanel implements IVistaCrearNave {

    private final JButton bExploradora;
    private final JButton bCarguera;
    private final JButton bCombate;
    private final JButton bAceptar;

    private final JLabel lblEstadoExploradora;
    private final JLabel lblEstadoCarguera;
    private final JLabel lblEstadoCombate;

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
                SwingConstants.CENTER);
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

        this.lblEstadoExploradora = new JLabel();
        this.bExploradora = crearTarjetaNave(
                "Exploradora",
                "RECONOCIMIENTO",
                "Sensores de largo alcance y equilibrio energético",
                imgExploradora,
                new Color(56, 189, 248),
                60,
                80,
                0,
                this.lblEstadoExploradora);
        this.bExploradora.setActionCommand(IVistaCrearNave.SELECCIONAR_EXPLORADORA);

        this.lblEstadoCarguera = new JLabel();
        this.bCarguera = crearTarjetaNave(
                "Carguero",
                "LOGÍSTICA PESADA",
                "Máxima autonomía inicial de combustible en ruta",
                imgCarguera,
                new Color(251, 191, 36),
                100,
                60,
                0,
                this.lblEstadoCarguera);
        this.bCarguera.setActionCommand(IVistaCrearNave.SELECCIONAR_CARGUERA);

        this.lblEstadoCombate = new JLabel();
        this.bCombate = crearTarjetaNave(
                "Combate",
                "DEFENSA TÁCTICA",
                "Núcleo energético de máxima potencia táctica",
                imgCombate,
                new Color(192, 132, 252),
                80,
                100,
                0,
                this.lblEstadoCombate);
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

        JPanel badgeSeleccion = crearPanelTranslucido(
                new Color(15, 23, 42, 180),
                new Color(56, 189, 248, 95));
        badgeSeleccion.setLayout(new FlowLayout(FlowLayout.CENTER, 16, 6));
        badgeSeleccion.add(this.txtSeleccion);

        JPanel filaSeleccion = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        filaSeleccion.setOpaque(false);
        filaSeleccion.add(badgeSeleccion);

        this.bAceptar = crearBoton(
                "Confirmar y Crear Nave",
                new Color(16, 185, 129, 215),
                new Color(34, 197, 94, 245),
                new Color(74, 222, 128, 220),
                10);
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

    private JButton crearTarjetaNave(
            String nombre,
            String rol,
            String descripcion,
            BufferedImage imagen,
            Color colorAcento,
            int combustible,
            int energia,
            int desgaste,
            JLabel lblEstado) {
        JButton tarjeta = new JButton();
        tarjeta.setLayout(new BorderLayout(0, 10));
        tarjeta.setOpaque(true);
        tarjeta.setBackground(new Color(15, 23, 42, 175));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(56, 189, 248, 120), 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        tarjeta.setFocusPainted(false);
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 1. Fila superior: Badge de rol (izquierda) y estado (derecha)
        JPanel filaSuperior = new JPanel(new BorderLayout());
        filaSuperior.setOpaque(false);

        JLabel lblRol = new JLabel(rol);
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblRol.setForeground(colorAcento);
        lblRol.setOpaque(true);
        lblRol.setBackground(new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 35));
        lblRol.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 120), 1, true),
                new EmptyBorder(2, 8, 2, 8)));

        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblEstado.setText("DISPONIBLE");
        lblEstado.setForeground(new Color(148, 163, 184));
        lblEstado.setOpaque(true);
        lblEstado.setBackground(new Color(255, 255, 255, 15));
        lblEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(148, 163, 184, 60), 1, true),
                new EmptyBorder(2, 8, 2, 8)));

        filaSuperior.add(lblRol, BorderLayout.WEST);
        filaSuperior.add(lblEstado, BorderLayout.EAST);

        // 2. Títulos: nombre y descripción
        JPanel bloqueTexto = new JPanel(new GridLayout(2, 1, 0, 4));
        bloqueTexto.setOpaque(false);

        JLabel lblNombre = new JLabel(nombre.toUpperCase());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblNombre.setForeground(Color.WHITE);

        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(148, 163, 184));

        bloqueTexto.add(lblNombre);
        bloqueTexto.add(lblDesc);

        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setOpaque(false);
        panelNorte.add(filaSuperior);
        panelNorte.add(Box.createVerticalStrut(8));
        panelNorte.add(bloqueTexto);

        tarjeta.add(panelNorte, BorderLayout.NORTH);

        // 3. Vitrina central con la ilustración
        JPanel panelVitrina = new JPanel(new BorderLayout());
        panelVitrina.setOpaque(true);
        panelVitrina.setBackground(new Color(8, 15, 30, 95));
        panelVitrina.setBorder(BorderFactory.createLineBorder(
                new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 80), 1, true));

        if (imagen != null) {
            int maxW = 220;
            int maxH = 140;
            double factor = Math.min((double) maxW / imagen.getWidth(), (double) maxH / imagen.getHeight());
            int drawW = Math.max(1, (int) Math.round(imagen.getWidth() * factor));
            int drawH = Math.max(1, (int) Math.round(imagen.getHeight() * factor));
            Image escalada = imagen.getScaledInstance(drawW, drawH, Image.SCALE_SMOOTH);
            JLabel lblImagen = new JLabel(new ImageIcon(escalada), SwingConstants.CENTER);
            panelVitrina.add(lblImagen, BorderLayout.CENTER);
        }

        tarjeta.add(panelVitrina, BorderLayout.CENTER);

        // 4. Panel inferior de configuración inicial
        JPanel panelConfig = new JPanel(new GridLayout(4, 1, 0, 4));
        panelConfig.setOpaque(true);
        panelConfig.setBackground(new Color(255, 255, 255, 12));
        panelConfig.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 60), 1, true),
                new EmptyBorder(8, 12, 8, 12)));

        JPanel filaEncabezado = new JPanel(new BorderLayout());
        filaEncabezado.setOpaque(false);
        JLabel lblTituloConfig = new JLabel("CONFIGURACIÓN INICIAL");
        lblTituloConfig.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTituloConfig.setForeground(new Color(186, 230, 253));
        JLabel lblMotor = new JLabel("Warp: Disponible");
        lblMotor.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblMotor.setForeground(new Color(74, 222, 128));
        filaEncabezado.add(lblTituloConfig, BorderLayout.WEST);
        filaEncabezado.add(lblMotor, BorderLayout.EAST);

        panelConfig.add(filaEncabezado);
        panelConfig.add(crearFilaBarraRecurso("Combustible", combustible, new Color(56, 189, 248)));
        panelConfig.add(crearFilaBarraRecurso("Energía", energia, new Color(52, 211, 153)));
        panelConfig.add(crearFilaBarraRecurso("Desgaste", desgaste, new Color(251, 191, 36)));

        tarjeta.add(panelConfig, BorderLayout.SOUTH);

        // Efectos hover
        tarjeta.addMouseListener(new MouseListener() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (tipoSeleccionado == null || !tarjetaEsSeleccionada(nombre)) {
                    tarjeta.setBackground(new Color(20, 32, 56, 205));
                    lblEstado.setText("SELECCIONAR");
                    lblEstado.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boolean seleccionada = tarjetaEsSeleccionada(nombre);
                boolean atenuada = tipoSeleccionado != null && !seleccionada;
                actualizarEstadoTarjeta(tarjeta, lblEstado, colorAcento, seleccionada, atenuada);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });

        return tarjeta;
    }

    private JPanel crearFilaBarraRecurso(String etiqueta, int valor, Color colorBarra) {
        JPanel fila = new JPanel(new BorderLayout(6, 2));
        fila.setOpaque(false);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(new Color(226, 232, 240));

        JLabel val = new JLabel(valor + " / 100", SwingConstants.RIGHT);
        val.setFont(new Font("Segoe UI", Font.BOLD, 11));
        val.setForeground(colorBarra);

        JProgressBar barra = new JProgressBar(0, 100);
        barra.setValue(valor);
        barra.setPreferredSize(new Dimension(0, 6));
        barra.setForeground(colorBarra);
        barra.setBackground(new Color(255, 255, 255, 24));
        barra.setBorder(BorderFactory.createEmptyBorder());

        JPanel textos = new JPanel(new BorderLayout());
        textos.setOpaque(false);
        textos.add(lbl, BorderLayout.WEST);
        textos.add(val, BorderLayout.EAST);

        fila.add(textos, BorderLayout.NORTH);
        fila.add(barra, BorderLayout.SOUTH);
        return fila;
    }

    private boolean tarjetaEsSeleccionada(String nombre) {
        if (this.tipoSeleccionado == null) {
            return false;
        }
        return (tipoSeleccionado == TipoNave.EXPLORADORA && "Exploradora".equalsIgnoreCase(nombre))
                || (tipoSeleccionado == TipoNave.CARGUERO && "Carguero".equalsIgnoreCase(nombre))
                || (tipoSeleccionado == TipoNave.COMBATE && "Combate".equalsIgnoreCase(nombre));
    }

    private void actualizarEstadoTarjeta(
            JButton tarjeta,
            JLabel lblEstado,
            Color colorAcento,
            boolean seleccionada,
            boolean atenuada) {
        if (seleccionada) {
            tarjeta.setBackground(new Color(18, 30, 54, 220));
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                            new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 235), 2,
                            true),
                    new EmptyBorder(15, 15, 15, 15)));
            lblEstado.setText("● ACTIVA");
            lblEstado.setForeground(new Color(74, 222, 128));
            lblEstado.setBackground(new Color(16, 185, 129, 45));
            lblEstado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(74, 222, 128, 180), 1, true),
                    new EmptyBorder(2, 8, 2, 8)));
        } else if (atenuada) {
            tarjeta.setBackground(new Color(15, 23, 42, 140));
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 116, 139, 75), 1, true),
                    new EmptyBorder(16, 16, 16, 16)));
            lblEstado.setText("DISPONIBLE");
            lblEstado.setForeground(new Color(148, 163, 184));
            lblEstado.setBackground(new Color(255, 255, 255, 15));
            lblEstado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(148, 163, 184, 60), 1, true),
                    new EmptyBorder(2, 8, 2, 8)));
        } else {
            tarjeta.setBackground(new Color(15, 23, 42, 175));
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                            new Color(colorAcento.getRed(), colorAcento.getGreen(), colorAcento.getBlue(), 120), 1,
                            true),
                    new EmptyBorder(16, 16, 16, 16)));
            lblEstado.setText("DISPONIBLE");
            lblEstado.setForeground(new Color(148, 163, 184));
            lblEstado.setBackground(new Color(255, 255, 255, 15));
            lblEstado.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(148, 163, 184, 60), 1, true),
                    new EmptyBorder(2, 8, 2, 8)));
        }
        tarjeta.repaint();
    }

    private JPanel crearPanelTranslucido(Color colorFondo, Color colorBorde) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(colorFondo);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBorde, 1, true),
                new EmptyBorder(6, 16, 6, 16)));
        return panel;
    }

    private JButton crearBoton(String texto, Color normal, Color hover, Color borde, int paddingV) {
        JButton btn = new JButton(texto);
        btn.setOpaque(true);
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 1, true),
                BorderFactory.createEmptyBorder(paddingV, 20, paddingV, 20)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseListener() {
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

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });
        return btn;
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
                BufferedImage.TYPE_INT_ARGB);

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
                maxY - minY + 1);
    }

    @Override
    public TipoNave getTipoSeleccionado() {
        return this.tipoSeleccionado;
    }

    @Override
    public void setTipoSeleccionado(TipoNave tipo) {
        this.tipoSeleccionado = tipo;

        boolean haySeleccion = tipo != null;
        actualizarEstadoTarjeta(this.bExploradora, this.lblEstadoExploradora, new Color(56, 189, 248),
                tipo == TipoNave.EXPLORADORA, haySeleccion && tipo != TipoNave.EXPLORADORA);
        actualizarEstadoTarjeta(this.bCarguera, this.lblEstadoCarguera, new Color(251, 191, 36),
                tipo == TipoNave.CARGUERO, haySeleccion && tipo != TipoNave.CARGUERO);
        actualizarEstadoTarjeta(this.bCombate, this.lblEstadoCombate, new Color(192, 132, 252),
                tipo == TipoNave.COMBATE, haySeleccion && tipo != TipoNave.COMBATE);

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
                JOptionPane.ERROR_MESSAGE);
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
}
