# tpg-programacion-c-nave

Software embarcado de una nave interestelar tripulada — TPG 2026, Programación C. Entrega 1 y Entrega 2.

## Requisitos de ejecución
* JDK 17 o superior (el proyecto se compila para Java 17; también se probó con JDK 25). El comando `java` debe estar en el PATH.
* Maven 3.8 o superior.
* Entorno gráfico (las pantallas de la E2 usan Swing).

## Configuración local
No requiere instalar un servidor de base de datos. Si Maven no está en el PATH, puede usarse el que trae IntelliJ IDEA
(`<carpeta de IntelliJ>/plugins/maven-plugin/lib/maven3/bin/mvn`) o la ventana *Maven* del IDE.

**Base de datos (E2):** las credenciales de los usuarios se guardan con JDBC en una base **H2** embebida en modo archivo,
`datos/nave.mv.db`, relativa a la carpeta desde la que se ejecuta el sistema. Se crea sola al primer inicio y está excluida del
repositorio por el `.gitignore`. Para empezar con la base vacía, basta con borrar la carpeta `datos/`.

**Script SQL (`database/schema.sql`):** es el DDL del sistema y el único lugar donde se define la tabla `usuario`
(`CREATE TABLE IF NOT EXISTS`). Maven lo copia al classpath (`target/classes/database/schema.sql`) y, al iniciar, `app.App` lo
ejecuta mediante `UsuarioDAOJDBC.crearTablaSiNoExiste()`: no hace falta correrlo a mano. `EsquemaSQLTest` lo ejecuta sobre una
H2 en memoria y verifica que la tabla resultante funciona con el DAO.

**Biblioteca adicional declarada (Reglas Operativas, punto 12):** `com.h2database:h2:2.3.232`, el controlador JDBC y motor de la
base H2. Se usa sólo como driver: el acceso a datos se programa con JDBC estándar (`java.sql`), sin frameworks de persistencia.

## Comandos Maven
| Comando | Qué hace |
|---|---|
| `mvn compile` | Compila el código y copia los recursos (imágenes y `database/schema.sql`) a `target/classes`. |
| `mvn clean test` | Compila desde cero y ejecuta todas las pruebas JUnit 5 con las aserciones activas. |
| `mvn compile exec:exec` | Inicia el sistema (`app.App`) con las aserciones activas (`-ea`). |

Si `mvn clean` no puede borrar `target/` en Windows (un archivo tomado por el IDE), basta con cerrar el IDE o borrar la carpeta
`target/` y volver a ejecutar el comando.

## Ejecución (forma de iniciar el sistema)
El programa principal `app.App` abre la pantalla de **inicio de sesión** (E2, Control de Acceso al Sistema). Desde ella se puede
pasar al **registro** de un usuario nuevo. Al iniciar sesión se cierra el login y se abre la **ventana principal**, con un menú
lateral para sus secciones:
* **Nave:** elegir el tipo de nave y crearla (queda registrada en el centro de control con su asistente); luego, su estado.
* **Tripulación:** dar de alta Capitán, Consejero, Teniente y Alférez (al menos 5 integrantes, uno de ellos capitán/a) y asignarlos
  a la nave; después, la nómina con el sueldo de cada tripulante y el recibo con su desglose ("Ver recibo").
* **Control de la nave:** recursos actuales, Bitácora en vivo, selección e inicio de una misión, tablero de 20 x 20 que se
  actualiza solo mientras la nave avanza (la misión corre en su propio hilo y puede cancelarse) e historial de informes finales.
* **Visor:** el universo paralelo de la Plataforma de Carga, con los botones para agregar generadoras y naves consumidoras.
* **Cerrar sesión:** detiene la misión en curso y los hilos del Visor y vuelve al login.

Se ejecuta con las aserciones activas (`-ea`) mediante:
```
mvn compile exec:exec
```
Desde IntelliJ IDEA: abrir la carpeta como proyecto Maven, ejecutar `app.App` y agregar `-ea` en *Run > Edit Configurations > VM options*.

