package modelo.tripulacion;

/**
 * Cargos admitidos para la tripulación (E1-04).
 * Identifica el cargo de cada tripulante; los importes del haber los define cada subclase de Tripulante.
 */
public enum Cargo {
    CAPITAN("Capitán"),
    CONSEJERO("Consejero"),
    TENIENTE("Teniente"),
    ALFEREZ("Alférez");

    private final String nombre;

    Cargo(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
