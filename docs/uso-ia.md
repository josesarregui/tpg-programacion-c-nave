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

### Registro de Asistencia: Auditoría del login y registro de la Entrega 2 (22/10/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** auditar la implementación preliminar del login y del registro de la rama `Seba-Vista` (Sebastian Barrionuebo) contra el Enunciado de la Parte 2, la Guía TP Nave (reparto Vista / Controlador / servicio / DAO y Reglas Operativas) y los apuntes de MVC, Interfaces gráficas, Excepciones y La clase Object, y corregirla en la rama `luca-EvaluacionVista`.

* **Componente o documento afectado:** paquetes `vista.usuario`, `controlador.usuario`, `modelo.usuario` y `persistencia` (nuevo); excepciones del acceso y `PersistenciaException`; `App` y `DemostracionEntrega1`; `pom.xml` (dependencia H2); `database/schema.sql`; pruebas de la E2; `README.md` y `docs/diseño.md`.

* **Resultado aprovechado:**
  * Capa de persistencia con DAO, DTO y JDBC sobre H2 (no existía: los usuarios se guardaban en una lista en memoria y se perdían al cerrar).
  * Servicio con las reglas del acceso y las excepciones propias; validaciones de formato en la vista mediante clases oyentes `KeyAdapter`.
  * Corrección de paquetes que no coincidían con las carpetas, de `equals` de `Usuario` y de la demostración de la E1, que había quedado comentada.
  * Se conservaron el diseño visual, las interfaces de vista con comandos de acción y los controladores `ActionListener` propuestos por Sebastian.

* **Revisión o modificación realizada por el equipo:**
  * Luego de analizar el código propuesto por Claude a partir lo que hicimos, aplicamos las correciones que vimos necesarias y decidimos que estaba listo para ser mergeado a la rama main.
  
* **Forma en que se verificó el resultado:** `mvn test` pasa con 148 pruebas en verde (antes 120), `app.App` inicia y crea `datos/nave.mv.db`, y `app.DemostracionEntrega1` reproduce la salida de la E1.

### Registro de Asistencia: Motor Warp por turnos según el Anexo (13/11/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** analizar el Anexo "Misiones y Motor Warp" junto con José Arregui, comparar alternativas de diseño para el motor (quién verifica y cobra los costos) y adaptar el paquete `modelo.warp` y su uso, verificando cada decisión contra el Anexo, la Aclaración y el Enunciado de la Parte 2.

* **Componente o documento afectado:** paquete `modelo.warp` (`MotorWarp`, `State`, los cuatro estados y la nueva interfaz `SuministroMotor`); `Nave` (clase interna `SuministroDeNave`) y `Recursos` (`sumarDesgaste`); `Asistente` y `AsistenteComando` (`saltar()` y `saltarWarp()`); `Mision.cerrar()`; `DemostracionEntrega1` (Escenario C); pruebas `MotorWarpTest`, `AsistenteComandoTest`, `NaveTest`, `NaveFactoryTest`, `ObservadorNaveTest` (eliminada en el Paso 4 de la E2) y `MisionTest`; `README.md`, `docs/diseño.md` y `docs/diagrama_clases.md`.

* **Resultado aprovechado:**
  * Motor que expone sólo `saltar()` y `saltarWarp()`, con preparación de 2 turnos, verificación de energía, aborto del salto, enfriamiento con desgaste y salto normal con consumo de combustible.
  * Elección entre tres alternativas (motor que cobra, motor que sólo informa el costo, motor que sólo cuenta turnos): se eligió que cada estado verifique, cobre y cambie de estado en el mismo paso.
  * Decisiones de interpretación documentadas en `docs/diseño.md`, sección 4 (warp sin combustible, salto normal rechazado durante la maniobra, desgaste del enfriamiento limitado a 100, cierre de misión sin salto).

* **Revisión o modificación realizada por el equipo:** *(a completar por el equipo).*

* **Forma en que se verificó el resultado:** `mvn test` pasa con 217 pruebas en verde (28 omitidas por no tener entorno gráfico) y `app.DemostracionEntrega1` muestra el nuevo Escenario C.

### Registro de Asistencia: Paso 1 de la E2 — entorno espacial y `ElementoDelUniverso` (13/11/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** completar el Paso 1 del Plan de Trabajo de la E2 en la rama `luca-EntornoEspacial`: integrar en `modelo.universo` el módulo Destino y Tablero de la cátedra, definir las firmas polimórficas de `ElementoDelUniverso` e implementar `Asteroide` y `EstacionDeCarga` según el Enunciado de la Parte 2, los apuntes de Interfaces, Excepciones y Diseño por Contrato, y la Aclaración (R2 y R5).

* **Componente o documento afectado:** paquete `modelo.universo` (clases de la cátedra movidas desde `modelo.destinoytablero`, `ElementoDelUniverso`, `Asteroide`, `EstacionDeCarga`, firma de las naves enemigas; se eliminó `ResultadoInteraccion`); `Recursos` (`descontarEnergia`, `reabastecer`), `Nave` (`recibirImpacto`, `reabastecer`), `Asistente` y `AsistenteComando` (`recibirImpacto`, `reabastecer`); pruebas `AsteroideTest`, `EstacionDeCargaTest`, `RecursosTest` y `TableroYDestinoTest`; `README.md`, `docs/diseño.md` y `docs/diagrama_clases.md`.

