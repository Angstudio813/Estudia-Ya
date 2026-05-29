package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.dto.AsistenteIARespuestaDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class AsistenteIAService {

    @Value("${app.ai.custom.gemini.api-key}")
    private String apiKey;

    @Value("${app.ai.custom.gemini.model}")
    private String model;

    private final RestClient restClient = RestClient.create();

    // Supongamos que tienes un UsuarioRepository o similar para sacar info del
    // alumno de H2
    // En este ejemplo simularé los datos del alumno, pero puedes inyectar tu
    // repositorio aquí.

    /**
     * Genera un reporte personalizado analizando el perfil del alumno
     */
    public AsistenteIARespuestaDTO generarAsistencia(Long usuarioId) {
        // 1. Simulación de datos del alumno (Sustitúyelo por tu consulta a la base de
        // datos H2)
        String nombreAlumno = "Juan Pérez";
        String cursosMalos = "Matemáticas y Estructuras de Datos";

        // 2. Armar un prompt estricto para que Gemini devuelva un JSON estructurado
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
            // Llamar a Gemini
            String jsonRespuestaIA = llamarApiGemini(prompt);

            // Parsear la respuesta estructurada de la IA
            JsonObject jsonObject = JsonParser.parseString(jsonRespuestaIA).getAsJsonObject();

            String mensajePrincipal = jsonObject.get("mensajePrincipal").getAsString();

            List<String> temasRefuerzo = new ArrayList<>();
            JsonArray temasArray = jsonObject.getAsJsonArray("temasRefuerzo");
            temasArray.forEach(elem -> temasRefuerzo.add(elem.getAsString()));

            List<String> recomendaciones = new ArrayList<>();
            JsonArray recsArray = jsonObject.getAsJsonArray("recomendaciones");
            recsArray.forEach(elem -> recomendaciones.add(elem.getAsString()));

            // Retornar el DTO mapeado que espera tu controlador
            return new AsistenteIARespuestaDTO(usuarioId, nombreAlumno, mensajePrincipal, temasRefuerzo,
                    recomendaciones);

        } catch (Exception e) {
            // Plan de contingencia si la IA falla o el límite gratuito se satura
            // momentáneamente
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
        // Podrías buscar al usuario en H2 para darle contexto a la IA ("Juan pregunta:
        // ...")
        String promptContextualizado = "Como tutor de la plataforma EstudiaYa, responde brevemente a la siguiente duda del alumno: "
                + pregunta;

        try {
            return llamarApiGemini(promptContextualizado);
        } catch (Exception e) {
            // ESTO IMPRIMIRÁ EL ERROR REAL EN TU CONSOLA DE SPRING BOOT (IDE)
            e.printStackTrace();
            return "Error real: " + e.getMessage();
        }
    }

    /**
     * Método auxiliar privado que gestiona la comunicación HTTP POST directa con
     * Google AI Studio
     */
    private String llamarApiGemini(String prompt) {
        // Construimos la URL uniendo las variables limpiamente
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + this.model.trim()
                + ":generateContent?key=" + this.apiKey.trim();

        // El JSON que le enviaremos a Google
        String jsonBody = "{\"contents\": [{\"parts\":[{\"text\": \"" + prompt.replace("\"", "\\\"") + "\"}]}]}";

        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(jsonBody,
                headers);

        // Hacemos la petición POST a Google
        String response = restTemplate.postForObject(url, entity, String.class);

        // Recortamos la respuesta para extraer solo el texto que nos interesa
        try {
            int inicio = response.indexOf("\"text\": \"") + 9;
            int fin = response.indexOf("\"", inicio);
            return response.substring(inicio, fin).replace("\\n", "\n");
        } catch (Exception e) {
            return response; // Si hay problemas recortando, te muestra el JSON crudo para ver la respuesta
        }
    }

}