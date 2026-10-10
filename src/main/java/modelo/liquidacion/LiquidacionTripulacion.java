package modelo.liquidacion;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado de la liquidación mensual de toda la tripulación de una nave: un recibo por tripulante.
 * Sólo lo crea {@link LiquidadorHaberes} (constructor de paquete): nadie puede fabricar una liquidación desde afuera.
 *
 * Invariante: período y recibos no nulos, sin recibos nulos y todos del período liquidado.
 */
public class LiquidacionTripulacion {

    private final YearMonth periodo;
    private final List<ReciboHaberes> recibos;

    /**
     * @pre periodo != null y recibos != null.
     * @post La liquidación tiene una copia de los recibos recibidos.
     */
    LiquidacionTripulacion(YearMonth periodo, List<ReciboHaberes> recibos) {
        assert periodo != null : "El período liquidado no puede ser nulo.";
        assert recibos != null : "La lista de recibos no puede ser nula.";
        this.periodo = periodo;
        this.recibos = new ArrayList<>(recibos);
        assert invariante() : "Fallo invariante: la liquidación de la tripulación quedó inconsistente.";
    }

    public YearMonth getPeriodo() {
        return periodo;
    }

    /**
     * @return lista de solo lectura, en el orden de la tripulación.
     */
    public List<ReciboHaberes> getRecibos() {
        return Collections.unmodifiableList(recibos);
    }

    /**
     * @post El valor retornado es igual a la suma de los totales de los recibos.
     */
    public double calcularTotal() {
        double total = 0;
        for (ReciboHaberes recibo : recibos) {
            total += recibo.getTotal();
        }
        return total;
    }

    private boolean invariante() {
        boolean recibosValidos = periodo != null && recibos != null;
        // Invariante de ciclo: los recibos ya recorridos no son nulos y corresponden al período liquidado.
        for (int i = 0; recibosValidos && i < recibos.size(); i++) {
            recibosValidos = recibos.get(i) != null && periodo.equals(recibos.get(i).getPeriodo());
        }
        return recibosValidos;
    }
}
