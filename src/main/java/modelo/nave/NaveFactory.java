package modelo.nave;

/**
 * Patron Factory para la creacion de naves espacial.
 * 
 */
public class NaveFactory {

    /**
     * Crea una nave del tipo indicado con los recursos por defecto.
     *
     * @pre tipoDeNave != null.
     * @pre tipoDeNave debe ser "exploradora", "carguero" o "combate" (sin distinguir mayusculas).
     * @post Retorna una nave distinta de null del tipo indicado, con recursos por defecto, motor inicializado y sin tripulacion.
     * @param tipoDeNave tipo de nave a crear.
     * @return la nave creada.
     */
    public Nave crearNave(String tipoDeNave) {
        assert tipoDeNave != null : "El tipo de nave no puede ser nulo.";
        
        String tipo = tipoDeNave.toLowerCase();
        assert tipo.equals("exploradora") || tipo.equals("carguero") || tipo.equals("combate") : "Tipo de nave desconocido: " + tipoDeNave;

        Nave nave;
        switch (tipo) {
            case "exploradora":
                nave = new Exploradora(new Recursos(60, 80, 0));
                break;
            case "carguero":
                nave = new Carguero(new Recursos(100, 60, 0));
                break;
            case "combate":
                nave = new Combate(new Recursos(80, 100, 0));
                break;
            default:
                nave = null;
        }

        assert nave != null : "Fallo postcondicion: la nave creada no puede ser nula.";
        return nave;
    }
}
