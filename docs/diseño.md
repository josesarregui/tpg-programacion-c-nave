# Justificación de Diseño y Modularización — Entrega 1

## 1. Criterio de Modularización: Package by Feature
En lugar de organizar el código por tipos de componentes técnicos (capas horizontales), se optó por una organización orientada a **dominios y características funcionales** (`Package by Feature`).

* **Alta cohesión:** Cada paquete agrupa clases, interfaces y enumeraciones que colaboran directamente para resolver un único subsistema de la nave (SRP - Principio de Responsabilidad Única).
* **Bajo acoplamiento:** Los subsistemas no exponen sus estructuras internas; interactúan mediante interfaces y contratos de métodos bien delimitados.
---

## 2. Mapa y Responsabilidades de Paquetes

### `tripulacion` (E1-04)
* **Contenido:** `Tripulante` (abstracta), `Capitan`, `Consejero`, `Teniente`, `Alferez`, `Tripulacion`, `Cargo` (Enum), `Origen` (Enum).
* **Responsabilidad:** Representa la dotación de la nave. Cada `Tripulante` tiene identidad (id autogenerado), nombre, cargo, planeta de origen y antigüedad. `Tripulacion` agrupa a los tripulantes de una nave y protege la composición mínima exigida por la Ficha de Inicio.
* **Decisiones de diseño:**
    * Cada subclase define la remuneración de su cargo y **calcula su propio adicional por antigüedad** (`calcularAdicionalAntiguedad()`) con el porcentaje que le corresponde.
    * El adicional por consejos es responsabilidad exclusiva de `Consejero`: sus consejos se registran **por período mensual** (`YearMonth`), y redefine `calcularConceptos()` para agregar ese concepto a los de su cargo. Así cada liquidación cobra sólo los consejos de su mes.
    * Se descartó declarar `getConsejos()` en la interfaz `Liquidacion` e implementarlo en `Tripulante` devolviendo 0. Eso violaba el **principio de segregación de interfaces**: obligaba a todos los tripulantes a depender de un método que sólo tiene sentido para el consejero (el mismo caso que el robot obligado a implementar `descansar()`).
    * **Indicación de la cátedra:** el docente Lucas observó esa violación y propuso trasladar el cálculo del adicional por antigüedad y por consejos a las subclases de `Tripulante`, eliminando `ConsejosDecorator`. Por eso el adicional por consejos (el único "concepto particular" de E1-08) lo agrega el propio `Consejero` y no un decorador.
    * `Origen` conoce su subsidio mensual, de modo que no hace falta un `switch` para calcularlo.
* **Contratos:**
    * `Tripulante`: nombre no nulo ni vacío, antigüedad >= 0 y origen informado; si no se cumple, se lanza `TripulanteInvalidoException` con los datos rechazados. Invariante verificado con `assert invariante()`; `incrementarAntiguedad()` verifica su postcondición guardando el valor anterior.
    * `Consejero.registrarConsejo(periodo)`: el período llega desde fuera del modelo, por eso un período nulo se rechaza con `LiquidacionInvalidaException` (concepto `CONSEJOS`) sin registrar nada.
    * `Tripulacion` (invariante): exactamente un/a capitán/a, al menos 1 + 4 integrantes y sin repetidos ni nulos. `incorporar` y `desembarcar` rechazan con `TripulacionInvalidaException` cualquier operación que rompa el invariante, **sin modificar parcialmente** la tripulación. `buscarPorId` lanza `TripulanteInexistenteException` con el id buscado.

