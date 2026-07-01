package ProyectoEstudiaYa.webapp.controller;

import ProyectoEstudiaYa.webapp.dto.AuthLoginRequest;
import ProyectoEstudiaYa.webapp.dto.AuthTokenResponse;
import ProyectoEstudiaYa.webapp.entities.Usuario;
import ProyectoEstudiaYa.webapp.services.UsuarioService;
import ProyectoEstudiaYa.webapp.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    public AuthTokenResponse login(@RequestBody AuthLoginRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String token = jwtService.generateToken(authentication.getName());
        Usuario usuario = usuarioService.obtenerPorEmail(authentication.getName());
        Cookie jwtCookie = new Cookie(JwtService.COOKIE_NAME, token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge((int) Duration.ofMillis(jwtService.getExpirationMs()).getSeconds());
        response.addCookie(jwtCookie);

        return new AuthTokenResponse(token, usuario);
    }
}
