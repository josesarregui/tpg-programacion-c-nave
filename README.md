# tpg-programacion-c-nave

Software embarcado de una nave interestelar tripulada — TPG 2026, Programación C.

## Requisitos de ejecución
* Java 17 (JDK).
* Maven 3.8 o superior.
* No requiere configuración local adicional.

## Compilación
```
mvn compile
```

## Ejecución
El programa principal (`app.App`) simula al usuario (Aclaración, R6). Liquida los haberes de una tripulación mínima y, después, da de alta en el centro de control una nave de cada tipo, creada mediante la fábrica y con su asistente. Selecciona una nave por vez y, siempre a través de su asistente, ejecuta M-01, M-02 y M-03 mostrando informe, recursos y Bitácora (Escenario A), opera sobre los recursos y el mantenimiento (Escenario D), muestra una misión rechazada por recursos insuficientes (Escenario B) y recorre el ciclo del Motor Warp con una transición inválida (Escenario C):
```
mvn compile
java -ea -cp target/classes app.App
```
La opción `-ea` activa las aserciones (invariantes y postcondiciones).

## Verificación
```
mvn test
```
Ejecuta las pruebas JUnit 5 de `src/test/java` (Maven Surefire activa las aserciones por defecto). Las pruebas de Tripulación (`TripulacionTest`) y Liquidación (`LiquidacionTest`) son la evidencia de E1-04 y E1-08: haberes de los cuatro cargos con los tres orígenes, composición de decoradores y casos de rechazo. Las pruebas de Nave (`NaveFactoryTest`, `NaveTest` y `RecursosTest`) son la evidencia de E1-01, E1-07 y E1-09: creación de los tres tipos de nave mediante la fábrica con los valores de la Ficha de Inicio, cargas, consumos y mantenimiento, y los Escenarios B (recursos insuficientes) y D (carga que excede la capacidad), verificando que el estado anterior se conserva. `MotorWarpTest` es la evidencia de E1-02 (Escenario C: transiciones válidas e inválidas, y contratos del motor) y `BitacoraTest` la de E1-05 (orden temporal, eventos inmutables y rechazo de eventos nulos, vacíos o fuera de orden). `AsistenteComandoTest` es la evidencia de E1-03 (registro en la Bitácora de las operaciones y de los rechazos de los Escenarios C y D, misiones pendientes, realizadas y canceladas, e informe de cada misión en la Bitácora) y `MisionTest` la evidencia de E1-06 y E1-10 (Escenarios A y B): ciclo completo de las tres misiones con los costos de la Ficha de Inicio, informe, registro en la Bitácora, rechazos sin cambios parciales y contratos incumplidos (entre ellos, realizar una misión sin pasar por su asistente). `CentroDeControlTest` es la evidencia de la Aclaración "Naves, asistentes y misiones" (R1, R2 y Pedidos del diseño): registro y búsqueda de naves, rechazos, una única nave en uso y una nueva variante de asistente registrada sin modificar el centro de control.

## Entrega
La Entrega 1 se identifica con la etiqueta Git `entrega-e1`.

## Arquitectura y Modularización
El código fuente se encuentra organizado por subsistemas funcionales bajo el paquete `modelo` (`tripulacion`, `liquidacion`, `warp`, `bitacora`, `nave`, `mision`, `asistente`, `universo`), con las excepciones propias en `excepcion` y la demostración en `app`.
Para conocer la justificación del desacoplamiento, la distribución de responsabilidades y la aplicación de los patrones de diseño (Factory, State, Template Method y Decorator), consultar el documento [docs/diseño.md](docs/diseño.md). Allí también se explica el diseño pedido por la Aclaración de la cátedra: el centro de control (`CentroDeControl`) y la interfaz `Asistente`, y las decisiones que quedan a confirmar con la cátedra (sección 4). El diagrama de clases está en [docs/diagrama_clases.md](docs/diagrama_clases.md) y el registro de uso de IA en [docs/uso-ia.md](docs/uso-ia.md).
