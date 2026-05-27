package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.entities.Usuario;
import ProyectoEstudiaYa.webapp.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario saveNewUsuario(Usuario u) {
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        return usuarioRepository.save(u);
    }

    public Usuario obtenerPorId(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + usuarioId));
    }

    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + email));
    }
}
