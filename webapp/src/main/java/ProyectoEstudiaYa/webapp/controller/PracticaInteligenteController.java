package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.PracticaInteligenteDTO;
import ProyectoEstudiaYa.webapp.services.PracticaInteligenteService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/practica-inteligente")
public class PracticaInteligenteController {

    private final PracticaInteligenteService practicaInteligenteService;

    public PracticaInteligenteController(PracticaInteligenteService practicaInteligenteService) {
        this.practicaInteligenteService = practicaInteligenteService;
    }

    @GetMapping
    public List<PracticaInteligenteDTO> listarPracticaInteligente(
            @RequestParam(required = false) Long usuarioId) {
        return practicaInteligenteService.listarEjerciciosDeRefuerzo(usuarioId);
    }
}