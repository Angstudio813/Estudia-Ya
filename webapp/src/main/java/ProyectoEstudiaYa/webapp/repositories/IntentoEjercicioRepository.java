package ProyectoEstudiaYa.webapp.repositories;

import ProyectoEstudiaYa.webapp.entities.IntentoEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IntentoEjercicioRepository extends JpaRepository<IntentoEjercicio, Long> {

    List<IntentoEjercicio> findByUsuarioId(Long usuarioId);
    List<IntentoEjercicio> findByUsuarioIdAndEjercicioId(Long usuarioId, Long ejercicioId);
    long countByUsuarioIdAndEsCorrecta(Long usuarioId, Boolean esCorrecta);

    // Último intento de un usuario en un ejercicio específico
    @Query("SELECT ie FROM IntentoEjercicio ie WHERE ie.usuario.id = :usuarioId AND ie.ejercicio.id = :ejercicioId ORDER BY ie.fechaIntento DESC")
    List<IntentoEjercicio> findUltimosIntentos(@Param("usuarioId") Long usuarioId, @Param("ejercicioId") Long ejercicioId);

    // Todos los intentos de un usuario en un tema
    @Query("SELECT ie FROM IntentoEjercicio ie WHERE ie.usuario.id = :usuarioId AND ie.ejercicio.tema.id = :temaId")
    List<IntentoEjercicio> findByUsuarioIdAndTemaId(@Param("usuarioId") Long usuarioId, @Param("temaId") Long temaId);

    // Promedio de tiempo de respuesta de un usuario (en segundos)
    @Query("SELECT AVG(ie.tiempoSegundos) FROM IntentoEjercicio ie WHERE ie.usuario.id = :usuarioId")
    Double promedioTiempoRespuesta(@Param("usuarioId") Long usuarioId);

    // Intentos correctos de un usuario en un tema específico
    @Query("SELECT COUNT(ie) FROM IntentoEjercicio ie WHERE ie.usuario.id = :usuarioId AND ie.ejercicio.tema.id = :temaId AND ie.esCorrecta = true")
    Long contarCorrectosPorTema(@Param("usuarioId") Long usuarioId, @Param("temaId") Long temaId);
}
