package app;

import controlador.usuario.ControladorLogin;
import excepcion.PersistenciaException;
import javax.swing.SwingUtilities;
import modelo.universo.CentroDeControl;
import modelo.usuario.ServicioUsuarios;
import persistencia.ConexionBD;
import persistencia.UsuarioDAOJDBC;
import vista.FabricaDeVistas;
import vista.FabricaDeVistasSwing;
import vista.usuario.IVistaLogin;

/**
 * Programa principal de la Entrega 2: arma las piezas del acceso al sistema y muestra la pantalla de login,
 * como el ProgramaDeConversion del apunte "MVC - interfaz de usuario" (el modelo, la vista, el control,
 * se configura la vista y arranca).
 *
 * Es el punto de inicio que conecta el DAO, los servicios del modelo (usuarios y centro de control),
 * la fábrica de ventanas Swing, la vista inicial y su controlador. La interfaz se arma en el hilo de eventos de
 * Swing (EDT), como exige Swing para crear y modificar componentes.
 *
 * La demostración por consola de la Entrega 1 está en {@link DemostracionEntrega1}.
 */
public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::iniciar);
    }

    private static void iniciar() {
        // la persistencia y el modelo:
        UsuarioDAOJDBC usuarioDAO = new UsuarioDAOJDBC(new ConexionBD());
        ServicioUsuarios modelo = new ServicioUsuarios(usuarioDAO);
        CentroDeControl centroDeControl = new CentroDeControl();
        // la vista:
        FabricaDeVistas fabricaDeVistas = new FabricaDeVistasSwing();
        IVistaLogin vista = fabricaDeVistas.crearVistaLogin();
        try {
            usuarioDAO.crearTablaSiNoExiste();
            // el control, y se configura la vista:
            new ControladorLogin(vista, modelo, centroDeControl, fabricaDeVistas);
            // arranca la interfaz:
            vista.arrancar();
        } catch (PersistenciaException e) {
            vista.mostrarError(e.getMessage() + " El sistema no puede iniciarse.");
            vista.cerrar();
        }
    }
}