* **Resultado aprovechado:**
  * Luca Zuanetti desarrolló el código del paso, partiendo de la primera versión del universo que Sebastian Barrionuebo había iniciado (commit `f142b6b`). Claude analizó ese código y propuso las firmas (`interactuarCon(Asistente)`, `getSimbolo()`), los métodos de soporte en `Recursos`, `Nave` y el asistente, y las pruebas.
  * Revisamos en conjunto las propuestas y corregimos lo necesario: que la interacción reciba el `Asistente` (dueño de la Bitácora y único que ordena a la nave), que el impacto y la recarga no se rechacen sino que respeten los topes de `Recursos`, que la Bitácora registre lo realmente cambiado y que no queden excepciones capturadas para ignorarlas.
  * Implementamos todo de la forma más coherente posible con el diseño de la E1: delegación en `Recursos`, contratos con aserciones, sin `instanceof`, `getClass()` ni condicionales de tipo, y sin consola ni Swing en el modelo.

* **Revisión o modificación realizada por el equipo:** Luca Zuanetti aprobó las firmas antes de implementarlas, revisó el código resultante y la documentación, y decidió mover el módulo de la cátedra a `modelo.universo` en lugar de duplicarlo. La resolución completa de las batallas (Double Dispatch puro y destrucción de la nave) queda para el Paso 2.

* **Forma en que se verificó el resultado:** `mvn test` pasa con 230 pruebas en verde (antes 217: 15 nuevas de Asteroide, Estación de carga y Recursos, y 2 de `TableroYDestinoTest` reemplazadas por ellas).

### Registro de Asistencia: Paso 2 de la E2 — batallas por Double Dispatch y estado de destrucción (14/11/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** completar el Paso 2 del Plan de Trabajo de la E2 en la rama `luca-Batallas`: que `Nave` implemente `ElementoDelUniverso`, resolver las batallas por Double Dispatch puro según la tabla del Enunciado de la Parte 2 (punto A), incorporar el estado de destrucción de la nave (Fase 2) y las operaciones mínimas de gemas y suministros para M-01 y M-02, respetando la Aclaración (R2 y R5) y los apuntes de Interfaces, Excepciones y Diseño por Contrato.

* **Componente o documento afectado:** `Nave` (`ElementoDelUniverso`, `enfrentarseCon`, métodos de vuelta, `aplicarResultado`, `destruida`, gemas y suministros), `Exploradora`, `Carguero`, `Combate`, `TipoNave` (símbolo), `Recursos` (`agotados`) y el nuevo `ResultadoBatalla`; `Asistente` y `AsistenteComando` (`enfrentarseCon`, `naveDestruida`, `getGemas`, `acreditarGemas`, `entregarSuministros` y el registro de la destrucción); `Mision` (aborto con la nave destruida) y `NaveNoDisponibleException` (`naveDestruida`). Se eliminaron `NaveEnemiga`, `ExploradoraEnemiga`, `CargueroEnemigo`, `CombateEnemigo` y `ResultadoCombate`. Pruebas nuevas `BatallaTest`, `DestruccionNaveTest` y `GemasYSuministrosTest`; adaptadas `TableroYDestinoTest`, `AsteroideTest` y `MisionTest`; `README.md`, `docs/diseño.md` y `docs/diagrama_clases.md`.

* **Resultado aprovechado:**
  * Luca Zuanetti hizo el código del paso. Partió de las naves enemigas provisorias y de la primera tabla de resultados que había escrito Sebastian Barrionuebo (commit `f142b6b`); esa tabla se conserva en los métodos de vuelta de cada subclase. Claude propuso las firmas (`enfrentarseCon(Nave)`, los tres `serEnfrentadaPor...` de paquete, `ResultadoBatalla` con `destruyeNavePropia()`, `aplicarResultado`), el estado de destrucción y su registro en la Bitácora, y las pruebas.
  * Revisamos y corregimos juntos las propuestas: que la ida pase por el asistente (R2); que cada subclase implemente su columna de la tabla y no su fila, porque el resultado se expresa para la nave propia; que la tabla quede separada de sus consecuencias; que la destrucción sea definitiva y se actualice después de todo consumo, incluido el Motor Warp; que la muerte de la tripulación sea un estado derivado; y que no queden excepciones capturadas para ignorarlas.
  * Implementamos todo de la forma más coherente posible con el diseño existente: delegación en `Recursos`, contratos con aserciones e invariante de `Nave` ("recursos agotados implica nave destruida"), sin `instanceof`, `getClass()` ni condicionales de tipo, y sin consola ni Swing en el modelo. Las decisiones de interpretación quedaron en `docs/diseño.md`, sección 4.

* **Revisión o modificación realizada por el equipo:** Luca Zuanetti aprobó las firmas antes de implementarlas, discutió y confirmó la eliminación de las clases provisorias, y revisó el código y la documentación resultantes. La lógica de las misiones sobre el tablero (radares, travesía por turnos y aplicación de gemas y suministros en el cierre) queda para el Paso 3.

