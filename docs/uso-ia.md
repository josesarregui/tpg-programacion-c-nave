### Registro de Asistencia: Configuración inicial del entorno y `pom.xml` (30/09/2026)

* **Herramienta utilizada:** Asistente IA (Gemini).

* **Propósito de la consulta:** inicializar el proyecto con la estructura estándar de Maven exigida por la cátedra, resolver conflictos al clonar el repositorio en IntelliJ IDEA y definir las dependencias mínimas para las pruebas automatizadas.

* **Componente o documento afectado:** `pom.xml` y estructura inicial de directorios (commit `642d017`, José Arregui).

* **Resultado aprovechado:**
  * Estructura base del `pom.xml` (`groupId`, `artifactId`, versión).
  * Compilación para Java 17 con codificación UTF-8.
  * Dependencia de JUnit 5 (`junit-jupiter`) en ámbito `test`, para las evidencias de E1.

* **Revisión o modificación realizada por el equipo:**
  * José Arregui corroboró que las rutas locales coincidieran con el directorio clonado en Git.
  * El 02/10 se agregó el plugin Surefire 3.2.5, para que `mvn test` ejecute las pruebas JUnit 5 (las versiones anteriores del plugin las ignoran).

* **Forma en que se verificó el resultado:** el proyecto compila con `mvn compile` y `mvn test` ejecuta las pruebas JUnit 5 de `src/test/java`.

### Registro de Asistencia: Validación de nulos en `MotorWarp.setEstado()` (01/10/2026)

* **Herramienta utilizada:** asistente de IA consultado por José Arregui (la entrada original no registró cuál).

* **Propósito de la consulta:** entender por qué validar el estado recibido en `setEstado(State nuevoEstado)` en lugar de la asignación directa (`estado = nuevoEstado`) que mostraban las diapositivas de la cátedra.

* **Componente o documento afectado:** `MotorWarp.setEstado()` y `MotorWarpTest` (commit `3cc8880`, José Arregui).

* **Resultado aprovechado:** la explicación de que un estado nulo debe detectarse en el momento en que se asigna y no más tarde, cuando el motor intente delegar en él: el motor siempre debe tener un estado (invariante de la clase). En ese momento se implementó con `Objects.requireNonNull`.

* **Revisión o modificación realizada por el equipo:** el 09/10 (commit `2f30286`) se reemplazó `Objects.requireNonNull` por una aserción (`assert nuevoEstado != null`), porque `java.util.Objects` no forma parte de lo visto en la materia y un estado nulo sólo puede deberse a un error de programación (apunte de Aserciones). Además, `setEstado()` pasó a ser de paquete y se agregó `assert invariante()`.

* **Forma en que se verificó el resultado:** `MotorWarpTest` verificaba que `setEstado(null)` lanzara `NullPointerException`; hoy verifica que lance `AssertionError` y que el motor conserve su estado (`mvn test`).

### Registro de Asistencia: Lista de eventos de sólo lectura en `Bitacora` (01/10/2026)

* **Herramienta utilizada:** asistente de IA consultado por José Arregui (la entrada original no registró cuál).

* **Propósito de la consulta:** impedir que quien consulta la Bitácora pueda agregar, borrar o reemplazar eventos a través de la lista que devuelve `getEventos()`.

* **Componente o documento afectado:** `Bitacora.getEventos()` y `BitacoraTest` (commit `cfef95a`, José Arregui).

* **Resultado aprovechado:** devolver la lista con `Collections.unmodifiableList(eventos)`: si se intenta `add()` o `clear()` sobre la lista devuelta, Java lanza `UnsupportedOperationException` y la Bitácora no cambia.

* **Revisión o modificación realizada por el equipo:** se mantuvo. Más adelante se completó el contrato de la Bitácora: rechazo de eventos nulos, vacíos o fuera de orden temporal, y eventos inmutables (ver las entradas del 03/10 y del 09/10).

* **Forma en que se verificó el resultado:** la prueba `testGetEventosRetornaColeccionInmutable` de `BitacoraTest` intenta `add()` y `clear()` sobre la lista devuelta y comprueba que la Bitácora conserva sus eventos (`mvn test`).

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
  * Ante las dudas de diseño (energía adicional, asistente y salto), decidió seguir lo que establecen la Guía y la Aclaración.
  * Compartió el análisis con Axel, que confirmó las fallas y planteó cómo evolucionarían los costos en la E2; la duda se resolvió con la Tabla de Recursos de la Guía.
  * Antes de realizar el commit, revisamos minuciosamente el código para verificar que se ajustara a lo solicitado por la cátedra y a lo explicado en clase (incluidos los ejemplos de las clases teórico-prácticas de los miércoles). Luego modificamos lo que consideramos pertinente y realizamos el commit.

