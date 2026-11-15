package modelo.universo;

import modelo.asistente.Asistente;
import modelo.bitacora.TipoEvento;

/**
 * Obstáculo espacial (Enunciado E2, punto B): al colisionar descuenta un monto fijo de energía. Si la energía
 * llega a 0, la nave queda inoperativa y se destruye (Enunciado E2, Fase 2).
 *
 * Invariante: nombre != null y no vacío.
 */
public class Asteroide implements ElementoDelUniverso {

    public static final int DANIO_ENERGIA = 25;
    public static final String SIMBOLO = "A";

    private final String nombre;

    public Asteroide() {
        this("Asteroide");
    }

    /**
     * @pre nombre != null y no vacío.
     * @post getNombre() == nombre.
     */
    public Asteroide(String nombre) {
        assert nombre != null && !nombre.isBlank() : "El nombre del asteroide no puede ser nulo ni vacío.";
        this.nombre = nombre;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String getSimbolo() {
        return SIMBOLO;
    }

    /**
     * Registra el impacto en la Bitácora y descuenta DANIO_ENERGIA de energía a la nave del asistente.
     *
     * @pre asistente != null.
     * @post asistente.getEnergia() == max(energía anterior - DANIO_ENERGIA, 0); combustible y desgaste no cambian.
     */
    @Override
    public void interactuarCon(Asistente asistente) {
        assert asistente != null : "El asistente no puede ser nulo.";
        int energiaAnterior = asistente.getEnergia();
        asistente.registrarEvento(TipoEvento.RELEVANTE, "Impacto con " + nombre + ".");
        asistente.recibirImpacto(DANIO_ENERGIA);
        assert asistente.getEnergia() == Math.max(energiaAnterior - DANIO_ENERGIA, 0)
                : "Fallo postcondición: el impacto debe descontar DANIO_ENERGIA de energía.";
    }

    @Override
    public String toString() {
        return nombre;
    }
}
