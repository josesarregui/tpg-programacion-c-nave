package modelo.usuario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evidencia de equals y hashCode de {@link Usuario} (apunte "La clase Object").
 */
public class UsuarioTest {

    @Test
    @DisplayName("equals: dos usuarios con el mismo nombre son iguales y tienen el mismo hashCode")
    public void igualesPorNombre() {
        Usuario uno = new Usuario("kirk");
        Usuario otro = new Usuario("kirk");
        assertEquals(uno, otro);
        assertEquals(uno.hashCode(), otro.hashCode());
        assertNotEquals(uno, new Usuario("spock"));
    }

    @Test
    @DisplayName("equals: comparar con null o con un objeto de otra clase devuelve false sin lanzar excepciones")
    public void equalsDefensivo() {
        Usuario kirk = new Usuario("kirk");
        assertNotEquals(null, kirk);
        assertFalse(kirk.equals(null));
        assertFalse(kirk.equals("kirk"));
    }

    @Test
    @DisplayName("Contrato: crear un usuario sin nombre es un error de programación (aserción)")
    public void usuarioSinNombre() {
        assertThrows(AssertionError.class, () -> new Usuario(" "));
    }
}
