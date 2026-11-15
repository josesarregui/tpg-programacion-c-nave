package modelo.mision;

import modelo.universo.Posicion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Informe final de una misión (E1-10; Enunciado E2, Fase 3; Guía, puntos 7 y 8): misión ejecutada, objetivo,
 * resultado (exitosa o fallida), acciones principales, ruta recorrida, balance de recursos, estado de la nave
 * al evaluar y observaciones.
 *
 * Lo confecciona la misión en {@code evaluar()}, como pide el Enunciado; el cierre aplica después a la nave lo
 * ganado o entregado que figura en el balance (gemas y suministros) y, en M-03, la recarga y la reparación en la
 * base. Por eso el estado de la nave que muestra el informe es el del final de la travesía, antes del cierre; el
 * asistente registra en su Bitácora el estado posterior.
 *
 * Es inmutable: todos sus atributos son final, no tiene setters y las listas se devuelven de sólo lectura.
 * Sólo lo crea {@link Mision} (constructor de paquete), de modo que nadie puede fabricar ni alterar el resultado
 * de una misión desde afuera. Devuelve datos y no texto formateado, para que pueda mostrarse por consola o en
 * Swing sin cambiar el modelo.
 *
 * Invariante:
 *  - mision no nula ni vacía; objetivo, acciones, ruta, balance, estadoMotorFinal y observaciones no nulos;
 *  - la ruta no está vacía (empieza en la posición de partida) y los valores finales no son negativos.
 */
public class InformeMision {

    private final String mision;
    private final boolean exitosa;
    private final List<String> acciones;
    private final Posicion objetivo;
    private final List<Posicion> ruta;
    private final BalanceRecursos balance;
    private final int combustibleFinal;
    private final int energiaFinal;
    private final int desgasteFinal;
    private final String estadoMotorFinal;
    private final boolean naveOperativa;
    private final String observaciones;

    /**
     * @pre Los datos los calcula la propia misión al evaluarse, por eso un dato inválido sólo puede deberse a un
     *      error de programación y se verifica con aserciones.
     * @post El informe queda con los datos recibidos y no puede modificarse.
     */
    InformeMision(String mision, boolean exitosa, List<String> acciones, Posicion objetivo, List<Posicion> ruta,
                  BalanceRecursos balance, int combustibleFinal, int energiaFinal, int desgasteFinal,
                  String estadoMotorFinal, boolean naveOperativa, String observaciones) {
        assert acciones != null && ruta != null : "Las acciones y la ruta no pueden ser nulas.";
        this.mision = mision;
        this.exitosa = exitosa;
        // Se copian las listas: si la misión las modificara después, el informe no cambiaría.
        this.acciones = new ArrayList<>(acciones);
        this.objetivo = objetivo;
        this.ruta = new ArrayList<>(ruta);
        this.balance = balance;
        this.combustibleFinal = combustibleFinal;
        this.energiaFinal = energiaFinal;
        this.desgasteFinal = desgasteFinal;
        this.estadoMotorFinal = estadoMotorFinal;
        this.naveOperativa = naveOperativa;
        this.observaciones = observaciones;
        assert invariante() : "Fallo invariante: el informe de la misión quedó incompleto.";
    }

    public String getMision() {
        return mision;
    }

    /**
     * @return true si la misión resultó exitosa: llegó al objetivo, la nave no fue destruida y superó la
     *         verificación en destino.
     */
    public boolean isExitosa() {
        return exitosa;
    }

    /**
     * @return acciones principales, en el orden en que se realizaron (lista de sólo lectura).
     */
    public List<String> getAcciones() {
        return Collections.unmodifiableList(acciones);
    }

    /**
     * @return coordenada objetivo de la misión.
     */
    public Posicion getObjetivo() {
        return objetivo;
    }

    /**
     * @return posiciones que ocupó la nave, desde la de partida, en orden (lista de sólo lectura).
     */
    public List<Posicion> getRuta() {
        return Collections.unmodifiableList(ruta);
    }

    /**
     * @return balance de recursos ganados, consumidos, perdidos y entregados.
     */
    public BalanceRecursos getBalance() {
        return balance;
    }

    public int getCombustibleConsumido() {
        return balance.getCombustibleConsumido();
    }

    public int getEnergiaConsumida() {
        return balance.getEnergiaConsumida();
    }

    public int getDesgasteProducido() {
        return balance.getDesgasteProducido();
    }

    /**
     * @return combustible de la nave al evaluar la misión (antes del cierre).
     */
    public int getCombustibleFinal() {
        return combustibleFinal;
    }

    /**
     * @return energía de la nave al evaluar la misión (antes del cierre).
     */
    public int getEnergiaFinal() {
        return energiaFinal;
    }

    /**
     * @return desgaste de la nave al evaluar la misión (antes del cierre).
     */
    public int getDesgasteFinal() {
        return desgasteFinal;
    }

    public String getEstadoMotorFinal() {
        return estadoMotorFinal;
    }

    /**
     * @return true si, al evaluar la misión, la nave quedó lista para operar.
     */
    public boolean isNaveOperativa() {
        return naveOperativa;
    }

    public String getObservaciones() {
        return observaciones;
    }

    private boolean invariante() {
        return mision != null && !mision.isBlank()
                && objetivo != null && acciones != null && ruta != null && !ruta.isEmpty() && balance != null
                && observaciones != null && estadoMotorFinal != null
                && combustibleFinal >= 0 && energiaFinal >= 0 && desgasteFinal >= 0;
    }
}
