package modelo.tripulacion;

import excepcion.TripulacionInvalidaException;
import modelo.liquidacion.Liquidacion;

/**
 * Componente Concreto del Patrón Decorator.
 * Representa un miembro genérico de la tripulación con atributos de identidad, origen y antigüedad.
 */
public abstract class Tripulante implements Liquidacion {

    private static int idAuto = 1;

    protected int id;
    protected String nombre;
    protected int antiguedad;
    protected Origen origen;

    /**
     * Gestiona la secuencia de identidades autogeneradas por el sistema.
     * @return Siguiente ID numérico único.
     */
    public static int siguienteId() {
        return idAuto++;
    }

    /**
     * Constructor base de tod tripulante. Valida precondiciones estrictas (Fail-Fast).
     */
    public Tripulante(String nombre, int antiguedad, Origen origen) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new TripulacionInvalidaException("El nombre del tripulante no puede ser nulo ni vacío.");
        }
        if (antiguedad < 0) {
            throw new TripulacionInvalidaException("La antigüedad del tripulante no puede ser negativa.");
        }
        if (origen == null) {
            throw new TripulacionInvalidaException("El origen planetario no puede ser nulo.");
        }

        this.id = siguienteId();
        this.nombre = nombre;
        this.antiguedad = antiguedad;
        this.origen = origen;

        assert invariante() : "Fallo invariante: El estado del tripulante es inconsistente tras su creación."; //[cite: 4]
    }

    private boolean invariante() {
        return this.id > 0 && this.nombre != null && !this.nombre.trim().isEmpty()
                && this.antiguedad >= 0 && this.origen != null;
    }

    public int getId() { return this.id; }

    @Override public int getAntiguedad() { return this.antiguedad; }
    @Override public Origen getOrigen() { return this.origen; }

}