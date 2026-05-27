package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.RegistroUsuarioDTO;
import ProyectoEstudiaYa.webapp.entities.Usuario;
import ProyectoEstudiaYa.webapp.services.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/gestion-usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuarioDTO", new RegistroUsuarioDTO());
        model.addAttribute("niveles", Usuario.NivelEducativo.values());
        return "gestion-usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        RegistroUsuarioDTO dto = new RegistroUsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setNivel(usuario.getNivel());
        dto.setGrado(usuario.getGrado());
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("usuarioDTO", dto);
        model.addAttribute("niveles", Usuario.NivelEducativo.values());
        return "gestion-usuarios";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(RegistroUsuarioDTO dto, Model model) {
        if (!validarUsuarioDTO(dto, model)) {
            return "gestion-usuarios";
        }

        Usuario usuario;
        if (dto.getId() != null) {
            usuario = usuarioService.obtenerPorId(dto.getId());
            usuario.setNombre(dto.getNombre());
            usuario.setApellido(dto.getApellido());
            usuario.setEmail(dto.getEmail());
            usuario.setNivel(dto.getNivel());
            usuario.setGrado(dto.getGrado());
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                usuario.setPassword(dto.getPassword());
            }
        } else {
            usuario = new Usuario();
            usuario.setNombre(dto.getNombre());
            usuario.setApellido(dto.getApellido());
            usuario.setEmail(dto.getEmail());
            usuario.setPassword(dto.getPassword());
            usuario.setNivel(dto.getNivel());
            usuario.setGrado(dto.getGrado());
        }
        usuarioService.saveUsuario(usuario);
        return "redirect:/gestion-usuarios";
    }

    private boolean validarUsuarioDTO(RegistroUsuarioDTO dto, Model model) {
        List<String> errores = new ArrayList<>();

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            errores.add("El nombre es obligatorio.");
        }
        if (dto.getApellido() == null || dto.getApellido().isBlank()) {
            errores.add("El apellido es obligatorio.");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            errores.add("El email es obligatorio.");
        }
        if (dto.getNivel() == null) {
            errores.add("El nivel educativo es obligatorio.");
        }
        if (dto.getGrado() == null) {
            errores.add("El grado es obligatorio.");
        }
        if (dto.getId() == null && (dto.getPassword() == null || dto.getPassword().isBlank())) {
            errores.add("La contraseña es obligatoria para nuevos usuarios.");
        }

        if (!errores.isEmpty()) {
            model.addAttribute("errorMessage", String.join(" ", errores));
            model.addAttribute("usuarios", usuarioService.findAll());
            model.addAttribute("niveles", Usuario.NivelEducativo.values());
            model.addAttribute("usuarioDTO", dto);
            return false;
        }
        return true;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return "redirect:/gestion-usuarios";
    }
}
