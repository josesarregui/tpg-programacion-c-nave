package modelo.tripulacion;

/**
 * Orígenes planetarios admitidos para la tripulación (E1-04) con su subsidio mensual (E1-08).
 * Cada origen conoce su propio subsidio, por lo que el decorador que lo liquida no necesita
 * un switch y agregar un origen nuevo no obliga a modificarlo.
 */
public enum Origen {
    TERRICOLA("Terrícola", 20.0),
    VULCANO("Vulcano", 30.0),
    MARCIANO("Marciano", 18.0);

    private final String nombre;
    private final double subsidioMensual;  // En PG.

    /**
     * @pre subsidioMensual >= 0.
     */
    Origen(String nombre, double subsidioMensual) {
        assert subsidioMensual >= 0 : "El subsidio por origen no puede ser negativo.";
        this.nombre = nombre;
        this.subsidioMensual = subsidioMensual;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * @post El valor retornado es mayor o igual a 0.
     * @return subsidio mensual en PG correspondiente al origen.
     */
    public double getSubsidioMensual() {
        return subsidioMensual;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