### Demostración por consola de la Entrega 1
La demostración de la E1 se conserva en `app.DemostracionEntrega1`, que simula al usuario (Aclaración, R6) y recorre los cuatro
escenarios de la Ficha de Inicio:
```
java -ea -cp target/classes app.DemostracionEntrega1
```
Qué muestra, en orden: la liquidación de haberes de una tripulación; el alta de una nave de cada tipo en el centro de control;
el **Escenario A** (M-01, M-02 y M-03 sobre un tablero de 20 x 20 aleatorio, con recursos antes y después, informe con su
balance y Bitácora de cada misión; como el universo es aleatorio, una misión puede fallar o rechazarse); las operaciones sobre
recursos y mantenimiento con el **Escenario D**; el **Escenario B** y el **Escenario C**.

## Verificación
```
mvn clean test
```
Ejecuta las 376 pruebas JUnit 5 de `src/test/java` (Surefire activa las aserciones). Todas deben terminar en verde.
Para una sola clase: `mvn test -Dtest=EsquemaSQLTest`.
Las pruebas de misiones son deterministas: usan un tablero de prueba con los elementos ubicados por la prueba, un Destino que
no mueve elementos (o que los mueve como indica la prueba), un `Random` con semilla y pausa 0 entre turnos. La única
excepción es la prueba del universo de 20 x 20 con el Destino de la cátedra, que verifica propiedades válidas para cualquier sorteo.
Las pruebas de la E2 usan bases H2 en memoria, de modo que no modifican `datos/nave.mv.db`. Los controladores se prueban con
vistas de prueba que implementan las interfaces `IVista...` sin abrir ventanas (`controlador/VistasDePrueba`); las del hilo de la
misión esperan los avisos con colas, `CountDownLatch` y `join` con tiempo límite, sin pausas fijas; lo que esas vistas de
prueba comparten entre hilos se lee y se escribe con métodos `synchronized` (sin `volatile`). Las pruebas de las vistas
Swing (`VistasAccesoTest`, `VistaDetallesNaveTest`, `VistaConcurrenciaTest` y las demás de `src/test/java/vista`) se omiten si no
hay entorno gráfico.

### Matriz de verificación — Entrega 2: acceso al sistema
| Caso | Qué se verifica (resultado esperado) | Prueba automática |
|---|---|---|
| Registro | Un usuario nuevo queda guardado y puede iniciar sesión. Se rechazan: nombre vacío, con espacios o de más de 30 caracteres (`UsuarioInvalidoException`), contraseña vacía o de más de 30 (`ContrasenaInvalidaException`) y nombre repetido (`UsuarioExistenteException`), sin guardar nada. | `ServicioUsuariosTest` |
| Login | Con credenciales correctas el operador queda en sesión. Usuario inexistente y contraseña incorrecta dan el mismo mensaje (`LoginInvalidoException`) y no cambian la sesión. | `ServicioUsuariosTest`, `ControladorLoginTest` |
| Persistencia (DAO/DTO, JDBC) | Las credenciales se conservan para otra ejecución; consultas parametrizadas (sin inyección de SQL); toda `SQLException` se transforma en `PersistenciaException`. | `UsuarioDAOJDBCTest`, `ServicioUsuariosTest` |
| Script SQL (`database/schema.sql`, Reglas Operativas V.5) | El script, ejecutado tal cual sobre una H2 en memoria, crea la tabla con la que el DAO guarda y recupera credenciales; la clave primaria rechaza un nombre repetido (`PersistenciaException`) y volver a ejecutarlo no borra usuarios. El sistema ejecuta ese mismo script: su copia del classpath es idéntica y crea la misma tabla. | `EsquemaSQLTest` |
| MVC | Los controladores trabajan con las vistas por su interfaz y piden las ventanas a la `FabricaDeVistas`: se prueban con vistas sin ventanas. Los errores del servicio se muestran en la vista. Login y registro navegan entre sí. | `ControladorLoginTest` |
| Transición al panel central | Al autenticar, se cierra el login y se abre la ventana principal con el operador y sus secciones (el Visor ya observa su plataforma). Con credenciales incorrectas no se abre nada. | `ControladorLoginTest`, `ControladorSesionTest` |
| Validación de formato en la Vista | "Ingresar" y "Registrar" se habilitan sólo con los campos completos; el registro avisa si las contraseñas no coinciden. | `VistasAccesoTest` |

