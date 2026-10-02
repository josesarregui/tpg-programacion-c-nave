package modelo.nave;

/**
 * Representa los recursos de una nave espacial.
 *
 * Invariante de clase:
 *   - 0 <= combustible <= CAPACIDADMAXCOMBUSTIBLE
 *   - 0 <= energia <= CAPACIDADMAXENERGIA
 *   - 0 <= desgaste <= MAXDESGASTE
 *
 */
public class Recursos {
    private static final int CAPACIDADMAXCOMBUSTIBLE = 100;
    private static final int CAPACIDADMAXENERGIA = 100;
    private static final int MAXDESGASTE = 100;
    private static final int UMBRALMANTENIMIENTO = 80;
    
    private int combustible;
    private int energia;
    private int desgaste;

    /**
     * Crea una instancia de Recursos con valores iniciales.
     *
     * @pre combustibleInicial >= 0 y combustibleInicial <= CAPACIDADMAXCOMBUSTIBLE.
     * @pre energiaInicial >= 0 y energiaInicial <= CAPACIDADMAXENERGIA.
     * @pre desgasteInicial >= 0 y desgasteInicial <= MAXDESGASTE.
     * @post Se crean los recursos con los valores indicados.
     * @param combustibleInicial Cantidad inicial de combustible.
     * @param energiaInicial Cantidad inicial de energia.
     * @param desgasteInicial Cantidad inicial de desgaste.
     * @throws IllegalArgumentException si alguno de los valores iniciales esta fuera de su rango permitido.
     */
    public Recursos(int combustibleInicial, int energiaInicial, int desgasteInicial) {
        if (combustibleInicial < 0 || combustibleInicial > CAPACIDADMAXCOMBUSTIBLE) { 
            throw new IllegalArgumentException("Combustible inicial fuera de rango.");
        }

        if (energiaInicial < 0 || energiaInicial > CAPACIDADMAXENERGIA) {
            throw new IllegalArgumentException("Energia inicial fuera de rango.");
        }

        if (desgasteInicial < 0 || desgasteInicial > MAXDESGASTE) {
            throw new IllegalArgumentException("Desgaste inicial fuera de rango.");
        }

        this.combustible = combustibleInicial;
        this.energia = energiaInicial;
        this.desgaste = desgasteInicial;

        assert invariante() : "Fallo invariante tras crear Recursos.";
    }



    /**
     * Invariante de clase: verifica que todos los recursos esten dentro de sus rangos validos.
     *
     * @return true si el estado interno es consistente.
     */
    private boolean invariante() {
        return combustible >= 0 && combustible <= CAPACIDADMAXCOMBUSTIBLE
                && energia >= 0 && energia <= CAPACIDADMAXENERGIA
                && desgaste >= 0 && desgaste <= MAXDESGASTE;
    }

    
    
    /**
     * Consulta la cantidad de combustible actual.
     *
     * @post El valor retornado esta entre 0 y CAPACIDADMAXCOMBUSTIBLE.
     * @return cantidad de combustible.
     */
    public int getCombustible() { 
        return combustible; 
    }

    /**
     * Carga combustible a la nave.
     *
     * @pre cantidad >= 0.
     * @pre combustible + cantidad <= CAPACIDADMAXCOMBUSTIBLE.
     * @post El combustible aumenta en la cantidad indicada
     * @param cantidad cantidad de combustible a cargar.
     * @throws IllegalArgumentException si cantidad es negativa.
     * @throws IllegalStateException si la carga supera la capacidad maxima.
     */
    public void cargarCombustible(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad a cargar no puede ser negativa.");
        }

        if (this.combustible + cantidad > CAPACIDADMAXCOMBUSTIBLE) {
            throw new IllegalStateException("La carga supera la capacidad maxima de combustible.");
        }

        int combustibleAnterior = this.combustible;
        this.combustible += cantidad;

