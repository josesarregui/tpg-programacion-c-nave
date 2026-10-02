package modelo.nave;

/**
 * Nave de tipo Combate.
 *
 */
public class Combate extends Nave {
    
    /**
     * Crea una nave de Combate con los recursos indicados.
     *
     * @pre recursos != null.
     * @post Se crea la nave de combate con un id unico, recursos asignados, motor inicializado y sin tripulacion.
     * @param recursos recursos de la nave.
     */
    public Combate(Recursos recursos) {
        super(recursos);
    }

    /**
     * Retorna el tipo de nave.
     *
     * @post El valor retornado es "Combate".
     * @return tipo de nave.
     */
    @Override
    public String getTipo() {
        return "Combate";
    }

    /**
     * Representacion en texto de la nave de combate.
     *
     * @post El valor retornado es distinto de null.
     * @return representacion en texto de la nave de combate.
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
