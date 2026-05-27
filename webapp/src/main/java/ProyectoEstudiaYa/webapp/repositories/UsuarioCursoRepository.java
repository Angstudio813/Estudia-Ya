package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.UsuarioCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioCursoRepository extends JpaRepository<UsuarioCurso, Long> {

    List<UsuarioCurso> findByUsuarioId(Long usuarioId);
    Optional<UsuarioCurso> findByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);
    boolean existsByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);

    // Cursos completados al 100% por el usuario
    @Query("SELECT uc FROM UsuarioCurso uc WHERE uc.usuario.id = :usuarioId AND uc.porcentajeCompletado = 100")
    List<UsuarioCurso> findCursosCompletados(@Param("usuarioId") Long usuarioId);

    // Promedio de progreso del usuario en todos sus cursos
    @Query("SELECT AVG(uc.porcentajeCompletado) FROM UsuarioCurso uc WHERE uc.usuario.id = :usuarioId")
    Double promedioProgresoGeneral(@Param("usuarioId") Long usuarioId);

    // Cursos ordenados por última práctica (más reciente primero)
    @Query("SELECT uc FROM UsuarioCurso uc WHERE uc.usuario.id = :usuarioId ORDER BY uc.ultimaPractica DESC NULLS LAST")
    List<UsuarioCurso> findByUsuarioIdOrdenadosPorActividad(@Param("usuarioId") Long usuarioId);

    // Cantidad de usuarios inscritos en un curso
    @Query("SELECT COUNT(uc) FROM UsuarioCurso uc WHERE uc.curso.id = :cursoId")
    Long contarUsuariosPorCurso(@Param("cursoId") Long cursoId);
}
