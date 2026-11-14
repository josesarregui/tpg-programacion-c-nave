package modelo.universo;

import modelo.nave.Carguero;
import modelo.nave.Combate;
import modelo.nave.Exploradora;
import modelo.asistente.Asistente;
import modelo.bitacora.TipoEvento;
import modelo.nave.TipoNave;

public class ExploradoraEnemiga implements NaveEnemiga {

    private final String nombre;

    public ExploradoraEnemiga() {
        this("Nave Exploradora Hostil");
    }

    public ExploradoraEnemiga(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getNombre() {
        return this.nombre;
    }

    @Override
    public String getSimbolo() {
        return "🛸";
    }

    @Override
    public TipoNave getTipoNave() {
        return TipoNave.EXPLORADORA;
    }

    @Override
    public ResultadoCombate enfrentarCon(Exploradora exploradora) {
        return ResultadoCombate.PACTO_DE_PAZ;
    }

    @Override
    public ResultadoCombate enfrentarCon(Carguero carguero) {
        return ResultadoCombate.PIERDE;
    }

    @Override
    public ResultadoCombate enfrentarCon(Combate combate) {
        return ResultadoCombate.GANA;
    }

    /**
     * Adaptación de firma: la resolución completa del encuentro (destrucción) corresponde al Paso 2.
     *
     * @pre asistente != null.
     * @post El resultado del enfrentamiento quedó registrado en la Bitácora.
     */
    @Override
    public void interactuarCon(Asistente asistente) {
        assert asistente != null : "El asistente no puede ser nulo.";
        ResultadoCombate resultado = asistente.getNave().enfrentar(this);
        asistente.registrarEvento(TipoEvento.RELEVANTE, "Encuentro con " + nombre + ": "
                + resultado.getDescripcion() + ".");
    }
}
