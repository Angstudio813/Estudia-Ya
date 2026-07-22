package ProyectoEstudiaYa.webapp.dto;

import java.util.List;

public class GamificacionDTO {

    private Long usuarioId;
    private Integer puntos;
    private Integer nivel;
    private Integer rachaActual;
    private Integer rachaMasAlta;
    private List<LogroDTO> logros;

    public GamificacionDTO() {
    }

    public GamificacionDTO(Long usuarioId, Integer puntos, Integer nivel, Integer rachaActual,
            Integer rachaMasAlta, List<LogroDTO> logros) {
        this.usuarioId = usuarioId;
        this.puntos = puntos;
        this.nivel = nivel;
        this.rachaActual = rachaActual;
        this.rachaMasAlta = rachaMasAlta;
        this.logros = logros;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public Integer getNivel() {
        return nivel;
    }

    public Integer getRachaActual() {
        return rachaActual;
    }

    public Integer getRachaMasAlta() {
        return rachaMasAlta;
    }

    public List<LogroDTO> getLogros() {
        return logros;
    }

    public static class LogroDTO {
        private Long id;
        private String nombre;
        private String descripcion;
        private String icono;
        private String tipo;
        private String fechaDesbloqueado;

        public LogroDTO() {
        }

        public LogroDTO(Long id, String nombre, String descripcion, String icono, String tipo,
                String fechaDesbloqueado) {
            this.id = id;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.icono = icono;
            this.tipo = tipo;
            this.fechaDesbloqueado = fechaDesbloqueado;
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public String getIcono() {
            return icono;
        }

        public String getTipo() {
            return tipo;
        }

        public String getFechaDesbloqueado() {
            return fechaDesbloqueado;
        }
    }
}
