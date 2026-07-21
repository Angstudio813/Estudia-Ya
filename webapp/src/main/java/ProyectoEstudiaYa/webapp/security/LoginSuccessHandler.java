package ProyectoEstudiaYa.webapp.security;

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

    public LoginSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String token = jwtService.generateToken(authentication.getName());
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
