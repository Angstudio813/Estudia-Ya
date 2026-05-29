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

        @Query("SELECT s FROM SesionAuditoria s WHERE s.usuario.id = :usuarioId AND s.fechaEvento BETWEEN :desde AND :hasta ORDER BY s.fechaEvento DESC")
        List<SesionAuditoria> findByUsuarioIdEntreFechas(
            @Param("usuarioId") Long usuarioId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
        );

        @Query("SELECT s FROM SesionAuditoria s WHERE s.usuario.id = :usuarioId ORDER BY s.fechaEvento DESC")
        List<SesionAuditoria> findUltimosEventos(@Param("usuarioId") Long usuarioId);

        @Query("""
            SELECT COUNT(s) FROM SesionAuditoria s
            WHERE s.usuario.id = :usuarioId
            AND s.tipoEvento IN (
                :salioViewport,
                :cambioPestana
            )
            AND s.fechaEvento BETWEEN :desde AND :hasta
            """)
        long contarEventosAntiTrampa(
            @Param("usuarioId") Long usuarioId,
            @Param("salioViewport") SesionAuditoria.TipoEvento salioViewport,
            @Param("cambioPestana") SesionAuditoria.TipoEvento cambioPestana,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
        );

        @Query("SELECT s.modulo, COUNT(s) FROM SesionAuditoria s WHERE s.usuario.id = :usuarioId AND s.tipoEvento = :tipoEvento GROUP BY s.modulo ORDER BY COUNT(s) DESC")
        List<Object[]> modulosMasVisitados(
            @Param("usuarioId") Long usuarioId,
            @Param("tipoEvento") SesionAuditoria.TipoEvento tipoEvento
        );
    }
