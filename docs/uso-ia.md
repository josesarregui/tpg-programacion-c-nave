
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


### Consulta: Protección de Invariantes y Validación de Nulos en el Contexto (`MotorWarp`) --> ACTUALMENTE NO ESTA ASI

* **Contexto del debate:**
  Al implementar el método `setEstado(State nuevoEstado)` en la clase de contexto `MotorWarp`, surgió la duda sobre por qué utilizar `Objects.requireNonNull(nuevoEstado, ...)` en lugar de la asignación directa habitual (`estado = nuevoEstado`), tal como figuraba en las diapositivas introductorias de la cátedra.

* **Aporte y explicación conceptual de la IA:**
  La IA explicó que, aunque la asignación directa es sintácticamente válida, en un entorno de desarrollo profesional y bajo las reglas de evaluación del TP introduce una debilidad en el **Diseño por Contrato**:
    1. **Principio *Fail-Fast* (Fallo temprano):** Si una transición asignara por error una referencia `null`, la asignación simple no fallaría de inmediato. El error se manifestaría mucho después (lanzando un `NullPointerException` diferido al intentar delegar un método como `prepararSalto()`), oscureciendo el origen real de la falla. Con `Objects.requireNonNull`, la ejecución se interrumpe exactamente en el momento y en el método donde se intentó violar el contrato.
    2. **Protección de Invariantes de Clase:** Un motor interestelar sin estado es una entidad conceptualmente rota. La clase de contexto tiene la responsabilidad de garantizar que su estado interno sea consistente en todo su ciclo de vida.
    3. **Buenas prácticas en Java moderno:** Uso de la utilidad estándar `java.util.Objects` (incorporada para validación defensiva limpia y expresiva).

* **Decisión de diseño adoptada por el alumno/equipo:**
  Se incorporó `Objects.requireNonNull` en el mutador `setEstado` de `MotorWarp` para cumplir de forma estricta con la rúbrica de diseño por contrato e impedir estados inconsistentes en el modelo sin depender de interfaces gráficas o consola.


### Validación de Nulos en el Contexto (`Bitacora.java`)
* public List<Evento> getEventos() {
*   return Collections.unmodifiableList(eventos); // 
* }
* --> Se uso la libreria java.util.Collections. Si alguien intenta hacer un .add() o .clear() sobre esa lista devuelta, Java lanza una excepción y no permite alterar la bitácora.

### Registro de Asistencia: Revisión y corrección de los módulos Tripulación y Liquidación (01/10/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:**
    1. Analizar `modelo.tripulacion` y `modelo.liquidacion` contra E1-04, E1-08, las Observaciones y las Evidencias del enunciado.
    2. Aplicar la corrección sugerida por el profesor Lucas: quitar `getConsejos()` de la interfaz `Liquidacion` (violaba el principio de segregación de interfaces), trasladar el cálculo del adicional por antigüedad y por consejos a las subclases de `Tripulante` y eliminar `ConsejosDecorator`.
    3. Adaptar el código a los apuntes de la cátedra (Decorator, Interfaces, Excepciones, Aserciones).

* **Componente o documento afectado:** paquetes `modelo.tripulacion` y `modelo.liquidacion`, excepciones `TripulanteInvalidoException`, `TripulacionInvalidaException`, `TripulanteInexistenteException` y `LiquidacionInvalidaException`, pruebas `TripulacionTest` y `LiquidacionTest`, `pom.xml` (plugin Surefire), `docs/diagrama_clases.md` y `docs/diseño.md`.

* **Resultado aprovechado:**
    * Cada cargo calcula su adicional por antigüedad; `Consejero` calcula y agrega su adicional por consejos, registrados por período mensual.
    * `Tripulante` como componente concreto del Decorator, con `AntiguedadDecorator` y `OrigenDecorator` como decoradores concretos.
    * Detalle identificable de cada concepto (`ConceptoHaber`, `ReciboHaberes`), requerido por E1-08.
    * Clase `Tripulacion` con el invariante de la Ficha de Inicio (un/a capitán/a + al menos 4 tripulantes).
    * Excepciones propias comprobadas que guardan el dato que provocó el error, y aserciones para invariantes, postcondiciones e invariantes de ciclo.

