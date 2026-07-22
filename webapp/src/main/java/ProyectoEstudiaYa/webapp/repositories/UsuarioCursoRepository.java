package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.UsuarioCursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioCursoRepository extends JpaRepository<UsuarioCursoEntity, Long> {

    List<UsuarioCursoEntity> findByUsuarioId(Long usuarioId);
    Optional<UsuarioCursoEntity> findByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);
    boolean existsByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);

    @Query("SELECT uc FROM UsuarioCursoEntity uc WHERE uc.usuario.id = :usuarioId AND uc.porcentajeCompletado = 100")
    List<UsuarioCursoEntity> findCursosCompletados(@Param("usuarioId") Long usuarioId);

    @Query("SELECT AVG(uc.porcentajeCompletado) FROM UsuarioCursoEntity uc WHERE uc.usuario.id = :usuarioId")
    Double promedioProgresoGeneral(@Param("usuarioId") Long usuarioId);

    @Query("SELECT uc FROM UsuarioCursoEntity uc WHERE uc.usuario.id = :usuarioId ORDER BY uc.ultimaPractica DESC NULLS LAST")
    List<UsuarioCursoEntity> findByUsuarioIdOrdenadosPorActividad(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(uc) FROM UsuarioCursoEntity uc WHERE uc.curso.id = :cursoId")
    Long contarUsuariosPorCurso(@Param("cursoId") Long cursoId);
}
