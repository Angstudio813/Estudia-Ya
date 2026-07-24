package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.dto.ProgresoStatsProjectionDTO;
import ProyectoEstudiaYa.webapp.entities.ProgresoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgresoRepository extends JpaRepository<ProgresoEntity, Long> {

    List<ProgresoEntity> findByUsuarioId(Long usuarioId);

    Optional<ProgresoEntity> findByUsuarioIdAndTemaId(Long usuarioId, Long temaId);

    List<ProgresoEntity> findByUsuarioIdAndNecesitaRefuerzo(Long usuarioId, Boolean necesitaRefuerzo);

    @Query(value = """
            SELECT
                COUNT(p.id) AS totalTemas,
                COALESCE(AVG(p.porcentaje_acierto), 0) AS promedioAcierto,
                SUM(CASE WHEN p.necesita_refuerzo = true THEN 1 ELSE 0 END) AS temasEnRefuerzo,
                COALESCE((SELECT COUNT(ie.id) FROM intentos_ejercicio ie WHERE ie.usuario_id = :usuarioId AND ie.es_correcta = true), 0) AS ejerciciosCorrectos,
                COALESCE((SELECT COUNT(l.id) FROM logros l WHERE l.usuario_id = :usuarioId), 0) AS logrosTotales
            FROM progresos p
            WHERE p.usuario_id = :usuarioId
            """, nativeQuery = true)
    ProgresoStatsProjectionDTO findStatsByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT AVG(p.porcentajeAcierto) FROM ProgresoEntity p WHERE p.usuario.id = :usuarioId")
    Double promedioAciertosPorUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT SUM(p.ejerciciosIntentados) FROM ProgresoEntity p WHERE p.usuario.id = :usuarioId")
    Long totalEjerciciosIntentados(@Param("usuarioId") Long usuarioId);

    @Query("SELECT p FROM ProgresoEntity p WHERE p.usuario.id = :usuarioId AND p.tema.curso.id = :cursoId")
    List<ProgresoEntity> findByUsuarioIdAndCursoId(@Param("usuarioId") Long usuarioId, @Param("cursoId") Long cursoId);

    @Query("SELECT p FROM ProgresoEntity p WHERE p.usuario.id = :usuarioId AND p.porcentajeAcierto >= 80 AND p.necesitaRefuerzo = false")
    List<ProgresoEntity> findTemasCompletados(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(p) FROM ProgresoEntity p WHERE p.usuario.id = :usuarioId AND p.necesitaRefuerzo = true")
    Long contarTemasConRefuerzo(@Param("usuarioId") Long usuarioId);

    @Query("SELECT p FROM ProgresoEntity p JOIN FETCH p.tema WHERE p.usuario.id = :usuarioId")
    List<ProgresoEntity> findByUsuarioIdWithTema(@Param("usuarioId") Long usuarioId);

    @Query("SELECT p FROM ProgresoEntity p JOIN FETCH p.tema WHERE p.usuario.id = :usuarioId AND p.tema.id IN :temaIds")
    List<ProgresoEntity> findByUsuarioIdAndTemaIdsIn(@Param("usuarioId") Long usuarioId, @Param("temaIds") List<Long> temaIds);

    @Query("SELECT p FROM ProgresoEntity p JOIN FETCH p.tema WHERE p.usuario.id = :usuarioId AND p.necesitaRefuerzo = :refuerzo")
    List<ProgresoEntity> findByUsuarioIdAndNecesitaRefuerzoWithTema(@Param("usuarioId") Long usuarioId, @Param("refuerzo") Boolean refuerzo);
}
