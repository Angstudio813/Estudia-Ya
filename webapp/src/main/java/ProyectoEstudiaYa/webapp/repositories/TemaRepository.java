package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.Tema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TemaRepository extends JpaRepository<Tema, Long> {

    List<Tema> findByCursoIdOrderByOrden(Long cursoId);

@Query("SELECT t FROM Tema t WHERE t.curso.id = :cursoId AND LOWER(t.nombre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
List<Tema> findByCursoIdAndNombreContiene(@Param("cursoId") Long cursoId, @Param("keyword") String keyword);

@Query("SELECT COUNT(t) FROM Tema t WHERE t.curso.id = :cursoId")
Long contarTemasPorCurso(@Param("cursoId") Long cursoId);

}
