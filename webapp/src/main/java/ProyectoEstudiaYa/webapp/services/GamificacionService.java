package ProyectoEstudiaYa.webapp.services;

import ProyectoEstudiaYa.webapp.dto.GamificacionDTO;
import ProyectoEstudiaYa.webapp.entities.Logro;
import ProyectoEstudiaYa.webapp.entities.Usuario;
import ProyectoEstudiaYa.webapp.repositories.LogroRepository;
import ProyectoEstudiaYa.webapp.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class GamificacionService {

    private static final int XP_POR_RETO = 15;
    private static final int XP_POR_LOGRO = 25;

    private final UsuarioRepository usuarioRepository;
    private final LogroRepository logroRepository;

    public GamificacionService(UsuarioRepository usuarioRepository, LogroRepository logroRepository) {
        this.usuarioRepository = usuarioRepository;
        this.logroRepository = logroRepository;
    }

    public GamificacionDTO obtenerProgreso(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        List<GamificacionDTO.LogroDTO> logrosDTO = logroRepository.findByUsuarioIdOrdenadosPorFecha(usuarioId)
                .stream()
                .map(this::convertirLogroADTO)
                .toList();

        return new GamificacionDTO(
                usuario.getId(),
                usuario.getXpTotal(),
                usuario.getNivel_juego(),
                usuario.getRachaActual(),
                usuario.getRachaMasAlta(),
                logrosDTO);
    }

    @Transactional
    public GamificacionDTO completarReto(Long usuarioId, String retoTexto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));

        usuario.setXpTotal(usuario.getXpTotal() + XP_POR_RETO);
        recalcularNivel(usuario);
        usuarioRepository.save(usuario);

        String nombreLogro = "Reto: " + retoTexto;
        boolean yaExiste = logroRepository.existsByUsuarioIdAndNombre(usuarioId, nombreLogro);
        if (!yaExiste) {
            Logro logro = Logro.builder()
                    .nombre(nombreLogro)
                    .descripcion("Completaste el reto \"" + retoTexto + "\"")
                    .icono("fa-flag-checkered")
                    .tipo(Logro.TipoLogro.ESPECIAL)
                    .fechaDesbloqueado(LocalDateTime.now())
                    .usuario(usuario)
                    .build();
            logroRepository.save(logro);
        }

        return obtenerProgreso(usuarioId);
    }

    private void recalcularNivel(Usuario usuario) {
        int xp = usuario.getXpTotal();
        int nivel = (xp / 100) + 1;
        usuario.setNivel_juego(nivel);
    }

    private GamificacionDTO.LogroDTO convertirLogroADTO(Logro logro) {
        String fecha = logro.getFechaDesbloqueado() != null
                ? logro.getFechaDesbloqueado().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : null;
        return new GamificacionDTO.LogroDTO(
                logro.getId(),
                logro.getNombre(),
                logro.getDescripcion(),
                logro.getIcono(),
                logro.getTipo() != null ? logro.getTipo().name() : "ESPECIAL",
                fecha);
    }
}
