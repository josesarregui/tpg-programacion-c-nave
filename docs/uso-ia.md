
### Registro de Asistencia: Configuración inicial del entorno y `pom.xml`

* **Herramienta utilizada:** Asistente IA (Gemini).


* **Propósito de la consulta:** Asistencia técnica para la inicialización del proyecto bajo el estándar de Maven exigido por la cátedra, resolución de conflictos de clonación del repositorio en el entorno local (IntelliJ IDEA) y definición de las dependencias mínimas para testing automatizado.


* **Componente o documento afectado:** Archivo `pom.xml` en la raíz del proyecto y estructura inicial de directorios.


* **Resultado aprovechado:**
* Estructura base del archivo `pom.xml` con declaración de `groupId`, `artifactId` y empaquetado.
* Configuración de propiedades para compilación bajo Java 17 y codificación UTF-8.
* Incorporación de la dependencia de **JUnit 5 (`junit-jupiter`)** en ámbito de pruebas (`test`) para satisfacer los requerimientos de verificación funcional de E1.


* **Revisión o modificación realizada por el equipo:**
* Se corroboró la coincidencia de las rutas locales del sistema de archivos con el directorio clonado en Git.


### Consulta: Protección de Invariantes y Validación de Nulos en el Contexto (`MotorWarp`)

* **Contexto del debate:**
  Al implementar el método `setEstado(State nuevoEstado)` en la clase de contexto `MotorWarp`, surgió la duda sobre por qué utilizar `Objects.requireNonNull(nuevoEstado, ...)` en lugar de la asignación directa habitual (`estado = nuevoEstado`), tal como figuraba en las diapositivas introductorias de la cátedra.

* **Aporte y explicación conceptual de la IA:**
  La IA explicó que, aunque la asignación directa es sintácticamente válida, en un entorno de desarrollo profesional y bajo las reglas de evaluación del TP introduce una debilidad en el **Diseño por Contrato**:
    1. **Principio *Fail-Fast* (Fallo temprano):** Si una transición asignara por error una referencia `null`, la asignación simple no fallaría de inmediato. El error se manifestaría mucho después (lanzando un `NullPointerException` diferido al intentar delegar un método como `prepararSalto()`), oscureciendo el origen real de la falla. Con `Objects.requireNonNull`, la ejecución se interrumpe exactamente en el momento y en el método donde se intentó violar el contrato.
    2. **Protección de Invariantes de Clase:** Un motor interestelar sin estado es una entidad conceptualmente rota. La clase de contexto tiene la responsabilidad de garantizar que su estado interno sea consistente en todo su ciclo de vida.
    3. **Buenas prácticas en Java moderno:** Uso de la utilidad estándar `java.util.Objects` (incorporada para validación defensiva limpia y expresiva).

* **Decisión de diseño adoptada por el alumno/equipo:**
  Se incorporó `Objects.requireNonNull` en el mutador `setEstado` de `MotorWarp` para cumplir de forma estricta con la rúbrica de diseño por contrato e impedir estados inconsistentes en el modelo sin depender de interfaces gráficas o consola.