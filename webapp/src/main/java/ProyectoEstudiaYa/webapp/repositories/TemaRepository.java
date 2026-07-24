package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.TemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TemaRepository extends JpaRepository<TemaEntity, Long> {

    List<TemaEntity> findByCursoIdOrderByOrden(Long cursoId);

    @Query("SELECT t FROM TemaEntity t JOIN FETCH t.curso WHERE t.id = :id")
    Optional<TemaEntity> findWithCursoById(@Param("id") Long id);

    @Query("SELECT DISTINCT t FROM TemaEntity t LEFT JOIN FETCH t.ejercicios WHERE t.curso.id = :cursoId ORDER BY t.orden")
    List<TemaEntity> findByCursoIdOrderByOrdenWithEjercicios(@Param("cursoId") Long cursoId);

@Query("SELECT t FROM TemaEntity t WHERE t.curso.id = :cursoId AND LOWER(t.nombre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
List<TemaEntity> findByCursoIdAndNombreContiene(@Param("cursoId") Long cursoId, @Param("keyword") String keyword);

@Query("SELECT COUNT(t) FROM TemaEntity t WHERE t.curso.id = :cursoId")
Long contarTemasPorCurso(@Param("cursoId") Long cursoId);

}
