package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    List<Ejercicio> findByTemaId(Long temaId);
    List<Ejercicio> findByTemaIdAndDificultad(Long temaId, Ejercicio.Dificultad dificultad);
    List<Ejercicio> findByGeneradoPorIA(Boolean generadoPorIA);

 
    @Query("SELECT e FROM Ejercicio e WHERE e.tema.curso.id = :cursoId")
    List<Ejercicio> findByCursoId(@Param("cursoId") Long cursoId);


    @Query("""
        SELECT e FROM Ejercicio e
        WHERE e.tema.id = :temaId
        AND e.id NOT IN (
            SELECT ie.ejercicio.id FROM IntentoEjercicio ie WHERE ie.usuario.id = :usuarioId
        )
        """)
    List<Ejercicio> findEjerciciosNoIntentados(@Param("temaId") Long temaId, @Param("usuarioId") Long usuarioId);


    @Query("SELECT COUNT(e) FROM Ejercicio e WHERE e.tema.id = :temaId")
    Long contarPorTema(@Param("temaId") Long temaId);


    @Query("SELECT e FROM Ejercicio e WHERE e.tema.id = :temaId ORDER BY e.dificultad ASC")
    List<Ejercicio> findByTemaIdOrdenadosPorDificultad(@Param("temaId") Long temaId);
}