### `warp` (Patrón State - E1-02)
* **Contenido:** `MotorWarp` (contexto), `State` (interfaz), `DisponibleState`, `PreparandoSaltoState`, `EnWarpState`, `EnfriamientoState`.
* **Responsabilidad:** Encapsula el ciclo de estados del motor de propulsión. Elimina estructuras condicionales (`switch`/`if`) en la nave; cada clase concreta valida las transiciones permitidas y rechaza las transiciones inválidas.
* **Encapsulamiento del ciclo:** `MotorWarp.setEstado()` es de paquete: sólo los estados lo invocan al completar una transición válida, de modo que ningún cliente (por ejemplo, desde `nave.getMotor()`) puede saltear el ciclo de estados. Cada estado se identifica con su propio `toString()` ("Disponible", "Preparando salto", "En warp", "Enfriamiento").
* **Transiciones inválidas:** se rechazan con `EstadoMotorInvalidoException` y el estado no cambia. La excepción guarda el estado en el que se rechazó la transición (`getEstadoActual()`), para que el invocante pueda registrarlo en la Bitácora (Escenario C).
* **Consulta `estaDisponible()`:** cada estado responde si el motor puede iniciar una nueva operación (sólo Disponible devuelve `true`) y `MotorWarp` delega la pregunta en su estado actual. La usa `Nave.estaListaParaOperar()` para cumplir la Aclaración (R4: antes de actuar, la misión verifica que la nave esté disponible) sin preguntar por la clase del estado ni llenar la nave de condicionales.

### `nave` (E1-01, Patrón Factory - E1-07, E1-09)
* **Contenido:** `Nave` (abstracta), `Exploradora`, `Carguero`, `Combate`, `NaveFactory`, `Recursos`, `TipoNave` (Enum), `TipoRecurso` (Enum).
* **Responsabilidad:** `Nave` es la abstracción común: reúne identidad (id autogenerado), tipo, recursos, tripulación y Motor Warp. No implementa la lógica de sus subsistemas: delega las operaciones sobre recursos en `Recursos` y las transiciones del motor en `MotorWarp` (State), para no convertirse en una clase que concentre toda la lógica (Guía I.3.3).
* **Aplicación del patrón Factory (E1-07):**
    * El cliente pide `new NaveFactory().crearNave(TipoNave.CARGUERO)` y recibe una referencia de tipo `Nave`, sin conocer las clases concretas: "se crea un objeto sin exponer la lógica de la creación al cliente y retorna una referencia al nuevo objeto creado, usando una interfaz común" (apunte de Patrones).
    * Los constructores de `Nave`, `Exploradora`, `Carguero` y `Combate` son **de paquete**: fuera de `modelo.nave` no pueden instanciarse directamente, de modo que toda nave se crea mediante la fábrica.
    * El tipo se pide con el enum `TipoNave` y no con un texto (mismo criterio que `Cargo` y `Origen`): un tipo inexistente no compila, por eso no hace falta validar textos ni existe el caso de una fábrica que devuelva `null`.
    * Cada subclase conoce su **configuración inicial** (Ficha de Inicio, punto 2) en sus constantes: Exploradora 60/80/0, Carguero 100/60/0, Combate 80/100/0 (combustible/energía/desgaste). La fábrica sólo decide qué clase crear. Agregar un tipo requiere una subclase, una constante en `TipoNave` y un caso en la fábrica.
    * Toda nave creada empieza en un estado válido: recursos en rango, motor en Disponible y sin tripulación (se asigna después con `asignarTripulacion`, como indica el Escenario A).
* **Recursos y mantenimiento (E1-09):**
    * `Recursos` no es pública: sólo la usa `Nave`. Los recursos se consultan (`getCombustible()`, `getEnergia()`, `getDesgaste()`, `requiereMantenimiento()`) y se modifican (`cargarCombustible`, `cargarEnergia`, `consumirRecursos`, `realizarMantenimiento`) únicamente a través de la nave, de modo que su estado queda encapsulado (E1-01).
    * `consumirRecursos(combustible, energia, desgaste)` aplica el costo de una operación **en un solo paso**: verifica las tres condiciones antes de modificar cualquier valor, para que una operación rechazada no deje cambios parciales (Ficha de Inicio, punto 2; Escenario B).
    * Los controles de capacidad comparan contra el espacio libre (`cantidad > MAXIMO - actual`) y no contra la suma, porque la suma de dos `int` grandes se desborda y da un número negativo.
    * Requiere mantenimiento con desgaste 80 o más; `realizarMantenimiento()` lleva el desgaste a 0 y puede hacerse en cualquier momento (la Ficha no lo restringe). Una nave que requiere mantenimiento no está lista para operar (`estaListaParaOperar()`), porque el desgaste debe tener consecuencias sobre la capacidad operativa (Guía I.8).
    * `estaListaParaOperar()` exige además tripulación asignada y el motor en Disponible (`getMotor().estaDisponible()`, Aclaración R4).
