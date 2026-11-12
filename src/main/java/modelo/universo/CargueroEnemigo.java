package modelo.universo;

import modelo.nave.Carguero;
import modelo.nave.Combate;
import modelo.nave.Exploradora;
import modelo.nave.Nave;
import modelo.nave.TipoNave;

public class CargueroEnemigo implements NaveEnemiga {

    private final String nombre;

    public CargueroEnemigo() {
        this("Carguero No Identificado");
    }

    public CargueroEnemigo(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getNombre() {
        return this.nombre;
    }

    @Override
    public String getSimbolo() {
        return "🚚";
    }

    @Override
    public TipoNave getTipoNave() {
        return TipoNave.CARGUERO;
    }

    @Override
    public ResultadoCombate enfrentarCon(Exploradora exploradora) {
        return ResultadoCombate.GANA;
    }

    @Override
    public ResultadoCombate enfrentarCon(Carguero carguero) {
        return ResultadoCombate.PACTO_DE_PAZ;
    }

    @Override
    public ResultadoCombate enfrentarCon(Combate combate) {
        return ResultadoCombate.PIERDE;
    }

    @Override
    public ResultadoInteraccion interactuar(Nave nave) {
        if (nave == null) {
            return new ResultadoInteraccion("Sin interaccion", false, false, 0, 0, 0);
        } else {
            ResultadoCombate resultado = nave.enfrentar(this);
            String encuentro = "Encuentro de naves: " + nave.getTipo().getDescripcion() + " vs " + this.nombre + ".\n";

            return switch (resultado) {
                case GANA ->
                    new ResultadoInteraccion(
                            encuentro + "La nave enemiga fue destruida. Hay que caranchearla!",
                            false,
                            false,
                            0,
                            0,
                            0);

                case PIERDE ->
                    new ResultadoInteraccion(
                            encuentro + "La nave fue destruida. Todos han MUERTO!",
                            true,
                            true,
                            0,
                            0,
                            0);

                case PACTO_DE_PAZ ->
                    new ResultadoInteraccion(
                            encuentro + "Se establecio un pacto de paz.",
                            false,
                            false,
                            0,
                            0,
                            0);
            };
        }
    }
}
