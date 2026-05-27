package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.SesionAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SesionAuditoriaRepository extends JpaRepository<SesionAuditoria, Long> {

    List<SesionAuditoria> findByUsuarioId(Long usuarioId);
    List<SesionAuditoria> findByUsuarioIdAndTipoEvento(Long usuarioId, SesionAuditoria.TipoEvento tipoEvento);
    long countByUsuarioIdAndTipoEvento(Long usuarioId, SesionAuditoria.TipoEvento tipoEvento);

    // Eventos de un usuario en un rango de fechas (útil para reportes de actividad)
    @Query("SELECT s FROM SesionAuditoria s WHERE s.usuario.id = :usuarioId AND s.fechaEvento BETWEEN :desde AND :hasta ORDER BY s.fechaEvento DESC")
    List<SesionAuditoria> findByUsuarioIdEntreFechas(
        @Param("usuarioId") Long usuarioId,
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
    );

    // Últimos N eventos de un usuario (para mostrar actividad reciente)
    @Query("SELECT s FROM SesionAuditoria s WHERE s.usuario.id = :usuarioId ORDER BY s.fechaEvento DESC")
    List<SesionAuditoria> findUltimosEventos(@Param("usuarioId") Long usuarioId);

    // Conteo de intentos anti-trampa (SALIO_VIEWPORT + CAMBIO_PESTANA) en una sesión
    @Query("""
        SELECT COUNT(s) FROM SesionAuditoria s
        WHERE s.usuario.id = :usuarioId
        AND s.tipoEvento IN (
            ProyectoEstudiaYa.webapp.entities.SesionAuditoria.TipoEvento.SALIO_VIEWPORT,
            ProyectoEstudiaYa.webapp.entities.SesionAuditoria.TipoEvento.CAMBIO_PESTANA
        )
        AND s.fechaEvento BETWEEN :desde AND :hasta
        """)
    long contarEventosAntiTrampa(
        @Param("usuarioId") Long usuarioId,
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
    );

    // Módulos más visitados por un usuario
    @Query("SELECT s.modulo, COUNT(s) FROM SesionAuditoria s WHERE s.usuario.id = :usuarioId AND s.tipoEvento = ProyectoEstudiaYa.webapp.entities.SesionAuditoria.TipoEvento.VISITA_MODULO GROUP BY s.modulo ORDER BY COUNT(s) DESC")
    List<Object[]> modulosMasVisitados(@Param("usuarioId") Long usuarioId);
}
