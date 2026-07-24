package ProyectoEstudiaYa.webapp.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "progresos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProgresoEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tema_id", nullable = false)
    private TemaEntity tema;
 
    private Integer ejerciciosIntentados = 0;
    private Integer ejerciciosCorrectos = 0;
    private Double porcentajeAcierto = 0.0; 
    
    private LocalDateTime ultimaPractica;
 

    private Boolean necesitaRefuerzo = false;

}