* **Contratos:**
    * Invariante de `Recursos`: combustible y energía entre 0 y su capacidad máxima (100), desgaste entre 0 y 100. Invariante de `Nave`: id > 0, recursos y motor no nulos.
    * Una cantidad negativa se rechaza con `CantidadInvalidaException`, una carga o un desgaste que supera el máximo con `CapacidadExcedidaException` y un consumo mayor a lo disponible con `RecursoInsuficienteException`. Las tres guardan el recurso (`TipoRecurso`) y los valores involucrados, para que el Asistente pueda registrar el motivo en la Bitácora (Escenario B).
    * Los valores iniciales de los recursos, un tipo de nave nulo en la fábrica y una tripulación nula en `asignarTripulacion` se verifican con aserciones: sólo pueden deberse a un error de programación (los fijan las constantes de cada tipo o el propio código cliente).

### `liquidacion` (Patrón Decorator - E1-08)
* **Contenido:** `Liquidacion` (interfaz, componente), `Decorator` (decorador abstracto), `AntiguedadDecorator`, `OrigenDecorator`, `ConceptoHaber`, `TipoConcepto` (Enum), `LiquidadorHaberes`, `ReciboHaberes`, `LiquidacionTripulacion`.
* **Responsabilidad:** Calcula el haber mensual de cada tripulante y de la tripulación completa, dejando identificable el aporte de cada concepto.
* **Aplicación del patrón:**
    * **Componente:** la interfaz `Liquidacion`. Sólo declara operaciones que todo tripulante puede cumplir (`calcularConceptos`, `calcularHaberTotal`, `calcularAdicionalAntiguedad`, `getOrigen`).
    * **Componente concreto:** `Tripulante` y sus subclases. Aportan la remuneración del cargo y, en el caso del consejero, el adicional por consejos.
    * **Decorador:** `Decorator` implementa la misma interfaz que el componente, para poder sustituirlo (principio de sustitución de Liskov). Mantiene una referencia al objeto decorado, inyectada en el constructor, y propaga los mensajes a ese objeto.
    * **Decoradores concretos:** `AntiguedadDecorator` agrega el adicional por antigüedad que calcula cada cargo. `OrigenDecorator` agrega el subsidio por origen. Pueden combinarse en cualquier orden y se aplican dinámicamente en tiempo de ejecución.
    * **Sin explosión de clases:** 4 cargos x 3 orígenes x antigüedad se resuelven con 4 componentes concretos y 2 decoradores, en lugar de una subclase por combinación.
    * **Armado centralizado:** `LiquidadorHaberes` arma la cadena (`new OrigenDecorator(new AntiguedadDecorator(tripulante))`) y devuelve un `ReciboHaberes`. Agregar un concepto nuevo implica crear un decorador y sumarlo allí, sin modificar los existentes.
    * **Combinaciones inválidas:** la flexibilidad del patrón permite combinaciones que no funcionan, por ejemplo aplicar dos veces el mismo decorador. `Decorator` las rechaza recorriendo la cadena de capas.
