package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.Curso;
import ProyectoEstudiaYa.webapp.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MisCursosRepository extends JpaRepository<Curso, Long> {

    /**
     * Busca todos los cursos de un nivel educativo específico
     */
    @Query("SELECT c FROM Curso c WHERE c.nivel = :nivel ORDER BY c.grado ASC")
    List<Curso> findByNivel(@Param("nivel") Usuario.NivelEducativo nivel);

    /**
     * Busca cursos por nivel y grado
     */
    @Query("SELECT c FROM Curso c WHERE c.nivel = :nivel AND c.grado = :grado")
    List<Curso> findByNivelAndGrado(
            @Param("nivel") Usuario.NivelEducativo nivel,
            @Param("grado") Integer grado);

    /**
     * Busca todos los cursos de un grado específico
     */
    @Query("SELECT c FROM Curso c WHERE c.grado = :grado ORDER BY c.nivel ASC")
    List<Curso> findByGrado(@Param("grado") Integer grado);

    /**
     * Busca cursos que contienen un tema específico
     */
    @Query("SELECT DISTINCT c FROM Curso c JOIN c.temas t WHERE t.id = :temaId")
    Curso findCursoByTemaId(@Param("temaId") Long temaId);

    /**
     * Cuenta los cursos por nivel
     */
    @Query("SELECT COUNT(c) FROM Curso c WHERE c.nivel = :nivel")
    Long countByNivel(@Param("nivel") Usuario.NivelEducativo nivel);
}