### Matriz de verificación — Entrega 2: entorno espacial
| Caso | Qué se verifica (resultado esperado) | Prueba automática |
|---|---|---|
| Tablero y Destino (módulo de la cátedra en `modelo.universo`) | El tablero ubica la nave en el centro, elige objetivo y notifica a sus observadores; Destino mueve elementos y avisa al cerrar el turno. | `TableroYDestinoTest` |
| Contrato del tablero que usan los radares | En los bordes, `observarAlrededores()` devuelve `null` fuera del tablero, los vecinos en su casilla y la nave en [1][1]; `verTablero()` es una matriz nueva de filas x columnas sin la nave ni el objetivo; dimensiones fuera de 5 a 100 y más de x · y / 4 · 3 elementos se rechazan con las excepciones de la cátedra; el objetivo cae en una casilla libre a distancia de Chebyshev 2 o más; cada turno de Destino mueve a lo sumo la mitad de los elementos, a casillas vecinas dentro del tablero y nunca al objetivo, sin perder elementos, y avisa "se movieron M de N". | `TableroYDestinoTest` |
| Asteroide (E2, punto B) | Descuenta 25 de energía sin tocar combustible ni desgaste; si la energía no alcanza queda en 0 y la nave se destruye. El impacto, la pérdida real y la destrucción quedan en la Bitácora. | `AsteroideTest`, `RecursosTest`, `DestruccionNaveTest` |
| Estación de carga (E2, punto C) | Recarga 30 de combustible y 30 de energía sin superar 100. El acople y lo realmente cargado quedan en la Bitácora. | `EstacionDeCargaTest`, `RecursosTest` |
| Polimorfismo en `ElementoDelUniverso` | La interacción y el símbolo para la vista se obtienen sin condicionales de tipo. Las naves de la fábrica son elementos del tablero, con nombre y símbolo de su tipo (`EX`, `CA`, `CO`). | `AsteroideTest`, `EstacionDeCargaTest`, `BatallaTest`, `TableroYDestinoTest` |
| Batallas por Double Dispatch (E2, punto A) | Los 9 cruces de la tabla (nave propia vs. enemiga): mismo tipo, pacto de paz; Exploradora gana a Carguero, Carguero a Combate y Combate a Exploradora (y la inversa pierde). Resolver la batalla no modifica las naves. Sin `instanceof`, `getClass()` ni condicionales de tipo. | `BatallaTest` |
| Derrota en batalla | La nave propia queda destruida, su tripulación muere, deja de estar lista para operar y la Bitácora registra la batalla y la destrucción. Victoria y pacto de paz no la modifican. La enemiga no cambia. | `BatallaTest` |
| Causas de destrucción (E2, Fase 2) | Combustible 0, energía 0 o desgaste 100 por consumo, impacto, salto normal, salto warp, enfriamiento o entrega de suministros destruyen la nave; la causa queda registrada una sola vez. La destrucción es definitiva: recargar o hacer mantenimiento no la repara. | `DestruccionNaveTest`, `AsteroideTest` |
| Gemas (M-02) y suministros (M-01) | La nave acumula las gemas acreditadas; entregar suministros descuenta combustible y energía en un solo paso y acumula lo entregado. Una entrega insuficiente o negativa se rechaza sin cambios y queda en la Bitácora. Una nave destruida no recibe gemas ni entrega suministros. | `GemasYSuministrosTest`, `DestruccionNaveTest` |

