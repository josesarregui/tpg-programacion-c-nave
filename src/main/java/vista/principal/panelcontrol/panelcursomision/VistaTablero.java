package vista.principal.panelcontrol.panelcursomision;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import modelo.nave.TipoNave;
import modelo.universo.Asteroide;
import modelo.universo.ElementoDelUniverso;
import modelo.universo.EstacionDeCarga;
import modelo.universo.Posicion;
import modelo.universo.Tablero;
import vista.principal.panelcontrol.EstiloControl;

/**
 * Tablero de 20 × 20 en tiempo real (Enunciado E2, Vistas, punto 4; Plan, Paso 6).
 *
 * Observa el {@link Tablero} (patrón Observer, Versión 2 de la cátedra). En {@link #actualizar(String)} consulta,
 * en el hilo que notifica (el hilo de la misión), la matriz de {@link Tablero#verTablero()}, la posición de la nave
 * y el objetivo (modelo pull), y arma una instantánea con el símbolo de cada elemento, obtenido por polimorfismo con
 * {@link ElementoDelUniverso#getSimbolo()}, sin condicionales de tipo. La instantánea sólo contiene textos y
 * posiciones inmutables; se aplica y se repinta en el hilo de eventos de Swing con invokeLater. También muestra el
 * mensaje recibido (modelo push).
 *
 * El estado que se dibuja ({@code instantanea}) sólo se lee y se escribe en el hilo de eventos de Swing.
 */
public class VistaTablero extends JPanel implements IVistaTablero {

    private static final Color COLOR_CELDA = new Color(15, 23, 42, 200);
    private static final Color COLOR_LINEA = new Color(56, 189, 248, 50);
    private static final Color COLOR_ELEMENTO = new Color(226, 232, 240);
    private static final Color COLOR_NAVE = new Color(56, 189, 248);
    private static final Color COLOR_OBJETIVO = new Color(251, 191, 36);

    /**
     * Tablero observado. Se asigna antes de lanzar el hilo de la misión ({@code Thread.start()} hace visible la
     * asignación a ese hilo) y se anula cuando el hilo ya terminó ({@code join}).
     */
    private Tablero tablero;

    /** Último estado del tablero que se dibuja. Confinado al hilo de eventos de Swing. */
    private Instantanea instantanea;

    private final Grilla grilla;
    private final JLabel lblMensaje;

    public VistaTablero() {
        this.setOpaque(false);
        this.setLayout(new BorderLayout(0, 6));
        this.instantanea = Instantanea.VACIA;

        this.grilla = new Grilla();
        this.add(this.grilla, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout(0, 2));
        pie.setOpaque(false);
        pie.setBorder(new EmptyBorder(2, 4, 0, 4));
        this.lblMensaje = EstiloControl.crearEtiqueta("Tablero en espera de la misión.", Font.PLAIN, 12,
                new Color(186, 230, 253));
        pie.add(this.lblMensaje, BorderLayout.NORTH);
        pie.add(EstiloControl.crearEtiqueta(leyenda(), Font.PLAIN, 11, EstiloControl.GRIS), BorderLayout.SOUTH);
        this.add(pie, BorderLayout.SOUTH);
    }

    @Override
    public void observar(Tablero tablero) {
        assert tablero != null : "La vista debe observar un tablero.";
        dejarDeObservar();
        this.tablero = tablero;
        this.tablero.agregarObservador(this);
        final Instantanea inicial = Instantanea.de(tablero, "Tablero de " + tablero.getX() + " x " + tablero.getY()
                + " listo.");
        SwingUtilities.invokeLater(() -> aplicar(inicial));
    }

    @Override
    public void dejarDeObservar() {
        Tablero observado = this.tablero;
        if (observado != null) {
            observado.quitarObservador(this);
            this.tablero = null;
        }
    }

    /**
     * Recibe la notificación del tablero (push) y consulta su estado (pull) en el hilo que notifica: el tablero sólo
     * cambia en ese hilo, así que la consulta no compite con él. El repintado se hace en el hilo de eventos.
     *
     * @pre La vista observa un tablero (sólo ese tablero la notifica).
     */
    @Override
    public void actualizar(String mensaje) {
        Tablero observado = this.tablero;
        assert observado != null : "La vista recibió una notificación sin observar un tablero.";
        final Instantanea nueva = Instantanea.de(observado, mensaje);
        SwingUtilities.invokeLater(() -> aplicar(nueva));
    }

    private void aplicar(Instantanea nueva) {
        this.instantanea = nueva;
        this.lblMensaje.setText(nueva.mensaje);
        this.grilla.repaint();
    }