* **Forma en que se verificó el resultado:** `mvn test` pasa con 276 pruebas en verde (antes 230: 46 nuevas de batallas, destrucción, gemas, suministros y misiones; la prueba de Double Dispatch de `TableroYDestinoTest` se reemplazó por una con naves de la fábrica).

### Registro de Asistencia: Paso 3 de la E2 — radares de navegación y ciclo de misiones sobre el tablero (15/11/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** integrar el Paso 2 (`main`) en la rama `Axel-EvolucionCicloMisiones`, auditar el Paso 3 que escribió Axel Sanches contra el Enunciado de la Parte 2 (Fases 1 a 4), la Guía (4.2 y Tabla de Recursos) y la Documentación de Destino y Tablero, evaluar sus decisiones de diseño y corregirlo para que M-01, M-02 y M-03 recorran el tablero turno a turno con radares, Destino, interacciones y destrucción, y apliquen gemas, suministros y la reparación en el cierre.

* **Componente o documento afectado:** `Mision`, `MisionIntercepcion`, `MisionRecoleccion`, `MisionRetorno`, `InformeMision` y los nuevos `BalanceRecursos` y `Navegador` (`modelo.mision`); se eliminó `TipoMision`. `Asistente` y `AsistenteComando` (se quitaron `getTablero`, `setTablero` y `getPosicionNave`; se agregó `motorDisponible()`; la Bitácora resume el estado posterior al cierre). `ElementoDelUniverso.esAmenaza()` en `Asteroide`, `EstacionDeCarga` y `Nave`; nueva `FabricaDeUniverso`. `ControladorControl`, `ControladorSelectMision`, `VistaSelectMision` y `DemostracionEntrega1`. Pruebas nuevas `TravesiaTest` y dobles de prueba `TableroDePrueba`, `DestinoSinMovimiento` y `DestinoProgramado`; reescrita `MisionTest`; adaptadas `AsistenteComandoTest`, `CentroDeControlTest` y `VistaSelectMisionTest`; `README.md`, `docs/diseño.md` y `docs/diagrama_clases.md`.

* **Resultado aprovechado:**
  * Axel Sanches hizo el código del Paso 3 (commit `947dd8c`): el bucle por turnos con `Thread.sleep`, la búsqueda de la ruta, el Destino en las misiones, los ganchos para mostrar gemas y suministros, los montos aleatorios y el tablero en el asistente. Su commit dejaba pendiente la transición entre casillas y planteaba la duda de quién elige la casilla.
  * Luca Zuanetti lo corrigió con asistencia de Claude. Claude auditó la rama (tras el merge, 14 pruebas fallaban por el tablero nulo y el bucle no movía la nave ni terminaba), propuso las firmas (inyección de `Tablero` y `Destino` por constructor, primitivas y ganchos con implementación por defecto, `esAmenaza()`, `motorDisponible()`, `BalanceRecursos`, `Navegador`), la política de salto y los costos según la Tabla de Recursos y el Anexo, y las pruebas deterministas.
  * Corregimos en conjunto las decisiones de Axel: se conservaron los ganchos, ajustados para que alimenten el balance y el cierre real; se conservaron los montos aleatorios, con `Random` inyectable, rangos constantes y contratos; el tablero pasó del asistente a la misión. Se resolvió su duda (el juego elige la casilla) y se aplicó la precedencia de fuentes ante la contradicción entre la Tabla de Recursos y el Enunciado sobre los encuentros.
  * Implementamos todo de la forma más coherente posible con el diseño existente: Template Method con las cuatro fases del Enunciado, toda orden a la nave a través del asistente, transición validada entre casillas vecinas dentro del tablero, interrupción de la pausa sin silenciarla, sin `instanceof`, `getClass()` ni condicionales de tipo, y sin consola ni Swing en el modelo. Las decisiones de interpretación quedaron en `docs/diseño.md`, sección 4.

* **Revisión o modificación realizada por el equipo:** Luca Zuanetti revisó el diagnóstico, decidió la precedencia de fuentes (el Enunciado, la Guía y la Documentación de Destino y Tablero tienen el mismo nivel), aprobó las firmas antes de implementarlas y revisó el código y la documentación resultantes. Durante la implementación se sumaron dos consultas públicas, `getCombustibleMinimo()` y `getEnergiaMinima()`, para que `VistaSelectMision` no repitiera el cálculo del costo fijo eliminado.

* **Forma en que se verificó el resultado:** `mvn clean test` pasa con 293 pruebas en verde (antes 276: 15 nuevas de travesía y 2 más en `MisionTest`, que se reescribió sobre el tablero). Además, `app.DemostracionEntrega1` corrió 20 veces con `-ea` sobre universos aleatorios de 20 x 20 sin errores de aserción.

### Registro de Asistencia: Paso 4 de la E2 — desacoplamiento del modelo hacia la vista con el patrón Observer (17/11/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** completar el Paso 4 del Plan de Trabajo de la E2 en la rama `luca-paso4`: unificar todas las notificaciones del modelo hacia la vista en `Observable` y `Observador` de la cátedra (Versión 2), con `Tablero`, el asistente y `PlataformaDeCarga` como sujetos, verificando contra los apuntes de la materia (Clase 11, Observer-Observable; Clase 13, MVC) quién debe suscribir a los observadores y cómo manejar las notificaciones que llegan desde otros hilos, sin que el modelo conozca Swing.

