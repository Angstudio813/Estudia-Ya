package ProyectoEstudiaYa.webapp.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @Value("${app.frontend.login-url:http://localhost:4200/login}")
    private String frontendLoginUrl;

    @GetMapping("/login")
    public String login() {
        return "redirect:" + frontendLoginUrl;
    }
}
