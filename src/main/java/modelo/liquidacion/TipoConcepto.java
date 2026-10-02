package modelo.liquidacion;

/**
 * Conceptos remunerativos que componen el haber mensual (E1-08).
 * Permiten identificar el aporte de cada concepto y detectar si un decorador se aplicó dos veces.
 */
public enum TipoConcepto {
    CARGO,       // Remuneración correspondiente al cargo (la aporta el tripulante).
    CONSEJOS,    // 2 PG por consejo del período (la aporta el consejero).
    ANTIGUEDAD,  // Adicional por años de antigüedad (lo agrega AntiguedadDecorator).
    ORIGEN       // Subsidio mensual por planeta de origen (lo agrega OrigenDecorator).
}
