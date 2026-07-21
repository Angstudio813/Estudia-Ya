package ProyectoEstudiaYa.webapp.dto;

import java.util.List;

public class CursoDetalleDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String nivel;
    private Integer grado;
    private String colorHex;
    private String icono;
    private Integer totalTemas;
    private Integer totalEjercicios;
    private Integer progreso;
    private String estado;
    private List<TemaResumenDTO> temas;

    public CursoDetalleDTO() {
    }

    public CursoDetalleDTO(Long id, String nombre, String descripcion, String nivel, Integer grado,
            String colorHex, String icono, Integer totalTemas, Integer totalEjercicios,
            Integer progreso, String estado, List<TemaResumenDTO> temas) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.nivel = nivel;
        this.grado = grado;
        this.colorHex = colorHex;
        this.icono = icono;
        this.totalTemas = totalTemas;
        this.totalEjercicios = totalEjercicios;
        this.progreso = progreso;
        this.estado = estado;
        this.temas = temas;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getNivel() { return nivel; }
    public Integer getGrado() { return grado; }
    public String getColorHex() { return colorHex; }
    public String getIcono() { return icono; }
    public Integer getTotalTemas() { return totalTemas; }
    public Integer getTotalEjercicios() { return totalEjercicios; }
    public Integer getProgreso() { return progreso; }
    public String getEstado() { return estado; }
    public List<TemaResumenDTO> getTemas() { return temas; }

    public static class TemaResumenDTO {
        private Long id;
        private String nombre;
        private String descripcion;
        private Integer orden;
        private Integer totalEjercicios;
        private Integer ejerciciosResueltos;
        private Double porcentajeAcierto;
        private Boolean necesitaRefuerzo;
        private String estado;

        public TemaResumenDTO() {
        }

        public TemaResumenDTO(Long id, String nombre, String descripcion, Integer orden,
                Integer totalEjercicios, Integer ejerciciosResueltos,
                Double porcentajeAcierto, Boolean necesitaRefuerzo, String estado) {
            this.id = id;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.orden = orden;
            this.totalEjercicios = totalEjercicios;
            this.ejerciciosResueltos = ejerciciosResueltos;
            this.porcentajeAcierto = porcentajeAcierto;
            this.necesitaRefuerzo = necesitaRefuerzo;
            this.estado = estado;
        }

        public Long getId() { return id; }
        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
        public Integer getOrden() { return orden; }
        public Integer getTotalEjercicios() { return totalEjercicios; }
        public Integer getEjerciciosResueltos() { return ejerciciosResueltos; }
        public Double getPorcentajeAcierto() { return porcentajeAcierto; }
        public Boolean getNecesitaRefuerzo() { return necesitaRefuerzo; }
        public String getEstado() { return estado; }
    }
}
