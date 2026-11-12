package modelo.universo;

import modelo.nave.Nave;

/**
 * Obstáculo espacial que impacta contra el casco de la nave,
 * descontando energía e incrementando desgaste estructural.
 */
public class Asteroide implements ElementoDelUniverso {

    public static final int ENERGIA_DESCONTADA = 25;
    public static final int DESGASTE_SUMADO = 10;

    private final String nombre;

    public Asteroide() {
        this("Asteroide");
    }

    public Asteroide(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getNombre() {
        return this.nombre;
    }

    @Override
    public String getSimbolo() {
        return "☄️";
    }

    @Override
    public ResultadoInteraccion interactuar(Nave nave) {
        if (nave == null) {
            return new ResultadoInteraccion("Sin interaccion", false, false, 0, 0, 0);
        } else {
            int nuevaEnergia = nave.getEnergia() - ENERGIA_DESCONTADA;
            int nuevoDesgaste = nave.getDesgaste() + DESGASTE_SUMADO;

            try {
                nave.consumirRecursos(0, Math.min(nave.getEnergia(), ENERGIA_DESCONTADA), DESGASTE_SUMADO);
            } catch (Exception ex) {
                // Manejado segun el estado resultante
            }

            boolean naveDestruida = nuevaEnergia <= 0 || nuevoDesgaste >= 100;
            String mensaje;
            if (naveDestruida) {
                mensaje = " ¡La nave ha sufrido daños catastroficos y la tripulacion ha MUERTO!";
            } else {
                mensaje = "¡Impacto con Asteroide! Energia perdida " + ENERGIA_DESCONTADA + " y desgaste producido "
                        + DESGASTE_SUMADO + ".";
            }

            return new ResultadoInteraccion(mensaje, naveDestruida, naveDestruida, 0, -ENERGIA_DESCONTADA,
                    DESGASTE_SUMADO);
            /// mensaje del evento, nuestra nave fue destruida, mision terminada , 0,
            /// energia perdida, desgaste producido
        }
    }
}
