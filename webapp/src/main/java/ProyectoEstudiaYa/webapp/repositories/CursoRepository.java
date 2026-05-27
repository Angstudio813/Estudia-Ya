package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.Curso;
import ProyectoEstudiaYa.webapp.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findByNivelAndGrado(Usuario.NivelEducativo nivel, Integer grado);
    List<Curso> findByNivel(Usuario.NivelEducativo nivel);

    
    @Query("SELECT uc.curso FROM UsuarioCurso uc WHERE uc.usuario.id = :usuarioId")
    List<Curso> findCursosDeUsuario(@Param("usuarioId") Long usuarioId);

    @Query("""
        SELECT c FROM Curso c
        WHERE c.nivel = :nivel AND c.grado = :grado
        AND c.id NOT IN (
            SELECT uc.curso.id FROM UsuarioCurso uc WHERE uc.usuario.id = :usuarioId
        )
        """)
    List<Curso> findCursosNoInscritos(
        @Param("usuarioId") Long usuarioId,
        @Param("nivel") Usuario.NivelEducativo nivel,
        @Param("grado") Integer grado
    );

  
    @Query("SELECT c.nombre, COUNT(t) FROM Curso c JOIN c.temas t WHERE c.id = :cursoId GROUP BY c.nombre")
    List<Object[]> contarTemasPorCurso(@Param("cursoId") Long cursoId);
}