package app;

import controlador.usuario.ControladorLogin;
import excepcion.PersistenciaException;
import modelo.universo.CentroDeControl;
import modelo.usuario.ServicioUsuarios;
import persistencia.ConexionBD;
import persistencia.UsuarioDAOJDBC;
import vista.usuario.IVistaLogin;
import vista.usuario.VistaLogin;

/**
 * Programa principal de la Entrega 2: arma las piezas del acceso al sistema y muestra la pantalla de login,
 * como el ProgramaDeConversion del apunte "MVC - interfaz de usuario" (el modelo, la vista, el control,
 * se configura la vista y arranca).
 *
 * Es el punto de inicio que conecta el DAO, los servicios del modelo (usuarios y centro de control),
 * la vista inicial y su controlador.
 *
 * La demostración por consola de la Entrega 1 está en {@link DemostracionEntrega1}.
 */
public class App {

    public static void main(String[] args) {
        // la persistencia y el modelo:
        UsuarioDAOJDBC usuarioDAO = new UsuarioDAOJDBC(new ConexionBD());
        ServicioUsuarios modelo = new ServicioUsuarios(usuarioDAO);
        CentroDeControl centroDeControl = new CentroDeControl();
        // la vista:
        IVistaLogin vista = new VistaLogin();
        try {
            usuarioDAO.crearTablaSiNoExiste();
            // el control, y se configura la vista:
            new ControladorLogin(vista, modelo, centroDeControl);
            // arranca la interfaz:
            vista.arrancar();
        } catch (PersistenciaException e) {
            vista.mostrarError(e.getMessage() + " El sistema no puede iniciarse.");
            vista.cerrar();
        }
    }
}
