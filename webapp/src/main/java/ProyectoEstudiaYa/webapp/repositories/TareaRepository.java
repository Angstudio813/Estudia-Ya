package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.TareaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<TareaEntity, Long> {

    List<TareaEntity> findByUsuarioId(Long usuarioId);
    List<TareaEntity> findByUsuarioIdAndEstado(Long usuarioId, TareaEntity.EstadoTarea estado);
    List<TareaEntity> findByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);

    @Query("SELECT t FROM TareaEntity t WHERE t.usuario.id = :usuarioId AND t.fechaVencimiento < :hoy AND t.estado <> :estadoCompletada")
    List<TareaEntity> findTareasVencidas(@Param("usuarioId") Long usuarioId, @Param("hoy") LocalDate hoy, @Param("estadoCompletada") TareaEntity.EstadoTarea estadoCompletada);

    @Query("SELECT t FROM TareaEntity t WHERE t.usuario.id = :usuarioId AND t.fechaVencimiento BETWEEN :hoy AND :limite AND t.estado <> :estadoCompletada ORDER BY t.fechaVencimiento ASC")
    List<TareaEntity> findTareasProximasAVencer(
        @Param("usuarioId") Long usuarioId,
        @Param("hoy") LocalDate hoy,
        @Param("limite") LocalDate limite,
        @Param("estadoCompletada") TareaEntity.EstadoTarea estadoCompletada
    );

    @Query("SELECT t.estado, COUNT(t) FROM TareaEntity t WHERE t.usuario.id = :usuarioId GROUP BY t.estado")
    List<Object[]> contarTareasPorEstado(@Param("usuarioId") Long usuarioId);

    @Query("SELECT t FROM TareaEntity t WHERE t.usuario.id = :usuarioId ORDER BY t.fechaVencimiento ASC NULLS LAST")
    List<TareaEntity> findByUsuarioIdOrdenadosPorVencimiento(@Param("usuarioId") Long usuarioId);
}