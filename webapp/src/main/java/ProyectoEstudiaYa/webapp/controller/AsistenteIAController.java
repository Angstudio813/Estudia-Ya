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

    @GetMapping("/{usuarioId}") 
    public AsistenteIARespuestaDTO obtenerAsistenciaApi(@PathVariable Long usuarioId) {
        return asistenteIAService.generarAsistencia(usuarioId);
    }


    @PostMapping("/{usuarioId}/chat") 
    public Map<String, String> chatApi(@PathVariable Long usuarioId, @RequestParam String pregunta) {
        return Map.of("respuesta", asistenteIAService.chatLibre(usuarioId, pregunta));
    }
    }