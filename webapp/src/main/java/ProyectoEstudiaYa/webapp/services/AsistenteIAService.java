package ProyectoEstudiaYa.webapp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ProyectoEstudiaYa.webapp.dto.AsistenteIARespuestaDTO;
import ProyectoEstudiaYa.webapp.entities.Progreso;
import ProyectoEstudiaYa.webapp.repositories.ProgresoRepository;
import ProyectoEstudiaYa.webapp.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class AsistenteIAService {

    private final UsuarioRepository usuarioRepository;
    private final ProgresoRepository progresoRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${app.ai.provider:none}")
    private String aiProvider;

    @Value("${app.ai.openai.api-key:}")
    private String openAiApiKey;

    @Value("${app.ai.openai.model:gpt-4o-mini}")
    private String openAiModel;

    @Value("${app.ai.anthropic.api-key:}")
    private String anthropicApiKey;

    @Value("${app.ai.anthropic.model:claude-3-5-haiku-latest}")
    private String anthropicModel;

    // --- GEMINI ---
    @Value("${app.ai.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${app.ai.gemini.model:gemini-2.0-flash}")
    private String geminiModel;

    public AsistenteIAService(
            UsuarioRepository usuarioRepository,
            ProgresoRepository progresoRepository,
            ObjectMapper objectMapper
    ) {
        this.usuarioRepository = usuarioRepository;
        this.progresoRepository = progresoRepository;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(8))
                .build();
    }

    public AsistenteIARespuestaDTO generarAsistencia(Long usuarioId) {
        usuarioRepository.findById(usuarioId);
        String nombreUsuario = "estudiante";

        List<Progreso> temasConRefuerzo = progresoRepository.findByUsuarioIdAndNecesitaRefuerzo(usuarioId, true);
        List<String> temasRefuerzo = new ArrayList<>();

        AsistenteGenerada generado = generarConIA(nombreUsuario, temasConRefuerzo.size(), temasRefuerzo.size());

        return new AsistenteIARespuestaDTO(
                usuarioId,
                nombreUsuario,
                generado.mensajePrincipal(),
                temasRefuerzo,
                generado.recomendaciones()
        );
    }

    public String chatLibre(Long usuarioId, String pregunta) {
        String provider = aiProvider != null ? aiProvider.trim().toLowerCase() : "none";
        try {
            if ("gemini".equals(provider) && !geminiApiKey.isBlank()) {
                return invocarGeminiChat(pregunta);
            }
            if ("openai".equals(provider) && !openAiApiKey.isBlank()) {
                return invocarOpenAIChat(pregunta);
            }
            if ("anthropic".equals(provider) && !anthropicApiKey.isBlank()) {
                return invocarAnthropicChat(pregunta);
            }
        } catch (Exception e) {
            return "No pude conectarme al asistente. Intenta más tarde.";
        }
        return "Configura tu proveedor de IA en application.properties.";
    }

    private AsistenteGenerada generarConIA(String nombreUsuario, int temasConRefuerzo, int totalTemasConDetalle) {
        String provider = aiProvider != null ? aiProvider.trim().toLowerCase() : "none";
        try {
            if ("gemini".equals(provider) && !geminiApiKey.isBlank()) {
                return invocarGemini(nombreUsuario, temasConRefuerzo, totalTemasConDetalle);
            }
            if ("openai".equals(provider) && !openAiApiKey.isBlank()) {
                return invocarOpenAI(nombreUsuario, temasConRefuerzo, totalTemasConDetalle);
            }
            if ("anthropic".equals(provider) && !anthropicApiKey.isBlank()) {
                return invocarAnthropic(nombreUsuario, temasConRefuerzo, totalTemasConDetalle);
            }
        } catch (Exception ignored) {
            // Fallback local si la API falla
        }
        String mensajePrincipal = construirMensajePrincipal(nombreUsuario, temasConRefuerzo);
        List<String> recomendaciones = construirRecomendaciones(List.of());
        return new AsistenteGenerada(mensajePrincipal, recomendaciones);
    }

    private AsistenteGenerada invocarGemini(String nombreUsuario, int temasConRefuerzo, int totalTemasConDetalle) throws Exception {
        String prompt = construirPrompt(nombreUsuario, temasConRefuerzo, totalTemasConDetalle);

        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "contents", List.of(java.util.Map.of(
                        "parts", List.of(java.util.Map.of("text", prompt))
                ))
        ));

        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + geminiModel + ":generateContent?key=" + geminiApiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Gemini status " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        String content = root.path("candidates").path(0)
                .path("content").path("parts").path(0)
                .path("text").asText("");

        // limpiar posibles backticks de markdown que Gemini agrega
        content = content.replaceAll("```json", "").replaceAll("```", "").trim();
        return parsearRespuestaAsistente(content);
    }

private String invocarGeminiChat(String pregunta) throws Exception {
    String promptCompleto = "Eres un tutor educativo para estudiantes peruanos de secundaria. "
            + "Explica de forma clara y simple en español. Responde la siguiente pregunta: " + pregunta;

    String body = objectMapper.writeValueAsString(java.util.Map.of(
            "contents", List.of(java.util.Map.of(
                    "parts", List.of(java.util.Map.of("text", promptCompleto))
            ))
    ));

    String url = "https://generativelanguage.googleapis.com/v1beta/models/"
            + geminiModel + ":generateContent?key=" + geminiApiKey;

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(20))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    System.out.println("=== GEMINI STATUS: " + response.statusCode());
    System.out.println("=== GEMINI BODY: " + response.body());

    JsonNode root = objectMapper.readTree(response.body());
    String texto = root.path("candidates").path(0)
            .path("content").path("parts").path(0)
            .path("text").asText("");

    if (texto.isBlank()) {
        return root.path("error").path("message").asText("Sin respuesta de Gemini.");
    }
    return texto;
}

