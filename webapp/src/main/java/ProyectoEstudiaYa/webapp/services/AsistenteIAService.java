package ProyectoEstudiaYa.webapp.services;

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

    // Cambiamos las anotaciones Value para que lean las nuevas variables de Groq
    @Value("${app.ai.custom.groq.api-key}")
    private String apiKey;

    @Value("${app.ai.custom.groq.model}")
    private String model;

    /**
     * Genera un reporte personalizado analizando el perfil del alumno
     */
    public AsistenteIARespuestaDTO generarAsistencia(Long usuarioId) {
        String nombreAlumno = "Juan Pérez";
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
            e.printStackTrace();
            return "Error real en Groq: " + e.getMessage();
        }
    }

    /**
     * NUEVO MÉTODO: Gestiona la comunicación con Groq (Formato estándar OpenAI Chat)
     */
private String llamarApiGroq(String prompt) {
    // REEMPLAZA ESTA LÍNEA: Asegúrate de que esté exactamente así, entre comillas simples y limpias
    String url = "https://api.groq.com/openai/v1/chat/completions";

    // El resto del código se queda igual...
    String promptEscapado = prompt.replace("\"", "\\\"");
    String jsonBody = "{"
            + "\"model\": \"" + this.model.trim() + "\","
            + "\"messages\": [{\"role\": \"user\", \"content\": \"" + promptEscapado + "\"}],"
            + "\"temperature\": 0.7"
            + "}";
    
    // ... (HttpHeaders, RestTemplate y el resto siguen igual)
        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        
        // Groq requiere autenticación mediante el Header 'Authorization: Bearer Tu_Llave'
        headers.set("Authorization", "Bearer " + this.apiKey.trim());

        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(jsonBody, headers);

        // Hacemos la petición POST
        String response = restTemplate.postForObject(url, entity, String.class);

        // Parseamos la respuesta de Groq para extraer exclusivamente el texto de la respuesta
        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            return jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .get("message").getAsJsonObject()
                    .get("content").getAsString();
        } catch (Exception e) {
            return response; // En caso de error estructural, devuelve el JSON completo para depurar
        }
    }
}
