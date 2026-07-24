package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.dto.CursoDetalleDTO;
import ProyectoEstudiaYa.webapp.dto.CursoDetalleDTO.TemaResumenDTO;
import ProyectoEstudiaYa.webapp.entities.CursoEntity;
import ProyectoEstudiaYa.webapp.entities.EjercicioEntity;
import ProyectoEstudiaYa.webapp.entities.ProgresoEntity;
import ProyectoEstudiaYa.webapp.entities.TemaEntity;
import ProyectoEstudiaYa.webapp.entities.UsuarioCursoEntity;
import ProyectoEstudiaYa.webapp.repositories.CursoRepository;
import ProyectoEstudiaYa.webapp.repositories.EjercicioRepository;
import ProyectoEstudiaYa.webapp.repositories.ProgresoRepository;
import ProyectoEstudiaYa.webapp.repositories.UsuarioCursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoDetalleService {

    private final CursoRepository cursoRepository;
    private final UsuarioCursoRepository usuarioCursoRepository;
    private final EjercicioRepository ejercicioRepository;
    private final ProgresoRepository progresoRepository;

    public CursoDetalleService(CursoRepository cursoRepository,
                               UsuarioCursoRepository usuarioCursoRepository,
                               EjercicioRepository ejercicioRepository,
                               ProgresoRepository progresoRepository) {
        this.cursoRepository = cursoRepository;
        this.usuarioCursoRepository = usuarioCursoRepository;
        this.ejercicioRepository = ejercicioRepository;
        this.progresoRepository = progresoRepository;
    }

    @Transactional(readOnly = true)
    public CursoDetalleDTO obtenerDetalle(Long cursoId, Long usuarioId) {
        CursoEntity curso = cursoRepository.findWithTemasById(cursoId)
                .orElseThrow(() -> new RuntimeException("CursoEntity no encontrado"));

        List<TemaEntity> temas = curso.getTemas() != null ? curso.getTemas() : List.of();
        temas.sort((a, b) -> {
            Integer ordenA = a.getOrden() != null ? a.getOrden() : 0;
            Integer ordenB = b.getOrden() != null ? b.getOrden() : 0;
            return ordenA.compareTo(ordenB);
        });

        int totalEjercicios = temas.stream()
                .mapToInt(t -> t.getEjercicios() != null ? t.getEjercicios().size() : 0)
                .sum();

        Integer progreso = 0;
        if (usuarioId != null) {
            Optional<UsuarioCursoEntity> inscripcion = usuarioCursoRepository.findByUsuarioIdAndCursoId(usuarioId, cursoId);
            progreso = inscripcion.map(UsuarioCursoEntity::getPorcentajeCompletado).orElse(0);
        }

        Map<Long, ProgresoEntity> progresosPorTema = Map.of();
        if (usuarioId != null) {
            List<Long> temaIds = temas.stream().map(TemaEntity::getId).toList();
            if (!temaIds.isEmpty()) {
                progresosPorTema = progresoRepository.findByUsuarioIdAndTemaIdsIn(usuarioId, temaIds)
                        .stream()
                        .collect(Collectors.toMap(p -> p.getTema().getId(), p -> p));
            }
        }

        final Map<Long, ProgresoEntity> progresosFinales = progresosPorTema;
        List<TemaResumenDTO> temasDTO = temas.stream()
                .map(tema -> construirTemaResumen(tema, progresosFinales.get(tema.getId())))
                .toList();

        return new CursoDetalleDTO(
                curso.getId(),
                curso.getNombre(),
                curso.getDescripcion(),
                curso.getNivel() != null ? curso.getNivel().name() : "",
                curso.getGrado(),
                curso.getColorHex(),
                curso.getIcono(),
                temas.size(),
                totalEjercicios,
                progreso != null ? progreso : 0,
                obtenerEstado(progreso != null ? progreso : 0),
                temasDTO
        );
    }

    private TemaResumenDTO construirTemaResumen(TemaEntity tema, ProgresoEntity progreso) {
        int totalEjercicios = tema.getEjercicios() != null ? tema.getEjercicios().size() : 0;
        int ejerciciosResueltos = 0;
        double porcentajeAcierto = 0;
        boolean necesitaRefuerzo = false;

        if (progreso != null) {
            ejerciciosResueltos = progreso.getEjerciciosIntentados() != null ? progreso.getEjerciciosIntentados() : 0;
            porcentajeAcierto = progreso.getPorcentajeAcierto() != null ? progreso.getPorcentajeAcierto() : 0;
            needsRefuerzo(progreso);
            necesitaRefuerzo = progreso.getNecesitaRefuerzo() != null ? progreso.getNecesitaRefuerzo() : false;
        }

        String estado;
        if (totalEjercicios == 0) {
            estado = "Sin ejercicios";
        } else if (ejerciciosResueltos >= totalEjercicios && porcentajeAcierto >= 70) {
            estado = "Completado";
        } else if (ejerciciosResueltos > 0) {
            estado = "En progreso";
        } else {
            estado = "Por iniciar";
        }

        return new TemaResumenDTO(
                tema.getId(),
                tema.getNombre(),
                tema.getDescripcion(),
                tema.getOrden(),
                totalEjercicios,
                ejerciciosResueltos,
                porcentajeAcierto,
                necesitaRefuerzo,
                estado
        );
    }

    private void needsRefuerzo(ProgresoEntity p) {
        if (p.getPorcentajeAcierto() != null && p.getPorcentajeAcierto() < 50) {
            p.setNecesitaRefuerzo(true);
        }
    }

    private String obtenerEstado(Integer progreso) {
        if (progreso >= 100) return "Completado";
        if (progreso >= 60) return "En buen ritmo";
        if (progreso > 0) return "En progreso";
        return "Por iniciar";
    }
}
