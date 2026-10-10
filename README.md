# tpg-programacion-c-nave

Software embarcado de una nave interestelar tripulada — TPG 2026, Programación C. Entrega 1.

## Requisitos de ejecución
* JDK 17 o superior (el proyecto se compila para Java 17; también se probó con JDK 25). El comando `java` debe estar en el PATH.
* Maven 3.8 o superior.

## Configuración local
No requiere configuración adicional ni base de datos. Si Maven no está en el PATH, puede usarse el que trae IntelliJ IDEA
(`<carpeta de IntelliJ>/plugins/maven-plugin/lib/maven3/bin/mvn`) o la ventana *Maven* del IDE.

## Compilación
```
mvn compile
```

## Ejecución (forma de iniciar el sistema)
El programa principal `app.App` simula al usuario (Aclaración, R6) y recorre los cuatro escenarios de la Ficha de Inicio.
Cualquiera de estas dos formas lo ejecuta con las aserciones activas (`-ea`: invariantes, precondiciones y postcondiciones):
```
mvn compile exec:exec
```
```
java -ea -cp target/classes app.App
```
Desde IntelliJ IDEA: abrir la carpeta como proyecto Maven, ejecutar `app.App` y agregar `-ea` en *Run > Edit Configurations > VM options*.

Qué muestra, en orden: la liquidación de haberes de una tripulación; el alta de una nave de cada tipo en el centro de control;
el **Escenario A** (M-01, M-02 y M-03 con recursos antes y después, informe y Bitácora de cada misión); las operaciones sobre
recursos y mantenimiento con el **Escenario D**; el **Escenario B** y el **Escenario C**.

## Verificación
```
mvn test
```
Ejecuta las 120 pruebas JUnit 5 de `src/test/java` (Surefire activa las aserciones). Todas deben terminar en verde.

### Matriz de verificación
| Caso | Qué se verifica (resultado esperado) | Prueba automática | En `App` |
|---|---|---|---|
| E1-01, E1-07 (Factory) | La fábrica crea los tres tipos con la Ficha de Inicio: Exploradora 60/80/0, Carguero 100/60/0, Combate 80/100/0 (combustible/energía/desgaste), motor Disponible, sin tripulación y con id único. | `NaveFactoryTest` | "NAVES REGISTRADAS" |
| E1-07: sin instanciación directa | Los constructores de las naves son de paquete: `new Exploradora()` fuera de `modelo.nave` **no compila**. El cliente sólo usa `NaveFactory.crearNave(TipoNave)`. | Verificable en el código | `darDeAlta()` |
| E1-02, Escenario C (State) | Disponible → Preparando salto → En warp → Enfriamiento → Disponible. Una transición inválida lanza `EstadoMotorInvalidoException`, el estado no cambia y el rechazo queda en la Bitácora. | `MotorWarpTest`, `AsistenteComandoTest` | "ESCENARIO C" |
| E1-03 | El asistente delega en la nave y en el motor, registra cada resultado y cada rechazo, y propaga la excepción. | `AsistenteComandoTest` | Todas las secciones |
| E1-04 | Tripulante con id, cargo, origen y antigüedad ≥ 0; tripulación con un/a capitán/a + 4. Los rechazos no modifican la tripulación. | `TripulacionTest` | "LIQUIDACIÓN DE HABERES" |
| E1-05 | Eventos en orden temporal e inmutables; se rechazan los nulos, vacíos o fuera de orden. | `BitacoraTest`, `MisionTest` | "BITÁCORA" |
| E1-06, Escenario A (Template Method) | M-01, M-02 y M-03 hacen preparar → ejecutar → evaluar → cerrar. Cada una consume 4 de combustible y 4 de desgaste, más 5/5/0 de energía; si es exitosa, la nave salta y vuelve a Disponible. Exploradora: 60/80/0 → 56/75/4 → 52/70/8 → 48/70/12. | `MisionTest` | "ESCENARIO A" |
| E1-08 (Decorator) | Haber de los 4 cargos con los 3 orígenes, dos decoradores en cualquier orden, consejos del período y detalle por concepto. Ej.: capitán terrícola con 3 años = 1000 + 600 + 20 = 1620 PG. Se rechazan conceptos nulos o repetidos. | `LiquidacionTest` | "LIQUIDACIÓN DE HABERES" |
| E1-09, Escenario D | Carga hasta la capacidad (100); con desgaste ≥ 80 requiere mantenimiento y no opera; el mantenimiento lo lleva a 0. Una carga que excede la capacidad se rechaza y la nave conserva su estado. | `RecursosTest`, `NaveTest`, `AsistenteComandoTest` | "OPERACIONES SOBRE RECURSOS", "ESCENARIO D" |
| Escenario B | Con combustible 3 (< 4), la misión se rechaza con `RecursoInsuficienteException`: la nave no cambia, el motivo queda en la Bitácora y la misión sigue pendiente. | `MisionTest`, `AsistenteComandoTest`, `RecursosTest` | "ESCENARIO B" |
| E1-10 | El informe trae misión, resultado, acciones, recursos consumidos, estado final de la nave y observaciones. | `MisionTest` | "INFORME M-0x" |
| Contratos | Una precondición o invariante incumplida se detecta con `assert` (`AssertionError`): misión realizada dos veces o sin pasar por su asistente, tripulación nula, importe negativo, estado nulo del motor. | Pruebas "Rechazo por contrato" y "Aserción" | — |
| Aclaración R1, R2 y Pedidos del diseño | El centro registra y devuelve naves, rechaza una repetida o inexistente sin cambiar, usa una nave a la vez y admite otra variante de asistente sin modificarse. | `CentroDeControlTest` | "CASOS DE RECHAZO DEL CENTRO DE CONTROL" |

## Entrega
La Entrega 1 se identifica con la etiqueta Git `entrega-e1`.

## Documentación
* [docs/diseño.md](docs/diseño.md): responsabilidades de cada paquete, patrones (Factory, State, Template Method y Decorator), contratos, manejo de errores y decisiones de interpretación adoptadas ante ambigüedades de la consigna (sección 4).
* [docs/diagrama_clases.md](docs/diagrama_clases.md): diagrama de clases.
* [docs/uso-ia.md](docs/uso-ia.md): registro del uso de inteligencia artificial.

El código está organizado por subsistema en `modelo` (`tripulacion`, `liquidacion`, `warp`, `bitacora`, `nave`, `mision`,
`asistente`, `universo`), con las excepciones propias en `excepcion` y la demostración en `app`.
