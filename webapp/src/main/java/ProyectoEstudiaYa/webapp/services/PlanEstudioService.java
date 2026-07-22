package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.dto.PlanEstudioRequestDTO;
import ProyectoEstudiaYa.webapp.dto.PlanEstudioResponseDTO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlanEstudioService {

    @Value("${app.ai.custom.groq.api-key}")
    private String apiKey;

    @Value("${app.ai.custom.groq.model}")
    private String model;

    public PlanEstudioResponseDTO generarPlan(PlanEstudioRequestDTO request) {
        String cursosStr = String.join(", ", request.getCursos());

        String prompt = String.format("""
                Genera un plan de estudio semanal personalizado.

                Cursos: %s
                Horas disponibles por dia: %d
                Dias disponibles a la semana: %d

                Devuelve la respuesta ESTRICTAMENTE en formato JSON, sin markdown, con esta estructura exacta:
                {
                  "horario": {
                    "Lunes": ["09:00-10:00 Matematicas", "10:00-11:00 Fisica"],
                    "Martes": ["09:00-10:00 Quimica"],
                    "Miercoles": [],
                    "Jueves": [],
                    "Viernes": [],
                    "Sabado": [],
                    "Domingo": []
                  }
                }

                Reglas:
                - Distribuye las horas de estudio de forma equilibrada entre los cursos indicados.
                - Usa formato HH:MM-HH:MM Nombre del curso para cada bloque.
                - Solo incluye los dias que el usuario tiene disponibles (los demas deben estar vacios).
                - Respeta las horas diarias indicadas.
                """, cursosStr, request.getHorasDisponiblesPorDia(), request.getDiasDisponibles());

        String jsonRespuesta = llamarApiGroq(prompt);
        String jsonLimpio = jsonRespuesta.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

        try {
            JsonObject jsonResponse = JsonParser.parseString(jsonLimpio).getAsJsonObject();
            JsonObject horarioJson = jsonResponse.getAsJsonObject("horario");

            Map<String, String[]> horario = new LinkedHashMap<>();
            for (String dia : horarioJson.keySet()) {
                List<String> bloques = new ArrayList<>();
                horarioJson.getAsJsonArray(dia).asList()
                        .forEach(element -> bloques.add(element.getAsString()));
                horario.put(dia, bloques.toArray(new String[0]));
            }

            return new PlanEstudioResponseDTO(horario);
        } catch (Exception e) {
            System.err.println("[PlanEstudioService] JSON parse error. Response was: " + jsonLimpio);
            throw new RuntimeException("La IA no devolvio un formato valido. Respuesta: "
                    + jsonLimpio.substring(0, Math.min(200, jsonLimpio.length())));
        }
    }

    private String llamarApiGroq(String prompt) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);

        com.google.gson.JsonArray messages = new com.google.gson.JsonArray();
        messages.add(message);

        JsonObject body = new JsonObject();
        body.addProperty("model", this.model.trim());
        body.add("messages", messages);
        body.addProperty("temperature", 0.7);

        String jsonBody = body.toString();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + this.apiKey.trim());

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        String response;
        try {
            response = restTemplate.postForObject(url, entity, String.class);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("[PlanEstudioService] Groq HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Error al conectar con la IA (HTTP " + e.getStatusCode() + "): " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("[PlanEstudioService] Groq connection error: " + e.getMessage());
            throw new RuntimeException("No se pudo conectar con el servicio de IA: " + e.getMessage());
        }

        if (response == null) {
            throw new RuntimeException("La IA devolvio una respuesta vacia");
        }

        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            return jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .get("message").getAsJsonObject()
                    .get("content").getAsString();
        } catch (Exception e) {
            System.err.println("[PlanEstudioService] Groq response parse error. Raw: " + response);
            throw new RuntimeException("La IA devolvio un formato inesperado: " + response.substring(0, Math.min(200, response.length())));
        }
    }
}
