package vista.usuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * Pantalla de registro de un usuario nuevo. Sólo muestra datos y envía los gestos del usuario al controlador:
 * no conoce el servicio ni la base de datos.
 *
 * Validación de formato: el botón "Registrar" se habilita cuando están completos los tres campos y las dos
 * contraseñas coinciden; si no coinciden, se avisa debajo de los campos ({@link OyenteCamposRegistro}).
 */
public class VistaRegistro extends VentanaAcceso implements IVistaRegistro {

    private static final Color COLOR_AVISO = new Color(255, 120, 120);

    private final JPasswordField campoRepetirContrasena;
    private final JLabel etiquetaAviso;
    private final JButton botonRegistrar;
    private final JButton botonVolver;
    private final OyenteCamposRegistro oyenteCampos;

    public VistaRegistro() {
        super("Acceso al sistema - Registro de usuario");

        campoRepetirContrasena = new JPasswordField();
        campoRepetirContrasena.setPreferredSize(TAMANIO_CAMPO);
        etiquetaAviso = new JLabel(" ");
        etiquetaAviso.setForeground(COLOR_AVISO);

        botonRegistrar = new JButton("Registrar");
        botonRegistrar.setActionCommand(IVistaRegistro.REGISTRAR);
        botonRegistrar.setEnabled(false);
        botonVolver = new JButton("Volver");
        botonVolver.setActionCommand(IVistaRegistro.VOLVER);

        oyenteCampos = new OyenteCamposRegistro(this);
        getCampoUsuario().addKeyListener(oyenteCampos);
        getCampoContrasena().addKeyListener(oyenteCampos);
        campoRepetirContrasena.addKeyListener(oyenteCampos);

        armarVentana();
    }

    @Override
    protected JPanel crearPanelCampos() {
        JPanel panelGrilla = new JPanel(new GridLayout(3, 1, 0, 15));
        panelGrilla.setOpaque(false);
        panelGrilla.add(crearCampo("Usuario:", getCampoUsuario()));
        panelGrilla.add(crearCampo("Contraseña:", getCampoContrasena()));
        panelGrilla.add(crearCampo("Repetir contraseña:", campoRepetirContrasena));

        JPanel panelCampos = new JPanel(new BorderLayout(0, 5));
        panelCampos.setOpaque(false);
        panelCampos.add(panelGrilla, BorderLayout.CENTER);
        panelCampos.add(etiquetaAviso, BorderLayout.SOUTH);
        return panelCampos;
    }

    @Override
    protected JPanel crearPanelBotones() {
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.add(botonRegistrar);
        panelBotones.add(botonVolver);
        return panelBotones;
    }

    @Override
    public void setControlador(ActionListener controlador) {
        botonRegistrar.addActionListener(controlador);
        botonVolver.addActionListener(controlador);
    }

    @Override
    public void limpiarCampos() {
        super.limpiarCampos();
        campoRepetirContrasena.setText("");
        oyenteCampos.verificarCampos();
    }

    JPasswordField getCampoRepetirContrasena() {
        return campoRepetirContrasena;
    }

    JLabel getEtiquetaAviso() {
        return etiquetaAviso;
    }

    JButton getBotonRegistrar() {
        return botonRegistrar;
    }
}