* **Resultado identificable:** `ReciboHaberes` contiene el detalle (`ConceptoHaber`: tipo, descripción e importe) y el total. Son datos, no texto formateado, de modo que pueden mostrarse por consola o en Swing sin cambiar el modelo.
* **Contratos:**
    * Un decorador no acepta una liquidación nula ni un concepto repetido: se lanza `LiquidacionInvalidaException` con el concepto rechazado.
    * `ConceptoHaber` verifica con aserciones que el importe no sea negativo: los importes los calcula el propio modelo, por lo que un valor inválido sólo puede deberse a un error de programación.
    * `ReciboHaberes` verifica con `assert invariante()` que el total coincida con la suma del detalle.

### `mision` (Patrón Template Method - E1-06, E1-10)
* **Contenido:** `Mision` (abstracta), `MisionIntercepcion` (Intercepción y asistencia), `MisionRecoleccion` (Recolección), `MisionRetorno` (Retorno seguro), `InformeMision`, `EtapaMision` (Enum).
* **Responsabilidad:** Modela el ciclo de una misión (`preparar` -> `ejecutar` -> `evaluar` -> `cerrar`) y produce su informe al cerrarse.
* **Template Method (apunte 04-02):**
    * `realizar()` es el **método plantilla**: es `final` y llama a los cuatro pasos siempre en el mismo orden. Los pasos son privados y contienen lo común a toda misión: verificar que la nave esté lista y tenga recursos, consumir el costo en un solo paso, ordenar el salto si fue exitosa y armar el informe.
    * Cada misión concreta redefine sólo sus **operaciones primitivas**: `getEnergiaAdicional()`, `realizarAccion()`, `objetivoCumplido()` y `getCondicionDeExito()` (Ficha de Inicio, puntos 4 y 5). Agregar una misión nueva no requiere modificar las existentes.
* **Costos (Ficha de Inicio, punto 5):** toda misión consume 4 de combustible y suma 4 de desgaste (constantes de `Mision`). La "energía adicional" se toma como costo, porque la sección se titula "Costos de la misión simplificada" y la Tabla de Recursos (IV, punto 4) la llama "Costo adicional" de la acción final: M-01 y M-02 consumen 5 de energía y M-03, 0.
* **Evolución en la E2:** la Tabla de Recursos (IV, punto 1) indica que "todas las misiones utilizan las mismas reglas de consumo" (escaneos, movimientos y recálculos) y que sólo cambia el costo de la acción final de cada misión. Por eso el cálculo del consumo seguirá en los pasos comunes de `Mision` y la energía adicional seguirá siendo una operación primitiva de cada subclase: se amplían las mismas clases, como pide E1-06, sin cambiar la estructura del patrón.
* **Condición de éxito (Ficha de Inicio, punto 4):** M-01 y M-02 son exitosas si realizaron su acción. M-03 es exitosa si la nave queda lista para operar: si el desgaste de la misión la lleva a 80 o más, no es exitosa.
* **Relación con el asistente (Aclaración, R2 y R4):** la misión no conoce a la nave; toda consulta u orden pasa por el `AsistenteComando` al que fue encomendada, que registra cada paso en su Bitácora. Una misión exitosa ordena preparar el salto y saltar.
* **Contratos:**
    * `EtapaMision` registra en qué paso está la misión. Cada paso verifica con `assert` que el anterior se haya hecho (Observaciones: no se ejecuta sin preparación previa ni se cierra sin resultado e informe). Una misión se realiza una sola vez y se encomienda a un solo asistente (R4).
    * Invariante de `Mision`: código y nombre no vacíos, y tiene informe si y sólo si está cerrada.
    * Si la nave no está lista (`NaveNoDisponibleException`) o no alcanzan los recursos (`RecursoInsuficienteException`), la misión se rechaza en `preparar`, **antes de modificar nada**: no hay cambios parciales y la misión sigue en CREADA, por lo que puede reintentarse (Escenario B).
* **`InformeMision` (E1-10):** inmutable (atributos `final`, sin setters, acciones de sólo lectura) y con constructor de paquete: sólo lo crea la misión. Contiene misión, resultado, acciones principales, recursos consumidos, estado final de la nave y observaciones, como datos y no como texto formateado.

