package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.EjercicioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PracticaInteligenteRepository extends JpaRepository<EjercicioEntity, Long> {

    /**
     * Busca todos los ejercicios de un tema específico
     */
    @Query("SELECT e FROM EjercicioEntity e WHERE e.tema.id = :temaId ORDER BY e.dificultad ASC")
    List<EjercicioEntity> findByTemaId(@Param("temaId") Long temaId);

    /**
     * Busca ejercicios por nivel de dificultad
     */
    @Query("SELECT e FROM EjercicioEntity e WHERE e.dificultad = :dificultad ORDER BY e.tema.id ASC")
    List<EjercicioEntity> findByDificultad(@Param("dificultad") EjercicioEntity.Dificultad dificultad);

    /**
     * Busca ejercicios generados por IA
     */
    @Query("SELECT e FROM EjercicioEntity e WHERE e.generadoPorIA = :generadoPorIA ORDER BY e.dificultad ASC")
    List<EjercicioEntity> findByGeneradoPorIA(@Param("generadoPorIA") Boolean generadoPorIA);

    /**
     * Busca ejercicios de un tema con dificultad específica
     */
    @Query("SELECT e FROM EjercicioEntity e WHERE e.tema.id = :temaId AND e.dificultad = :dificultad ORDER BY e.id ASC")
    List<EjercicioEntity> findByTemaIdAndDificultad(
            @Param("temaId") Long temaId,
            @Param("dificultad") EjercicioEntity.Dificultad dificultad);

    /**
     * Busca ejercicios de un curso específico
     */
    @Query("SELECT e FROM EjercicioEntity e JOIN e.tema t WHERE t.curso.id = :cursoId ORDER BY t.id ASC, e.dificultad ASC")
    List<EjercicioEntity> findByCursoId(@Param("cursoId") Long cursoId);

    /**
     * Cuenta los ejercicios generados por IA en un tema
     */
    @Query("SELECT COUNT(e) FROM EjercicioEntity e WHERE e.tema.id = :temaId AND e.generadoPorIA = true")
    Long countGeneradosPorIAByTemaId(@Param("temaId") Long temaId);

    /**
     * Busca ejercicios sin resolver (que no tienen intentos)
     */
    @Query("SELECT e FROM EjercicioEntity e WHERE e.tema.id = :temaId AND SIZE(e.intentos) = 0")
    List<EjercicioEntity> findNoResolvedByTemaId(@Param("temaId") Long temaId);
}
