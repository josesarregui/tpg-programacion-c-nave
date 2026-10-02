package modelo.nave;

/**
 * Nave de tipo Carguero.
 *
 */
public class Carguero extends Nave {
    
    /**
     * Crea un Carguero con los recursos indicados.
     *
     * @pre recursos != null.
     * @post Se crea el carguero con un id unico, recursos asignados, motor inicializado y sin tripulacion.
     * @param recursos recursos de la nave.
     */
    public Carguero(Recursos recursos) {
        super(recursos);
    }

    /**
     * Retorna el tipo de nave.
     *
     * @post El valor retornado es "Carguero".
     * @return tipo de nave.
     */
    @Override
    public String getTipo() {
        return "Carguero";
    }

    /**
     * Representacion en texto del carguero.
     *
     * @post El valor retornado es distinto de null.
     * @return representacion en texto del carguero.
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