### `bitacora` (E1-05)
* **Contenido:** `Bitacora`, `Evento`, `TipoEvento` (Enum).
* **Responsabilidad:** Registro cronológico de sucesos, consumos y cambios de estado para auditoría y reconstrucción de acontecimientos.
* **Decisiones de diseño:**
    * `Bitacora` guarda los eventos en una `List` (`ArrayList`) en orden de llegada y los devuelve con `Collections.unmodifiableList`: se consultan en orden temporal y nadie puede alterarlos desde afuera.
    * `Evento` es inmutable (atributos `final`, sin modificadores): un evento registrado no puede modificarse (Aclaración, R5). Guarda cuándo sucedió (`LocalDateTime`), su tipo (`TipoEvento`: MOTOR, MISION, RECURSO, ERROR, SISTEMA, RELEVANTE) y qué pasó.
* **Contratos:** la Bitácora no acepta eventos nulos ni vacíos (Observaciones): `registrarEvento(null)` y un `Evento` sin fecha, sin tipo o con descripción vacía se rechazan con `IllegalArgumentException`. Los eventos los crea el propio sistema, por lo que un dato faltante es un error de programación (apunte de Excepciones: las `RuntimeException` corresponden a errores del programador). Invariante de `Bitacora`: no contiene nulos; invariante de `Evento`: datos completos.

### `asistente` (E1-03) — versión mínima
* **Contenido:** `AsistenteComando`.
* **Responsabilidad:** Opera una sola nave (Aclaración, R2) y lleva su Bitácora (R5). Recibe órdenes (asignar tripulación, cargar combustible o energía, mantenimiento, consumir recursos, preparar salto y saltar), las delega en la nave o en el Motor Warp sin implementar su comportamiento interno, y registra el resultado. Se le encomiendan misiones y las ejecuta.
* **Manejo de errores:** cuando una orden o una misión se rechaza, registra el motivo en la Bitácora (tipo ERROR) y propaga la excepción al invocante (`App` o, en la E2, un controlador). No captura excepciones para ignorarlas (apunte de Excepciones).
* **Salto (Aclaración, R3):** `saltar()` lleva el motor de Preparando salto a En warp y, al terminar el salto, a Disponible pasando por Enfriamiento, tal como recorre el Escenario C. Así la nave queda lista para la misión siguiente.
* **Transiciones inválidas (Escenario C):** si una orden al motor no es válida en su estado actual, el asistente registra el rechazo en la Bitácora con el estado en que ocurrió y propaga `EstadoMotorInvalidoException`; el motor no cambia.
* **Pendiente:** el Universo / centro de control (R1) y el resto de E1-03 quedan fuera de esta versión.

### `excepcion`
* **Contenido:** Excepciones de negocio (`EstadoMotorInvalidoException`, `TripulanteInvalidoException`, `TripulacionInvalidaException`, `TripulanteInexistenteException`, `LiquidacionInvalidaException`, `CantidadInvalidaException`, `CapacidadExcedidaException`, `RecursoInsuficienteException`, `NaveNoDisponibleException`).
* **Responsabilidad:** Soporte del diseño por contrato, interrumpiendo operaciones no autorizadas e impidiendo que los recursos caigan en estados inconsistentes.
* **Criterio adoptado en Tripulación, Liquidación, Nave, Misión y Asistente:**
    * **Excepciones propias (comprobadas, extienden `Exception`):** se usan cuando el problema no puede resolverse dentro del método y debe propagarse al invocante (el Asistente o, en la E2, un controlador). Cada una guarda el dato que provocó el error, con su getter, para que la zona de recuperación (`catch`) pueda decidir qué hacer. Se declaran con `throws` porque forman parte del contrato del método.
    * **Aserciones (`assert`):** se usan para invariantes de clase (`private boolean invariante()` comprobado al final de constructores y métodos modificadores), postcondiciones (guardando el valor anterior en una variable local, por ejemplo `cantidadAnterior`), invariantes de ciclo y precondiciones cuya violación sólo puede deberse a un error de programación. Se activan con `-ea`; Maven Surefire las activa por defecto al ejecutar `mvn test`.
    * **Importes de los conceptos:** se verifican con aserciones (`ConceptoHaber`, `Decorator`) y no con excepciones, porque los calcula el propio modelo a partir de datos ya validados (antigüedad >= 0, origen y cargo informados). Un importe negativo sólo puede deberse a un error de programación; el enunciado reserva las excepciones para errores que deban propagarse al coordinador o al cliente.
