package modelo.mision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Informe que genera una misión al cerrarse (E1-10): misión ejecutada, resultado, acciones principales,
 * recursos consumidos, estado final de la nave y observaciones relevantes.
 *
 * Es inmutable: todos sus atributos son final, no tiene setters y la lista de acciones se devuelve
 * de sólo lectura. Sólo lo crea {@link Mision} (constructor de paquete), de modo que nadie puede
 * fabricar ni alterar el resultado de una misión desde afuera.
 * Devuelve datos y no texto formateado, para que pueda mostrarse por consola o en Swing sin cambiar el modelo.
 *
 * Invariante:
 *  - mision no nula ni vacía; acciones y observaciones no nulas;
 *  - los recursos consumidos y los valores finales no son negativos;
 *  - estadoMotorFinal no nulo.
 */
public class InformeMision {

    private final String mision;
    private final boolean exitosa;
    private final List<String> acciones;
    private final int combustibleConsumido;
    private final int energiaConsumida;
    private final int desgasteProducido;
    private final int combustibleFinal;
    private final int energiaFinal;
    private final int desgasteFinal;
    private final String estadoMotorFinal;
    private final boolean naveOperativa;
    private final String observaciones;

    /**
     * @pre Los datos los calcula la propia misión al cerrarse, por eso un dato inválido sólo puede
     *      deberse a un error de programación y se verifica con aserciones.
     * @post El informe queda con los datos recibidos y no puede modificarse.
     */
    InformeMision(String mision, boolean exitosa, List<String> acciones,
                  int combustibleConsumido, int energiaConsumida, int desgasteProducido,
                  int combustibleFinal, int energiaFinal, int desgasteFinal,
                  String estadoMotorFinal, boolean naveOperativa, String observaciones) {
        assert acciones != null : "La lista de acciones no puede ser nula.";
        this.mision = mision;
        this.exitosa = exitosa;
        // Se copia la lista: si la misión la modificara después, el informe no cambiaría.
        this.acciones = new ArrayList<>(acciones);
        this.combustibleConsumido = combustibleConsumido;
        this.energiaConsumida = energiaConsumida;
        this.desgasteProducido = desgasteProducido;
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
     * @return true si se cumplió la condición de éxito de la misión (Ficha de Inicio, punto 4).
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

    public int getCombustibleConsumido() {
        return combustibleConsumido;
    }

    public int getEnergiaConsumida() {
        return energiaConsumida;
    }

    public int getDesgasteProducido() {
        return desgasteProducido;
    }

    public int getCombustibleFinal() {
        return combustibleFinal;
    }

    public int getEnergiaFinal() {
        return energiaFinal;
    }

    public int getDesgasteFinal() {
        return desgasteFinal;
    }

    public String getEstadoMotorFinal() {
        return estadoMotorFinal;
    }

    /**
     * @return true si, al cerrar la misión, la nave quedó lista para operar.
     */
    public boolean isNaveOperativa() {
        return naveOperativa;
    }

    public String getObservaciones() {
        return observaciones;
    }

    private boolean invariante() {
        return mision != null && !mision.isBlank()
                && acciones != null && observaciones != null && estadoMotorFinal != null
                && combustibleConsumido >= 0 && energiaConsumida >= 0 && desgasteProducido >= 0
                && combustibleFinal >= 0 && energiaFinal >= 0 && desgasteFinal >= 0;
    }
}
