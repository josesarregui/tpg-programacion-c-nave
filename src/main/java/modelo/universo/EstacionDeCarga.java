package modelo.universo;

import modelo.asistente.Asistente;
import modelo.bitacora.TipoEvento;

/**
 * Punto de asistencia espacial (Enunciado E2, punto C): recarga un monto fijo de combustible y energía, sin
 * superar la capacidad máxima de cada recurso (invariante de Recursos).
 *
 * Invariante: nombre != null y no vacío.
 */
public class EstacionDeCarga implements ElementoDelUniverso {

    public static final int RECARGA_COMBUSTIBLE = 30;
    public static final int RECARGA_ENERGIA = 30;
    public static final String SIMBOLO = "E";

    private final String nombre;

    public EstacionDeCarga() {
        this("Estación de carga");
    }

    /**
     * @pre nombre != null y no vacío.
     * @post getNombre() == nombre.
     */
    public EstacionDeCarga(String nombre) {
        assert nombre != null && !nombre.isBlank() : "El nombre de la estación no puede ser nulo ni vacío.";
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
     * Registra el acople en la Bitácora y reabastece la nave del asistente.
     *
     * @pre asistente != null.
     * @post combustible == min(anterior + RECARGA_COMBUSTIBLE, 100) y energía == min(anterior + RECARGA_ENERGIA, 100);
     *       el desgaste no cambia.
     */
    @Override
    public void interactuarCon(Asistente asistente) {
        assert asistente != null : "El asistente no puede ser nulo.";
        int combustibleAnterior = asistente.getCombustible();
        int energiaAnterior = asistente.getEnergia();
        asistente.registrarEvento(TipoEvento.RELEVANTE, "Acople con " + nombre + ".");
        asistente.reabastecer(RECARGA_COMBUSTIBLE, RECARGA_ENERGIA);
        assert asistente.getCombustible() >= combustibleAnterior && asistente.getEnergia() >= energiaAnterior
                : "Fallo postcondición: el reabastecimiento no puede disminuir los recursos.";
    }

    @Override
    public String toString() {
        return nombre;
    }
}
