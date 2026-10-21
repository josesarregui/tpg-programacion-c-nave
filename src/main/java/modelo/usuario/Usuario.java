
package modelo.usuario;

import java.util.Objects;

/**
 *
 */
public class Usuario {
    private String nombreUsuario;
    private String contrasena;

    
    public Usuario(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }else{
            Usuario otro = (Usuario) obj;

            return Objects.equals(this.nombreUsuario,otro.nombreUsuario);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreUsuario);
    }
}
