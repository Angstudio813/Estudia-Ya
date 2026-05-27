package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.Logro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LogroRepository extends JpaRepository<Logro, Long> {

    List<Logro> findByUsuarioId(Long usuarioId);
    List<Logro> findByUsuarioIdAndTipo(Long usuarioId, Logro.TipoLogro tipo);
    long countByUsuarioId(Long usuarioId);

    // Logros de un usuario ordenados por fecha de desbloqueo (más reciente primero)
    @Query("SELECT l FROM Logro l WHERE l.usuario.id = :usuarioId ORDER BY l.fechaDesbloqueado DESC")
    List<Logro> findByUsuarioIdOrdenadosPorFecha(@Param("usuarioId") Long usuarioId);

    // Verificar si un usuario ya tiene un logro con un nombre específico (evitar duplicados)
    @Query("SELECT COUNT(l) > 0 FROM Logro l WHERE l.usuario.id = :usuarioId AND l.nombre = :nombre")
    boolean existsByUsuarioIdAndNombre(@Param("usuarioId") Long usuarioId, @Param("nombre") String nombre);

    // Cantidad de logros por tipo para un usuario
    @Query("SELECT l.tipo, COUNT(l) FROM Logro l WHERE l.usuario.id = :usuarioId GROUP BY l.tipo")
    List<Object[]> contarLogrosPorTipo(@Param("usuarioId") Long usuarioId);
}
