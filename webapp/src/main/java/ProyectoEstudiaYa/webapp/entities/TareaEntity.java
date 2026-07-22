package ProyectoEstudiaYa.webapp.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "tareas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TareaEntity {

  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false)
    private String titulo;
 
    private String descripcion;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTarea estado; // PENDIENTE, EN_PROGRESO, COMPLETADA
 
    private LocalDate fechaVencimiento;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaCompletado;
 
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;
 
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private CursoEntity curso;
 
    public enum EstadoTarea {
        PENDIENTE, EN_PROGRESO, COMPLETADA
    }


}
