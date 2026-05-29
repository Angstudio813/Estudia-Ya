package ProyectoEstudiaYa.webapp.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    public LoginSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String token = jwtService.generateToken(authentication.getName());
        Cookie jwtCookie = new Cookie(JwtService.COOKIE_NAME, token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge((int) Duration.ofMillis(jwtService.getExpirationMs()).getSeconds());
        response.addCookie(jwtCookie);

        String next = request.getParameter("next");
        if (next == null || next.isBlank()) {
            next = "/";
        }

        getRedirectStrategy().sendRedirect(request, response, next);
    }
}