### Matriz de verificación — Entrega 2: ciclo de vida de las misiones (Template Method sobre el tablero)
| Caso | Qué se verifica (resultado esperado) | Prueba automática |
|---|---|---|
| Fase 1: preparar | Verifica disponibilidad y recursos mínimos (incluido el consumo inicial del Anexo); si se rechaza, ni la nave ni el tablero cambian y la misión sigue pendiente (Escenario B). Fija el objetivo (`obtenerObjetivo()` en M-01/M-02, centro en M-03) y calcula la ruta con el radar de largo alcance, evitando amenazas. | `MisionTest`, `TravesiaTest` |
| Fase 2: travesía por turnos | Despegue (Anexo): 4 de combustible, 4 de desgaste y la energía inicial de la misión (M-01 5, M-02 10, M-03 0), más 5 de energía del escaneo de largo alcance. Por turno: inspección e interacción, escaneo corto (1 de energía), salto por el asistente (Anexo) con 1 de desgaste por casilla, turno de Destino y pausa. Con 3 casillas libres, M-02 cuesta 10 de combustible, 18 de energía y 7 de desgaste. M-03 que ya está en la base sólo paga el despegue. | `MisionTest` |
| Interacciones en el recorrido | Asteroide: energía perdida en el balance. Estación: combustible y energía ganados (tope 100). Batalla perdida: nave y tripulación destruidas, misión fallida; batalla ganada: sigue. | `TravesiaTest` |
| Destrucción durante el recorrido | Un impacto que deja la energía en 0 corta la travesía en ese turno; la misión se aborta, resulta fallida y el cierre no aplica nada. | `TravesiaTest` |
| Obstáculo en la próxima casilla (Anexo) | Si Destino cruza un asteroide en el próximo paso, la nave inicia la maniobra warp y salta por encima de él (sin impacto ni recálculo). Sin energía para completar la maniobra, recalcula la ruta (2 de energía, Tabla de Recursos IV.3) y lo esquiva. | `TravesiaTest` |
| Transición entre casillas | Cada movimiento queda dentro del tablero y es vecino del anterior, también desde un borde; el salto warp avanza 2 casillas en el mismo turno (ante un obstáculo o sin combustible para un salto normal) y termina con el motor Disponible. Sin recursos para avanzar, la misión se aborta sin destruir la nave: la maniobra warp sólo se inicia si la energía alcanza para completarla y, sin energía para el escaneo de corto alcance, la nave no escanea ni avanza. | `TravesiaTest` |
| Verificación en destino (M-01) | Si la nave consumió en el viaje los suministros a entregar, la misión falla y no se descuenta nada. | `TravesiaTest` |
| Fase 3: informe | Objetivo, ruta recorrida, resultado, acciones, balance como datos (consumidos, perdidos, ganados, gemas y suministros), estado al evaluar y observaciones; listas de sólo lectura. | `MisionTest` |
| Fase 4: cierre | M-01 entrega los suministros, M-02 acredita las gemas y M-03 repara el desgaste y recarga al 100 % en la base (Anexo). La Bitácora registra el estado posterior al cierre. | `MisionTest`, `AsistenteComandoTest` |
| Gemas y suministros aleatorios (Anexo) | Suministros de M-01 de 0 a 50 de cada recurso (con 0 la asistencia se completa sin entrega) y gemas de M-02 de 20 a 100: se prueban los extremos del sorteo. Con la misma semilla son reproducibles. | `MisionTest` |
| Pausa y concurrencia | La pausa por turno es configurable (0 en las pruebas). Si el hilo se interrumpe, se restaura la interrupción, termina la travesía y la misión falla, aunque la nave llegue en ese turno (el cierre no aplica nada). Una maniobra warp en curso se completa antes de terminar, para que la nave quede lista para otra misión. | `MisionTest`, `TravesiaTest` |
| Universo de 20 x 20 | Con `FabricaDeUniverso` y el Destino de la cátedra la misión termina con un informe válido y un recorrido válido. | `TravesiaTest` |
| Nave ya destruida | La misión se rechaza con `NaveNoDisponibleException` (`isNaveDestruida()`). | `MisionTest` |

