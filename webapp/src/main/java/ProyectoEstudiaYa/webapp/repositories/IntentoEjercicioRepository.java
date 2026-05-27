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


    @Query("SELECT ie FROM IntentoEjercicio ie WHERE ie.usuario.id = :usuarioId AND ie.ejercicio.id = :ejercicioId ORDER BY ie.fechaIntento DESC")
    List<IntentoEjercicio> findUltimosIntentos(@Param("usuarioId") Long usuarioId, @Param("ejercicioId") Long ejercicioId);


    @Query("SELECT ie FROM IntentoEjercicio ie WHERE ie.usuario.id = :usuarioId AND ie.ejercicio.tema.id = :temaId")
    List<IntentoEjercicio> findByUsuarioIdAndTemaId(@Param("usuarioId") Long usuarioId, @Param("temaId") Long temaId);


    @Query("SELECT AVG(ie.tiempoSegundos) FROM IntentoEjercicio ie WHERE ie.usuario.id = :usuarioId")
    Double promedioTiempoRespuesta(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(ie) FROM IntentoEjercicio ie WHERE ie.usuario.id = :usuarioId AND ie.ejercicio.tema.id = :temaId AND ie.esCorrecta = true")
    Long contarCorrectosPorTema(@Param("usuarioId") Long usuarioId, @Param("temaId") Long temaId);
}
