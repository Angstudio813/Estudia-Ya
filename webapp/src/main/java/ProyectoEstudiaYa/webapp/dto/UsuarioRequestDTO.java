package ProyectoEstudiaYa.webapp.dto;

import ProyectoEstudiaYa.webapp.entities.UsuarioEntity;

public class UsuarioRequestDTO {

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private UsuarioEntity.NivelEducativo nivel;
    private Integer grado;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UsuarioEntity.NivelEducativo getNivel() {
        return nivel;
    }

    public void setNivel(UsuarioEntity.NivelEducativo nivel) {
        this.nivel = nivel;
    }

    public Integer getGrado() {
        return grado;
    }

    public void setGrado(Integer grado) {
        this.grado = grado;
    }
}
