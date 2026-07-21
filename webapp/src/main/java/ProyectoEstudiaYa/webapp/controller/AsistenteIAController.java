package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.AsistenteIARespuestaDTO;
import ProyectoEstudiaYa.webapp.services.AsistenteIAService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/asistente-ia")
public class AsistenteIAController {

    private final AsistenteIAService asistenteIAService;

    public AsistenteIAController(AsistenteIAService asistenteIAService) {
        this.asistenteIAService = asistenteIAService;
    }

// SI EN ANGULAR MANDAS: `${this.apiUrl}/api/${usuarioId}`
    // ENTONCES AQUÍ DEBES AGREGAR "/api" EN EL GETMAPPING:
    @GetMapping("/api/{usuarioId}") 
    public AsistenteIARespuestaDTO obtenerAsistenciaApi(@PathVariable Long usuarioId) {
        return asistenteIAService.generarAsistencia(usuarioId);
    }


// SI EN ANGULAR MANDAS: `${this.apiUrl}/api/${usuarioId}/chat`
    // ENTONCES AQUÍ DEBES AGREGAR "/api" EN EL POSTMAPPING:
    @PostMapping("/api/{usuarioId}/chat") 
    public Map<String, String> chatApi(@PathVariable Long usuarioId, @RequestParam String pregunta) {
        return Map.of("respuesta", asistenteIAService.chatLibre(usuarioId, pregunta));
    }
    }