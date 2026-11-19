package vista.principal.panelcontrol;

import vista.principal.panelcontrol.panelcursomision.IVistaCursoMision;
import vista.principal.panelcontrol.panelrecursos.IVistaRecursos;
import vista.principal.panelcontrol.panelresultadosmision.IVistaResultadosMision;
import vista.principal.panelcontrol.panelselectmision.IVistaSelectMision;

/**
 * Interfaz contenedora de la sección Control de la Nave (Enunciado E2, Vistas, punto 4).
 *
 * Muestra siempre el estado actual de los recursos ({@link IVistaRecursos}) y alterna entre las tres etapas del
 * ciclo de misión:
 * 1. Selección de Misión y Bitácora (SELECT_MISION)
 * 2. Misión en Curso, con el tablero en tiempo real (MISION_CURSO)
 * 3. Historial de informes finales (RESULTADOS_MISION)
 *
 * Los métodos se invocan desde el hilo de eventos de Swing.
 */
public interface IVistaControl {
    public String SELECT_MISION = "SELECT_MISION";
    public String MISION_CURSO = "MISION_CURSO";
    public String RESULTADOS_MISION = "RESULTADOS_MISION";

    public void mostrarSelectMision();

    public void mostrarMisionCurso();

    public void mostrarResultadosMision();

    public IVistaRecursos getVistaRecursos();

    public IVistaSelectMision getVistaSelectMision();

    public IVistaCursoMision getVistaCursoMision();

    public IVistaResultadosMision getVistaResultadosMision();
}
