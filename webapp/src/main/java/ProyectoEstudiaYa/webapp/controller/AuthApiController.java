package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.AuthLoginRequestDTO;
import ProyectoEstudiaYa.webapp.dto.AuthTokenResponseDTO;
import ProyectoEstudiaYa.webapp.entities.CursoEntity;
import ProyectoEstudiaYa.webapp.entities.UsuarioCursoEntity;
import ProyectoEstudiaYa.webapp.entities.UsuarioEntity;
import ProyectoEstudiaYa.webapp.repositories.CursoRepository;
import ProyectoEstudiaYa.webapp.repositories.UsuarioCursoRepository;
import ProyectoEstudiaYa.webapp.services.UsuarioService;
import ProyectoEstudiaYa.webapp.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final CursoRepository cursoRepository;
    private final UsuarioCursoRepository usuarioCursoRepository;

    public AuthApiController(AuthenticationManager authenticationManager, JwtService jwtService,
                             UsuarioService usuarioService, CursoRepository cursoRepository,
                             UsuarioCursoRepository usuarioCursoRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.cursoRepository = cursoRepository;
        this.usuarioCursoRepository = usuarioCursoRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequestDTO request) {
        if (request == null || esVacio(request.getEmail()) || esVacio(request.getPassword())) {
            return ResponseEntity.badRequest().body(new AuthErrorResponse("Ingresa tu email y contrasena."));
        }

        String email = request.getEmail().trim().toLowerCase();

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthErrorResponse("Credenciales incorrectas."));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthErrorResponse("No se pudo iniciar sesion."));
        }

        UsuarioEntity usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado tras autenticacion exitosa."));

        String rol = usuario.getRol() != null ? usuario.getRol().name() : "ESTUDIANTE";
        String token = jwtService.generateToken(authentication.getName(), rol);

        return ResponseEntity.ok(new AuthTokenResponseDTO(token, usuario));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        if (request == null || esVacio(request.email()) || esVacio(request.password())
                || esVacio(request.nombre()) || esVacio(request.apellido())) {
            return ResponseEntity.badRequest().body(new AuthErrorResponse("Todos los campos son obligatorios."));
        }

        String email = request.email().trim().toLowerCase();

        if (usuarioService.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new AuthErrorResponse("Ya existe una cuenta con ese email."));
        }

        if (request.nivel() == null) {
            return ResponseEntity.badRequest().body(new AuthErrorResponse("Selecciona tu nivel educativo."));
        }

        if (request.grado() == null || request.grado() < 1) {
            return ResponseEntity.badRequest().body(new AuthErrorResponse("Selecciona tu grado."));
        }

        boolean primaria = request.nivel() == UsuarioEntity.NivelEducativo.PRIMARIA;
        if (primaria && request.grado() > 6) {
            return ResponseEntity.badRequest().body(new AuthErrorResponse("En primaria el grado maximo es 6."));
        }
        if (!primaria && request.grado() > 5) {
            return ResponseEntity.badRequest().body(new AuthErrorResponse("En secundaria el grado maximo es 5."));
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(request.nombre().trim());
        usuario.setApellido(request.apellido().trim());
        usuario.setEmail(email);
        usuario.setPassword(request.password());
        usuario.setNivel(request.nivel());
        usuario.setGrado(request.grado());
        usuario.setRol(UsuarioEntity.Rol.ESTUDIANTE);
        usuario.setFechaRegistro(java.time.LocalDateTime.now());
        usuario.setUltimoAcceso(java.time.LocalDateTime.now());

        usuarioService.saveNewUsuario(usuario);

        List<CursoEntity> cursosDelGrado = cursoRepository.findByNivelAndGrado(request.nivel(), request.grado());
        LocalDateTime ahora = LocalDateTime.now();
        for (CursoEntity curso : cursosDelGrado) {
            UsuarioCursoEntity uc = new UsuarioCursoEntity();
            uc.setUsuario(usuario);
            uc.setCurso(curso);
            uc.setPorcentajeCompletado(0);
            uc.setFechaInscripcion(ahora);
            usuarioCursoRepository.save(uc);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthErrorResponse("Cuenta creada correctamente. Ahora inicia sesion."));
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private record RegistroRequest(String nombre, String apellido, String email, String password,
                                   UsuarioEntity.NivelEducativo nivel, Integer grado) {
    }

    private record AuthErrorResponse(String message) {
    }
}
