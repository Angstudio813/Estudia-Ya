package ProyectoEstudiaYa.webapp.dto;

public class AuthTokenResponse {
    private final String token;
    private final String type;

    public AuthTokenResponse(String token) {
        this.token = token;
        this.type = "Bearer";
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }
}