### Matriz de verificación — Entrega 2: patrón Observer (modelo hacia la vista)
| Caso | Qué se verifica (resultado esperado) | Prueba automática |
|---|---|---|
| Mecanismo único | `Observable` / `Observador` de la cátedra (Versión 2) en todo el sistema: mensaje descriptivo en `actualizar(String)` (push) y estado consultado con getters (pull). Sujetos: `Tablero`, `AsistenteComando` y `PlataformaDeCarga`; la nave no notifica. | `AsistenteObservadorTest`, `PlataformaDeCargaObservadorTest`, `TableroYDestinoTest` |
| Asistente: recursos | Cada carga, consumo, impacto, reabastecimiento y mantenimiento produce una notificación con la descripción de la Bitácora, y el observador ya consulta el combustible, la energía o el desgaste nuevos. Una orden rechazada notifica su entrada de error y el estado no cambia. | `AsistenteObservadorTest` |
| Asistente: motor, tripulación y Bitácora | Salto normal y turno warp notifican una vez con el estado del motor nuevo; la asignación de la tripulación, una vez con la tripulación ya consultable; cada entrada registrada por misiones o el centro de control, con su descripción. Un evento inválido no notifica. | `AsistenteObservadorTest` |
| Asistente: destrucción e informe | La destrucción se notifica aparte del cambio que la causa, con la nave ya destruida (también al notificar la batalla perdida). El cierre de la misión es la última notificación ("Informe de ..."), con la misión ya entre las realizadas. | `AsistenteObservadorTest` |
| Sin duplicados ni faltantes | Los mensajes recibidos coinciden, en orden, con las entradas agregadas a la Bitácora; un observador registrado dos veces recibe un aviso por cambio; tras quitarlo no recibe más; uno nulo viola la precondición. | `AsistenteObservadorTest`, `PlataformaDeCargaObservadorTest` |
| Plataforma de carga | Producción, solicitud, espera (llena o vacía), depósito, retiro, ingreso y egreso con los mensajes del Enunciado y los adicionales; el stock se consulta con `getBaterias()` en el momento de cada evento. | `PlataformaDeCargaObservadorTest`, `ControladorConcurrenciaTest` |
| Suscripción desde la vista | La vista recibe el sujeto del controlador (`observar(...)`) y se suscribe ella misma; al dejar de observar no recibe más avisos; al observar otro asistente sólo sigue a ése. | `VistaDetallesNaveTest`, `VistaConcurrenciaTest`, `ControladorConcurrenciaTest`, `ControladorPrincipalTest`, `ControladorControlTest`, `VistaTableroTest` |
| Hilos | Una notificación desde otro hilo (misión o plataforma) actualiza los componentes en el EDT con `invokeLater`, sin que el hilo que notifica espere al EDT. Suscribir y quitar observadores mientras otro hilo notifica no rompe la lista ni pierde avisos. | `VistaDetallesNaveTest`, `VistaConcurrenciaTest`, `VistaTableroTest`, `VistaCursoMisionTest`, `VistaRecursosTest`, `VistaResultadosMisionTest`, `VistaSelectMisionTest`, `AsistenteObservadorTest`, `PlataformaDeCargaObservadorTest` |

