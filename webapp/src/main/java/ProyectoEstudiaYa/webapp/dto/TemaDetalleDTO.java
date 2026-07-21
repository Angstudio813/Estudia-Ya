package ProyectoEstudiaYa.webapp.dto;

import java.util.List;

public class TemaDetalleDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Integer orden;
    private Long cursoId;
    private String cursoNombre;
    private String cursoColor;
    private Integer totalEjercicios;
    private Integer ejerciciosResueltos;
    private Double porcentajeAcierto;
    private Boolean necesitaRefuerzo;
    private List<EjercicioResumenDTO> ejercicios;

    public TemaDetalleDTO() {
    }

    public TemaDetalleDTO(Long id, String nombre, String descripcion, Integer orden,
            Long cursoId, String cursoNombre, String cursoColor,
            Integer totalEjercicios, Integer ejerciciosResueltos,
            Double porcentajeAcierto, Boolean necesitaRefuerzo,
            List<EjercicioResumenDTO> ejercicios) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.orden = orden;
        this.cursoId = cursoId;
        this.cursoNombre = cursoNombre;
        this.cursoColor = cursoColor;
        this.totalEjercicios = totalEjercicios;
        this.ejerciciosResueltos = ejerciciosResueltos;
        this.porcentajeAcierto = porcentajeAcierto;
        this.necesitaRefuerzo = necesitaRefuerzo;
        this.ejercicios = ejercicios;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Integer getOrden() { return orden; }
    public Long getCursoId() { return cursoId; }
    public String getCursoNombre() { return cursoNombre; }
    public String getCursoColor() { return cursoColor; }
    public Integer getTotalEjercicios() { return totalEjercicios; }
    public Integer getEjerciciosResueltos() { return ejerciciosResueltos; }
    public Double getPorcentajeAcierto() { return porcentajeAcierto; }
    public Boolean getNecesitaRefuerzo() { return necesitaRefuerzo; }
    public List<EjercicioResumenDTO> getEjercicios() { return ejercicios; }

    public static class EjercicioResumenDTO {
        private Long id;
        private String pregunta;
        private String dificultad;
        private Boolean generadoPorIA;

        public EjercicioResumenDTO() {
        }

        public EjercicioResumenDTO(Long id, String pregunta, String dificultad, Boolean generadoPorIA) {
            this.id = id;
            this.pregunta = pregunta;
            this.dificultad = dificultad;
            this.generadoPorIA = generadoPorIA;
        }

        public Long getId() { return id; }
        public String getPregunta() { return pregunta; }
        public String getDificultad() { return dificultad; }
        public Boolean getGeneradoPorIA() { return generadoPorIA; }
    }
}
