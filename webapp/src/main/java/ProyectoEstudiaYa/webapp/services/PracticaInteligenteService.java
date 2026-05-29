package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.dto.PracticaInteligenteDTO;
import ProyectoEstudiaYa.webapp.entities.Ejercicio;
import ProyectoEstudiaYa.webapp.entities.Progreso;
import ProyectoEstudiaYa.webapp.entities.Tema;
import ProyectoEstudiaYa.webapp.repositories.PracticaInteligenteRepository;
import ProyectoEstudiaYa.webapp.repositories.ProgresoRepository;
import java.lang.reflect.Field;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PracticaInteligenteService {

    private final PracticaInteligenteRepository practicaInteligenteRepository;
    private final ProgresoRepository progresoRepository;

    public PracticaInteligenteService(
            PracticaInteligenteRepository practicaInteligenteRepository,
            ProgresoRepository progresoRepository) {
        this.practicaInteligenteRepository = practicaInteligenteRepository;
        this.progresoRepository = progresoRepository;
    }

    public List<PracticaInteligenteDTO> listarEjercicios() {
        return practicaInteligenteRepository.findAll()
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
                .flatMap(temaId -> practicaInteligenteRepository.findByTemaId(temaId).stream())
                .map(this::convertirADTO)
                .toList();
    }

    private PracticaInteligenteDTO convertirADTO(Ejercicio ejercicio) {
        Tema tema = obtenerCampo(ejercicio, "tema", Tema.class);
        Ejercicio.Dificultad dificultad = obtenerCampo(ejercicio, "dificultad", Ejercicio.Dificultad.class);
        Object curso = tema == null ? null : obtenerCampo(tema, "curso", Object.class);
        String temaNombre = tema == null ? "" : obtenerCampo(tema, "nombre", String.class);
        String dificultadTexto = dificultad == null ? "" : dificultad.name();

        return new PracticaInteligenteDTO(
                obtenerCampo(ejercicio, "id", Long.class),
                obtenerCampo(ejercicio, "pregunta", String.class),
                obtenerCampo(ejercicio, "opcionA", String.class),
                obtenerCampo(ejercicio, "opcionB", String.class),
                obtenerCampo(ejercicio, "opcionC", String.class),
                obtenerCampo(ejercicio, "opcionD", String.class),
                dificultadTexto,
                obtenerCampo(ejercicio, "generadoPorIA", Boolean.class),
                tema == null ? null : obtenerCampo(tema, "id", Long.class),
                temaNombre,
                curso == null ? "" : obtenerCampo(curso, "nombre", String.class),
                obtenerCampo(ejercicio, "respuestaCorrecta", String.class),
                obtenerCampo(ejercicio, "explicacion", String.class),
                obtenerHabilidad(temaNombre, dificultadTexto),
                obtenerRecomendacion(dificultadTexto, temaNombre),
                obtenerXp(dificultadTexto));
    }

    private String obtenerHabilidad(String temaNombre, String dificultad) {
        if (temaNombre == null || temaNombre.isBlank()) {
            return "Razonamiento";
        }
        if ("DIFICIL".equals(dificultad)) {
            return "Dominio avanzado de " + temaNombre;
        }
        if ("MEDIO".equals(dificultad)) {
            return "Aplicacion guiada de " + temaNombre;
        }
        return "Base conceptual de " + temaNombre;
    }

    private String obtenerRecomendacion(String dificultad, String temaNombre) {
        if ("DIFICIL".equals(dificultad)) {
            return "Resuelve sin mirar pistas y luego contrasta tu razonamiento.";
        }
        if ("MEDIO".equals(dificultad)) {
            return "Anota el paso clave antes de elegir una alternativa.";
        }
        return "Lee cada opcion y descarta primero las respuestas imposibles.";
    }

    private Integer obtenerXp(String dificultad) {
        if ("DIFICIL".equals(dificultad)) {
            return 25;
        }
        if ("MEDIO".equals(dificultad)) {
            return 18;
        }
        return 12;
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
    
