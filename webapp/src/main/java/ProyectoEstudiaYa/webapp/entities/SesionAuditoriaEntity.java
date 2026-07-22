package ProyectoEstudiaYa.webapp.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
 
@Entity
@Table(name = "sesiones_auditoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SesionAuditoriaEntity {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;
 
    @Column(nullable = false)
    private String modulo; // "PRACTICA", "CURSOS", "DASHBOARD", etc.
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvento tipoEvento;
 
    private String descripcion; // detalle del evento
 
    private String ipAddress;
    private LocalDateTime fechaEvento;
 
    public enum TipoEvento {
        LOGIN,
        LOGOUT,
        VISITA_MODULO,
        INICIO_EJERCICIO,
        FIN_EJERCICIO,
        SALIO_VIEWPORT,      // anti-trampa: salió de la pantalla
        CAMBIO_PESTANA,      // anti-trampa: cambió de pestaña
        SUBIO_SILABO,
        DESBLOQUEO_LOGRO
    }


}