* **Forma en que se verificó el resultado:** `mvn test` pasa con 99 pruebas en verde (20 nuevas: 14 de Misión y 6 del Asistente) y `app.App` demuestra los Escenarios A y B.

### Registro de Asistencia: Módulo Asistente y revisión general del código (09/10/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** completar el módulo Asistente según E1-03 y la Aclaración "Naves, asistentes y misiones", revisar todo el código contra la Guía y los apuntes, y preparar dos informes en PDF para el equipo.

* **Componente o documento afectado:** paquetes `modelo.asistente`, `modelo.universo` (nuevo), `modelo.warp`, `modelo.mision`, `modelo.bitacora`, `modelo.tripulacion` y `modelo.liquidacion`; excepciones del motor y del centro de control; `App.java`; pruebas y documentación.

* **Resultado aprovechado:**
  * Interfaz `Asistente`, `CentroDeControl` y `AsistenteComando` completo: misiones pendientes y realizadas, e informe de cada misión en la Bitácora.
  * `EstadoMotorInvalidoException` pasó a ser comprobada.
  * Errores corregidos: una misión podía realizarse sin pasar por el asistente, la Bitácora aceptaba eventos fuera de orden, faltaban contratos en el Motor Warp y se usaban funcionalidades no vistas en clase.

* **Revisión o modificación realizada por el equipo:**
  * Luca Zuanetti fijó como criterio la Guía y los apuntes, y pidió un análisis previo antes de modificar código.
  * Delegó las decisiones pendientes con la condición de seguir la Guía e indicar las decisiones de interpretación adoptadas (sección 4 de `docs/diseño.md`).
  * Aportó como excepción la indicación del docente Lucas sobre `getConsejos()` y aprobó las correcciones antes de aplicarlas.
  * Luego de realizar un análisis minucioso de su código y las correcciones planteadas por Claude, se decidió que el código estaba listo para ser llevado a la rama principal.

* **Forma en que se verificó el resultado:** `mvn test` pasa con 120 pruebas en verde (antes 99) y `app.App` demuestra los Escenarios A, B, C y D con las aserciones activas.

### Registro de Asistencia: Revisión final de la Entrega 1 (09/10/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** revisar el proyecto integrado en `main` contra la Guía TP Nave (E1-01 a E1-10, Ficha de Inicio, Lista de comprobación, Rúbrica y Reglas Operativas), la Aclaración "Naves, asistentes y misiones" y los apuntes de la cátedra antes de etiquetar la entrega, y aplicar las correcciones en la rama `luca-RevisionFinal`.

* **Componente o documento afectado:** estados del Motor Warp (`modelo.warp`), `Asistente`, `AsistenteComando`, `Tripulante`, `App`, `BitacoraTest`, `pom.xml`, `.gitignore`, `README.md`, `docs/diseño.md` y `docs/diagrama_clases.md`.

* **Resultado aprovechado:**
  * Detección de que los constructores públicos de los estados permitían saltear el ciclo del Motor Warp sin excepción ni registro; se hicieron de paquete.
  * Matriz de verificación con resultados esperados en el README y ejecución de la demostración mediante Maven (`mvn compile exec:exec`).
  * Diagrama de clases rehecho por módulo y comparado con el código.
  * Nuevas decisiones de interpretación documentadas (tripulación inicial, energía adicional y salto ordenado directamente al asistente).
  * Informe en PDF para el equipo con una guía de defensa.

* **Revisión o modificación realizada por el equipo:**
  * Luca Zuanetti fijó como criterio la Guía y los apuntes, y pidió un análisis previo sin modificar código, ordenado por gravedad.
  * Realizó el análisis correspondiente, aplicó las correcciones que vio necesarias, consultó con Claude sobre sus correcciones y terminaron de refinar el código para la entrega.
  * Mantuvo sin cambios las decisiones ya tomadas por el equipo (indicación del docente Lucas sobre los consejos, enfriamiento inmediato, excepciones comprobadas).

* **Forma en que se verificó el resultado:** los errores se reprodujeron con un programa externo antes de corregirlos. `mvn test` pasa con 120 pruebas en verde y `app.App` corre sin errores con `java -ea` y con `mvn compile exec:exec`.