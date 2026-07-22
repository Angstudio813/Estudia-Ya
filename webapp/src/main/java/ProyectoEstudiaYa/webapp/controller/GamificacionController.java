package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.GamificacionDTO;
import ProyectoEstudiaYa.webapp.services.GamificacionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gamificacion")
public class GamificacionController {

    private final GamificacionService gamificacionService;

    public GamificacionController(GamificacionService gamificacionService) {
        this.gamificacionService = gamificacionService;
    }

    @GetMapping("/{usuarioId}")
    public GamificacionDTO obtenerProgreso(@PathVariable Long usuarioId) {
        return gamificacionService.obtenerProgreso(usuarioId);
    }

    @PostMapping("/reto")
    public GamificacionDTO completarReto(
            @RequestParam Long usuarioId,
            @RequestParam String reto) {
        return gamificacionService.completarReto(usuarioId, reto);
    }
}
