package vista.usuario;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Pantalla de inicio de sesión. Sólo muestra datos y envía los gestos del usuario al controlador: no conoce el
 * servicio ni la base de datos.
 *
 * Validación de formato: el botón "Ingresar" permanece deshabilitado mientras falte el usuario o la contraseña
 * ({@link OyenteCamposLogin}).
 */
public class VistaLogin extends VentanaAcceso implements IVistaLogin {

    private final JButton botonIngresar;
    private final JButton botonIrARegistro;
    private final OyenteCamposLogin oyenteCampos;

    public VistaLogin() {
        super("Acceso al sistema - Inicio de sesión");

        botonIngresar = new JButton("Ingresar");
        botonIngresar.setActionCommand(IVistaLogin.INGRESAR);
        botonIngresar.setEnabled(false);
        botonIrARegistro = new JButton("Registrarse");
        botonIrARegistro.setActionCommand(IVistaLogin.IR_A_REGISTRO);

        oyenteCampos = new OyenteCamposLogin(this);
        getCampoUsuario().addKeyListener(oyenteCampos);
        getCampoContrasena().addKeyListener(oyenteCampos);

        armarVentana();
    }

    @Override
    protected JPanel crearPanelCampos() {
        JPanel panelCampos = new JPanel(new GridLayout(2, 1, 0, 15));
        panelCampos.setOpaque(false);
        panelCampos.add(crearCampo("Usuario:", getCampoUsuario()));
        panelCampos.add(crearCampo("Contraseña:", getCampoContrasena()));
        return panelCampos;
    }

    @Override
    protected JPanel crearPanelBotones() {
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.add(botonIngresar);
        panelBotones.add(botonIrARegistro);
        return panelBotones;
    }

    @Override
    public void setControlador(ActionListener controlador) {
        botonIngresar.addActionListener(controlador);
        botonIrARegistro.addActionListener(controlador);
    }

    @Override
    public void limpiarCampos() {
        super.limpiarCampos();
        oyenteCampos.verificarCampos();
    }

    @Override
    public void limpiarContrasena() {
        getCampoContrasena().setText("");
        oyenteCampos.verificarCampos();
    }

    JButton getBotonIngresar() {
        return botonIngresar;
    }
}
