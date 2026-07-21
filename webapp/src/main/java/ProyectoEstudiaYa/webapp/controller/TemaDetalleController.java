package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.TemaDetalleDTO;
import ProyectoEstudiaYa.webapp.services.TemaDetalleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/temas")
public class TemaDetalleController {

    private final TemaDetalleService temaDetalleService;

    public TemaDetalleController(TemaDetalleService temaDetalleService) {
        this.temaDetalleService = temaDetalleService;
    }

    @GetMapping("/{temaId}")
    public TemaDetalleDTO obtenerDetalle(
            @PathVariable Long temaId,
            @RequestParam(required = false) Long usuarioId) {
        return temaDetalleService.obtenerDetalle(temaId, usuarioId);
    }
}
