package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.Progreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgresoRepository extends JpaRepository<Progreso, Long> {

    List<Progreso> findByUsuarioId(Long usuarioId);

    Optional<Progreso> findByUsuarioIdAndTemaId(Long usuarioId, Long temaId);

    // la IA usa esto para saber qué temas reforzar
    List<Progreso> findByUsuarioIdAndNecesitaRefuerzo(Long usuarioId, Boolean necesitaRefuerzo);

    // Promedio de aciertos de un usuario en todos sus temas
    @Query("SELECT AVG(p.porcentajeAcierto) FROM Progreso p WHERE p.usuario.id = :usuarioId")
    Double promedioAciertosPorUsuario(@Param("usuarioId") Long usuarioId);

    // Total de ejercicios intentados por un usuario
    @Query("SELECT SUM(p.ejerciciosIntentados) FROM Progreso p WHERE p.usuario.id = :usuarioId")
    Long totalEjerciciosIntentados(@Param("usuarioId") Long usuarioId);

    // Progresos de un usuario dentro de un curso específico (via tema->curso)
    @Query("SELECT p FROM Progreso p WHERE p.usuario.id = :usuarioId AND p.tema.curso.id = :cursoId")
    List<Progreso> findByUsuarioIdAndCursoId(@Param("usuarioId") Long usuarioId, @Param("cursoId") Long cursoId);

    // Temas completados al 100% (sin necesidad de refuerzo y con acierto >= 80)
    @Query("SELECT p FROM Progreso p WHERE p.usuario.id = :usuarioId AND p.porcentajeAcierto >= 80 AND p.necesitaRefuerzo = false")
    List<Progreso> findTemasCompletados(@Param("usuarioId") Long usuarioId);

    // Conteo de temas que necesitan refuerzo para un usuario
    @Query("SELECT COUNT(p) FROM Progreso p WHERE p.usuario.id = :usuarioId AND p.necesitaRefuerzo = true")
    Long contarTemasConRefuerzo(@Param("usuarioId") Long usuarioId);
}
