package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.CursoDetalleDTO;
import ProyectoEstudiaYa.webapp.services.CursoDetalleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cursos")
public class CursoDetalleController {

    private final CursoDetalleService cursoDetalleService;

    public CursoDetalleController(CursoDetalleService cursoDetalleService) {
        this.cursoDetalleService = cursoDetalleService;
    }

    @GetMapping("/{cursoId}")
    public CursoDetalleDTO obtenerDetalle(
            @PathVariable Long cursoId,
            @RequestParam(required = false) Long usuarioId) {
        return cursoDetalleService.obtenerDetalle(cursoId, usuarioId);
    }
}
