package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.dto.ProgresoResumenDTO;
import ProyectoEstudiaYa.webapp.dto.ProgresoTemaDTO;
import ProyectoEstudiaYa.webapp.entities.Progreso;
import ProyectoEstudiaYa.webapp.repositories.IntentoEjercicioRepository;
import ProyectoEstudiaYa.webapp.repositories.LogroRepository;
import ProyectoEstudiaYa.webapp.repositories.ProgresoRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ProgresoService {

    private final ProgresoRepository progresoRepository;
    private final IntentoEjercicioRepository intentoEjercicioRepository;
    private final LogroRepository logroRepository;

    public ProgresoService(
            ProgresoRepository progresoRepository,
            IntentoEjercicioRepository intentoEjercicioRepository,
            LogroRepository logroRepository
    ) {
        this.progresoRepository = progresoRepository;
        this.intentoEjercicioRepository = intentoEjercicioRepository;
        this.logroRepository = logroRepository;
    }

    public ProgresoResumenDTO obtenerProgresoUsuario(Long usuarioId) {
        List<Progreso> progresos = progresoRepository.findByUsuarioId(usuarioId);
        List<ProgresoTemaDTO> detalleTemas = Collections.emptyList();

        Double promedio = progresoRepository.promedioAciertosPorUsuario(usuarioId);
        double promedioAcierto = promedio == null ? 0.0 : promedio;
        long temasEnRefuerzo = progresoRepository.findByUsuarioIdAndNecesitaRefuerzo(usuarioId, true).size();

        long ejerciciosCorrectos = intentoEjercicioRepository.countByUsuarioIdAndEsCorrecta(usuarioId, true);
        long logrosTotales = logroRepository.countByUsuarioId(usuarioId);

        return new ProgresoResumenDTO(
                usuarioId,
                progresos.size(),
                promedioAcierto,
                temasEnRefuerzo,
                ejerciciosCorrectos,
                logrosTotales,
                detalleTemas
        );
    }
}
