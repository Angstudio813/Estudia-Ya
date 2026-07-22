    package ProyectoEstudiaYa.webapp.repositories;

    import ProyectoEstudiaYa.webapp.entities.SesionAuditoriaEntity;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;
    import java.time.LocalDateTime;
    import java.util.List;

    @Repository
    public interface SesionAuditoriaRepository extends JpaRepository<SesionAuditoriaEntity, Long> {

        List<SesionAuditoriaEntity> findByUsuarioId(Long usuarioId);
        List<SesionAuditoriaEntity> findByUsuarioIdAndTipoEvento(Long usuarioId, SesionAuditoriaEntity.TipoEvento tipoEvento);
        long countByUsuarioIdAndTipoEvento(Long usuarioId, SesionAuditoriaEntity.TipoEvento tipoEvento);

        @Query("SELECT s FROM SesionAuditoriaEntity s WHERE s.usuario.id = :usuarioId AND s.fechaEvento BETWEEN :desde AND :hasta ORDER BY s.fechaEvento DESC")
        List<SesionAuditoriaEntity> findByUsuarioIdEntreFechas(
            @Param("usuarioId") Long usuarioId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
        );

        @Query("SELECT s FROM SesionAuditoriaEntity s WHERE s.usuario.id = :usuarioId ORDER BY s.fechaEvento DESC")
        List<SesionAuditoriaEntity> findUltimosEventos(@Param("usuarioId") Long usuarioId);

        @Query("""
            SELECT COUNT(s) FROM SesionAuditoriaEntity s
            WHERE s.usuario.id = :usuarioId
            AND s.tipoEvento IN (
                :salioViewport,
                :cambioPestana
            )
            AND s.fechaEvento BETWEEN :desde AND :hasta
            """)
        long contarEventosAntiTrampa(
            @Param("usuarioId") Long usuarioId,
            @Param("salioViewport") SesionAuditoriaEntity.TipoEvento salioViewport,
            @Param("cambioPestana") SesionAuditoriaEntity.TipoEvento cambioPestana,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
        );

        @Query("SELECT s.modulo, COUNT(s) FROM SesionAuditoriaEntity s WHERE s.usuario.id = :usuarioId AND s.tipoEvento = :tipoEvento GROUP BY s.modulo ORDER BY COUNT(s) DESC")
        List<Object[]> modulosMasVisitados(
            @Param("usuarioId") Long usuarioId,
            @Param("tipoEvento") SesionAuditoriaEntity.TipoEvento tipoEvento
        );
    }
