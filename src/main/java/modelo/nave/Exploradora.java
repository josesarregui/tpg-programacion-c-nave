package modelo.nave;

/**
 * Nave de tipo Exploradora.
 *
 */
public class Exploradora extends Nave {

    /**
     * Crea una nave Exploradora con los recursos indicados.
     *
     * @pre recursos != null.
     * @post Se crea la nave exploradora con un id unico, recursos asignados, motor inicializado y sin tripulacion.
     * @param recursos recursos de la nave.
     */
    public Exploradora(Recursos recursos) {
        super(recursos);
    }
    
    /**
     * Retorna el tipo de nave.
     *
     * @post El valor retornado es "Exploradora".
     * @return tipo de nave.
     */
    @Override
    public String getTipo() {
        return "Exploradora";
    }
    
    /**
     * Representacion en texto de la nave exploradora.
     *
     * @post El valor retornado es distinto de null.
     * @return representacion en texto de la nave exploradora.
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
