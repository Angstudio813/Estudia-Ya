package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.dto.PracticaInteligenteDTO;
import ProyectoEstudiaYa.webapp.entities.Ejercicio;
import ProyectoEstudiaYa.webapp.entities.Progreso;
import ProyectoEstudiaYa.webapp.entities.Tema;
import ProyectoEstudiaYa.webapp.repositories.EjercicioRepository;
import ProyectoEstudiaYa.webapp.repositories.ProgresoRepository;
import java.lang.reflect.Field;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PracticaInteligenteService {

    private final EjercicioRepository ejercicioRepository;
    private final ProgresoRepository progresoRepository;

    public PracticaInteligenteService(
            EjercicioRepository ejercicioRepository,
            ProgresoRepository progresoRepository) {
        this.ejercicioRepository = ejercicioRepository;
        this.progresoRepository = progresoRepository;
    }

    public List<PracticaInteligenteDTO> listarEjercicios() {
        return ejercicioRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<PracticaInteligenteDTO> listarEjerciciosDeRefuerzo(Long usuarioId) {
        if (usuarioId == null) {
            return listarEjercicios();
        }

        List<Long> temasParaReforzar = progresoRepository
                .findByUsuarioIdAndNecesitaRefuerzo(usuarioId, true)
                .stream()
                .map(progreso -> obtenerCampo(progreso, "tema", Tema.class))
                .map(tema -> obtenerCampo(tema, "id", Long.class))
                .toList();

        if (temasParaReforzar.isEmpty()) {
            return listarEjercicios();
        }

        return temasParaReforzar.stream()
                .flatMap(temaId -> ejercicioRepository.findByTemaId(temaId).stream())
                .map(this::convertirADTO)
                .toList();
    }

    private PracticaInteligenteDTO convertirADTO(Ejercicio ejercicio) {
        Tema tema = obtenerCampo(ejercicio, "tema", Tema.class);
        Ejercicio.Dificultad dificultad = obtenerCampo(ejercicio, "dificultad", Ejercicio.Dificultad.class);
        Object curso = tema == null ? null : obtenerCampo(tema, "curso", Object.class);

        return new PracticaInteligenteDTO(
                obtenerCampo(ejercicio, "id", Long.class),
                obtenerCampo(ejercicio, "pregunta", String.class),
                obtenerCampo(ejercicio, "opcionA", String.class),
                obtenerCampo(ejercicio, "opcionB", String.class),
                obtenerCampo(ejercicio, "opcionC", String.class),
                obtenerCampo(ejercicio, "opcionD", String.class),
                dificultad == null ? "" : dificultad.name(),
                obtenerCampo(ejercicio, "generadoPorIA", Boolean.class),
                tema == null ? null : obtenerCampo(tema, "id", Long.class),
                tema == null ? "" : obtenerCampo(tema, "nombre", String.class),
                curso == null ? "" : obtenerCampo(curso, "nombre", String.class));
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
    