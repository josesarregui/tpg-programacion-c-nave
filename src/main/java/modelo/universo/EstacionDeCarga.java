package modelo.universo;

import modelo.nave.Nave;

/**
 * Punto de asistencia espacial que permite reabastecer a la nave,
 * recargando combustible y energia.
 */
public class EstacionDeCarga implements ElementoDelUniverso {

    public static final int RECARGA_COMBUSTIBLE = 30;
    public static final int RECARGA_ENERGIA = 30;

    private final String nombre;

    public EstacionDeCarga() {
        this("Estacion de carga");
    }

    public EstacionDeCarga(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getNombre() {
        return this.nombre;
    }

    @Override
    public String getSimbolo() {
        return "🛰️";
    }

    @Override
    public ResultadoInteraccion interactuar(Nave nave) {
        if (nave == null) {
            return new ResultadoInteraccion("Sin interaccion", false, false, 0, 0, 0);
        } else {
            int combustiblePrevio = nave.getCombustible();
            int energiaPrevia = nave.getEnergia();

            try {
                nave.cargarCombustible(RECARGA_COMBUSTIBLE);
            } catch (Exception ex) {
                // Si supera capacidad maxima, se mantendrá en el maximo admisible
            }

            try {
                nave.cargarEnergia(RECARGA_ENERGIA);
            } catch (Exception ex) {
                // Si supera capacidad maxima
            }

            int combustibleGanado = nave.getCombustible() - combustiblePrevio;
            int energiaGanada = nave.getEnergia() - energiaPrevia;

            String mensaje = "Acople con " + this.nombre + ": + " + combustibleGanado + " Combustible, + "
                    + energiaGanada + " Energía.";

            return new ResultadoInteraccion(mensaje, false, false, combustibleGanado, energiaGanada, 0);
        }
    }
}
