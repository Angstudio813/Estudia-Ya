package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.dto.MisCursosDTO;
import ProyectoEstudiaYa.webapp.entities.Curso;
import ProyectoEstudiaYa.webapp.entities.Tema;
import ProyectoEstudiaYa.webapp.entities.Usuario;
import ProyectoEstudiaYa.webapp.entities.UsuarioCurso;
import ProyectoEstudiaYa.webapp.repositories.MisCursosRepository;
import ProyectoEstudiaYa.webapp.repositories.UsuarioCursoRepository;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MisCursosService {

    private final MisCursosRepository misCursosRepository;
    private final UsuarioCursoRepository usuarioCursoRepository;

    public MisCursosService(MisCursosRepository misCursosRepository, UsuarioCursoRepository usuarioCursoRepository) {
        this.misCursosRepository = misCursosRepository;
        this.usuarioCursoRepository = usuarioCursoRepository;
    }

    public List<MisCursosDTO> listarCursos() {
        return listarCursos(null);
    }

    public List<MisCursosDTO> listarCursos(Long usuarioId) {
        return misCursosRepository.findAll()
                .stream()
                .map(curso -> convertirADTO(curso, buscarInscripcion(usuarioId, curso)))
                .toList();
    }

    public List<MisCursosDTO> listarPorNivelYGrado(Usuario.NivelEducativo nivel, Integer grado) {
        return misCursosRepository.findByNivelAndGrado(nivel, grado)
                .stream()
                .map(curso -> convertirADTO(curso, null))
                .toList();
    }

    private MisCursosDTO convertirADTO(Curso curso, UsuarioCurso inscripcion) {
        List<?> temas = obtenerCampo(curso, "temas", List.class);
        int totalTemas = temas == null ? 0 : temas.size();
        int totalEjercicios = contarEjercicios(temas);
        Integer progreso = inscripcion == null ? 0 : obtenerCampo(inscripcion, "porcentajeCompletado", Integer.class);
        progreso = progreso == null ? 0 : progreso;
        Usuario.NivelEducativo nivel = obtenerCampo(curso, "nivel", Usuario.NivelEducativo.class);
        String siguienteTema = obtenerSiguienteTema(temas, progreso);

        return new MisCursosDTO(
                obtenerCampo(curso, "id", Long.class),
                obtenerCampo(curso, "nombre", String.class),
                obtenerCampo(curso, "descripcion", String.class),
                nivel == null ? "" : nivel.name(),
                obtenerCampo(curso, "grado", Integer.class),
                obtenerCampo(curso, "colorHex", String.class),
                obtenerCampo(curso, "icono", String.class),
                totalTemas,
                totalEjercicios,
                progreso,
                obtenerEstado(progreso),
                siguienteTema,
                obtenerRecomendacion(progreso, siguienteTema));
    }

    private UsuarioCurso buscarInscripcion(Long usuarioId, Curso curso) {
        Long cursoId = obtenerCampo(curso, "id", Long.class);
        if (usuarioId == null || cursoId == null) {
            return null;
        }

        Optional<UsuarioCurso> inscripcion = usuarioCursoRepository.findByUsuarioIdAndCursoId(usuarioId, cursoId);
        return inscripcion.orElse(null);
    }

    private int contarEjercicios(List<?> temas) {
        if (temas == null) {
            return 0;
        }

        return temas.stream()
                .map(tema -> obtenerCampo(tema, "ejercicios", List.class))
                .mapToInt(ejercicios -> ejercicios == null ? 0 : ejercicios.size())
                .sum();
    }

    private String obtenerSiguienteTema(List<?> temas, Integer progreso) {
        if (temas == null || temas.isEmpty()) {
            return "Temario por preparar";
        }

        int indice = Math.min(Math.max((progreso * temas.size()) / 100, 0), temas.size() - 1);
        Tema tema = (Tema) temas.get(indice);
        String nombre = obtenerCampo(tema, "nombre", String.class);
        return nombre == null ? "Siguiente tema" : nombre;
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

    private <T> T obtenerCampo(Object origen, String nombreCampo, Class<T> tipo) {
        if (origen == null) {
            return null;
        }

        try {
            Field field = origen.getClass().getDeclaredField(nombreCampo);
            field.setAccessible(true);
            return tipo.cast(field.get(origen));
        } catch (ReflectiveOperationException | ClassCastException ex) {
            return null;
        }
    }
}
