package ProyectoEstudiaYa.webapp.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "logros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LogroEntity {

@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false)
    private String nombre; // "Primera racha", "10 correctas seguidas"
 
    private String descripcion;
    private String icono; // emoji o nombre del icono
 
    @Enumerated(EnumType.STRING)
    private TipoLogro tipo; // RACHA, EJERCICIOS, CURSO, ESPECIAL
 
    private LocalDateTime fechaDesbloqueado;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;
 
    public enum TipoLogro {
        RACHA, EJERCICIOS, CURSO, ESPECIAL
    }

}