private AsistenteGenerada invocarOpenAI(String nombreUsuario, int temasConRefuerzo, int totalTemasConDetalle) throws Exception {
        String prompt = construirPrompt(nombreUsuario, temasConRefuerzo, totalTemasConDetalle);

        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "model", openAiModel,
                "temperature", 0.6,
                "response_format", java.util.Map.of("type", "json_object"),
                "messages", List.of(
                        java.util.Map.of("role", "system", "content", "Eres un tutor IA para secundaria. Responde SIEMPRE en JSON valido."),
                        java.util.Map.of("role", "user", "content", prompt)
                )
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("OpenAI status " + response.statusCode());
        }
        JsonNode root = objectMapper.readTree(response.body());
        String content = root.path("choices").path(0).path("message").path("content").asText("");
        return parsearRespuestaAsistente(content);
    }

    private String invocarOpenAIChat(String pregunta) throws Exception {
        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "model", openAiModel,
                "messages", List.of(
                        java.util.Map.of("role", "system", "content", "Eres un tutor para estudiantes peruanos de secundaria. Explica simple y claro."),
                        java.util.Map.of("role", "user", "content", pregunta)
                )
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());
        return root.path("choices").path(0).path("message").path("content").asText("Sin respuesta.");
    }

    private AsistenteGenerada invocarAnthropic(String nombreUsuario, int temasConRefuerzo, int totalTemasConDetalle) throws Exception {
        String prompt = construirPrompt(nombreUsuario, temasConRefuerzo, totalTemasConDetalle);

        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "model", anthropicModel,
                "max_tokens", 400,
                "temperature", 0.6,
                "system", "Eres un tutor IA para secundaria. Responde SIEMPRE en JSON valido.",
                "messages", List.of(java.util.Map.of("role", "user", "content", prompt))
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.anthropic.com/v1/messages"))
                .timeout(Duration.ofSeconds(20))
                .header("x-api-key", anthropicApiKey)
                .header("anthropic-version", "2023-06-01")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Anthropic status " + response.statusCode());
        }
        JsonNode root = objectMapper.readTree(response.body());
        String content = root.path("content").path(0).path("text").asText("");
        return parsearRespuestaAsistente(content);
    }

    private String invocarAnthropicChat(String pregunta) throws Exception {
        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "model", anthropicModel,
                "max_tokens", 600,
                "system", "Eres un tutor para estudiantes peruanos de secundaria. Explica simple y claro.",
                "messages", List.of(java.util.Map.of("role", "user", "content", pregunta))
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.anthropic.com/v1/messages"))
                .timeout(Duration.ofSeconds(20))
                .header("x-api-key", anthropicApiKey)
                .header("anthropic-version", "2023-06-01")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());
        return root.path("content").path(0).path("text").asText("Sin respuesta.");
    }


    private String construirPrompt(String nombreUsuario, int temasConRefuerzo, int totalTemasConDetalle) {
        return "Genera recomendaciones de estudio para un estudiante. "
                + "Nombre: " + nombreUsuario + ". "
                + "Temas en refuerzo: " + temasConRefuerzo + ". "
                + "Temas con detalle disponible: " + totalTemasConDetalle + ". "
                + "Responde SOLO JSON con este formato exacto: "
                + "{\"mensajePrincipal\":\"...\",\"recomendaciones\":[\"...\",\"...\",\"...\"]}. "
                + "No agregues markdown ni texto fuera del JSON.";
    }

    private AsistenteGenerada parsearRespuestaAsistente(String content) throws Exception {
        JsonNode parsed = objectMapper.readTree(content);
        String mensaje = parsed.path("mensajePrincipal").asText("Tu asistente IA no pudo generar un mensaje.");

        List<String> recomendaciones = new ArrayList<>();
        JsonNode recs = parsed.path("recomendaciones");
        if (recs.isArray()) {
            recs.forEach(item -> recomendaciones.add(item.asText()));
        }

        if (recomendaciones.isEmpty()) {
            recomendaciones.add("Define un bloque de estudio de 20 minutos y enfoca una sola habilidad.");
            recomendaciones.add("Resuelve 5 ejercicios y anota el tipo de error más frecuente.");
            recomendaciones.add("Cierra con repaso corto de conceptos clave.");
        }

        return new AsistenteGenerada(mensaje, recomendaciones);
    }

    private String construirMensajePrincipal(String nombreUsuario, int temasConRefuerzo) {
        if (temasConRefuerzo == 0) {
            return "Buen trabajo, " + nombreUsuario + ". No hay temas críticos por reforzar hoy.";
        }
        return "Hola " + nombreUsuario + ", detecté " + temasConRefuerzo + " tema(s) que conviene reforzar primero.";
    }

    private List<String> construirRecomendaciones(List<String> temasRefuerzo) {
        List<String> recomendaciones = new ArrayList<>();
        if (temasRefuerzo.isEmpty()) {
            recomendaciones.add("Resuelve 5 ejercicios de dificultad media para mantener ritmo.");
            recomendaciones.add("Repasa un tema dominado y explica el procedimiento en voz alta.");
            recomendaciones.add("Define una meta corta de 20 minutos de estudio activo.");
            return recomendaciones;
        }
        recomendaciones.add("Empieza por 10 minutos del tema con menor porcentaje de acierto.");
        recomendaciones.add("Resuelve 5 ejercicios guiados antes de pasar a ejercicios mixtos.");
        recomendaciones.add("Cierra con un mini repaso de errores frecuentes de hoy.");
        return recomendaciones;
    }

    private record AsistenteGenerada(String mensajePrincipal, List<String> recomendaciones) {}
}