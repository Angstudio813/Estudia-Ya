package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.UsuarioDTO;
import ProyectoEstudiaYa.webapp.dto.UsuarioRequestDTO;
import ProyectoEstudiaYa.webapp.entities.Usuario;
import ProyectoEstudiaYa.webapp.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioApiController {

    private final UsuarioService usuarioService;

    public UsuarioApiController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioDTO> listar() {
        return usuarioService.findAll().stream()
                .sorted(Comparator.comparing(Usuario::getId))
                .map(UsuarioDTO::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public UsuarioDTO obtener(@PathVariable Long id) {
        return UsuarioDTO.fromEntity(obtenerUsuario(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@RequestBody UsuarioRequestDTO request) {
        validarRequest(request, true);
        validarEmailDisponible(request.getEmail(), null);

        Usuario usuario = new Usuario();
        aplicarDatos(usuario, request);
        usuario.setPassword(request.getPassword());
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setUltimoAcceso(LocalDateTime.now());

        Usuario guardado = usuarioService.saveUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioDTO.fromEntity(guardado));
    }

    @PutMapping("/{id}")
    public UsuarioDTO actualizar(@PathVariable Long id, @RequestBody UsuarioRequestDTO request) {
        validarRequest(request, false);
        validarEmailDisponible(request.getEmail(), id);

        Usuario usuario = obtenerUsuario(id);
        aplicarDatos(usuario, request);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(request.getPassword());
        }

        return UsuarioDTO.fromEntity(usuarioService.saveUsuario(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        obtenerUsuario(id);
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Usuario obtenerUsuario(Long id) {
        try {
            return usuarioService.obtenerPorId(id);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.");
        }
    }

    private void aplicarDatos(Usuario usuario, UsuarioRequestDTO request) {
        usuario.setNombre(limpiar(request.getNombre()));
        usuario.setApellido(limpiar(request.getApellido()));
        usuario.setEmail(limpiar(request.getEmail()).toLowerCase());
        usuario.setNivel(request.getNivel());
        usuario.setGrado(request.getGrado());
    }

    private void validarRequest(UsuarioRequestDTO request, boolean passwordObligatorio) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de usuario obligatorios.");
        }
        if (esVacio(request.getNombre())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio.");
        }
        if (esVacio(request.getApellido())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El apellido es obligatorio.");
        }
        if (esVacio(request.getEmail()) || !request.getEmail().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email no es valido.");
        }
        if (request.getNivel() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nivel educativo es obligatorio.");
        }
        if (!gradoValido(request.getNivel(), request.getGrado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El grado no corresponde al nivel educativo.");
        }
        if (passwordObligatorio && esVacio(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contrasena es obligatoria.");
        }
    }

    private void validarEmailDisponible(String email, Long usuarioIdActual) {
        usuarioService.findByEmail(limpiar(email).toLowerCase()).ifPresent(usuario -> {
            if (!usuario.getId().equals(usuarioIdActual)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un usuario con ese email.");
            }
        });
    }

    private boolean gradoValido(Usuario.NivelEducativo nivel, Integer grado) {
        if (grado == null) {
            return false;
        }
        if (nivel == Usuario.NivelEducativo.PRIMARIA) {
            return grado >= 1 && grado <= 6;
        }
        return grado >= 1 && grado <= 5;
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
