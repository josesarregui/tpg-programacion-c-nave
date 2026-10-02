/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.nave;

/**
 *
 * @author Sebastian
 * 
 * Invariante de clase:
 *   - 0 <= combustible <= CAPACIDADMAXCOMBUSTIBLE
 *   - 0 <= energia <= CAPACIDADMAXENERGIA
 *   - 0 <= desgaste <= MAXDESGASTE
 */
public class Recursos {
    private static final int CAPACIDADMAXCOMBUSTIBLE = 100;
    private static final int CAPACIDADMAXENERGIA = 100;
    private static final int MAXDESGASTE = 100;
    private static final int UMBRALMANTENIMIENTO = 80;
    
    private int combustible;
    private int energia;
    private int desgaste;

    public Recursos(int combustibleInicial, int energiaInicial, int desgasteInicial) {
        assert combustibleInicial >= 0 && combustibleInicial <= CAPACIDADMAXCOMBUSTIBLE : "Combustible inicial fuera de rango.";
        assert energiaInicial >= 0 && energiaInicial <= CAPACIDADMAXENERGIA : "Energia inicial fuera de rango.";
        assert desgasteInicial >= 0 && desgasteInicial <= MAXDESGASTE : "Desgaste inicial fuera de rango.";

        this.combustible = combustibleInicial;
        this.energia = energiaInicial;
        this.desgaste = desgasteInicial;

        assert invariante();
    }

    /**
     * Invariante de clase: verifica que todos los recursos estén dentro de sus rangos válidos.
     * @return true si el estado interno es consistente
     */
    private boolean invariante() {
        return combustible >= 0 && combustible <= CAPACIDADMAXCOMBUSTIBLE
                && energia >= 0 && energia <= CAPACIDADMAXENERGIA
                && desgaste >= 0 && desgaste <= MAXDESGASTE;
    }

    
    
    public int getCombustible() { 
        return combustible; 
    }
    public void cargarCombustible(int cantidad) {
        assert cantidad >= 0 : "La cantidad a cargar no puede ser negativa.";
        assert this.combustible + cantidad <= CAPACIDADMAXCOMBUSTIBLE : "Se supera la capacidad maxima de combustible.";

        this.combustible += cantidad;
        assert invariante();
    }
    public void consumirCombustible(int cantidad) {
        assert cantidad >= 0 : "La cantidad a consumir no puede ser negativa.";
        assert this.combustible - cantidad >= 0 : "Combustible insuficiente.";

        this.combustible -= cantidad;
        assert invariante();
    }

    
    
    public int getEnergia() { 
        return energia; 
    }
    public void cargarEnergia(int cantidad) {
        assert cantidad >= 0 : "La cantidad a cargar no puede ser negativa.";
        assert this.energia + cantidad <= CAPACIDADMAXENERGIA : "Se supera la capacidad maxima de energia.";

        this.energia += cantidad;
        assert invariante();
    }
    public void consumirEnergia(int cantidad) {
        assert cantidad >= 0 : "La cantidad a consumir no puede ser negativa.";
        assert this.energia - cantidad >= 0 : "Energia insuficiente.";

        this.energia -= cantidad;

        assert invariante();
    }
    
    

    public int getDesgaste() { 
        return desgaste; 
    }
    public void aumentarDesgaste(int cantidad) {
        assert cantidad >= 0 : "La cantidad de desgaste no puede ser negativa.";
        assert this.desgaste + cantidad <= MAXDESGASTE : "El desgaste superaria el maximo permitido.";

        this.desgaste += cantidad;

        assert invariante();
    }
    public void realizarMantenimiento() {
        this.desgaste = 0;

        assert invariante();
    }
    public boolean requiereMantenimiento() {
        return this.desgaste >= UMBRALMANTENIMIENTO;
    }

    @Override
    public String toString() {
        return "Recursos{" 
                + "combustible=" + combustible + ", "
                + "energia=" + energia + ", "
                + "desgaste=" + desgaste + '}';
    }

    
    
    
}
