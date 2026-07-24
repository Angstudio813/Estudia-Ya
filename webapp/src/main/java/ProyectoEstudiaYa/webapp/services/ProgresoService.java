package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.dto.ProgresoResumenDTO;
import ProyectoEstudiaYa.webapp.dto.ProgresoStatsProjectionDTO;
import ProyectoEstudiaYa.webapp.dto.ProgresoTemaDTO;
import ProyectoEstudiaYa.webapp.entities.ProgresoEntity;
import ProyectoEstudiaYa.webapp.repositories.ProgresoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProgresoService {

    private final ProgresoRepository progresoRepository;

    public ProgresoService(ProgresoRepository progresoRepository) {
        this.progresoRepository = progresoRepository;
    }

    @Transactional(readOnly = true)
    public ProgresoResumenDTO obtenerProgresoUsuario(Long usuarioId) {
        List<ProgresoEntity> progresos = progresoRepository.findByUsuarioIdWithTema(usuarioId);

        List<ProgresoTemaDTO> detalleTemas = progresos.stream()
                .map(p -> new ProgresoTemaDTO(
                        p.getTema() != null ? p.getTema().getNombre() : "TemaEntity desconocido",
                        p.getEjerciciosIntentados(),
                        p.getEjerciciosCorrectos(),
                        p.getPorcentajeAcierto(),
                        p.getNecesitaRefuerzo()))
                .toList();

        ProgresoStatsProjectionDTO stats = progresoRepository.findStatsByUsuarioId(usuarioId);

        if (stats == null) {
            return new ProgresoResumenDTO(usuarioId, 0, 0.0, 0, 0, 0, detalleTemas);
        }

        return new ProgresoResumenDTO(
                usuarioId,
                stats.getTotalTemas() != null ? stats.getTotalTemas().intValue() : 0,
                stats.getPromedioAcierto() != null ? stats.getPromedioAcierto() : 0.0,
                stats.getTemasEnRefuerzo() != null ? stats.getTemasEnRefuerzo() : 0,
                stats.getEjerciciosCorrectos() != null ? stats.getEjerciciosCorrectos() : 0,
                stats.getLogrosTotales() != null ? stats.getLogrosTotales() : 0,
                detalleTemas);
    }
}
