package ProyectoEstudiaYa.webapp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import java.util.List;
 
@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CursoEntity {

 @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false)
    private String nombre; // Matemática, Comunicación, etc.
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UsuarioEntity.NivelEducativo nivel; // PRIMARIA, SECUNDARIA
 
    @Column(nullable = false)
    private Integer grado; // 1 al 6 o 1 al 5
 
    private String descripcion;
    private String colorHex;  // color por curso ej: #378ADD
    private String icono;     // emoji o nombre del icono
 
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    @BatchSize(size = 25)
    private List<TemaEntity> temas;
 
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private List<UsuarioCursoEntity> usuarios;
 
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private List<TareaEntity> tareas;


}