### Matriz de verificación — Entrega 2: concurrencia (Productor-Consumidor con monitores)
| Caso | Qué se verifica (resultado esperado) | Prueba automática |
|---|---|---|
| Contratos del monitor | Capacidad mayor a 0; depositar suma una batería y retirar resta una; un identificador nulo o vacío viola la precondición (`AssertionError`) sin publicar ni cambiar el stock. | `PlataformaDeCargaTest` |
| Plataforma vacía o llena | La nave queda bloqueada en `wait()` con la plataforma vacía y la generadora con la plataforma llena, hasta que otra entidad cambia el stock; entonces terminan su operación. | `PlataformaDeCargaTest`, `HilosProductorConsumidorTest`, `PlataformaDeCargaObservadorTest` |
| `notifyAll()` + `while` | Un solo depósito despierta a todas las naves en espera: una retira y las demás vuelven a evaluar la guarda y a esperar. | `PlataformaDeCargaTest` |
| Varios productores y consumidores | Con 4 generadoras y 4 naves sobre una plataforma de capacidad 2, el stock nunca supera la capacidad ni baja de 0, no hay bloqueos, ningún contrato falla y no se pierde ninguna batería. | `PlataformaDeCargaTest` |
| Orden de los mensajes | Generadora: producida → solicitando acceso → (esperando) → depositada; nave: solicitando → (esperando) → retirada y utilizada; el ingreso de cada entidad precede a sus eventos. | `HilosProductorConsumidorTest`, `PlataformaDeCargaObservadorTest`, `ControladorConcurrenciaTest` |
| Finalización por interrupción | Interrumpir una entidad en `wait()` o en su pausa la termina, con la interrupción restaurada al salir de `run()` y el stock sin cambios; la plataforma sigue operativa. | `HilosProductorConsumidorTest`, `PlataformaDeCargaTest` |
| Creación dinámica | Con la simulación en marcha se agregan generadoras y naves, cada una en su propio hilo y con la misma plataforma; todas operan y terminan al detenerlas. | `HilosProductorConsumidorTest`, `ControladorConcurrenciaTest` |
| Detención individual y total | Al detener una entidad (en su pausa o en `wait()`) su hilo ya terminó y se publica su egreso; detener todo no deja hilos vivos, publica cada egreso y después la vista deja de observar. Un alta que falla no deja nada registrado. | `ControladorConcurrenciaTest` |

### Matriz de verificación — Entrega 2: vistas y controladores de operación (MVC, Observer e hilo de la misión)
| Caso | Qué se verifica (resultado esperado) | Prueba automática |
|---|---|---|
| Sección de Creación: nave | La nave se crea con `NaveFactory`, se asocia a un `AsistenteComando`, queda registrada y seleccionada en `CentroDeControl` y la vista de detalles la observa. Aceptar sin elegir el tipo muestra "Debe seleccionar una nave." y no registra nada. | `ControladorPrincipalTest` |
| Sección de Creación: tripulación | Se dan de alta Capitán, Consejero (con sus consejos), Teniente y Alférez y la tripulación se asigna a la nave; se habilita Control de la Nave. Nombre vacío, capitán repetido, antigüedad o consejos con formato inválido, más de 999 consejos iniciales (`MAXIMO_CONSEJOS_INICIALES`, para no congelar la interfaz) y tripulación incompleta se rechazan en la vista con su mensaje, sin cambiar la nave. | `ControladorPrincipalTest`, `ControladorCrearTripulacionTest` |
| Sección de Tripulantes | Cada tripulante figura con el sueldo de `LiquidadorHaberes` (Decorator) y su recibo con el desglose por concepto (total = suma de conceptos); el total de la nómina es la suma de los sueldos. Un rechazo del liquidador se informa en la vista. | `ControladorPrincipalTest`, `ControladorDetallesTripulacionTest`, `VistaDetallesTripulacionTest` |
| Menú | Tripulación y Control de la Nave se rechazan hasta tener nave y tripulación; luego muestran su sección. | `ControladorPrincipalTest` |
| Control: suscripciones y Bitácora | Al crearse la sección, las vistas de recursos, Bitácora, misión en curso e informes observan al asistente y la del tablero observa el tablero; cada evento nuevo llega a la Bitácora (copiada en el hilo que notifica) y a la consola. Sin `instanceof` ni `switch` por tipo: cada misión se describe con `getNombre()` y `getCondicionDeExito()`. | `ControladorControlTest`, `VistaSelectMisionTest` |
| Iniciar no bloquea | Iniciar una misión vuelve enseguida (con una pausa de 60 s por turno), la misión corre en el hilo "Misión M-0x" y la sección pasa a la misión en curso. | `ControladorMisionTest` |
| Una misión a la vez | Con una misión en curso, iniciar otra muestra el error y no toca la que corre; tampoco se puede volver a la selección. | `ControladorMisionTest` |
| Actualización automática | Durante la misión, desde su hilo: la grilla de 20 x 20 muestra la nave y el objetivo de la misión en curso (`verTablero()` y símbolos por polimorfismo; en M-03, la base, aunque el tablero conserve el objetivo de la misión anterior), los recursos bajan, la Bitácora crece, la consola recibe "Informe de ..." y el informe exitoso aparece en el historial. | `ControladorMisionTest`, `VistaTableroTest`, `VistaRecursosTest`, `VistaResultadosMisionTest`, `VistaCursoMisionTest` |
| Cancelar la misión | Cancelar interrumpe el hilo y espera su fin con `join`: el hilo terminó, la misión resulta fallida con su informe en el historial y se informa como cancelada. | `ControladorMisionTest` |
| Excepciones del hilo | Una misión rechazada en su hilo (recursos insuficientes) se informa en la vista desde ese hilo, queda pendiente y puede reintentarse. | `ControladorMisionTest` |
| Componentes en el EDT | Con el EDT bloqueado, una notificación desde otro hilo vuelve sin esperar y los componentes no cambian; al liberar el EDT muestran el cambio. | `VistaTableroTest`, `VistaCursoMisionTest`, `VistaRecursosTest`, `VistaResultadosMisionTest`, `VistaSelectMisionTest` |
| Cierre de sesión | Detiene el hilo de la misión y los del Visor (ninguno queda vivo), todas las vistas dejan de observar asistente, tablero y plataforma (ningún aviso posterior les llega), cierra la sesión y la ventana y abre el login con su controlador. | `ControladorSesionTest` |

