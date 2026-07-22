package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.LogroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LogroRepository extends JpaRepository<LogroEntity, Long> {

    List<LogroEntity> findByUsuarioId(Long usuarioId);
    List<LogroEntity> findByUsuarioIdAndTipo(Long usuarioId, LogroEntity.TipoLogro tipo);
    long countByUsuarioId(Long usuarioId);

    @Query("SELECT l FROM LogroEntity l WHERE l.usuario.id = :usuarioId ORDER BY l.fechaDesbloqueado DESC")
    List<LogroEntity> findByUsuarioIdOrdenadosPorFecha(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(l) > 0 FROM LogroEntity l WHERE l.usuario.id = :usuarioId AND l.nombre = :nombre")
    boolean existsByUsuarioIdAndNombre(@Param("usuarioId") Long usuarioId, @Param("nombre") String nombre);

    @Query("SELECT l.tipo, COUNT(l) FROM LogroEntity l WHERE l.usuario.id = :usuarioId GROUP BY l.tipo")
    List<Object[]> contarLogrosPorTipo(@Param("usuarioId") Long usuarioId);
}
