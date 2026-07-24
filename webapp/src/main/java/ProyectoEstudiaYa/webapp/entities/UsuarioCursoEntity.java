package ProyectoEstudiaYa.webapp.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "usuario_cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class UsuarioCursoEntity {

 @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private CursoEntity curso;
 
    private Integer porcentajeCompletado = 0;
    private LocalDateTime fechaInscripcion;
    private LocalDateTime ultimaPractica;

}
