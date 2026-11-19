package vista;

import vista.principal.IVistaPrincipal;
import vista.principal.VistaPrincipal;
import vista.usuario.IVistaLogin;
import vista.usuario.IVistaRegistro;
import vista.usuario.VistaLogin;
import vista.usuario.VistaRegistro;

/**
 * Fábrica de las ventanas Swing de la aplicación. Sólo la crea {@code app.App}: los controladores la reciben por
 * su interfaz {@link FabricaDeVistas}.
 *
 * Debe usarse desde el hilo de eventos de Swing, porque crea componentes gráficos.
 */
public class FabricaDeVistasSwing implements FabricaDeVistas {

    @Override
    public IVistaLogin crearVistaLogin() {
        return new VistaLogin();
    }

    @Override
    public IVistaRegistro crearVistaRegistro() {
        return new VistaRegistro();
    }

    @Override
    public IVistaPrincipal crearVistaPrincipal() {
        return new VistaPrincipal();
    }
}