* **Revisión o modificación realizada por el equipo:**
    * La corrección conceptual no surgió de la IA: proviene de la observación del docente Lucas a Sebastian Barrionuebo (quitar `getConsejos()` de la interfaz `Liquidacion` y eliminar `ConsejosDecorator`), que Luca Zuanetti trasladó como consigna de trabajo.
    * Luca Zuanetti indicó a la IA los requerimientos a cumplir (E1-04, E1-08) y los apuntes de la cátedra como criterio, revisó el código generado y lo incorporó a su rama (commit `b260cd6`).
    * Los módulos parten del trabajo previo del equipo: la versión inicial de Sebastian Barrionuebo en `main` y la de Luca Zuanetti en su rama.

* **Forma en que se verificó el resultado:** pruebas JUnit 5 que cubren:
    * los 4 cargos con los 3 orígenes (12 casos con importes calculados a mano según E1-08);
    * la composición de dos decoradores en distinto orden;
    * los consejos por período y la liquidación de una tripulación completa;
    * rechazos por excepción (antigüedad negativa, tripulación sin capitán o incompleta, concepto nulo o repetido, id inexistente) y por aserción (importe negativo).

    Comando: `mvn test`.

### Registro de Asistencia: Análisis comparativo con main y preparación de la integración (02/10/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:**
    1. Comparar `modelo.tripulacion` y `modelo.liquidacion` de esta rama con los de `main` frente a la Guía TP Nave (E1-04, E1-08, Ficha de Inicio, Observaciones y Evidencias) y los apuntes de la cátedra.
    2. Dejar la rama lista para integrarse en `main`.

* **Componente o documento afectado:** `Consejero.registrarConsejo`, `TripulacionTest`, `app/App.java`, `README.md`, `docs/diseño.md`, `docs/diagrama_clases.md` y resolución del merge con `main`.

* **Resultado aprovechado:**
    * Informe comparativo (PDF) para el equipo: `main` calcula el subsidio por origen como porcentaje (E1-08 fija 20/30/18 PG), no liquida los consejos por período y no deja identificable cada concepto.
    * `registrarConsejo` rechaza un período nulo con `LiquidacionInvalidaException` en lugar de una aserción, porque el dato llega desde fuera del modelo.
    * Nueva demostración `App` con la API actual y casos de rechazo.
    * Merge de `main` en la rama conservando la versión de la rama de ambos módulos y eliminando `Origenes.java`, que ya no se usa.
    * README con instrucciones de compilación, ejecución y verificación.

* **Revisión o modificación realizada por el equipo:**
    * Luca Zuanetti solicitó el análisis y entregó como criterio la Guía TP Nave y los apuntes de la cátedra. Revisó el informe comparativo antes de compartirlo con el equipo y decidió qué cambios aplicar.
    * Se decidió integrar la rama `rama-luca-Creacion-Liquidacion-Tripulantes` (sin tilde), ya que la tilde del nombre original traía problemas al abrir los archivos desde GitHub web.
    * Los commits `29d50b6` y `4bca286` figuran con autor "Claude" porque se realizaron con Claude Code desde la sesión de Luca Zuanetti.
    * La integración a `main` se realiza mediante un Pull Request revisado por el equipo.

* **Forma en que se verificó el resultado:** `mvn test` (51 pruebas en verde) y ejecución de `app.App`, comparando los totales con los calculados a mano según E1-08.

### Registro de Asistencia: Análisis y corrección del módulo Nave, y revisión de Warp y Bitácora (03/10/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** analizar el módulo Nave de `main` según la Guía TP Nave y los apuntes de la cátedra, aplicar las correcciones en la rama `luca-correcionNave` y revisar la consistencia de Warp, Bitácora y la documentación.

* **Componente o documento afectado:** paquetes `modelo.nave`, `modelo.warp`, `modelo.bitacora` y `excepcion`; pruebas de Nave, Warp y Bitácora; `App.java`, `README.md`, `docs/diseño.md` y `docs/diagrama_clases.md`.

