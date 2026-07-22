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
                MIN(CASE WHEN t.orden > COALESCE(
                    (SELECT t2.orden FROM temas t2
                     JOIN progresos p ON p.tema_id = t2.id
                     WHERE p.usuario_id = :usuarioId AND t2.curso_id = c.id
                     AND p.porcentaje_acierto >= 80
                     ORDER BY t2.orden DESC LIMIT 1), 0)
                THEN t.orden ELSE NULL END) AS siguienteTemaOrden,
                (SELECT t3.nombre FROM temas t3
                 WHERE t3.curso_id = c.id
                 AND t3.orden > COALESCE(
                    (SELECT t4.orden FROM temas t4
                     JOIN progresos p2 ON p2.tema_id = t4.id
                     WHERE p2.usuario_id = :usuarioId AND t4.curso_id = c.id
                     AND p2.porcentaje_acierto >= 80
                     ORDER BY t4.orden DESC LIMIT 1), 0)
                 ORDER BY t3.orden ASC LIMIT 1) AS siguienteTemaNombre
            FROM usuario_cursos uc
            JOIN cursos c ON c.id = uc.curso_id
            LEFT JOIN temas t ON t.curso_id = c.id
            LEFT JOIN ejercicios e ON e.tema_id = t.id
            WHERE uc.usuario_id = :usuarioId
            GROUP BY c.id, c.nombre, c.descripcion, c.nivel, c.grado, c.color_hex, c.icono, uc.porcentaje_completado
            ORDER BY uc.ultima_practica DESC NULLS LAST
            """, nativeQuery = true)
    List<CursoInscritoProjectionDTO> findCursosInscritosOptimizado(@Param("usuarioId") Long usuarioId);

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
}
