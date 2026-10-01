package modelo.bitacora;

/**
 * Clasificación de los acontecimientos registrados en la bitácora
 * conforme a los requerimientos de auditoría de la nave.
 */

/*
* La Bitácora deberá registrar eventos relevantes, errores, cambios del Motor Warp, ejecución de misiones y
  operaciones sobre recursos. Los eventos deberán poder consultarse en orden temporal.
* */

public enum TipoEvento {
    MOTOR,      // Cambios de estado en el Motor Warp
    MISION,     // Ciclos de vida, hitos y resultados de misiones
    RECURSO,    // Consumos, cargas de combustible/energía y desgaste
    ERROR,      // Excepciones capturadas y maniobras rechazadas
    SISTEMA,    // Acontecimientos generales del asistente o la nave
    RELEVANTE   // Órdenes directas del capitán, arranque del sistema o sucesos de navegación general.
}