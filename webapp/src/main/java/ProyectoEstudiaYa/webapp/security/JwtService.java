package ProyectoEstudiaYa.webapp.security;

import ProyectoEstudiaYa.webapp.entities.UsuarioEntity;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    public static final String COOKIE_NAME = "ESTUDIAYA_JWT";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).build();
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, String role) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(expirationMs, ChronoUnit.MILLIS);

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(Date.from(issuedAt))
                .withExpiresAt(Date.from(expiresAt))
                .withClaim("rol", role)
                .sign(algorithm);
    }

    public String extractUsername(String token) {
        return verify(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            DecodedJWT decodedJWT = verify(token);
            return decodedJWT.getSubject() != null && decodedJWT.getSubject().equals(userDetails.getUsername());
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    private DecodedJWT verify(String token) {
        return verifier.verify(token);
    }
}