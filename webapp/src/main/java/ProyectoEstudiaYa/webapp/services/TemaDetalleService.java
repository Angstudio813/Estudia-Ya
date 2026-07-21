package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.dto.TemaDetalleDTO;
import ProyectoEstudiaYa.webapp.dto.TemaDetalleDTO.EjercicioResumenDTO;
import ProyectoEstudiaYa.webapp.entities.Ejercicio;
import ProyectoEstudiaYa.webapp.entities.Progreso;
import ProyectoEstudiaYa.webapp.entities.Tema;
import ProyectoEstudiaYa.webapp.repositories.EjercicioRepository;
import ProyectoEstudiaYa.webapp.repositories.ProgresoRepository;
import ProyectoEstudiaYa.webapp.repositories.TemaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemaDetalleService {

    private final TemaRepository temaRepository;
    private final EjercicioRepository ejercicioRepository;
    private final ProgresoRepository progresoRepository;

    public TemaDetalleService(TemaRepository temaRepository,
                              EjercicioRepository ejercicioRepository,
                              ProgresoRepository progresoRepository) {
        this.temaRepository = temaRepository;
        this.ejercicioRepository = ejercicioRepository;
        this.progresoRepository = progresoRepository;
    }

    public TemaDetalleDTO obtenerDetalle(Long temaId, Long usuarioId) {
        Tema tema = temaRepository.findById(temaId)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado"));

        List<Ejercicio> ejercicios = ejercicioRepository.findByTemaId(temaId);

        int ejerciciosResueltos = 0;
        double porcentajeAcierto = 0;
        boolean necesitaRefuerzo = false;

        if (usuarioId != null) {
            Optional<Progreso> progreso = progresoRepository.findByUsuarioIdAndTemaId(usuarioId, temaId);
            if (progreso.isPresent()) {
                Progreso p = progreso.get();
                ejerciciosResueltos = p.getEjerciciosIntentados() != null ? p.getEjerciciosIntentados() : 0;
                porcentajeAcierto = p.getPorcentajeAcierto() != null ? p.getPorcentajeAcierto() : 0;
                necesitaRefuerzo = p.getNecesitaRefuerzo() != null && p.getNecesitaRefuerzo();
            }
        }

        List<EjercicioResumenDTO> ejerciciosDTO = ejercicios.stream()
                .map(e -> new EjercicioResumenDTO(
                        e.getId(),
                        e.getPregunta(),
                        e.getDificultad() != null ? e.getDificultad().name() : "",
                        e.getGeneradoPorIA()))
                .toList();

        return new TemaDetalleDTO(
                tema.getId(),
                tema.getNombre(),
                tema.getDescripcion(),
                tema.getOrden(),
                tema.getCurso().getId(),
                tema.getCurso().getNombre(),
                tema.getCurso().getColorHex(),
                ejercicios.size(),
                ejerciciosResueltos,
                porcentajeAcierto,
                necesitaRefuerzo,
                ejerciciosDTO
        );
    }
}
