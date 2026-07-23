package ProyectoEstudiaYa.webapp.security;

import ProyectoEstudiaYa.webapp.entities.UsuarioEntity;
import ProyectoEstudiaYa.webapp.repositories.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public LoginSuccessHandler(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String role = usuarioRepository.findByEmail(authentication.getName())
                .map(u -> u.getRol().name())
                .orElse("ESTUDIANTE");

        String token = jwtService.generateToken(authentication.getName(), role);
        ResponseCookie jwtCookie = ResponseCookie.from(JwtService.COOKIE_NAME, token)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(Duration.ofMillis(jwtService.getExpirationMs()))
                .build();
        response.setHeader("Set-Cookie", jwtCookie.toString());

        String next = request.getParameter("next");
        if (next == null || next.isBlank()) {
            next = "/";
        }

        getRedirectStrategy().sendRedirect(request, response, next);
    }
}