* **Excepción del Motor Warp:** `EstadoMotorInvalidoException` extiende `IllegalStateException` (no comprobada), por lo que no sigue todavía el criterio anterior (ver sección 4).
* **Excepción de Misión:** `NaveNoDisponibleException` (comprobada) guarda la misión rechazada y por qué la nave no está lista (tripulación, mantenimiento y estado del motor), para que el invocante sepa qué resolver.
* **Errores imposibles por diseño:** en `Mision.ejecutar()`, `CantidadInvalidaException` y `CapacidadExcedidaException` no pueden ocurrir (cantidades constantes y desgaste ya verificado en `preparar`). Si ocurrieran sería un error de programación, por eso se transforman en `IllegalStateException` (apunte de Excepciones: las `RuntimeException` corresponden a errores del programador) en lugar de declararse en el contrato de la misión.

### `app`
* **Contenido:** `App`.
* **Responsabilidad:** Programa de demostración que simula al usuario (E1-03): arma una tripulación válida, liquida sus haberes del mes mostrando el detalle de cada concepto y muestra casos de rechazo. También crea los tres tipos de nave mediante la fábrica, muestra los recursos antes y después de cargas, consumos y mantenimiento, y reproduce los Escenarios B y D. Luego recorre el ciclo válido del Motor Warp y muestra el rechazo de una transición inválida (Escenario C). Por último, el asistente ejecuta M-01, M-02 y M-03 mostrando informe, recursos finales y Bitácora (Escenario A), y una misión rechazada por combustible insuficiente sin cambios parciales (Escenario B). En el Escenario C también muestra una transición inválida ordenada a través del asistente, que queda registrada en la Bitácora. Es la única clase que escribe en consola; el modelo sólo devuelve datos (`ReciboHaberes`, `ConceptoHaber`), de modo que en la E2 puede reemplazarse por una vista Swing sin modificar el modelo.

---

## 3. Estrategia de Verificación y Pruebas Unitarias (JUnit 5)

Para validar el núcleo de dominio de manera automatizada y reproducible mediante Maven (`mvn test`), se implementaron suites de pruebas unitarias bajo **JUnit 5** (`Jupiter`) en el directorio `src/test/java`.

### Arquitectura de una Clase de Prueba (`MotorWarpTest`)

La suite de pruebas del subsistema de propulsión sigue las directivas del **Escenario C** exigido por la cátedra, estructurándose a través de tres componentes técnicos esenciales:

#### A. Aislamiento y Dependencias entre Paquetes (Imports de Dominio)
* **`import excepcion.EstadoMotorInvalidoException;`**  
  Como las pruebas residen en el paquete `modelo.warp`, requieren importar explícitamente las excepciones del paquete hermano `excepcion`. Esto corrobora el bajo acoplamiento: el modelo y sus pruebas interactúan con los tipos de error mediante contratos públicos bien definidos.

#### B. Ciclo de Vida y Autodocumentación (Anotaciones JUnit 5)
* **`@Test`:** Declara formalmente que el método es una unidad de prueba atómica que el ejecutor de Maven debe correr y evaluar de forma independiente.
* **`@BeforeEach` (Configuración de Fixture):** Ejecuta un método de preparación previo a cada caso de prueba individual. En `MotorWarpTest`, inicializa una instancia nueva y limpia (`this.motor = new MotorWarp();`) antes de cada `@Test`, garantizando que las mutaciones de estado de una prueba no generen efectos colaterales sobre las demás.
* **`@DisplayName`:** Etiqueta cada prueba con una descripción formal y legible (por ejemplo, *«Escenario C: Recorrer la secuencia válida completa de estados»*). Permite que los reportes de ejecución reflejen directamente los requerimientos funcionales del TP sin depender únicamente del identificador del método.

