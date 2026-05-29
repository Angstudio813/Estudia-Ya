package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.PracticaInteligenteDTO;
import ProyectoEstudiaYa.webapp.services.PracticaInteligenteService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PracticaInteligenteController {

    private final PracticaInteligenteService practicaInteligenteService;

    public PracticaInteligenteController(PracticaInteligenteService practicaInteligenteService) {
        this.practicaInteligenteService = practicaInteligenteService;
    }

    @GetMapping("/practica-inteligente")
    public String verPracticaInteligente(
            @RequestParam(required = false) Long usuarioId,
            Model model) {
        List<PracticaInteligenteDTO> practicas = practicaInteligenteService.listarEjerciciosDeRefuerzo(usuarioId);
        model.addAttribute("practicas", practicas);
        model.addAttribute("xpTotal", practicas.stream().mapToInt(practica -> valorSeguro(practica.getXp())).sum());
        return "practica-inteligente";
    }

    @GetMapping("/api/practica-inteligente")
    @ResponseBody
    public List<PracticaInteligenteDTO> listarPracticaInteligente(
            @RequestParam(required = false) Long usuarioId) {
        return practicaInteligenteService.listarEjerciciosDeRefuerzo(usuarioId);
    }

    private int valorSeguro(Integer valor) {
        return valor == null ? 0 : valor;
    }
}
