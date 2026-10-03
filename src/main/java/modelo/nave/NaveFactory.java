package modelo.nave;

/**
 * Fábrica de naves (patrón Factory, E1-07).
 *
 * El cliente pide el tipo que necesita y recibe una referencia a la abstracción común {@link Nave},
 * sin conocer ni instanciar las clases concretas (sus constructores son de paquete).
 * Cada tipo conoce su propia configuración inicial; la fábrica sólo decide qué clase crear.
 * Agregar un tipo nuevo requiere una subclase, una constante en {@link TipoNave} y un caso aquí.
 */
public class NaveFactory {

    /**
     * Crea una nave del tipo indicado, en un estado válido: recursos iniciales de la Ficha de Inicio,
     * Motor Warp en Disponible y sin tripulación asignada.
     *
     * @pre tipo != null.
     * @post Retorna una nave distinta de null con getTipo() == tipo.
     * @param tipo tipo de nave a crear.
     * @return la nave creada.
     */
    public Nave crearNave(TipoNave tipo) {
        assert tipo != null : "El tipo de nave no puede ser nulo.";

        Nave nave = null;
        switch (tipo) {
            case EXPLORADORA:
                nave = new Exploradora();
                break;
            case CARGUERO:
                nave = new Carguero();
                break;
            case COMBATE:
                nave = new Combate();
                break;
        }

        assert nave != null && nave.getTipo() == tipo : "Fallo postcondición: no se creó una nave del tipo " + tipo + ".";
        return nave;
    }
}