* **Componente o documento afectado:** `Asistente` y `AsistenteComando` (extiende `Observable`; `agregarObservador`, `quitarObservador`, notificación en `registrarEvento`; la batalla se aplica antes de registrarse); `Nave` (deja de ser observable); `PlataformaDeCarga` (extiende `Observable`, mensajes del Enunciado, stock por pull); `IVistaDetallesNave`, `VistaDetallesNave`, `IVistaConcurrencia` y `VistaConcurrencia` (extienden o implementan `Observador`, `observar(...)`, `dejarDeObservar()`, `actualizar(String)` con `invokeLater`); `ControladorDetallesNave` y `ControladorConcurrencia` (entregan el sujeto a la vista). Se eliminaron `ObservableNave`, `ObservadorNave` y `ObservadorConcurrencia`. Pruebas nuevas `AsistenteObservadorTest` y `PlataformaDeCargaObservadorTest`; reemplazada `ObservadorNaveTest`; adaptadas `VistaDetallesNaveTest`, `VistaConcurrenciaTest`, `ControladorConcurrenciaTest`, `ControladorPrincipalTest` y `PlataformaDeCargaTest`; `README.md`, `docs/diseño.md` y `docs/diagrama_clases.md`.

* **Resultado aprovechado:**
  * Partimos del Observer que había hecho Sebastian Barrionuebo: `ObservableNave` y `ObservadorNave` con la nave como sujeto y la delegación desde el asistente (commit `b530bd1`), `ObservadorConcurrencia` con `PlataformaDeCarga` (commit `b416b51`) y `VistaDetallesNave` como observadora con su actualización en el hilo de eventos de Swing (commit `8b14df4`). De ese código se conservaron las vistas, sus controladores y la idea de actualizar los componentes con `invokeLater`.
  * Luca Zuanetti hizo el código del paso y propuso las firmas (`agregarObservador` y `quitarObservador` en la interfaz `Asistente`, `AsistenteComando` y `PlataformaDeCarga` como subclases de `Observable`, `observar` y `dejarDeObservar` en las interfaces de las vistas), el asistente como sujeto en lugar del centro de control, la notificación en un único punto (`registrarEvento`), la suscripción desde la vista según los apuntes, la redefinición `synchronized` de los métodos del Observer para las suscripciones concurrentes y las pruebas.
  * Corregimos en conjunto lo que hacía falta: que la nave deje de notificar para que ningún cambio llegue dos veces; que la vista consulte el estado en el hilo que notifica; que la batalla se aplique antes de notificarla; que los mensajes de la plataforma sigan los del Enunciado y el stock se consulte por getter; y que las pruebas no usen `instanceof`.
  * Implementamos todo de la forma más coherente posible con el diseño existente: un único mecanismo Observer, toda orden a la nave a través del asistente, contratos con aserciones, la lógica del monitor sin cambios (Paso 5), sin `instanceof`, `getClass()` ni condicionales de tipo, y sin consola ni Swing en el modelo. Las decisiones de interpretación quedaron en `docs/diseño.md`, sección 4.

* **Revisión o modificación realizada por el equipo:** Luca Zuanetti fijó la precedencia de fuentes, aprobó las firmas y la suscripción desde la vista antes de implementarlas, y revisó el código y la documentación resultantes. Las vistas nuevas (grilla del tablero, Bitácora e informes en vivo) quedan para el Paso 6.

* **Forma en que se verificó el resultado:** `mvn clean test` pasa con 312 pruebas en verde (antes 293: se eliminaron las 10 de `ObservadorNaveTest` y se sumaron 17 de `AsistenteObservadorTest`, 7 de `PlataformaDeCargaObservadorTest` y 5 en las pruebas de vistas y controladores).

### Registro de Asistencia: Paso 5 de la E2 — Visor Recóndito, Productor-Consumidor con monitores (17/11/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** completar el Paso 5 del Plan de Trabajo de la E2 en la rama `luca-paso5`: verificar y completar el monitor `PlataformaDeCarga` y los hilos `GeneradoraDeEnergia` y `NaveConsumidora` contra la Especificación Técnica de Concurrencia del Enunciado y los apuntes de la materia (Clase 10 - Concurrencia; Threads, synchronized, wait y notify; Complemento de problemas 1.2, 1.3 y 2.2; reglas de oro de monitores del Resumen), con creación y detención de entidades en tiempo real, sin carreras, bloqueos mutuos ni hilos colgados, y publicando los eventos por el Observer del Paso 4.

