package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.dto.CursoInscritoProjectionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MisCursosRepository extends JpaRepository<ProyectoEstudiaYa.webapp.entities.CursoEntity, Long> {

    @Query(value = """
            SELECT
                c.id AS cursoId,
                c.nombre,
                c.descripcion,
                c.nivel,
                c.grado,
                c.color_hex AS colorHex,
                c.icono,
                uc.porcentaje_completado AS porcentajeCompletado,
                COUNT(DISTINCT t.id) AS totalTemas,
                COUNT(DISTINCT e.id) AS totalEjercicios,
                MIN(t.nombre) AS siguienteTemaNombre
            FROM usuario_cursos uc
            INNER JOIN cursos c ON c.id = uc.curso_id
            LEFT JOIN temas t ON t.curso_id = c.id
            LEFT JOIN ejercicios e ON e.tema_id = t.id
            WHERE uc.usuario_id = ?
            GROUP BY c.id, c.nombre, c.descripcion, c.nivel, c.grado, c.color_hex, c.icono, uc.porcentaje_completado
            """, nativeQuery = true)
    List<CursoInscritoProjectionDTO> findCursosInscritosOptimizado(Long usuarioId);

    @Query("SELECT c FROM CursoEntity c WHERE c.nivel = :nivel ORDER BY c.grado ASC")
    List<ProyectoEstudiaYa.webapp.entities.CursoEntity> findByNivel(@Param("nivel") ProyectoEstudiaYa.webapp.entities.UsuarioEntity.NivelEducativo nivel);

    @Query("SELECT c FROM CursoEntity c WHERE c.nivel = :nivel AND c.grado = :grado")
    List<ProyectoEstudiaYa.webapp.entities.CursoEntity> findByNivelAndGrado(
            @Param("nivel") ProyectoEstudiaYa.webapp.entities.UsuarioEntity.NivelEducativo nivel,
            @Param("grado") Integer grado);

    @Query("SELECT c FROM CursoEntity c WHERE c.grado = :grado ORDER BY c.nivel ASC")
    List<ProyectoEstudiaYa.webapp.entities.CursoEntity> findByGrado(@Param("grado") Integer grado);

    @Query("SELECT DISTINCT c FROM CursoEntity c JOIN c.temas t WHERE t.id = :temaId")
    ProyectoEstudiaYa.webapp.entities.CursoEntity findCursoByTemaId(@Param("temaId") Long temaId);

    @Query("SELECT COUNT(c) FROM CursoEntity c WHERE c.nivel = :nivel")
    Long countByNivel(@Param("nivel") ProyectoEstudiaYa.webapp.entities.UsuarioEntity.NivelEducativo nivel);

    @Query("SELECT DISTINCT c FROM CursoEntity c LEFT JOIN FETCH c.temas")
    List<ProyectoEstudiaYa.webapp.entities.CursoEntity> findAllWithTemas();

    @Query("SELECT DISTINCT c FROM CursoEntity c LEFT JOIN FETCH c.temas WHERE c.nivel = :nivel AND c.grado = :grado")
    List<ProyectoEstudiaYa.webapp.entities.CursoEntity> findByNivelAndGradoWithTemas(
            @Param("nivel") ProyectoEstudiaYa.webapp.entities.UsuarioEntity.NivelEducativo nivel,
            @Param("grado") Integer grado);
}
