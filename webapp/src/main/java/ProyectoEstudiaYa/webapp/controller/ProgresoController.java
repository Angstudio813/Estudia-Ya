package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.ProgresoResumenDTO;
import ProyectoEstudiaYa.webapp.services.ProgresoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progreso")
public class ProgresoController {

    private final ProgresoService progresoService;

    public ProgresoController(ProgresoService progresoService) {
        this.progresoService = progresoService;
    }

    @GetMapping("/{usuarioId}")
    public ProgresoResumenDTO obtenerProgresoApi(@PathVariable Long usuarioId) {
        return progresoService.obtenerProgresoUsuario(usuarioId);
    }
}