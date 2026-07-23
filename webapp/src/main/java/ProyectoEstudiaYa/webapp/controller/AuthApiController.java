package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.AuthLoginRequestDTO;
import ProyectoEstudiaYa.webapp.dto.AuthTokenResponseDTO;
import ProyectoEstudiaYa.webapp.entities.UsuarioEntity;
import ProyectoEstudiaYa.webapp.services.UsuarioService;
import ProyectoEstudiaYa.webapp.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public AuthApiController(AuthenticationManager authenticationManager, JwtService jwtService, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequestDTO request, HttpServletResponse response) {
        if (request == null || esVacio(request.getEmail()) || esVacio(request.getPassword())) {
            return ResponseEntity.badRequest().body(new AuthErrorResponse("Ingresa tu email y contrasena."));
        }

        String email = request.getEmail().trim().toLowerCase();
        UsuarioEntity usuario = usuarioService.findByEmail(email).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthErrorResponse("usuario no existe"));
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthErrorResponse("contrase\u00f1a incorrecta"));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthErrorResponse("No se pudo iniciar sesion."));
        }

        String rol = usuario.getRol().name();
        String token = jwtService.generateToken(authentication.getName(), rol);
        ResponseCookie jwtCookie = ResponseCookie.from(JwtService.COOKIE_NAME, token)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(Duration.ofMillis(jwtService.getExpirationMs()))
                .build();
        response.setHeader("Set-Cookie", jwtCookie.toString());

        return ResponseEntity.ok(new AuthTokenResponseDTO(token, usuario));
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private record AuthErrorResponse(String message) {
    }
}
