package ProyectoEstudiaYa.webapp.dto;

import ProyectoEstudiaYa.webapp.entities.UsuarioEntity;

import java.time.LocalDateTime;

public class UsuarioDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private UsuarioEntity.NivelEducativo nivel;
    private Integer grado;
    private Integer xpTotal;
    private Integer nivelJuego;
    private Integer rachaActual;
    private Integer rachaMasAlta;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;

    public static UsuarioDTO fromEntity(UsuarioEntity usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol() != null ? usuario.getRol().name() : "ESTUDIANTE");
        dto.setNivel(usuario.getNivel());
        dto.setGrado(usuario.getGrado());
        dto.setXpTotal(usuario.getXpTotal());
        dto.setNivelJuego(usuario.getNivel_juego());
        dto.setRachaActual(usuario.getRachaActual());
        dto.setRachaMasAlta(usuario.getRachaMasAlta());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setUltimoAcceso(usuario.getUltimoAcceso());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
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

    public Integer getXpTotal() {
        return xpTotal;
    }

    public void setXpTotal(Integer xpTotal) {
        this.xpTotal = xpTotal;
    }

    public Integer getNivelJuego() {
        return nivelJuego;
    }

    public void setNivelJuego(Integer nivelJuego) {
        this.nivelJuego = nivelJuego;
    }

    public Integer getRachaActual() {
        return rachaActual;
    }

    public void setRachaActual(Integer rachaActual) {
        this.rachaActual = rachaActual;
    }

    public Integer getRachaMasAlta() {
        return rachaMasAlta;
    }

    public void setRachaMasAlta(Integer rachaMasAlta) {
        this.rachaMasAlta = rachaMasAlta;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }
}
