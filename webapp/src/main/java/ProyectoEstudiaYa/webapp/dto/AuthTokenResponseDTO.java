package ProyectoEstudiaYa.webapp.dto;

import ProyectoEstudiaYa.webapp.entities.UsuarioEntity;

public class AuthTokenResponseDTO {
    private final String token;
    private final String type;
    private final Long usuarioId;
    private final String nombre;
    private final String apellido;
    private final String email;
    private final UsuarioEntity.NivelEducativo nivel;
    private final Integer grado;
    private final Integer xpTotal;
    private final Integer nivelJuego;
    private final Integer rachaActual;
    private final Integer rachaMasAlta;

    public AuthTokenResponseDTO(String token, UsuarioEntity usuario) {
        this.token = token;
        this.type = "Bearer";
        this.usuarioId = usuario.getId();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.email = usuario.getEmail();
        this.nivel = usuario.getNivel();
        this.grado = usuario.getGrado();
        this.xpTotal = usuario.getXpTotal();
        this.nivelJuego = usuario.getNivel_juego();
        this.rachaActual = usuario.getRachaActual();
        this.rachaMasAlta = usuario.getRachaMasAlta();
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public UsuarioEntity.NivelEducativo getNivel() {
        return nivel;
    }

    public Integer getGrado() {
        return grado;
    }

    public Integer getXpTotal() {
        return xpTotal;
    }

    public Integer getNivelJuego() {
        return nivelJuego;
    }

    public Integer getRachaActual() {
        return rachaActual;
    }

    public Integer getRachaMasAlta() {
        return rachaMasAlta;
    }
}
