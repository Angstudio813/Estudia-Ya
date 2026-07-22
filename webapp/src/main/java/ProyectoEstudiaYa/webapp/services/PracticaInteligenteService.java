package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.dto.PracticaInteligenteDTO;
import ProyectoEstudiaYa.webapp.entities.Ejercicio;
import ProyectoEstudiaYa.webapp.entities.IntentoEjercicio;
import ProyectoEstudiaYa.webapp.entities.Progreso;
import ProyectoEstudiaYa.webapp.entities.Tema;
import ProyectoEstudiaYa.webapp.entities.Usuario;
import ProyectoEstudiaYa.webapp.repositories.EjercicioRepository;
import ProyectoEstudiaYa.webapp.repositories.IntentoEjercicioRepository;
import ProyectoEstudiaYa.webapp.repositories.PracticaInteligenteRepository;
import ProyectoEstudiaYa.webapp.repositories.ProgresoRepository;
import ProyectoEstudiaYa.webapp.repositories.TemaRepository;
import ProyectoEstudiaYa.webapp.repositories.UsuarioRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PracticaInteligenteService {

    private final PracticaInteligenteRepository practicaInteligenteRepository;
    private final ProgresoRepository progresoRepository;
    private final IntentoEjercicioRepository intentoEjercicioRepository;
    private final EjercicioRepository ejercicioRepository;
    private final TemaRepository temaRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.ai.custom.groq.api-key}")
    private String apiKey;

    @Value("${app.ai.custom.groq.model}")
    private String model;

    public PracticaInteligenteService(
            PracticaInteligenteRepository practicaInteligenteRepository,
            ProgresoRepository progresoRepository,
            IntentoEjercicioRepository intentoEjercicioRepository,
            EjercicioRepository ejercicioRepository,
            TemaRepository temaRepository,
            UsuarioRepository usuarioRepository) {
        this.practicaInteligenteRepository = practicaInteligenteRepository;
        this.progresoRepository = progresoRepository;
        this.intentoEjercicioRepository = intentoEjercicioRepository;
        this.ejercicioRepository = ejercicioRepository;
        this.temaRepository = temaRepository;
        this.usuarioRepository = usuarioRepository;
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
                .map(progreso -> progreso.getTema().getId())
                .toList();

        if (temasParaReforzar.isEmpty()) {
            return listarEjercicios();
        }

        return temasParaReforzar.stream()
                .flatMap(temaId -> practicaInteligenteRepository.findByTemaId(temaId).stream())
                .map(this::convertirADTO)
                .toList();
    }

    public List<PracticaInteligenteDTO> listarEjerciciosPorCurso(Long cursoId) {
        return practicaInteligenteRepository.findByCursoId(cursoId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<PracticaInteligenteDTO> listarEjerciciosPorTema(Long temaId) {
        return practicaInteligenteRepository.findByTemaId(temaId)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Transactional
    public PracticaInteligenteDTO registrarIntento(Long usuarioId, Long ejercicioId, String respuestaElegida) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Ejercicio ejercicio = ejercicioRepository.findById(ejercicioId)
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));

        boolean esCorrecta = ejercicio.getRespuestaCorrecta().equalsIgnoreCase(respuestaElegida);

        IntentoEjercicio intento = IntentoEjercicio.builder()
                .usuario(usuario)
                .ejercicio(ejercicio)
                .respuestaElegida(respuestaElegida.toUpperCase())
                .esCorrecta(esCorrecta)
                .fechaIntento(LocalDateTime.now())
                .build();
        intentoEjercicioRepository.save(intento);

        actualizarProgreso(usuarioId, ejercicio.getTema().getId(), esCorrecta);

        return convertirADTO(ejercicio);
    }

    @Transactional
    public void actualizarProgreso(Long usuarioId, Long temaId, boolean esCorrecta) {
        Progreso progreso = progresoRepository.findByUsuarioIdAndTemaId(usuarioId, temaId)
                .orElse(Progreso.builder()
                        .usuario(usuarioRepository.getReferenceById(usuarioId))
                        .tema(temaRepository.getReferenceById(temaId))
                        .ejerciciosIntentados(0)
                        .ejerciciosCorrectos(0)
                        .porcentajeAcierto(0.0)
                        .necesitaRefuerzo(false)
                        .build());

        progreso.setEjerciciosIntentados(progreso.getEjerciciosIntentados() + 1);
        if (esCorrecta) {
            progreso.setEjerciciosCorrectos(progreso.getEjerciciosCorrectos() + 1);
        }
        progreso.setUltimaPractica(LocalDateTime.now());

        int total = progreso.getEjerciciosIntentados();
        int correctos = progreso.getEjerciciosCorrectos();
        double porcentaje = total > 0 ? (double) correctos / total * 100 : 0;
        progreso.setPorcentajeAcierto(porcentaje);
        progreso.setNecesitaRefuerzo(porcentaje < 50);

        progresoRepository.save(progreso);
    }

    @Transactional
    public List<PracticaInteligenteDTO> generarEjerciciosIA(Long usuarioId, Long temaId, int cantidad) {
        Tema tema = temaRepository.findById(temaId)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado con ID: " + temaId));

        Progreso progreso = progresoRepository.findByUsuarioIdAndTemaId(usuarioId, temaId).orElse(null);
        double porcentajeAcierto = progreso != null ? progreso.getPorcentajeAcierto() : 50.0;
        String nivelDificultad = porcentajeAcierto >= 70 ? "DIFICIL" : porcentajeAcierto >= 40 ? "MEDIO" : "FACIL";

        String prompt = String.format("""
                Genera %d ejercicios de opcion multiple para el tema "%s" del curso "%s".
                Nivel de dificultad sugerido: %s (basado en el rendimiento del alumno: %.0f%% de acierto).
                
                Devuelve la respuesta ESTRICTAMENTE en formato JSON array, sin markdown, con esta estructura:
                [
                  {
                    "pregunta": "Texto de la pregunta",
                    "opcionA": "Opcion A",
                    "opcionB": "Opcion B",
                    "opcionC": "Opcion C",
                    "opcionD": "Opcion D",
                    "respuestaCorrecta": "A",
                    "explicacion": "Explicacion breve de porque es correcta",
                    "dificultad": "FACIL"
                  }
                ]
                
                Asegurate de que las preguntas sean claras, las opciones plausibles, y la dificultad variada.
                """, cantidad, tema.getNombre(), tema.getCurso().getNombre(), nivelDificultad, porcentajeAcierto);

        String jsonRespuesta = llamarApiGroq(prompt);
        String jsonLimpio = jsonRespuesta.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

        JsonArray jsonArray;
        try {
            jsonArray = JsonParser.parseString(jsonLimpio).getAsJsonArray();
        } catch (Exception e) {
            System.err.println("[PracticaInteligenteService] JSON parse error. Response was: " + jsonLimpio);
            throw new RuntimeException("La IA no devolvió un formato válido. Respuesta: " + jsonLimpio.substring(0, Math.min(200, jsonLimpio.length())));
        }

        List<PracticaInteligenteDTO> ejerciciosGenerados = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject obj = jsonArray.get(i).getAsJsonObject();

            Ejercicio ejercicio = Ejercicio.builder()
                    .pregunta(obj.get("pregunta").getAsString())
                    .opcionA(obj.get("opcionA").getAsString())
                    .opcionB(obj.get("opcionB").getAsString())
                    .opcionC(obj.get("opcionC").getAsString())
                    .opcionD(obj.get("opcionD").getAsString())
                    .respuestaCorrecta(obj.get("respuestaCorrecta").getAsString().toUpperCase())
                    .explicacion(obj.get("explicacion").getAsString())
                    .dificultad(Ejercicio.Dificultad.valueOf(obj.get("dificultad").getAsString().toUpperCase()))
                    .generadoPorIA(true)
                    .tema(tema)
                    .build();

            ejercicio = ejercicioRepository.save(ejercicio);
            ejerciciosGenerados.add(convertirADTO(ejercicio));
        }

        return ejerciciosGenerados;
    }

    private PracticaInteligenteDTO convertirADTO(Ejercicio ejercicio) {
        Tema tema = ejercicio.getTema();
        Ejercicio.Dificultad dificultad = ejercicio.getDificultad();
        String temaNombre = tema != null ? tema.getNombre() : "";
        String cursoNombre = tema != null && tema.getCurso() != null ? tema.getCurso().getNombre() : "";
        String dificultadTexto = dificultad != null ? dificultad.name() : "";

        return new PracticaInteligenteDTO(
                ejercicio.getId(),
                ejercicio.getPregunta(),
                ejercicio.getOpcionA(),
                ejercicio.getOpcionB(),
                ejercicio.getOpcionC(),
                ejercicio.getOpcionD(),
                dificultadTexto,
                ejercicio.getGeneradoPorIA(),
                tema != null ? tema.getId() : null,
                temaNombre,
                cursoNombre,
                ejercicio.getRespuestaCorrecta(),
                ejercicio.getExplicacion(),
                obtenerHabilidad(temaNombre, dificultadTexto),
                obtenerRecomendacion(dificultadTexto, temaNombre),
                obtenerXp(dificultadTexto));
    }

    private String obtenerHabilidad(String temaNombre, String dificultad) {
        if (temaNombre == null || temaNombre.isBlank()) {
            return "Razonamiento";
        }
        return switch (dificultad) {
            case "DIFICIL" -> "Dominio avanzado de " + temaNombre;
            case "MEDIO" -> "Aplicacion guiada de " + temaNombre;
            default -> "Base conceptual de " + temaNombre;
        };
    }

    private String obtenerRecomendacion(String dificultad, String temaNombre) {
        return switch (dificultad) {
            case "DIFICIL" -> "Resuelve sin mirar pistas y luego contrasta tu razonamiento.";
            case "MEDIO" -> "Anota el paso clave antes de elegir una alternativa.";
            default -> "Lee cada opcion y descarta primero las respuestas imposibles.";
        };
    }

    private Integer obtenerXp(String dificultad) {
        return switch (dificultad) {
            case "DIFICIL" -> 25;
            case "MEDIO" -> 18;
            default -> 12;
        };
    }

    @Transactional
    public List<PracticaInteligenteDTO> generarEjerciciosCursoIA(Long usuarioId, Long cursoId, int cantidadPorTema) {
        List<Tema> temas = temaRepository.findByCursoIdOrderByOrden(cursoId);
        if (temas.isEmpty()) {
            throw new RuntimeException("No se encontraron temas para el curso con ID: " + cursoId);
        }

        List<PracticaInteligenteDTO> todos = new ArrayList<>();
        for (Tema tema : temas) {
            List<PracticaInteligenteDTO> generados = generarEjerciciosIA(usuarioId, tema.getId(), cantidadPorTema);
            todos.addAll(generados);
        }
        return todos;
    }

    private String llamarApiGroq(String prompt) {
        String url = "https://api.groq.com/openai/v1/chat/completions";
        String promptEscapado = prompt.replace("\"", "\\\"");
        String jsonBody = "{"
                + "\"model\": \"" + this.model.trim() + "\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"" + promptEscapado + "\"}],"
                + "\"temperature\": 0.7"
                + "}";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + this.apiKey.trim());

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        String response;
        try {
            response = restTemplate.postForObject(url, entity, String.class);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("[PracticaInteligenteService] Groq HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Error al conectar con la IA (HTTP " + e.getStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("[PracticaInteligenteService] Groq connection error: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servicio de IA: " + e.getMessage());
        }

        if (response == null) {
            throw new RuntimeException("La IA devolvió una respuesta vacía");
        }

        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            return jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .get("message").getAsJsonObject()
                    .get("content").getAsString();
        } catch (Exception e) {
            System.err.println("[PracticaInteligenteService] Groq response parse error. Raw: " + response);
            throw new RuntimeException("La IA devolvió un formato inesperado: " + response.substring(0, Math.min(200, response.length())));
        }
    }
}
