package ProyectoEstudiaYa.webapp.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "intentos_ejercicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class IntentoEjercicioEntity {

      @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejercicio_id", nullable = false)
    private EjercicioEntity ejercicio;
 
    private String respuestaElegida; // "A", "B", "C" o "D"
    private Boolean esCorrecta;
    private LocalDateTime fechaIntento;
    private Integer tiempoSegundos; // cuánto tardó en responder

}
