package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.dto.CursoDetalleDTO;
import ProyectoEstudiaYa.webapp.dto.CursoDetalleDTO.TemaResumenDTO;
import ProyectoEstudiaYa.webapp.entities.Curso;
import ProyectoEstudiaYa.webapp.entities.Ejercicio;
import ProyectoEstudiaYa.webapp.entities.Progreso;
import ProyectoEstudiaYa.webapp.entities.Tema;
import ProyectoEstudiaYa.webapp.entities.UsuarioCurso;
import ProyectoEstudiaYa.webapp.repositories.CursoRepository;
import ProyectoEstudiaYa.webapp.repositories.EjercicioRepository;
import ProyectoEstudiaYa.webapp.repositories.ProgresoRepository;
import ProyectoEstudiaYa.webapp.repositories.UsuarioCursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public CursoDetalleDTO obtenerDetalle(Long cursoId, Long usuarioId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        List<Tema> temas = curso.getTemas() != null ? curso.getTemas() : List.of();
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
            Optional<UsuarioCurso> inscripcion = usuarioCursoRepository.findByUsuarioIdAndCursoId(usuarioId, cursoId);
            progreso = inscripcion.map(UsuarioCurso::getPorcentajeCompletado).orElse(0);
        }

        List<TemaResumenDTO> temasDTO = temas.stream()
                .map(tema -> construirTemaResumen(tema, usuarioId))
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

    private TemaResumenDTO construirTemaResumen(Tema tema, Long usuarioId) {
        int totalEjercicios = tema.getEjercicios() != null ? tema.getEjercicios().size() : 0;
        int ejerciciosResueltos = 0;
        double porcentajeAcierto = 0;
        boolean necesitaRefuerzo = false;

        if (usuarioId != null) {
            Optional<Progreso> progreso = progresoRepository.findByUsuarioIdAndTemaId(usuarioId, tema.getId());
            if (progreso.isPresent()) {
                Progreso p = progreso.get();
                ejerciciosResueltos = p.getEjerciciosIntentados() != null ? p.getEjerciciosIntentados() : 0;
                porcentajeAcierto = p.getPorcentajeAcierto() != null ? p.getPorcentajeAcierto() : 0;
                needsRefuerzo(p);
                necesitaRefuerzo = p.getNecesitaRefuerzo() != null ? p.getNecesitaRefuerzo() : false;
            }
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

    private void needsRefuerzo(Progreso p) {
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
