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
El programa de demostración (`app.App`) liquida los haberes de una tripulación mínima, crea los tres tipos de nave mediante la fábrica, opera sobre sus recursos, recorre el ciclo del Motor Warp, ejecuta las misiones M-01, M-02 y M-03 mediante el asistente mostrando informe, recursos y Bitácora, y muestra casos de rechazo (Escenarios A, B, C y D):
```
mvn compile
java -ea -cp target/classes app.App
```
La opción `-ea` activa las aserciones (invariantes y postcondiciones).

## Verificación
```
mvn test
```
Ejecuta las pruebas JUnit 5 de `src/test/java` (Maven Surefire activa las aserciones por defecto). Las pruebas de Tripulación (`TripulacionTest`) y Liquidación (`LiquidacionTest`) son la evidencia de E1-04 y E1-08: haberes de los cuatro cargos con los tres orígenes, composición de decoradores y casos de rechazo. Las pruebas de Nave (`NaveFactoryTest`, `NaveTest` y `RecursosTest`) son la evidencia de E1-01, E1-07 y E1-09: creación de los tres tipos de nave mediante la fábrica con los valores de la Ficha de Inicio, cargas, consumos y mantenimiento, y los Escenarios B (recursos insuficientes) y D (carga que excede la capacidad), verificando que el estado anterior se conserva. `MotorWarpTest` es la evidencia de E1-02 (Escenario C: transiciones válidas e inválidas) y `BitacoraTest` la de E1-05 (orden temporal, eventos inmutables y rechazo de eventos nulos o vacíos). `AsistenteComandoTest` es la evidencia de E1-03 (registro en la Bitácora de las operaciones y de los rechazos de los Escenarios C y D) y `MisionTest` la evidencia de E1-06 y E1-10 (Escenarios A y B): ciclo completo de las tres misiones con los costos de la Ficha de Inicio, informe, registro en la Bitácora, rechazos sin cambios parciales y contratos incumplidos.

## Entrega
La Entrega 1 se identifica con la etiqueta Git `entrega-e1`.

## Arquitectura y Modularización
El código fuente se encuentra organizado por subsistemas funcionales bajo el paquete `modelo` (`tripulacion`, `liquidacion`, `warp`, `bitacora`, `nave`, `mision`, `asistente`), con las excepciones propias en `excepcion` y la demostración en `app`.
Para conocer la justificación del desacoplamiento, la distribución de responsabilidades y la aplicación de los patrones de diseño (Factory, State, Template Method y Decorator), consultar el documento [docs/diseño.md](docs/diseño.md). El diagrama de clases está en [docs/diagrama_clases.md](docs/diagrama_clases.md) y el registro de uso de IA en [docs/uso-ia.md](docs/uso-ia.md).