#### C. Aserciones y Legibilidad (Importaciones Estáticas)
* **`import static org.junit.jupiter.api.Assertions.*;`**  
  El uso de importaciones estáticas permite incorporar los métodos auxiliares de validación directamente en el espacio de nombres de la clase de prueba, prescindiendo del prefijo redundante `Assertions.`.
    * **`assertInstanceOf(Clase.class, objeto);`**  
      Verifica polimórficamente que el estado actual del motor sea una instancia concreta del tipo esperado, validando el correcto funcionamiento del patrón State tras una transición.
    * **`assertThrows(Excepcion.class, ejecutable);`**  
      Garantiza el cumplimiento del Diseño por Contrato y la regla de no admitir transiciones silenciosas: verifica que el motor interrumpa la ejecución y arroje la excepción correspondiente ante una maniobra inválida, dejando intacto el estado interno de la nave.

### Pruebas del módulo Nave (`NaveFactoryTest`, `NaveTest`, `RecursosTest`)
Siguen la misma estructura (`@BeforeEach`, `@DisplayName`, `assertThrows`) y son la evidencia de E1-01, E1-07 y E1-09: creación de los tres tipos mediante la fábrica con los valores de la Ficha de Inicio, cargas, consumos y mantenimiento, capacidad operativa según el mantenimiento y el estado del motor, y los Escenarios B y D. En cada rechazo se verifica, además de la excepción y los datos que guarda, que **el estado anterior de la nave se conserva**.

### Pruebas del Asistente (`AsistenteComandoTest`)
Evidencia de E1-03 y E1-05: el asistente registra las operaciones sobre recursos y los cambios del motor, y ante un rechazo (transición inválida del Escenario C o carga que excede la capacidad del Escenario D) registra el motivo, conserva el estado anterior y propaga la excepción.

### Pruebas del módulo Misión (`MisionTest`)
Evidencia de E1-06, E1-10 y los Escenarios A y B: ciclo completo de M-01, M-02 y M-03 con los costos de la Ficha de Inicio, orden de los cuatro pasos, contenido del informe, salto y vuelta a Disponible, registro en la Bitácora, M-03 no exitosa cuando la nave queda requiriendo mantenimiento, rechazos por combustible o energía insuficientes y por nave no disponible (sin tripulación o con mantenimiento pendiente) sin cambios parciales, reintento después de cargar combustible, y rechazos por contrato (misión realizada dos veces, encomendada a dos asistentes o sin asistente).

## 4. Mejoras y Extensiones Pendientes
* **excepcion (EstadoMotorInvalidoException):** evaluar con el equipo si pasa a ser comprobada (extender `Exception`), como el resto de las excepciones propias. Implica declarar `throws` en `State`, en los cuatro estados, en `MotorWarp` y en el código que lo invoque.
* **asistente y universo (E1-03, Aclaración R1):** el `AsistenteComando` actual es una versión mínima para ejecutar misiones; falta el Universo / centro de control.
* **Aclaración R3 (Motor Warp):** la Aclaración indica que, mientras no se modele el paso del tiempo, una nave en Salto warp vuelve a Disponible al terminar el salto y Enfriamiento existe pero todavía no se usa; el código actual sigue la secuencia del Escenario C de la Guía (En warp → Enfriamiento → Disponible). Acordar en el equipo cuál se aplica. Para las misiones el resultado es el mismo (la nave termina en Disponible) y la secuencia está concentrada en `AsistenteComando.saltar()`, por lo que un cambio sólo afectaría a ese método y a los estados del motor.