* **Componente o documento afectado:** `PlataformaDeCarga` (`depositarBateria` y `retirarBateria` en lugar de `depositar` y `retirar`, guardas privadas `estaLlena()` y `estaVacia()`, precondiciones y postcondiciones, orden de mensajes del Enunciado y mensaje adicional de depósito); `GeneradoraDeEnergia` y `NaveConsumidora` (sin bandera de detención: terminan por interrupción; pausa de consumo después de retirar; pausa como precondición); `ControladorConcurrencia` (mapa de identificador a hilo, alta registrada sólo si se lanzó el hilo, detención con `interrupt()` + `join` con tiempo límite, egreso también al detener todo). Pruebas nuevas `HilosProductorConsumidorTest` y el observador de prueba `RegistroDeEventos`; reescritas `PlataformaDeCargaTest` y `ControladorConcurrenciaTest`; adaptadas `PlataformaDeCargaObservadorTest` y `VistaConcurrenciaTest`; `README.md`, `docs/diseño.md` y `docs/diagrama_clases.md`.

* **Resultado aprovechado:**
  * Partimos del módulo de concurrencia que había hecho Sebastian Barrionuebo (commits `b416b51` y `fdb1fb5`): el monitor con métodos `synchronized`, guardas en `while`, `wait()` y `notifyAll()`, las tareas `Runnable` con su ciclo y la restauración de la interrupción, y el controlador que crea un hilo por entidad, lo lanza con `start()` y permite detenerlo. Esa estructura se conservó.
  * Luca Zuanetti hizo el código del paso. Claude propuso lo suyo: las firmas (`depositarBateria` / `retirarBateria`, guardas privadas, tareas sin bandera, mapa de identificador a hilo), detener sólo por interrupción porque la bandera común no era visible entre hilos y no despertaba a un hilo en `wait()`, publicar la producción antes de la solicitud según el orden del Enunciado sin que el hilo notifique por su cuenta, esperar con `join` antes de publicar el egreso y las pruebas deterministas con eventos, `CountDownLatch` y `join` con tiempo límite.
  * Corregimos en conjunto lo que hacía falta: el momento del mensaje de producción, la falta de un aviso del cambio de stock al depositar, la búsqueda del hilo por nombre (`endsWith`), las listas internas expuestas por getters, el ajuste silencioso de la pausa mínima, un alta que podía quedar registrada sin hilo si fallaba la publicación y el egreso que no se publicaba al detener todo.
  * Implementamos todo de la forma más coherente posible con el diseño existente: toda la sincronización en el monitor, un único monitor compartido, `notifyAll()` y guardas en `while`, pausas fuera del monitor, el Observer del Paso 4 intacto (mensajes del Enunciado, stock por getter, suscripción desde la vista e `invokeLater`), contratos con aserciones, sin `instanceof`, `getClass()` ni condicionales de tipo, sin `notify()`, `Thread.stop()` ni bloques `synchronized` en los hilos, y sin consola ni Swing en el modelo. Las decisiones de interpretación quedaron en `docs/diseño.md`, sección 4.

* **Revisión o modificación realizada por el equipo:** Luca Zuanetti fijó la precedencia de fuentes, aprobó las firmas y el mecanismo de detención antes de implementarlos, y revisó el código y la documentación resultantes. Las vistas nuevas y los controladores de operación quedan para el Paso 6.

* **Forma en que se verificó el resultado:** `mvn clean test` pasa con 325 pruebas en verde (antes 312: 8 de `HilosProductorConsumidorTest`, 4 más en `PlataformaDeCargaTest` y 1 más en `ControladorConcurrenciaTest`). Las 36 pruebas de concurrencia y de su vista se repitieron 9 veces sin fallas.

### Registro de Asistencia: Paso 6 de la E2 — vistas Swing y controladores MVC de operación (18/11/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** completar el Paso 6 del Plan de Trabajo de la E2 en la rama `luca-paso6`: auditar las pantallas existentes contra el Enunciado de la Parte 2 (Vistas, puntos 1 a 5, y "Requerimiento de Diseño — MVC y Observer-Observable") y el Plan, y dejar todas las secciones operativas en Swing con MVC y Observer: transición del login a la ventana principal y cierre de sesión, creación de nave y tripulación, nómina con sueldos y desglose, control de la nave con recursos, Bitácora, informes y tablero de 20 x 20 actualizándose solos mientras la misión corre en su propio hilo, y el Visor Recóndito integrado al flujo de sesión, sin congelar el hilo de eventos, sin carreras ni hilos colgados.

* **Componente o documento afectado:** nuevas `FabricaDeVistas` y `FabricaDeVistasSwing`; `ControladorLogin`, `ControladorRegistro` y `ControladorPrincipal` (fábrica de vistas, transición y cierre de sesión); `ControladorControl`, `ControladorSelectMision` (catálogo de misiones, una misión a la vez), `ControladorCursoMision` (hilo de la misión, cancelación con `interrupt()` + `join`) y `ControladorResultadosMision`; `IVistaControl`, `IVistaSelectMision`, `IVistaCursoMision` e `IVistaResultadosMision` y sus vistas; nuevas `IVistaRecursos`, `VistaRecursos`, `IVistaTablero`, `VistaTablero` y `EstiloControl`; `ControladorCrearTripulacion` y `ControladorDetallesTripulacion`, `IVistaDetallesTripulacion` y `VistaDetallesTripulacion`; `Mision` y sus subclases (`getCondicionDeExito()` pública); `App` (arranque en el EDT) y la carga de imágenes de las vistas. Pruebas nuevas `ControladorSesionTest`, `ControladorMisionTest`, `VistaTableroTest`, `VistaCursoMisionTest`, `VistaRecursosTest`, `VistaResultadosMisionTest` y `VistaControlTest`, con las ayudas `VistasDePrueba` y `EdtDePrueba`; reescritas `ControladorLoginTest`, `ControladorPrincipalTest` y `ControladorControlTest`; ampliadas `ControladorDetallesTripulacionTest` y `VistaSelectMisionTest`; `README.md`, `docs/diseño.md` y `docs/diagrama_clases.md`.