    private static String leyenda() {
        StringBuilder texto = new StringBuilder("■ Nave   ○ Objetivo   ");
        texto.append(Asteroide.SIMBOLO).append(" Asteroide   ").append(EstacionDeCarga.SIMBOLO).append(" Estación");
        for (TipoNave tipo : TipoNave.values()) {
            texto.append("   ").append(tipo.getSimbolo()).append(' ').append(tipo.getDescripcion());
        }
        return texto.toString();
    }

    // Consultas para las pruebas del paquete (se invocan en el hilo de eventos)

    String getSimboloEn(int fila, int columna) {
        return this.instantanea.simbolos[fila][columna];
    }

    int getFilas() {
        return this.instantanea.simbolos.length;
    }

    Posicion getPosicionNaveMostrada() {
        return this.instantanea.nave;
    }

    Posicion getObjetivoMostrado() {
        return this.instantanea.objetivo;
    }

    String getMensajeMostrado() {
        return this.lblMensaje.getText();
    }

    /**
     * Estado inmutable del tablero en un instante: símbolos por casilla ("" si está libre), posición de la nave y
     * objetivo (null si todavía no se fijó).
     */
    private static final class Instantanea {
        static final Instantanea VACIA = new Instantanea(new String[0][0], null, null, "");

        final String[][] simbolos;
        final Posicion nave;
        final Posicion objetivo;
        final String mensaje;

        private Instantanea(String[][] simbolos, Posicion nave, Posicion objetivo, String mensaje) {
            this.simbolos = simbolos;
            this.nave = nave;
            this.objetivo = objetivo;
            this.mensaje = mensaje;
        }

        /**
         * @pre Se invoca en el hilo que modifica el tablero (o sin ningún hilo modificándolo).
         */
        static Instantanea de(Tablero tablero, String mensaje) {
            ElementoDelUniverso[][] matriz = tablero.verTablero();
            String[][] simbolos = new String[matriz.length][];
            for (int fila = 0; fila < matriz.length; fila++) {
                simbolos[fila] = new String[matriz[fila].length];
                for (int columna = 0; columna < matriz[fila].length; columna++) {
                    ElementoDelUniverso elemento = matriz[fila][columna];
                    simbolos[fila][columna] = elemento != null ? elemento.getSimbolo() : "";
                }
            }
            return new Instantanea(simbolos, tablero.getPosicionNave(), tablero.getObjetivo(), mensaje);
        }
    }

    /**
     * Componente que dibuja la grilla a partir de la última instantánea (filas hacia abajo, columnas hacia la
     * derecha, como {@code matriz[fila][columna]}).
     */
    private final class Grilla extends JComponent {

        Grilla() {
            setPreferredSize(new Dimension(420, 420));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            String[][] simbolos = instantanea.simbolos;
            int filas = simbolos.length;
            int columnas = filas > 0 ? simbolos[0].length : 0;
            if (filas == 0 || columnas == 0) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int lado = Math.max(1, Math.min(getWidth() / columnas, getHeight() / filas));
            int x0 = (getWidth() - lado * columnas) / 2;
            int y0 = (getHeight() - lado * filas) / 2;

            g2.setColor(COLOR_CELDA);
            g2.fillRect(x0, y0, lado * columnas, lado * filas);
            g2.setColor(COLOR_LINEA);
            for (int i = 0; i <= filas; i++) {
                g2.drawLine(x0, y0 + i * lado, x0 + columnas * lado, y0 + i * lado);
            }
            for (int j = 0; j <= columnas; j++) {
                g2.drawLine(x0 + j * lado, y0, x0 + j * lado, y0 + filas * lado);
            }

            g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(8, lado / 2)));
            FontMetrics metricas = g2.getFontMetrics();
            g2.setColor(COLOR_ELEMENTO);
            for (int fila = 0; fila < filas; fila++) {
                for (int columna = 0; columna < columnas; columna++) {
                    String simbolo = simbolos[fila][columna];
                    int ancho = metricas.stringWidth(simbolo);
                    g2.drawString(simbolo, x0 + columna * lado + (lado - ancho) / 2,
                            y0 + fila * lado + (lado + metricas.getAscent() - metricas.getDescent()) / 2);
                }
            }

            Posicion objetivo = instantanea.objetivo;
            if (objetivo != null) {
                g2.setColor(COLOR_OBJETIVO);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x0 + objetivo.getY() * lado + 2, y0 + objetivo.getX() * lado + 2, lado - 4, lado - 4);
            }
            Posicion nave = instantanea.nave;
            if (nave != null) {
                g2.setColor(COLOR_NAVE);
                g2.fillRect(x0 + nave.getY() * lado + lado / 4, y0 + nave.getX() * lado + lado / 4, lado / 2,
                        lado / 2);
            }
            g2.dispose();
        }
    }
}
