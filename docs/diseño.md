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
    * `Origen` conoce su subsidio mensual, de modo que no hace falta un `switch` para calcularlo.
* **Contratos:**
    * `Tripulante`: nombre no nulo ni vacío, antigüedad >= 0 y origen informado; si no se cumple, se lanza `TripulanteInvalidoException` con los datos rechazados. Invariante verificado con `assert invariante()`; `incrementarAntiguedad()` verifica su postcondición guardando el valor anterior.
    * `Tripulacion` (invariante): exactamente un/a capitán/a, al menos 1 + 4 integrantes y sin repetidos ni nulos. `incorporar` y `desembarcar` rechazan con `TripulacionInvalidaException` cualquier operación que rompa el invariante, **sin modificar parcialmente** la tripulación. `buscarPorId` lanza `TripulanteInexistenteException` con el id buscado.

### `warp` (Patrón State - E1-02)
* **Contenido:** `MotorWarp`, `EstadoWarp` (interfaz), `EstadoDisponible`, `EstadoPreparandoSalto`, `EstadoEnWarp`, `EstadoEnfriamiento`.
* **Responsabilidad:** Encapsula el ciclo de estados del motor de propulsión. Elimina estructuras condicionales (`switch`/`if`) en la nave; cada clase concreta valida las transiciones permitidas y rechaza las transiciones inválidas.

### `nave` (Patrón Factory - E1-07)
* **Contenido:** `Nave` (abstracta), `NaveExploradora`, `NaveCarguero`, `NaveCombate`, `TipoNave` (Enum), `NaveFactory`.
* **Responsabilidad:** Administra el estado global de la nave, sus recursos (combustible, energía, desgaste) y sus componentes (`MotorWarp`, lista de `Tripulante`). La fábrica centraliza la creación garantizando invariantes iniciales según la ficha técnica sin que el cliente use `new` sobre clases concretas.

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

### `mision` (Patrón Template Method - E1-06)
* **Contenido:** `Mision` (abstracta), `MisionIntercepcion` (M-01), `MisionRecoleccion` (M-02), `MisionRetorno` (M-03), `InformeMision`.
* **Responsabilidad:** Modela el ciclo de vida inalterable de una misión (`preparar` -> `ejecutar` -> `evaluar` -> `cerrar`). Las subclases solo implementan el comportamiento particular de su objetivo, emitiendo un informe estructurado al cierre.

### `bitacora`
* **Contenido:** `Bitacora`, `Evento`.
* **Responsabilidad:** Registro cronológico de sucesos, consumos y cambios de estado para auditoría y reconstrucción de acontecimientos.

### `asistente`
* **Contenido:** `AsistenteComando`.
* **Responsabilidad:** Coordinador de operaciones de alto nivel. Asiste a la tripulación interactuando con la nave y despachando misiones sin absorber la lógica interna de los subsistemas (evitando actuar como una clase Dios).

### `excepcion`
* **Contenido:** Excepciones de negocio (`EstadoMotorInvalidoException`, `TripulanteInvalidoException`, `TripulacionInvalidaException`, `TripulanteInexistenteException`, `LiquidacionInvalidaException`).
* **Responsabilidad:** Soporte del diseño por contrato, interrumpiendo operaciones no autorizadas e impidiendo que los recursos caigan en estados inconsistentes.
* **Criterio adoptado en Tripulación y Liquidación:**
    * **Excepciones propias (comprobadas, extienden `Exception`):** se usan cuando el problema no puede resolverse dentro del método y debe propagarse al invocante (el Asistente o, en la E2, un controlador). Cada una guarda el dato que provocó el error, con su getter, para que la zona de recuperación (`catch`) pueda decidir qué hacer. Se declaran con `throws` porque forman parte del contrato del método.
    * **Aserciones (`assert`):** se usan para invariantes de clase (`private boolean invariante()` comprobado al final de constructores y métodos modificadores), postcondiciones (guardando el valor anterior en una variable local, por ejemplo `cantidadAnterior`), invariantes de ciclo y precondiciones cuya violación sólo puede deberse a un error de programación. Se activan con `-ea`; Maven Surefire las activa por defecto al ejecutar `mvn test`.

---

## 3. Estrategia de Verificación y Pruebas Unitarias (JUnit 5)

Para validar el núcleo de dominio de manera automatizada y reproducible mediante Maven (`mvn test`), se implementaron suites de pruebas unitarias bajo **JUnit 5** (`Jupiter`) en el directorio `src/test/java`.

### Arquitectura de una Clase de Prueba (`MotorWarpTest`)

La suite de pruebas del subsistema de propulsión sigue las directivas del **Escenario C** exigido por la cátedra, estructurándose a través de tres componentes técnicos esenciales[cite: 4, 9]:

#### A. Aislamiento y Dependencias entre Paquetes (Imports de Dominio)
* **`import excepcion.EstadoMotorInvalidoException;`**  
  Como las pruebas residen en el paquete `modelo.warp`, requieren importar explícitamente las excepciones del paquete hermano `excepcion`[cite: 4, 10]. Esto corrobora el bajo acoplamiento: el modelo y sus pruebas interactúan con los tipos de error mediante contratos públicos bien definidos[cite: 4, 7].

#### B. Ciclo de Vida y Autodocumentación (Anotaciones JUnit 5)
* **`@Test`:** Declara formalmente que el método es una unidad de prueba atómica que el ejecutor de Maven debe correr y evaluar de forma independiente[cite: 4, 5].
* **`@BeforeEach` (Configuración de Fixture):** Ejecuta un método de preparación previo a cada caso de prueba individual[cite: 6]. En `MotorWarpTest`, inicializa una instancia nueva y limpia (`this.motor = new MotorWarp();`) antes de cada `@Test`, garantizando que las mutaciones de estado de una prueba no generen efectos colaterales sobre las demás[cite: 4, 6].
* **`@DisplayName`:** Etiqueta cada prueba con una descripción formal y legible (por ejemplo, *«Escenario C: Recorrer la secuencia válida completa de estados»*)[cite: 4, 5]. Permite que los reportes de ejecución reflejen directamente los requerimientos funcionales del TP sin depender únicamente del identificador del método[cite: 4, 8].

#### C. Aserciones y Legibilidad (Importaciones Estáticas)
* **`import static org.junit.jupiter.api.Assertions.*;`**  
  El uso de importaciones estáticas permite incorporar los métodos auxiliares de validación directamente en el espacio de nombres de la clase de prueba, prescindiendo del prefijo redundante `Assertions.`[cite: 4, 5].
    * **`assertInstanceOf(Clase.class, objeto);`**  
      Verifica polimórficamente que el estado actual del motor sea una instancia concreta del tipo esperado, validando el correcto funcionamiento del patrón State tras una transición[cite: 4, 8].
    * **`assertThrows(Excepcion.class, ejecutable);`**  
      Garantiza el cumplimiento del Diseño por Contrato y la regla de no admitir transiciones silenciosas: verifica que el motor interrumpa la ejecución y arroje la excepción correspondiente ante una maniobra inválida, dejando intacto el estado interno de la nave[cite: 4, 9].

## 4. Mejoras y Extensiones Pendientes
* **modelo.bitacora (Evento):** Incorporar validación defensiva de precondiciones (`if (fecha == null)` o `Objects.requireNonNull`) para blindar la inmutabilidad antes de la entrega final.
* **modelo.warp (DisponibleState, EnfriamientoState, EnWarpState, MotorWarp, PreparandoSaltoState):** Incorporar validación defensiva de precondiciones (`if (fecha == null)` o `Objects.requireNonNull`) para blindar la inmutabilidad antes de la entrega final.