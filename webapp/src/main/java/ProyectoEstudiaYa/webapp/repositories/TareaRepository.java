package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByUsuarioId(Long usuarioId);
    List<Tarea> findByUsuarioIdAndEstado(Long usuarioId, Tarea.EstadoTarea estado);
    List<Tarea> findByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);

    // Tareas vencidas (fecha de vencimiento pasada y no completadas)
    @Query("SELECT t FROM Tarea t WHERE t.usuario.id = :usuarioId AND t.fechaVencimiento < :hoy AND t.estado <> ProyectoEstudiaYa.webapp.entities.Tarea.EstadoTarea.COMPLETADA")
    List<Tarea> findTareasVencidas(@Param("usuarioId") Long usuarioId, @Param("hoy") LocalDate hoy);

    // Tareas próximas a vencer (en los próximos N días)
    @Query("SELECT t FROM Tarea t WHERE t.usuario.id = :usuarioId AND t.fechaVencimiento BETWEEN :hoy AND :limite AND t.estado <> ProyectoEstudiaYa.webapp.entities.Tarea.EstadoTarea.COMPLETADA ORDER BY t.fechaVencimiento ASC")
    List<Tarea> findTareasProximasAVencer(
        @Param("usuarioId") Long usuarioId,
        @Param("hoy") LocalDate hoy,
        @Param("limite") LocalDate limite
    );

    // Conteo de tareas por estado para un usuario
    @Query("SELECT t.estado, COUNT(t) FROM Tarea t WHERE t.usuario.id = :usuarioId GROUP BY t.estado")
    List<Object[]> contarTareasPorEstado(@Param("usuarioId") Long usuarioId);

    // Tareas de un usuario ordenadas por fecha de vencimiento más cercana
    @Query("SELECT t FROM Tarea t WHERE t.usuario.id = :usuarioId ORDER BY t.fechaVencimiento ASC NULLS LAST")
    List<Tarea> findByUsuarioIdOrdenadosPorVencimiento(@Param("usuarioId") Long usuarioId);
}