* **Resultado aprovechado:**
  * Partimos del código de vistas y controladores que había hecho Sebastian Barrionuebo: login y registro (commit `b8b5b34`), ventana principal y creación de nave (`99a3e0d`), pantallas de creación de nave y tripulación (`b530bd1` y `395c8af`), sueldos y recibo de haberes en la nómina (`64192d6`), y la pantalla de selección de misión y Bitácora con los paneles de misión en curso y resultados (`817e7e1`, `eb69150` y `8b14df4`). Se conservaron sus pantallas, el menú, la navegación por tarjetas, el estilo visual y la estructura de interfaces y controladores por sección.
  * Luca Zuanetti hizo el código del paso. Claude propuso lo suyo: la auditoría de cada pantalla, las firmas de las interfaces `IVista...` nuevas y modificadas, la `FabricaDeVistas` para que los controladores no dependan de clases Swing, un observador por panel, el hilo dedicado de la misión con su lanzamiento, su detención y el manejo de sus excepciones, el orden del cierre de sesión y las pruebas deterministas (vistas de prueba sin ventanas, colas, `CountDownLatch`, `join` con tiempo límite y el EDT bloqueado para comprobar que los componentes cambian en él).
  * Corregimos en conjunto lo que hacía falta: los controladores que creaban ventanas concretas y consultaban `isHeadless()`; la misión que se encomendaba pero no se ejecutaba; los paneles vacíos de misión en curso y resultados; el refresco manual de la Bitácora, que leía el modelo desde el hilo de eventos; los `switch` por código de misión, el `instanceof Consejero` y el `instanceof Frame`; la excepción de liquidación que se descartaba y el `catch` vacío del recibo; y el uso de `volatile`, que no figura en los apuntes.
  * Implementamos todo de la forma más coherente posible con el diseño existente: MVC según el Enunciado (el `ActionListener` delega en el controlador, que invoca al modelo y coordina la respuesta), el Observer del Paso 4 y la concurrencia del Paso 5 intactos, toda consulta al modelo en el hilo que notifica y toda modificación de componentes con `invokeLater`, sin espera activa, `notify()`, `Thread.stop()` ni `Thread.sleep` en el EDT o en métodos `synchronized`, sin `instanceof`, `getClass()` ni condicionales de tipo, y sin consola ni Swing en el modelo. Las decisiones de interpretación quedaron en `docs/diseño.md`, sección 4.

* **Revisión o modificación realizada por el equipo:** Luca Zuanetti fijó la precedencia de fuentes, revisó la auditoría, aprobó las firmas, el lanzamiento y la detención del hilo de la misión antes de implementarlos, y revisó el código y la documentación resultantes. El script `database/schema.sql` y la etiqueta `entrega-e2` quedan para el Paso 7.

* **Forma en que se verificó el resultado:** `mvn clean test` pasa con 349 pruebas en verde (antes 325: 2 de `ControladorSesionTest`, 4 de `ControladorMisionTest`, 3 más en `ControladorLoginTest`, 3 más en `ControladorPrincipalTest`, 1 más en `ControladorDetallesTripulacionTest`, 2 más en `VistaSelectMisionTest` y 9 de las vistas nuevas, incluida `VistaControlTest`). Las pruebas de la sección Control, de la misión y de la sesión se repitieron 8 veces sin fallas.

### Registro de Asistencia: Paso 7 de la E2 — pruebas automatizadas, script SQL y documentación de cierre (18/11/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** completar el Paso 7 del Plan de Trabajo de la E2 en la rama `luca-paso7`: auditar el repositorio completo contra la Guía (4.2, puntos 5 a 8, y Reglas Operativas, punto 5), el Enunciado de la Parte 2 y la Documentación de Destino y Tablero, y después contra el Plan; verificar `database/schema.sql` con una prueba; completar la suite JUnit 5 de la matriz de verificación; resolver los pendientes de diseño aprobados y dejar el README y la documentación listos para etiquetar la entrega con `entrega-e2`.

* **Componente o documento afectado:** `database/schema.sql` y `pom.xml` (el script se copia al classpath); `UsuarioDAOJDBC` (ejecuta el script y ya no tiene una copia de la sentencia); `Liquidacion`, `Tripulante` y `Decorator` (`fueDecoradoCon` en lugar de `instanceof Decorator`). Prueba nueva `EsquemaSQLTest`, 5 pruebas más en `TableroYDestinoTest` y 1 más en `LiquidacionTest`; `VistasDePrueba` y `ControladorConcurrenciaTest` sin `volatile`; `README.md`, `docs/diseño.md`, `docs/diagrama_clases.md` (nueva sección 13, acceso y persistencia) y este registro.

