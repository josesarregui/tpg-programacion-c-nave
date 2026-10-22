/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista.principal.PanelCrearNave;

import controlador.crearNave.ControladorCrearNave;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import modelo.nave.TipoNave;

/**
 *
 * @author Damia
 */
public class VistaCrearNave extends JPanel implements IVistaCrearNave{
    private JButton bExploradora;
    private JButton bCarguera;
    private JButton bCombate;
    private JButton bAceptar;
    
    private Border bordeNormal;
    private Border bordeSeleccionado;

    private JLabel txtSeleccion;
    private TipoNave tipoSeleccionado;

    
    
    public VistaCrearNave() {
        this.setLayout(new BorderLayout(20, 20));
        this.setBackground(new Color(10, 15, 30));
        
        // TITULO
        JLabel titulo = new JLabel("Selecciona tu nave", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 26));

        this.add(titulo, BorderLayout.NORTH);

        // BOTONES DE NAVES
        this.bExploradora = crearBotonNave("/imagenes/naves/exploradora.png","Exploradora");
        this.bCarguera = crearBotonNave("/imagenes/naves/carguera.png","Carguera");
        this.bCombate = crearBotonNave("/imagenes/naves/combate.png","Combate");
        
        this.bordeNormal = BorderFactory.createLineBorder(Color.GRAY, 4);
        this.bordeSeleccionado = BorderFactory.createLineBorder(Color.YELLOW, 4);
        
        this.bExploradora.setBorder(this.bordeNormal);
        this.bCarguera.setBorder(this.bordeNormal);
        this.bCombate.setBorder(this.bordeNormal);

        this.bExploradora.setActionCommand(IVistaCrearNave.SELECCIONAR_EXPLORADORA);
        this.bCarguera.setActionCommand(IVistaCrearNave.SELECCIONAR_CARGUERA);
        this.bCombate.setActionCommand(IVistaCrearNave.SELECCIONAR_COMBATE);

        JPanel panelNaves = new JPanel(new GridLayout(1, 3, 20, 0));
        panelNaves.setOpaque(false);

        panelNaves.add(this.bExploradora);
        panelNaves.add(this.bCarguera);
        panelNaves.add(this.bCombate);

        this.add(panelNaves, BorderLayout.CENTER);

        // PARTE INFERIOR
        this.txtSeleccion = new JLabel("Ninguna nave seleccionada",SwingConstants.CENTER);

        this.txtSeleccion.setForeground(Color.WHITE);
        this.txtSeleccion.setFont(new Font("Arial", Font.BOLD, 16));

        this.bAceptar = new JButton("Aceptar");
        this.bAceptar.setActionCommand(IVistaCrearNave.ACEPTAR);

        JPanel panelInferior = new JPanel(new BorderLayout(0, 15));
        panelInferior.setOpaque(false);

        panelInferior.add(txtSeleccion, BorderLayout.NORTH);
        panelInferior.add(bAceptar, BorderLayout.SOUTH);

        this.add(panelInferior, BorderLayout.SOUTH);
    }
    
    
    

    
    private JButton crearBotonNave(String rutaImagen, String nombre) {
        URL ruta = getClass().getResource(rutaImagen);
        ImageIcon imagenOriginal = new ImageIcon(ruta);

        Image imagenEscalada = imagenOriginal.getImage().getScaledInstance(260,200,Image.SCALE_SMOOTH);
        ImageIcon imagen = new ImageIcon(imagenEscalada);

        JButton boton = new JButton(nombre, imagen);

        boton.setVerticalTextPosition(JButton.BOTTOM);
        boton.setHorizontalTextPosition(JButton.CENTER);

        boton.setPreferredSize(new Dimension(300, 260));

        boton.setForeground(Color.WHITE);
        boton.setBackground(new Color(20, 25, 45));

        boton.setFocusPainted(false);
        return boton;
    }

    
    private void marcarSeleccionado(JButton boton) {
        boton.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4));
    }
    
    
    @Override
    public TipoNave getTipoSeleccionado() {
        return tipoSeleccionado;
    }

    
    @Override
    public void setTipoSeleccionado(TipoNave tipo) {
        this.tipoSeleccionado = tipo;

        this.bExploradora.setBorder(this.bordeNormal);
        this.bCarguera.setBorder(this.bordeNormal);
        this.bCombate.setBorder(this.bordeNormal);

        if (tipo == TipoNave.EXPLORADORA) {
            this.bExploradora.setBorder(this.bordeSeleccionado);
            this.txtSeleccion.setText("Nave seleccionada: Exploradora");

        } else if (tipo == TipoNave.CARGUERO) {
            this.bCarguera.setBorder(this.bordeSeleccionado);
            this.txtSeleccion.setText("Nave seleccionada: Carguera");

        } else if (tipo == TipoNave.COMBATE) {
            this.bCombate.setBorder(this.bordeSeleccionado);
            this.txtSeleccion.setText("Nave seleccionada: Combate");
        }

        this.revalidate();
        this.repaint();
    }
    
    

    @Override
    public void setControlador(ControladorCrearNave controlador) {
        this.bExploradora.addActionListener(controlador);
        this.bCarguera.addActionListener(controlador);
        this.bCombate.addActionListener(controlador);
        this.bAceptar.addActionListener(controlador);
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this,mensaje,"Error",JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    @Override
    public void bloquearSeleccion() {
        this.bExploradora.setEnabled(false);
        this.bCarguera.setEnabled(false);
        this.bCombate.setEnabled(false);
        this.bAceptar.setEnabled(false);

        String nombre = "";
        if (tipoSeleccionado == TipoNave.EXPLORADORA) {
            nombre = "Exploradora";
        } else if (tipoSeleccionado == TipoNave.CARGUERO) {
            nombre = "Carguera";
        } else if (tipoSeleccionado == TipoNave.COMBATE) {
            nombre = "Combate";
        }
        this.txtSeleccion.setText("Nave creada: " + nombre);
        this.revalidate();
        this.repaint();
    }
}