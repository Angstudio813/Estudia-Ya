package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.entities.Usuario;
import ProyectoEstudiaYa.webapp.dto.AsistenteIARespuestaDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AsistenteIAService {

    private final UsuarioService usuarioService;

    // Cambiamos las anotaciones Value para que lean las nuevas variables de Groq
    @Value("${app.ai.custom.groq.api-key}")
    private String apiKey;

    @Value("${app.ai.custom.groq.model}")
    private String model;

    public AsistenteIAService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Genera un reporte personalizado analizando el perfil del alumno
     */
    public AsistenteIARespuestaDTO generarAsistencia(Long usuarioId) {
        Usuario usuario = usuarioService.obtenerPorId(usuarioId);
        String nombreAlumno = usuario.getNombre() + " " + usuario.getApellido();
        String cursosMalos = "Matemáticas y Estructuras de Datos";

        // El mismo prompt estricto que ya tenías
        String prompt = """
                Analiza al estudiante %s que va mal en las materias de: %s.
                Genera recomendaciones de estudio y temas específicos de refuerzo.
                Devuelve la respuesta ESTRICTAMENTE en formato JSON plano, sin formato markdown (sin ```json), usando la siguiente estructura:
                {
                  "mensajePrincipal": "Un saludo motivador personalizado",
                  "temasRefuerzo": ["Tema 1", "Tema 2"],
                  "recomendaciones": ["Consejo 1", "Consejo 2"]
                }
                """
                .formatted(nombreAlumno, cursosMalos);

        try {
            // Ahora llamamos a Groq en lugar de Gemini
            String jsonRespuestaIA = llamarApiGroq(prompt);

            JsonObject jsonObject = JsonParser.parseString(jsonRespuestaIA).getAsJsonObject();
            String mensajePrincipal = jsonObject.get("mensajePrincipal").getAsString();

            List<String> temasRefuerzo = new ArrayList<>();
            JsonArray temasArray = jsonObject.getAsJsonArray("temasRefuerzo");
            temasArray.forEach(elem -> temasRefuerzo.add(elem.getAsString()));

            List<String> recomendaciones = new ArrayList<>();
            JsonArray recsArray = jsonObject.getAsJsonArray("recomendaciones");
            recsArray.forEach(elem -> recomendaciones.add(elem.getAsString()));

            return new AsistenteIARespuestaDTO(usuarioId, nombreAlumno, mensajePrincipal, temasRefuerzo, recomendaciones);

        } catch (Exception e) {
            // Tu plan de contingencia por si Groq diera algún error aleatorio
            return new AsistenteIARespuestaDTO(
                    usuarioId,
                    nombreAlumno,
                    "¡Hola! Sigue esforzándote en tus materias.",
                    List.of("Repasar fundamentos generales"),
                    List.of("Organiza tu calendario de estudio semanal."));
        }
    }

    /**
     * Procesa una pregunta libre del alumno en la interfaz de chat
     */
    public String chatLibre(Long usuarioId, String pregunta) {
        String promptContextualizado = "Como tutor de la plataforma EstudiaYa, responde brevemente a la siguiente duda del alumno: " + pregunta;

        try {
            return llamarApiGroq(promptContextualizado);
        } catch (Exception e) {
            System.err.println("[AsistenteIAService] Error en chatLibre: " + e.getMessage());
            e.printStackTrace();
            return "Disculpa, tuve un problema al procesar tu pregunta. Por favor, intenta de nuevo.";
        }
    }

    /**
     * Gestiona la comunicación con Groq (Formato estándar OpenAI Chat)
     */
    private String llamarApiGroq(String prompt) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);

        JsonObject body = new JsonObject();
        body.addProperty("model", this.model.trim());
        body.add("messages", new com.google.gson.JsonArray() {{ add(message); }});
        body.addProperty("temperature", 0.7);

        String jsonBody = body.toString();

        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + this.apiKey.trim());

        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(jsonBody, headers);

        String response = restTemplate.postForObject(url, entity, String.class);

        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            return jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .get("message").getAsJsonObject()
                    .get("content").getAsString();
        } catch (Exception e) {
            return response;
        }
    }
}