* **Resultado aprovechado:**
  * Partimos de código propio y de compañeros: `TableroYDestinoTest` la comenzó Sebastian Barrionuebo (commit `f142b6b`) y se le agregaron pruebas; el módulo de liquidación con Decorator partió de su código (`06c3057`), y el `instanceof` que se quitó lo había introducido la corrección `b260cd6`; `ControladorConcurrenciaTest` partió de su código (`b416b51`) y se reescribió en el Paso 5. La persistencia (`1be0b54`) y las vistas de prueba (`0e427a4`) eran de Luca Zuanetti.
  * Luca Zuanetti hizo el código del paso. Claude propuso lo suyo: la auditoría de cierre, que el sistema ejecute el script en lugar de una copia de su sentencia, la prueba del script sin repetirla, las pruebas del contrato de `Tablero` y `Destino` deterministas sin modificar las clases de la cátedra, la firma `fueDecoradoCon` y los accesores `synchronized` en las vistas de prueba.
  * Corregimos en conjunto lo que hacía falta: la sentencia duplicada entre el script y el DAO, el `instanceof Decorator` de la E1, el `volatile` de las vistas de prueba, el README que sólo nombraba la etiqueta `entrega-e1`, los "(en desarrollo)" que ya no correspondían, el título de `docs/diseño.md`, la capa de acceso y persistencia que faltaba en el diagrama de clases y un enlace roto del diseño a la matriz del README.
  * Implementamos todo de la forma más coherente posible con el diseño existente: sin modificar las clases de la cátedra; con el Observer del Paso 4, la concurrencia del Paso 5 y el MVC y el hilo de la misión del Paso 6 intactos; sin `instanceof`, `getClass()` ni condicionales de tipo en el código nuevo o modificado; sin `volatile`; y con pruebas deterministas, sin pausas fijas. Las reglas de navegación de la Guía (punto 6), `Usuario.equals` y `Posicion` quedaron justificadas como decisiones de interpretación en `docs/diseño.md`, sección 4 (Paso 7).

* **Revisión o modificación realizada por el equipo:** Luca Zuanetti fijó la precedencia de fuentes, revisó la auditoría, aprobó la propuesta antes de implementarla (con las reglas de navegación de la Guía sólo documentadas) y revisó el código y la documentación resultantes. La etiqueta `entrega-e2` la crea Luca Zuanetti a mano sobre el commit final de `main`, después de verificar manualmente la aplicación.

* **Forma en que se verificó el resultado:** `mvn clean test` pasa con 359 pruebas en verde (antes 349: 4 de `EsquemaSQLTest`, 5 más en `TableroYDestinoTest` y 1 más en `LiquidacionTest`). Las pruebas de controladores, vistas, concurrencia, tablero y script se repitieron 8 veces sin fallas.

### Registro de Asistencia: Revisión final de la Entrega 2 (20/11/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** revisión final de la E2 en la rama `luca-revision-final` antes de etiquetarla: auditar el repositorio completo (requisitos, modelo, concurrencia, MVC y Swing, persistencia, restricciones de diseño, clases de la cátedra, pruebas, compilación desde cero y documentación) contra el Enunciado de la Parte 2, la Guía (4.2, puntos 5 a 8 y Reglas Operativas) y la Documentación de Destino y Tablero, con el mismo nivel, y después contra el Plan; corregir sólo los hallazgos aprobados, cada uno con una prueba que falle antes de la corrección.

* **Componente o documento afectado:** `Mision` (travesía: cancelación, maniobra warp y energía para avanzar), `Asistente` y `AsistenteComando` (contratos de las consultas de tripulación), `Origenes` (eliminado), `pom.xml`; pruebas nuevas en `TravesiaTest` (4, una de ellas parametrizada con 5 casos) y `AsistenteComandoTest` (1); `README.md`, `docs/diseño.md` (secciones 2, 3 y 4), `docs/diagrama_clases.md` y este registro.

* **Resultado aprovechado:**
  * Partimos de código propio y de compañeros: la travesía de `Mision` es la corrección del Paso 3 de Luca Zuanetti (commit `5f4b860`) sobre el código de Axel Sanches (`947dd8c`); las consultas de tripulación del asistente son de Sebastian Barrionuebo (`b530bd1`), igual que `Origenes` (`99a3e0d`), que se había eliminado en la integración del 02/10/2026 y volvió a aparecer con ese commit.
  * Luca Zuanetti hizo el código. Claude propuso lo suyo: la auditoría con los hallazgos reproducidos (cancelar durante una maniobra warp dejaba la nave inutilizable; cancelar en el turno de llegada daba una misión exitosa; con poco combustible y 7 a 11 de energía la misión destruía la nave con sus propios consumos), las correcciones mínimas y las pruebas deterministas que las demuestran.
  * Corregimos en conjunto los errores aprobados: los tres de la travesía, el enum sin uso con valores contrarios a E1-08, los contratos ausentes de las consultas de tripulación, los métodos públicos que faltaban en el diagrama de clases, dos textos ambiguos o inexactos del README y del diseño, y la configuración del compilador.
  * Implementamos todo de la forma más coherente posible con el diseño existente: sin modificar las clases de la cátedra ni firmas públicas; con el Observer del Paso 4, la concurrencia del Paso 5, el MVC y el hilo de la misión del Paso 6 y el script SQL del Paso 7 intactos; sin `instanceof`, `getClass()` ni condicionales de tipo en el código modificado; sin `volatile`; y con pruebas sin pausas fijas (la interrupción se simula con la marca de interrupción del hilo). Las decisiones nuevas están en `docs/diseño.md`, sección 4 (Revisión final).

