package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.MisCursosDTO;
import ProyectoEstudiaYa.webapp.entities.Usuario;
import ProyectoEstudiaYa.webapp.services.MisCursosService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class MisCursosController {

    private final MisCursosService misCursosService;

    public MisCursosController(MisCursosService misCursosService) {
        this.misCursosService = misCursosService;
    }

    @GetMapping("/")
    public String verInicio() {
        return "index";
    }

    @GetMapping("/mis-cursos")
    public String verMisCursos(@RequestParam(required = false, defaultValue = "1") Long usuarioId, Model model) {
        List<MisCursosDTO> cursos = misCursosService.listarCursos(usuarioId);
        model.addAttribute("cursos", cursos);
        model.addAttribute("totalTemas", cursos.stream().mapToInt(curso -> valorSeguro(curso.getTotalTemas())).sum());
        model.addAttribute("totalEjercicios", cursos.stream().mapToInt(curso -> valorSeguro(curso.getTotalEjercicios())).sum());
        return "mis-cursos";
    }

    @GetMapping("/api/mis-cursos")
    @ResponseBody
    public List<MisCursosDTO> listarMisCursos(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Usuario.NivelEducativo nivel,
            @RequestParam(required = false) Integer grado) {
        if (nivel != null && grado != null) {
            return misCursosService.listarPorNivelYGrado(nivel, grado);
        }

        return misCursosService.listarCursos(usuarioId);
    }

    private int valorSeguro(Integer valor) {
        return valor == null ? 0 : valor;
    }
}