### Matriz de verificación — Entrega 1
| Caso | Qué se verifica (resultado esperado) | Prueba automática | En `DemostracionEntrega1` |
|---|---|---|---|
| E1-01, E1-07 (Factory) | La fábrica crea los tres tipos con la Ficha de Inicio: Exploradora 60/80/0, Carguero 100/60/0, Combate 80/100/0 (combustible/energía/desgaste), motor Disponible, sin tripulación y con id único. | `NaveFactoryTest` | "NAVES REGISTRADAS" |
| E1-07: sin instanciación directa | Los constructores de las naves son de paquete: `new Exploradora()` fuera de `modelo.nave` **no compila**. El cliente sólo usa `NaveFactory.crearNave(TipoNave)`. | Verificable en el código | `darDeAlta()` |
| E1-02, Escenario C y Anexo (State) | Salto normal: avanza 1 y consume 2 de combustible. Salto warp, un turno por llamada: Disponible → Preparando salto (2 turnos, con 5 de energía) → En warp (avanza 2, consume 5 de energía; sin energía aborta y vuelve a preparar) → Enfriamiento (+3 de desgaste) → Disponible. Un salto normal durante la maniobra lanza `EstadoMotorInvalidoException`, nada cambia y el rechazo queda en la Bitácora. | `MotorWarpTest`, `AsistenteComandoTest` | "ESCENARIO C" |
| E1-03 | El asistente delega en la nave y en el motor, registra cada resultado y cada rechazo, y propaga la excepción. | `AsistenteComandoTest` | Todas las secciones |
| E1-04 | Tripulante con id, cargo, origen y antigüedad ≥ 0; tripulación con un/a capitán/a + 4. Los rechazos no modifican la tripulación. | `TripulacionTest` | "LIQUIDACIÓN DE HABERES" |
| E1-05 | Eventos en orden temporal e inmutables; se rechazan los nulos, vacíos o fuera de orden. | `BitacoraTest`, `MisionTest` | "BITÁCORA" |
| E1-06, Escenario A (Template Method) | M-01, M-02 y M-03 hacen preparar → ejecutar → evaluar → cerrar. En la E2, el costo de la E1 (4 de combustible y 4 de desgaste) es el consumo inicial del Anexo, con la energía inicial de cada misión (5/10/0), y se suman los costos de la travesía (ver la matriz del ciclo de vida de las misiones); la acción final ya no tiene costo. | `MisionTest` | "ESCENARIO A" |
| E1-08 (Decorator) | Haber de los 4 cargos con los 3 orígenes, dos decoradores en cualquier orden, consejos del período y detalle por concepto. Ej.: capitán terrícola con 3 años = 1000 + 600 + 20 = 1620 PG. Se rechazan conceptos nulos o repetidos; el repetido se detecta por polimorfismo (`fueDecoradoCon`: cada capa responde por su concepto y delega, el tripulante responde `false`), sin `instanceof`. | `LiquidacionTest` | "LIQUIDACIÓN DE HABERES" |
| E1-09, Escenario D | Carga hasta la capacidad (100); con desgaste ≥ 80 requiere mantenimiento y no opera; el mantenimiento lo lleva a 0. Una carga que excede la capacidad se rechaza y la nave conserva su estado. | `RecursosTest`, `NaveTest`, `AsistenteComandoTest` | "OPERACIONES SOBRE RECURSOS", "ESCENARIO D" |
| Escenario B | Sin los recursos mínimos (por ejemplo, 1 de combustible, o 10 de energía para M-02, que necesita 11), la misión se rechaza con `RecursoInsuficienteException`: la nave no cambia, el motivo queda en la Bitácora y la misión sigue pendiente. | `MisionTest`, `AsistenteComandoTest`, `RecursosTest` | "ESCENARIO B" |
| E1-10 | El informe trae misión, objetivo, resultado, acciones, ruta, balance de recursos, estado de la nave y observaciones. | `MisionTest` | "INFORME M-0x" |
| Contratos | Una precondición o invariante incumplida se detecta con `assert` (`AssertionError`): misión realizada dos veces o sin pasar por su asistente, tripulación nula, consulta de los tripulantes de una nave sin tripulación, importe negativo, estado nulo del motor. | Pruebas "Rechazo por contrato" y "Aserción" | — |
| Aclaración R1, R2 y Pedidos del diseño | El centro registra y devuelve naves, rechaza una repetida o inexistente sin cambiar, usa una nave a la vez y admite otra variante de asistente sin modificarse. | `CentroDeControlTest` | "CASOS DE RECHAZO DEL CENTRO DE CONTROL" |

