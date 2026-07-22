package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.CursoEntity;
import ProyectoEstudiaYa.webapp.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<CursoEntity, Long> {

    List<CursoEntity> findByNivelAndGrado(UsuarioEntity.NivelEducativo nivel, Integer grado);
    List<CursoEntity> findByNivel(UsuarioEntity.NivelEducativo nivel);

    
    @Query("SELECT uc.curso FROM UsuarioCursoEntity uc WHERE uc.usuario.id = :usuarioId")
    List<CursoEntity> findCursosDeUsuario(@Param("usuarioId") Long usuarioId);

    @Query("""
        SELECT c FROM CursoEntity c
        WHERE c.nivel = :nivel AND c.grado = :grado
        AND c.id NOT IN (
            SELECT uc.curso.id FROM UsuarioCursoEntity uc WHERE uc.usuario.id = :usuarioId
        )
        """)
    List<CursoEntity> findCursosNoInscritos(
        @Param("usuarioId") Long usuarioId,
        @Param("nivel") UsuarioEntity.NivelEducativo nivel,
        @Param("grado") Integer grado
    );

  
    @Query("SELECT c.nombre, COUNT(t) FROM CursoEntity c JOIN c.temas t WHERE c.id = :cursoId GROUP BY c.nombre")
    List<Object[]> contarTemasPorCurso(@Param("cursoId") Long cursoId);
}