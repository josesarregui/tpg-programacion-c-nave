package modelo.usuario;

import modelo.nave.Nave;

/**
 * Operador identificado en el sistema (Enunciado E2, "Control de Acceso al Sistema").
 *
 * Es un objeto del dominio y no guarda la contraseña: las credenciales sólo viajan entre el servicio y el DAO
 * dentro de un {@code UsuarioDTO}. Es inmutable respecto de su identidad (el nombre de usuario).
 *
 * Invariante: nombreUsuario != null y no vacío.
 */
public class Usuario {

    private final String nombreUsuario;
    private Nave nave;

    /**
     * @pre nombreUsuario != null y no vacío (lo valida el servicio antes de crearlo).
     */
    public Usuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        assert invariante() : "Fallo invariante tras crear el usuario.";
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public Nave getNave() {
        return nave;
    }

    public void setNave(Nave nave) {
        this.nave = nave;
    }

    /**
     * Dos usuarios son iguales si tienen el mismo nombre (apunte "La clase Object": equals compara valores).
     * Devuelve false ante null o un objeto de otra clase, en lugar de fallar con una conversión inválida.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Usuario otro = (Usuario) obj;
        return nombreUsuario.equals(otro.nombreUsuario);
    }

    /**
     * Coherente con {@link #equals(Object)}: usuarios iguales tienen el mismo código hash.
     */
    @Override
    public int hashCode() {
        return nombreUsuario.hashCode();
    }

    @Override
    public String toString() {
        return nombreUsuario;
    }

    private boolean invariante() {
        return nombreUsuario != null && !nombreUsuario.isBlank();
    }
}
