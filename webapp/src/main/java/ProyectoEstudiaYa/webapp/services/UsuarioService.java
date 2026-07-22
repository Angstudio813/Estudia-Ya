package ProyectoEstudiaYa.webapp.services;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ProyectoEstudiaYa.webapp.entities.UsuarioEntity;
import ProyectoEstudiaYa.webapp.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioEntity> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<UsuarioEntity> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public UsuarioEntity saveUsuario(UsuarioEntity usuario) {
        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
            String password = usuario.getPassword();
            if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                usuario.setPassword(passwordEncoder.encode(password));
            }
        }
        return usuarioRepository.save(usuario);
    }

    public UsuarioEntity saveNewUsuario(UsuarioEntity usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public UsuarioEntity obtenerPorId(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NoSuchElementException("UsuarioEntity no encontrado: " + usuarioId));
    }

    public UsuarioEntity obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("UsuarioEntity no encontrado: " + email));
    }
}