        assert this.combustible == combustibleAnterior + cantidad : "Fallo postcondicion al cargar combustible.";
        assert invariante() : "Fallo invariante tras cargar combustible.";
    }

    /**
     * Consume combustible de la nave.
     *
     * @pre cantidad >= 0.
     * @pre combustible - cantidad >= 0.
     * @post El combustible disminuye en la cantidad indicada.
     * @param cantidad Cantidad de combustible a consumir.
     */
    public void consumirCombustible(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad a consumir no puede ser negativa.");
        }

        if (cantidad > this.combustible) {
            throw new IllegalStateException("Combustible insuficiente.");
        }

        int combustibleAnterior = this.combustible;
        this.combustible -= cantidad;

        assert this.combustible == combustibleAnterior - cantidad : "Fallo postcondicion al consumir combustible.";
        assert invariante() : "Fallo invariante tras consumir combustible.";
    }

    
    
    /**
     * Consulta la cantidad de energia actual.
     *
     * @post El valor retornado esta entre 0 y CAPACIDADMAXENERGIA.
     * @return cantidad de energia.
     */
    public int getEnergia() { 
        return energia; 
    }

    /**
     * Carga energia a la nave.
     *
     * @pre cantidad >= 0.
     * @pre energia + cantidad <= CAPACIDADMAXENERGIA.
     * @post La energia aumenta en la cantidad indicada.
     * @param cantidad Cantidad de energia a cargar.
     */
    public void cargarEnergia(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad a cargar no puede ser negativa.");
        }

        if (this.energia + cantidad > CAPACIDADMAXENERGIA) {
            throw new IllegalStateException("La carga supera la capacidad maxima de energia.");
        }

        int energiaAnterior = this.energia;
        this.energia += cantidad;

        assert this.energia == energiaAnterior + cantidad : "Fallo postcondicion al cargar energia.";
        assert invariante() : "Fallo invariante tras cargar energia.";
    }

    /**
     * Consume energia de la nave.
     *
     * @pre cantidad >= 0.
     * @pre energia - cantidad >= 0.
     * @post La energia disminuye en la cantidad indicada.
     * @param cantidad Cantidad de energia a consumir.
     */
    public void consumirEnergia(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad a consumir no puede ser negativa.");
        }

        if (cantidad > this.energia) {
            throw new IllegalStateException("Energia insuficiente.");
        }

        int energiaAnterior = this.energia;
        this.energia -= cantidad;

        assert this.energia == energiaAnterior - cantidad : "Fallo postcondicion al consumir energia.";
        assert invariante() : "Fallo invariante tras consumir energia.";
    }
    
    

    /**
     * Consulta la cantidad de desgaste actual.
     *
     * @post El valor retornado esta entre 0 y MAXDESGASTE.
     * @return cantidad de desgaste.
     */
    public int getDesgaste() { 
        return desgaste; 
    }

    /**
     * Aumenta el desgaste de la nave.
     *
     * @pre cantidad >= 0.
     * @pre desgaste + cantidad <= MAXDESGASTE.
     * @post El desgaste aumenta en la cantidad indicada.
     * @param cantidad Cantidad de desgaste a agregar.
     */
    public void aumentarDesgaste(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad de desgaste no puede ser negativa.");
        }

        if (this.desgaste + cantidad > MAXDESGASTE) {
            throw new IllegalStateException("El desgaste superaria el maximo permitido.");
        }

        int desgasteAnterior = this.desgaste;
        this.desgaste += cantidad;

        assert this.desgaste == desgasteAnterior + cantidad : "Fallo postcondicion al aumentar desgaste.";
        assert invariante() : "Fallo invariante tras aumentar desgaste.";
    }



    /**
     * Realiza mantenimiento sobre la nave, reiniciando el desgaste a 0.
     *
     * @pre desgaste >= UMBRALMANTENIMIENTO.
     * @post desgaste == 0.
     * @throws IllegalStateException si la nave no requiere mantenimiento.
     */
    public void realizarMantenimiento() {
        if (!requiereMantenimiento()) {
            throw new IllegalStateException("La nave no requiere mantenimiento.");
        }

        this.desgaste = 0;
        assert invariante() : "Fallo invariante tras realizar mantenimiento.";
    }

    
    /**
     * Consulta si la nave requiere mantenimiento.
     *
     * @post Retorna true si desgaste >= UMBRALMANTENIMIENTO, false en caso contrario.
     * @return true si requiere mantenimiento.
     */
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
