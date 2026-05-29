package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.entities.Usuario;
import ProyectoEstudiaYa.webapp.dto.AsistenteIARespuestaDTO;
import ProyectoEstudiaYa.webapp.services.AsistenteIAService;
import ProyectoEstudiaYa.webapp.services.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/asistente-ia")
public class AsistenteIAController {

    private final AsistenteIAService asistenteIAService;
    private final UsuarioService usuarioService;

    public AsistenteIAController(AsistenteIAService asistenteIAService, UsuarioService usuarioService) {
        this.asistenteIAService = asistenteIAService;
        this.usuarioService = usuarioService;
    }

    // GET: Carga la vista de inmediato (Sin congelar por culpa de la IA)
    @GetMapping("/{usuarioId}")
    public String verAsistente(@PathVariable Long usuarioId, Authentication authentication, Model model) {
        Usuario usuarioAutenticado = obtenerUsuarioAutenticado(authentication);

        if (usuarioAutenticado != null && !usuarioAutenticado.getId().equals(usuarioId)) {
            return "redirect:/asistente-ia/" + usuarioAutenticado.getId();
        }

        if (usuarioAutenticado != null) {
            model.addAttribute("usuario", usuarioAutenticado);
            model.addAttribute("usuarioId", usuarioAutenticado.getId());
        } else {
            model.addAttribute("usuarioId", usuarioId);
        }
        return "asistente-ia";
    }

    // POST: El chat libre procesa su respuesta y redirecciona limpiamente
    @PostMapping("/chat")
    public String chat(@RequestParam Long usuarioId,
                       Authentication authentication,
                       @RequestParam String pregunta,
                       RedirectAttributes redirectAttributes) {

        Usuario usuarioAutenticado = obtenerUsuarioAutenticado(authentication);
        Long usuarioRealId = usuarioAutenticado != null ? usuarioAutenticado.getId() : usuarioId;
        
        // Solo llamamos a la función de chat libre (1 sola petición a la IA)
        String respuesta = asistenteIAService.chatLibre(usuarioRealId, pregunta);
        
        // Guardamos los datos para mostrarlos tras la redirección
        redirectAttributes.addFlashAttribute("respuestaChat", respuesta);
        redirectAttributes.addFlashAttribute("preguntaRealizada", pregunta);

        return "redirect:/asistente-ia/" + usuarioRealId;
    }

    // Tu API REST que usará JavaScript para cargar los datos pesados en segundo plano
    @GetMapping("/api/{usuarioId}")
    @ResponseBody
    public AsistenteIARespuestaDTO obtenerAsistenciaApi(@PathVariable Long usuarioId) {
        return asistenteIAService.generarAsistencia(usuarioId);
    }

    private Usuario obtenerUsuarioAutenticado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return null;
        }

        return usuarioService.findByEmail(authentication.getName()).orElse(null);
    }
}