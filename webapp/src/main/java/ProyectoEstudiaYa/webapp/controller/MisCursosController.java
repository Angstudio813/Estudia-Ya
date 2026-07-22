package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.MisCursosDTO;
import ProyectoEstudiaYa.webapp.services.MisCursosService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mis-cursos")
public class MisCursosController {

    private final MisCursosService misCursosService;

    public MisCursosController(MisCursosService misCursosService) {
        this.misCursosService = misCursosService;
    }

    @GetMapping
    public List<MisCursosDTO> listarMisCursos(
            @RequestParam(required = false) Long usuarioId) {
        return misCursosService.listarCursos(usuarioId);
    }
}