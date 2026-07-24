package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.dto.CursoInscritoProjectionDTO;
import ProyectoEstudiaYa.webapp.dto.MisCursosDTO;
import ProyectoEstudiaYa.webapp.entities.CursoEntity;
import ProyectoEstudiaYa.webapp.entities.UsuarioEntity;
import ProyectoEstudiaYa.webapp.repositories.MisCursosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MisCursosService {

    private final MisCursosRepository misCursosRepository;

    public MisCursosService(MisCursosRepository misCursosRepository) {
        this.misCursosRepository = misCursosRepository;
    }

    public List<MisCursosDTO> listarCursos() {
        return listarCursos(null);
    }

    @Transactional(readOnly = true)
    public List<MisCursosDTO> listarCursos(Long usuarioId) {
        if (usuarioId == null) {
            return misCursosRepository.findAllWithTemas()
                    .stream()
                    .map(curso -> convertirCursoADTO(curso))
                    .toList();
        }

        List<CursoInscritoProjectionDTO> proyecciones = misCursosRepository
                .findCursosInscritosOptimizado(usuarioId);

        return proyecciones.stream()
                .map(this::convertirProjectionADTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MisCursosDTO> listarPorNivelYGrado(UsuarioEntity.NivelEducativo nivel, Integer grado) {
        return misCursosRepository.findByNivelAndGradoWithTemas(nivel, grado)
                .stream()
                .map(curso -> convertirCursoADTO(curso))
                .toList();
    }

    private MisCursosDTO convertirProjectionADTO(CursoInscritoProjectionDTO p) {
        Integer progreso = p.getPorcentajeCompletado() != null ? p.getPorcentajeCompletado() : 0;
        String siguienteTema = p.getSiguienteTemaNombre() != null ? p.getSiguienteTemaNombre() : "Temario por preparar";

        return new MisCursosDTO(
                p.getCursoId(),
                p.getNombre(),
                p.getDescripcion(),
                p.getNivel() != null ? p.getNivel() : "",
                p.getGrado(),
                p.getColorHex(),
                p.getIcono(),
                p.getTotalTemas() != null ? p.getTotalTemas().intValue() : 0,
                p.getTotalEjercicios() != null ? p.getTotalEjercicios().intValue() : 0,
                progreso,
                obtenerEstado(progreso),
                siguienteTema,
                obtenerRecomendacion(progreso, siguienteTema));
    }

    private MisCursosDTO convertirCursoADTO(CursoEntity curso) {
        int totalTemas = curso.getTemas() != null ? curso.getTemas().size() : 0;
        int totalEjercicios = totalTemas > 0 && curso.getTemas() != null
                ? curso.getTemas().stream()
                        .mapToInt(t -> t.getEjercicios() != null ? t.getEjercicios().size() : 0)
                        .sum()
                : 0;

        return new MisCursosDTO(
                curso.getId(),
                curso.getNombre(),
                curso.getDescripcion(),
                curso.getNivel() != null ? curso.getNivel().name() : "",
                curso.getGrado(),
                curso.getColorHex(),
                curso.getIcono(),
                totalTemas,
                totalEjercicios,
                0,
                "Por iniciar",
                totalTemas > 0 && curso.getTemas() != null ? curso.getTemas().get(0).getNombre() : "Temario por preparar",
                "Empieza con el primer tema y completa una practica corta.");
    }

    private String obtenerEstado(Integer progreso) {
        if (progreso >= 100) {
            return "Completado";
        }
        if (progreso >= 60) {
            return "En buen ritmo";
        }
        if (progreso > 0) {
            return "En progreso";
        }
        return "Por iniciar";
    }

    private String obtenerRecomendacion(Integer progreso, String siguienteTema) {
        if (progreso >= 100) {
            return "Repasa con ejercicios dificiles para mantener dominio.";
        }
        if (progreso >= 60) {
            return "Completa una practica de 15 minutos sobre " + siguienteTema + ".";
        }
        if (progreso > 0) {
            return "Refuerza " + siguienteTema + " antes de avanzar al siguiente bloque.";
        }
        return "Empieza por " + siguienteTema + " y marca tu primera meta semanal.";
    }
}
