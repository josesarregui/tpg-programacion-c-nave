package modelo.bitacora;

/**
 * Clasificación de los acontecimientos registrados en la Bitácora (E1-05): la Bitácora debe registrar
 * eventos relevantes, errores, cambios del Motor Warp, ejecución de misiones y operaciones sobre recursos.
 */
public enum TipoEvento {
    MOTOR,      // Cambios de estado del Motor Warp.
    MISION,     // Ciclo, acciones, resultado e informe de las misiones.
    RECURSO,    // Cargas, consumos, desgaste y mantenimiento.
    ERROR,      // Órdenes y transiciones rechazadas, con su motivo.
    SISTEMA,    // Acontecimientos generales: puesta en servicio, registro y selección de la nave, tripulación.
    RELEVANTE   // Otros eventos relevantes, como órdenes o autorizaciones del/de la capitán/a.
}
