# Justificación de Diseño y Modularización — Entrega 1

## 1. Criterio de Modularización: Package by Feature
En lugar de organizar el código por tipos de componentes técnicos (capas horizontales), se optó por una organización orientada a **dominios y características funcionales** (`Package by Feature`).

* **Alta cohesión:** Cada paquete agrupa clases, interfaces y enumeraciones que colaboran directamente para resolver un único subsistema de la nave (SRP - Principio de Responsabilidad Única).
* **Bajo acoplamiento:** Los subsistemas no exponen sus estructuras internas; interactúan mediante interfaces y contratos de métodos bien delimitados.
---

## 2. Mapa y Responsabilidades de Paquetes

### `tripulacion`
* **Contenido:** `Tripulante`, `Cargo` (Enum), `Origen` (Enum).
* **Responsabilidad:** Representa la dotación de la nave. Es un módulo atómico e independiente que no conoce a la nave ni a los motores, permitiendo su instanciación y testeo unitario aislado.

### `warp` (Patrón State - E1-02)
* **Contenido:** `MotorWarp`, `EstadoWarp` (interfaz), `EstadoDisponible`, `EstadoPreparandoSalto`, `EstadoEnWarp`, `EstadoEnfriamiento`.
* **Responsabilidad:** Encapsula el ciclo de estados del motor de propulsión. Elimina estructuras condicionales (`switch`/`if`) en la nave; cada clase concreta valida las transiciones permitidas y rechaza las transiciones inválidas.

### `nave` (Patrón Factory - E1-07)
* **Contenido:** `Nave` (abstracta), `NaveExploradora`, `NaveCarguero`, `NaveCombate`, `TipoNave` (Enum), `NaveFactory`.
* **Responsabilidad:** Administra el estado global de la nave, sus recursos (combustible, energía, desgaste) y sus componentes (`MotorWarp`, lista de `Tripulante`). La fábrica centraliza la creación garantizando invariantes iniciales según la ficha técnica sin que el cliente use `new` sobre clases concretas.

### `liquidacion` (Patrón Decorator - E1-08)
* **Contenido:** `ComponenteSueldo` (interfaz), `SueldoBase`, `SueldoDecorator` (abstracto), `AntiguedadDecorator`, `OrigenDecorator`.
* **Responsabilidad:** Calcula la remuneración mensual de los tripulantes mediante composición dinámica recursiva. Evita la proliferación de subclases rígidas combinando libremente complementos sobre el haber base.

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
* **Contenido:** Excepciones de negocio (`EstadoMotorInvalidoException`, `RecursosInsuficientesException`, `TripulacionInvalidaException`).
* **Responsabilidad:** Soporte del diseño por contrato, interrumpiendo operaciones no autorizadas e impidiendo que los recursos caigan en estados inconsistentes.

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