package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.PracticaInteligenteDTO;
import ProyectoEstudiaYa.webapp.services.PracticaInteligenteService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/curso/{cursoId}")
    public List<PracticaInteligenteDTO> listarPorCurso(@PathVariable Long cursoId) {
        return practicaInteligenteService.listarEjerciciosPorCurso(cursoId);
    }

    @GetMapping("/tema/{temaId}")
    public List<PracticaInteligenteDTO> listarPorTema(@PathVariable Long temaId) {
        return practicaInteligenteService.listarEjerciciosPorTema(temaId);
    }

    @PostMapping("/responder")
    public Map<String, Object> registrarIntento(
            @RequestParam Long usuarioId,
            @RequestParam Long ejercicioId,
            @RequestParam String respuesta) {
        PracticaInteligenteDTO ejercicio = practicaInteligenteService.registrarIntento(usuarioId, ejercicioId, respuesta);
        boolean esCorrecta = ejercicio.getRespuestaCorrecta().equalsIgnoreCase(respuesta);
        return Map.of(
                "ejercicio", ejercicio,
                "esCorrecta", esCorrecta,
                "respuestaCorrecta", ejercicio.getRespuestaCorrecta()
        );
    }

    @PostMapping("/generar-ia")
    public List<PracticaInteligenteDTO> generarEjerciciosIA(
            @RequestParam Long usuarioId,
            @RequestParam Long temaId,
            @RequestParam(defaultValue = "5") int cantidad) {
        return practicaInteligenteService.generarEjerciciosIA(usuarioId, temaId, cantidad);
    }
}
