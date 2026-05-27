package ProyectoEstudiaYa.webapp.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
 
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Usuario {

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
    private String password;
 
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
    private List<UsuarioCurso> cursos;
 
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Tarea> tareas;
 
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Progreso> progresos;
 
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Logro> logros;
 
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<SesionAuditoria> sesiones;
 
    public enum NivelEducativo {
        PRIMARIA, SECUNDARIA
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