* **Revisión o modificación realizada por el equipo:** Luca Zuanetti fijó la precedencia de fuentes, revisó la auditoría, eligió qué hallazgos corregir (no se corrigió `Asistente.getNave()`, diseño de la E1 que usan las pruebas) y revisó el código y la documentación resultantes. La etiqueta `entrega-e2` la crea Luca Zuanetti a mano sobre el commit final de `main`.

* **Forma en que se verificó el resultado:** las 9 pruebas nuevas fallaron antes de la corrección y pasan después. `mvn clean test` pasa con 368 pruebas en verde (antes 359). Las pruebas de concurrencia, controladores, vistas y misiones se repitieron 10 veces sin fallas. Un clon limpio, sin `datos/` ni `target/`, compiló, pasó las pruebas y arrancó con `mvn compile exec:exec`, siguiendo sólo el README.

### Registro de Asistencia: Revisión final de la Entrega 2, segunda ronda (20/11/2026)

* **Herramienta utilizada:** Claude Code (Anthropic).

* **Propósito de la consulta:** repetir la auditoría completa de la E2 en la rama `luca-revision-final`, sobre la primera ronda, antes de etiquetar la entrega: requisitos, modelo, concurrencia, MVC y Swing, persistencia, restricciones de diseño, clases de la cátedra (comparadas con el código original), pruebas, compilación desde un clon limpio y documentación, contra el Enunciado de la Parte 2, la Guía (4.2, puntos 5 a 8 y Reglas Operativas) y la Documentación de Destino y Tablero, y después contra el Plan; corregir sólo los hallazgos aprobados, cada uno con una prueba que falle antes de la corrección.

* **Componente o documento afectado:** `IVistaTablero` y `VistaTablero` (objetivo que muestra el tablero), `ControladorCursoMision` (`lanzarMision()`), `ControladorCrearTripulacion` (consejos iniciales); el doble de prueba del tablero en `VistasDePrueba`; pruebas nuevas en `VistaTableroTest`, `ControladorMisionTest` y `ControladorCrearTripulacionTest` (1 cada una); `README.md`, `docs/diseño.md` (secciones 2, 3 y 4), `docs/diagrama_clases.md` y este registro.

* **Resultado aprovechado:**
  * Partimos de código propio y de compañeros: la vista del tablero es de Luca Zuanetti (Paso 6, commit `0e427a4`) y el alta de consejeros con sus consejos iniciales es de Sebastian Barrionuebo (`8b14df4`).
  * Luca Zuanetti hizo el código. Claude propuso lo suyo: la auditoría con los hallazgos reproducidos (durante una M-03 el tablero marcaba el objetivo de la misión anterior, porque M-03 no registra la base en el tablero; una cantidad de consejos iniciales de formato válido pero enorme congelaba la interfaz, porque el costo del alta crece con el cuadrado de la cantidad), las correcciones mínimas y las pruebas deterministas que las demuestran.
  * Corregimos en conjunto los dos errores aprobados: la vista del tablero muestra el objetivo de la misión que sigue (`seguirMision`, indicado por el controlador antes de lanzar el hilo) y el alta rechaza más de `MAXIMO_CONSEJOS_INICIALES` (999) consejos iniciales con un mensaje en la vista.
  * Implementamos todo de la forma más coherente posible con el diseño existente: sin modificar las clases de la cátedra ni el modelo; con el Observer del Paso 4, la concurrencia del Paso 5, el MVC y el hilo de la misión del Paso 6 y el script SQL del Paso 7 intactos; sin `instanceof`, `getClass()` ni condicionales de tipo en el código modificado; sin `volatile`; y con pruebas sin pausas fijas (colas con tiempo límite y espera del hilo de eventos). Las decisiones nuevas están en `docs/diseño.md`, sección 4 (Revisión final).

* **Revisión o modificación realizada por el equipo:** Luca Zuanetti fijó la precedencia de fuentes, revisó la auditoría, eligió qué hallazgos corregir (no se corrigieron los de severidad baja: el formato del Javadoc de las clases de la cátedra, cuyo código es idéntico al original; tres constructores sin uso de los controladores de Nave y Tripulación; y la prueba del universo real, que depende del azar de la cátedra) y revisó el código y la documentación resultantes. La etiqueta `entrega-e2` la crea Luca Zuanetti a mano sobre el commit final de `main`.

* **Forma en que se verificó el resultado:** las 3 pruebas nuevas fallaron antes de la corrección y pasan después. `mvn clean test` pasa con 371 pruebas en verde (antes 368). Las pruebas de concurrencia, controladores, vistas y misiones se repitieron 10 veces sin fallas. Un clon limpio, sin `datos/` ni `target/`, compiló, pasó las pruebas y arrancó con `mvn compile exec:exec`, siguiendo sólo el README.
