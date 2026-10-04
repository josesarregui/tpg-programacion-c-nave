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

### `mision` (Patrón Template Method - E1-06) — pendiente de implementación
* **Contenido previsto:** `Mision` (abstracta), `MisionIntercepcion` (M-01), `MisionRecoleccion` (M-02), `MisionRetorno` (M-03), `InformeMision`.
* **Responsabilidad:** Modela el ciclo de vida inalterable de una misión (`preparar` -> `ejecutar` -> `evaluar` -> `cerrar`). Las subclases solo implementan el comportamiento particular de su objetivo, emitiendo un informe estructurado al cierre.

### `bitacora` (E1-05)
* **Contenido:** `Bitacora`, `Evento`, `TipoEvento` (Enum).
* **Responsabilidad:** Registro cronológico de sucesos, consumos y cambios de estado para auditoría y reconstrucción de acontecimientos.
* **Decisiones de diseño:**
    * `Bitacora` guarda los eventos en una `List` (`ArrayList`) en orden de llegada y los devuelve con `Collections.unmodifiableList`: se consultan en orden temporal y nadie puede alterarlos desde afuera.
    * `Evento` es inmutable (atributos `final`, sin modificadores): un evento registrado no puede modificarse (Aclaración, R5). Guarda cuándo sucedió (`LocalDateTime`), su tipo (`TipoEvento`: MOTOR, MISION, RECURSO, ERROR, SISTEMA, RELEVANTE) y qué pasó.
* **Contratos:** la Bitácora no acepta eventos nulos ni vacíos (Observaciones): `registrarEvento(null)` y un `Evento` sin fecha, sin tipo o con descripción vacía se rechazan con `IllegalArgumentException`. Los eventos los crea el propio sistema, por lo que un dato faltante es un error de programación (apunte de Excepciones: las `RuntimeException` corresponden a errores del programador). Invariante de `Bitacora`: no contiene nulos; invariante de `Evento`: datos completos.

### `asistente` — pendiente de implementación
* **Contenido previsto:** `AsistenteComando`.
* **Responsabilidad:** Coordinador de operaciones de alto nivel. Asiste a la tripulación interactuando con la nave y despachando misiones sin absorber la lógica interna de los subsistemas (evitando actuar como una clase Dios).

### `excepcion`
* **Contenido:** Excepciones de negocio (`EstadoMotorInvalidoException`, `TripulanteInvalidoException`, `TripulacionInvalidaException`, `TripulanteInexistenteException`, `LiquidacionInvalidaException`, `CantidadInvalidaException`, `CapacidadExcedidaException`, `RecursoInsuficienteException`).
* **Responsabilidad:** Soporte del diseño por contrato, interrumpiendo operaciones no autorizadas e impidiendo que los recursos caigan en estados inconsistentes.
* **Criterio adoptado en Tripulación, Liquidación y Nave:**
    * **Excepciones propias (comprobadas, extienden `Exception`):** se usan cuando el problema no puede resolverse dentro del método y debe propagarse al invocante (el Asistente o, en la E2, un controlador). Cada una guarda el dato que provocó el error, con su getter, para que la zona de recuperación (`catch`) pueda decidir qué hacer. Se declaran con `throws` porque forman parte del contrato del método.
    * **Aserciones (`assert`):** se usan para invariantes de clase (`private boolean invariante()` comprobado al final de constructores y métodos modificadores), postcondiciones (guardando el valor anterior en una variable local, por ejemplo `cantidadAnterior`), invariantes de ciclo y precondiciones cuya violación sólo puede deberse a un error de programación. Se activan con `-ea`; Maven Surefire las activa por defecto al ejecutar `mvn test`.
    * **Importes de los conceptos:** se verifican con aserciones (`ConceptoHaber`, `Decorator`) y no con excepciones, porque los calcula el propio modelo a partir de datos ya validados (antigüedad >= 0, origen y cargo informados). Un importe negativo sólo puede deberse a un error de programación; el enunciado reserva las excepciones para errores que deban propagarse al coordinador o al cliente.
* **Excepción del Motor Warp:** `EstadoMotorInvalidoException` extiende `IllegalStateException` (no comprobada), por lo que no sigue todavía el criterio anterior (ver sección 4).

### `app`
* **Contenido:** `App`.
* **Responsabilidad:** Programa de demostración que simula al usuario (E1-03): arma una tripulación válida, liquida sus haberes del mes mostrando el detalle de cada concepto y muestra casos de rechazo. También crea los tres tipos de nave mediante la fábrica, muestra los recursos antes y después de cargas, consumos y mantenimiento, y reproduce los Escenarios B y D. Por último recorre el ciclo válido del Motor Warp y muestra el rechazo de una transición inválida (Escenario C). Es la única clase que escribe en consola; el modelo sólo devuelve datos (`ReciboHaberes`, `ConceptoHaber`), de modo que en la E2 puede reemplazarse por una vista Swing sin modificar el modelo.

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

## 4. Mejoras y Extensiones Pendientes
* **excepcion (EstadoMotorInvalidoException):** evaluar con el equipo si pasa a ser comprobada (extender `Exception`), como el resto de las excepciones propias. Implica declarar `throws` en `State`, en los cuatro estados, en `MotorWarp` y en el código que lo invoque.
* **mision y asistente (E1-03, E1-06, E1-10):** implementar `Mision` (Template Method) con M-01, M-02 y M-03, `InformeMision` y `AsistenteComando`. Las misiones deben aplicar su costo con `Nave.consumirRecursos(...)` y verificar antes `Nave.estaListaParaOperar()`; el Asistente debe capturar las excepciones de recursos y del motor y registrar el motivo en la Bitácora (Escenarios B y C).
* **Aclaración R3 (Motor Warp):** la Aclaración indica que, mientras no se modele el paso del tiempo, una nave en Salto warp vuelve a Disponible al terminar el salto y Enfriamiento existe pero todavía no se usa; el código actual sigue la secuencia del Escenario C de la Guía (En warp → Enfriamiento → Disponible). Acordar en el equipo cuál se aplica.