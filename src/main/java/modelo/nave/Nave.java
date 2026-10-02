package modelo.nave;

import excepcion.TripulanteInexistenteException;
import modelo.tripulacion.Tripulacion;
import modelo.tripulacion.Tripulante;
import modelo.warp.MotorWarp;

/**
 * Clase abstracta que representa una nave espacial.
 *
 * Invariante de clase:
 *   - id > 0
 *   - recursos != null
 *   - motor != null
 *
 */
public abstract class Nave {
    private static int idAuto = 0;
    private int id;
    private Recursos recursos;
    private Tripulacion tripulacion;
    private MotorWarp motor;


    /**
     * Crea una nave con recursos. La tripulacion queda sin asignar (null) y el motor se inicializa.
     *
     * @pre recursos != null.
     * @post Se crea la nave con un id unico auto-generado.
     * @post La tripulacion queda sin asignar (null).
     * @post El motor queda inicializado con un nuevo MotorWarp.
     * @param recursos recursos de la nave.
     * @throws IllegalArgumentException si los recursos son null.
     */
    public Nave(Recursos recursos) {
        if (recursos == null) {
            throw new IllegalArgumentException("Los recursos no pueden ser nulos.");
        }

        this.id = siguienteId();
        this.recursos = recursos;
        this.tripulacion = null;
        this.motor = new MotorWarp();

        assert invariante() : "Fallo invariante: la nave quedo en un estado inconsistente tras su creacion.";
    }


    /**
     * Invariante de clase: verifica la consistencia interna de la nave.
     *
     * @return true si el estado interno es consistente.
     */
    private boolean invariante() {
        return id > 0 && recursos != null && motor != null;
    }


    /**
     * Genera el siguiente id de Nave disponible.
     *
     * @post El id retornado es mayor que cualquier id generado previamente.
     * @post idAuto queda incrementado en 1.
     * @return siguiente id disponible.
     */
    private static int siguienteId(){
        idAuto += 1;
        return idAuto;
    }


    /**
     * Consulta el id correspondiente a la Nave.
     *
     * @post El valor retornado es mayor que 0.
     * @return id de la Nave.
     */
    public int getId() {
        return id;
    }


    /**
     * Consulta los recursos de la Nave.
     *
     * @post El valor retornado es distinto de null.
     * @return recursos de la Nave.
     */
    public Recursos getRecursos() {
        return recursos;
    }


    /**
     * Consulta la tripulacion de la Nave.
     *
     * @post Puede retornar null si la tripulacion no fue asignada.
     * @return tripulacion de la Nave, o null si no fue asignada.
     */
    public Tripulacion getTripulacion() {
        return this.tripulacion;
    }


    /**
     * Asigna o reemplaza la tripulacion de la nave.
     *
     * @pre nuevaTripulacion != null.
     * @post this.tripulacion == nuevaTripulacion.
     * @param nuevaTripulacion nueva tripulacion de la nave.
     * @throws IllegalArgumentException si nuevaTripulacion es null.
     */
    public void setTripulacion(Tripulacion nuevaTripulacion) {
        if (nuevaTripulacion == null) {
            throw new IllegalArgumentException("La nueva tripulacion no puede ser nula.");
        }

        this.tripulacion = nuevaTripulacion;
        assert invariante() : "Fallo invariante tras cambiar la tripulacion.";
    }




    /**
     * Busca un tripulante por su id en la tripulacion de la nave.
     *
     * @pre idTripulante > 0.
     * @pre La nave debe tener una tripulacion asignada.
     * @post El tripulante retornado tiene el id buscado.
     *
     * @param idTripulante id del tripulante a buscar.
     * @return el tripulante con el id indicado.
     * @throws IllegalArgumentException si idTripulante <= 0.
     * @throws IllegalStateException si la nave no tiene una tripulacion asignada.
     * @throws TripulanteInexistenteException si ningun tripulante tiene ese id.
     */
    public Tripulante buscarTripulantePorId(int idTripulante) throws TripulanteInexistenteException {
        if (idTripulante <= 0) {
            throw new IllegalArgumentException("El id del tripulante debe ser mayor a 0.");
        }

        if (this.tripulacion == null) {
            throw new IllegalStateException("La nave no tiene una tripulacion asignada.");
        }

        Tripulante tripulante = this.tripulacion.buscarPorId(idTripulante);

        assert tripulante.getId() == idTripulante : "Fallo postcondicion: el id retornado no coincide.";
        return tripulante;
    }


    /**
     * Consulta el motor warp de la Nave.
     *
     * @post El valor retornado es distinto de null.
     * @return motor warp de la Nave.
     */
    public MotorWarp getMotor() {
        return this.motor;
    }


    /**
     * Verifica si la nave esta lista para operar (tiene tripulacion asignada y motor inicializado).
     *
     * @post Retorna true si getTripulacion() != null && getMotor() != null, false en caso contrario.
     * @return true si la nave esta lista para operar.
     */
    public boolean estaListaParaOperar() {
        return this.tripulacion != null && this.motor != null;
    }


    /**
     * Retorna el tipo de nave.
     *
     * @post El valor retornado es distinto de null y no esta en blanco.
     * @return tipo de nave.
     */
    public abstract String getTipo();


    /**
     * Representacion en texto de la nave.
     *
     * @post El valor retornado es distinto de null.
     * @return representacion en texto de la nave.
     */
    @Override
    public String toString() {
        return "Nave{" 
                + "id=" + this.id + ", "
                + "recursos=" + this.recursos + ", "
                + "tripulacion=" + this.tripulacion + ", "
                + "motor=" + this.motor + '}';
    }
}
