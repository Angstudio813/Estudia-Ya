package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.EjercicioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EjercicioRepository extends JpaRepository<EjercicioEntity, Long> {

    List<EjercicioEntity> findByTemaId(Long temaId);
    List<EjercicioEntity> findByTemaIdAndDificultad(Long temaId, EjercicioEntity.Dificultad dificultad);
    List<EjercicioEntity> findByGeneradoPorIA(Boolean generadoPorIA);

 
    @Query("SELECT e FROM EjercicioEntity e WHERE e.tema.curso.id = :cursoId")
    List<EjercicioEntity> findByCursoId(@Param("cursoId") Long cursoId);


    @Query("""
        SELECT e FROM EjercicioEntity e
        WHERE e.tema.id = :temaId
        AND e.id NOT IN (
            SELECT ie.ejercicio.id FROM IntentoEjercicioEntity ie WHERE ie.usuario.id = :usuarioId
        )
        """)
    List<EjercicioEntity> findEjerciciosNoIntentados(@Param("temaId") Long temaId, @Param("usuarioId") Long usuarioId);


    @Query("SELECT COUNT(e) FROM EjercicioEntity e WHERE e.tema.id = :temaId")
    Long contarPorTema(@Param("temaId") Long temaId);


    @Query("SELECT e FROM EjercicioEntity e WHERE e.tema.id = :temaId ORDER BY e.dificultad ASC")
    List<EjercicioEntity> findByTemaIdOrdenadosPorDificultad(@Param("temaId") Long temaId);
}