## Entregas (etiquetas Git)
Cada entrega se identifica con una etiqueta Git anotada que señala el commit evaluado (Reglas Operativas, punto 4):

| Entrega | Etiqueta | Commit |
|---|---|---|
| Entrega 1 | `entrega-e1` | `afea664` (merge en `main` de la revisión final de la E1) |
| Entrega 2 | `entrega-e2` | commit final de `main` de la E2 |

Para obtener el código de una entrega:
```
git fetch --tags
git checkout entrega-e2
```

## Documentación
* [docs/diseño.md](docs/diseño.md): responsabilidades de cada paquete, patrones (Factory, State, Template Method, Decorator, Observer, MVC, DAO y DTO), Double Dispatch, Productor-Consumidor con monitores, contratos, manejo de errores y decisiones de interpretación adoptadas ante ambigüedades de la consigna (sección 4).
* [database/schema.sql](database/schema.sql): script DDL de la base de usuarios (E2).
* [docs/diagrama_clases.md](docs/diagrama_clases.md): diagrama de clases.
* [docs/uso-ia.md](docs/uso-ia.md): registro del uso de inteligencia artificial.

El código está organizado por subsistema en `modelo` (`tripulacion`, `liquidacion`, `warp`, `bitacora`, `nave`, `mision`,
`asistente`, `universo`, `concurrencia`, `usuario`), con las excepciones propias en `excepcion`, el acceso a datos en
`persistencia`, las pantallas Swing en `vista`, sus controladores en `controlador` y el programa principal y la demostración
en `app`.
