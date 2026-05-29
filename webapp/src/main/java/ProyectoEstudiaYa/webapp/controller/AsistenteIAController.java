package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.AsistenteIARespuestaDTO;
import ProyectoEstudiaYa.webapp.services.AsistenteIAService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/asistente-ia")
public class AsistenteIAController {

    private final AsistenteIAService asistenteIAService;

    public AsistenteIAController(AsistenteIAService asistenteIAService) {
        this.asistenteIAService = asistenteIAService;
    }

    // GET: Carga la vista de inmediato (Sin congelar por culpa de la IA)
    @GetMapping("/{usuarioId}")
    public String verAsistente(@PathVariable Long usuarioId, Model model) {
        // Pasamos solo el ID del usuario para que la vista sepa a quién consultar por JS
        model.addAttribute("usuarioId", usuarioId);
        return "asistente-ia";
    }

    // POST: El chat libre procesa su respuesta y redirecciona limpiamente
    @PostMapping("/chat")
    public String chat(@RequestParam Long usuarioId,
                       @RequestParam String pregunta,
                       RedirectAttributes redirectAttributes) {
        
        // Solo llamamos a la función de chat libre (1 sola petición a la IA)
        String respuesta = asistenteIAService.chatLibre(usuarioId, pregunta);
        
        // Guardamos los datos para mostrarlos tras la redirección
        redirectAttributes.addFlashAttribute("respuestaChat", respuesta);
        redirectAttributes.addFlashAttribute("preguntaRealizada", pregunta);

        return "redirect:/asistente-ia/" + usuarioId;
    }

    // Tu API REST que usará JavaScript para cargar los datos pesados en segundo plano
    @GetMapping("/api/{usuarioId}")
    @ResponseBody
    public AsistenteIARespuestaDTO obtenerAsistenciaApi(@PathVariable Long usuarioId) {
        return asistenteIAService.generarAsistencia(usuarioId);
    }
}