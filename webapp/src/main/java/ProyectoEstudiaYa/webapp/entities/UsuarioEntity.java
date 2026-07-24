package ProyectoEstudiaYa.webapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
 
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false)
    private String nombre;
 
    @Column(nullable = false)
    private String apellido;
 
    @Column(nullable = false, unique = true)
    private String email;
 
    @Column(nullable = false)
    @JsonIgnore
    private String password;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'ESTUDIANTE'")
    private Rol rol = Rol.ESTUDIANTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelEducativo nivel; // PRIMARIA, SECUNDARIA
 
    @Column(nullable = false)
    private Integer grado; // 1 al 6 primaria, 1 al 5 secundaria
 
    private Integer xpTotal = 0;
    private Integer nivel_juego = 1;
    private Integer rachaActual = 0;
    private Integer rachaMasAlta = 0;
 
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;
 
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UsuarioCursoEntity> cursos;
 
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TareaEntity> tareas;
 
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ProgresoEntity> progresos;
 
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<LogroEntity> logros;
 
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SesionAuditoriaEntity> sesiones;
 
    public UsuarioEntity() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol != null ? rol : Rol.ESTUDIANTE;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public NivelEducativo getNivel() {
        return nivel;
    }

    public void setNivel(NivelEducativo nivel) {
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

    public Integer getNivel_juego() {
        return nivel_juego;
    }

    public void setNivel_juego(Integer nivel_juego) {
        this.nivel_juego = nivel_juego;
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

    public List<UsuarioCursoEntity> getCursos() {
        return cursos;
    }

    public void setCursos(List<UsuarioCursoEntity> cursos) {
        this.cursos = cursos;
    }

    public List<TareaEntity> getTareas() {
        return tareas;
    }

    public void setTareas(List<TareaEntity> tareas) {
        this.tareas = tareas;
    }

    public List<ProgresoEntity> getProgresos() {
        return progresos;
    }

    public void setProgresos(List<ProgresoEntity> progresos) {
        this.progresos = progresos;
    }

    public List<LogroEntity> getLogros() {
        return logros;
    }

    public void setLogros(List<LogroEntity> logros) {
        this.logros = logros;
    }

    public List<SesionAuditoriaEntity> getSesiones() {
        return sesiones;
    }

    public void setSesiones(List<SesionAuditoriaEntity> sesiones) {
        this.sesiones = sesiones;
    }
 
    public enum NivelEducativo {
        PRIMARIA, SECUNDARIA
    }

    public enum Rol {
        ADMIN, ESTUDIANTE
    }
}