* **Resultado aprovechado:**
  * Detección de cuatro fallas en Nave: desborde que dejaba recursos negativos, cambios parciales al rechazar una operación, naves creadas sin la fábrica y una fábrica que devolvía `null`.
  * Corrección de Nave: enum `TipoNave`, constructores de paquete, recursos encapsulados, consumo sin cambios parciales y excepciones comprobadas con el dato rechazado.
  * Warp: consulta `estaDisponible()` (Aclaración R4) y `setEstado()` de paquete.
  * Bitácora: rechazo de eventos vacíos.
  * Documentación alineada con el código y reportes para el equipo.

* **Revisión o modificación realizada por el equipo:**
  * Luca Zuanetti fijó el criterio de trabajo: la Guía y los apuntes como fuente, sólo funcionalidades vistas en clase y un análisis previo sin modificar código. También aprobó el plan antes de implementarlo.
  * Contrastó el análisis con el reporte de otro integrante, lo que agregó puntos que ese reporte no cubría.
  * Acordó con el desarrollador de Warp el cambio en `State` antes de aplicarlo.
  * Pidió verificar la documentación, lo que permitió corregir inconsistencias previas.
  * Antes de realizar el commit, revisó minuciosamente el código para verificar que se ajustara a lo solicitado por la cátedra y a lo explicado en clase (incluidos los ejemplos de las clases teórico-prácticas de los miércoles). Luego modificó lo que consideró pertinente y realizó el commit.
  
* **Forma en que se verificó el resultado:** las fallas se reprodujeron ejecutando el código de `main` antes de corregirlas. `mvn test` pasa con 79 pruebas en verde (antes eran 51, ninguna de Nave) y `app.App` demuestra los Escenarios B, C y D.

### Registro de Asistencia: Análisis y corrección del módulo Misión (04/10/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** analizar el módulo Misión iniciado por Axel en la rama `Axel-Misiones` según la Guía TP Nave, la Aclaración "Naves, asistentes y misiones" y los apuntes de la cátedra, y aplicar las correcciones en `luca-correcionMisiones`.

* **Componente o documento afectado:** paquetes `modelo.mision` y `modelo.asistente`, excepción `NaveNoDisponibleException`, prueba `MisionTest`, `App.java`, `README.md`, `.gitignore`, `docs/diseño.md` y `docs/diagrama_clases.md`.

* **Resultado aprovechado:**
  * Detección de fallas en la versión inicial: no compilaba, dejaba cambios parciales en la nave, no completaba el salto, duplicaba la parte común de las misiones y no registraba en la Bitácora.
  * `Mision` con Template Method: `realizar()` fija el ciclo y cada misión redefine sólo su energía adicional, su acción y su condición de éxito.
  * `InformeMision` inmutable (E1-10) y `AsistenteComando` mínimo que registra en la Bitácora y propaga los rechazos.
  * Pruebas de los Escenarios A y B y demostración en `App`.

* **Revisión o modificación realizada por el equipo:**
  * Luca Zuanetti creó la rama a partir de la de Axel para conservar su trabajo, fijó como criterio la Guía y los apuntes, y pidió un análisis previo sin modificar código.
  * Ante las dudas de diseño (energía adicional, asistente y salto), indicó seguir lo que establecen la Guía y la Aclaración.
  * Compartió el análisis con Axel, que confirmó las fallas y planteó cómo evolucionarían los costos en la E2; la duda se resolvió con la Tabla de Recursos de la Guía.
  * Antes de realizar el commit, revisamos minuciosamente el código para verificar que se ajustara a lo solicitado por la cátedra y a lo explicado en clase (incluidos los ejemplos de las clases teórico-prácticas de los miércoles). Luego modificamos lo que consideramos pertinente y realizamos el commit.

* **Forma en que se verificó el resultado:** `mvn test` pasa con 99 pruebas en verde (20 nuevas: 14 de Misión y 6 del Asistente) y `app.App` demuestra los Escenarios A y B.
