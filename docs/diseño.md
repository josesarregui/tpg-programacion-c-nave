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
* **Contenido:** `ComponenteSueldo` (interfaz), `SueldoBase`, `SueldoDecorator` (abstracto), `AntiguedadDecorator`, `OrigenDecorator`, `ConsejosDecorator`.
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