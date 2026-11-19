-- Esquema de la base de datos del sistema (Entrega 2, Reglas Operativas del TPG, punto 5.9).
-- Motor: H2 en modo archivo (datos/nave.mv.db, creado al iniciar app.App).
--
-- Alcance de la persistencia (Enunciado E2): únicamente las credenciales de los usuarios.
-- No se persiste ninguna otra entidad del sistema (naves, tripulantes, misiones, etc.).
--
-- Es el único lugar donde se define la tabla: Maven copia este script al classpath (ver pom.xml) y
-- persistencia.UsuarioDAOJDBC.crearTablaSiNoExiste() lo ejecuta al iniciar el sistema (app.App).
-- Verificado por persistencia.EsquemaSQLTest.

CREATE TABLE IF NOT EXISTS usuario (
    nombre_usuario VARCHAR(30) PRIMARY KEY,
    contrasena     VARCHAR(30) NOT NULL
);
