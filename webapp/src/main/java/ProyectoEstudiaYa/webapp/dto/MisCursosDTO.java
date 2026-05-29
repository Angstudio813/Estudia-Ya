package ProyectoEstudiaYa.webapp.dto;

public class MisCursosDTO {

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
    private String siguienteTema;
    private String recomendacion;

    public MisCursosDTO() {
    }

    public MisCursosDTO(Long id, String nombre, String descripcion, String nivel, Integer grado,
            String colorHex, String icono, Integer totalTemas) {
        this(id, nombre, descripcion, nivel, grado, colorHex, icono, totalTemas, 0, 0,
                "Por iniciar", "Explorar temario", "Empieza con el primer tema y completa una practica corta.");
    }

    public MisCursosDTO(Long id, String nombre, String descripcion, String nivel, Integer grado,
            String colorHex, String icono, Integer totalTemas, Integer totalEjercicios,
            Integer progreso, String estado, String siguienteTema, String recomendacion) {
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
        this.siguienteTema = siguienteTema;
        this.recomendacion = recomendacion;
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

    public String getNivel() {
        return nivel;
    }

    public Integer getGrado() {
        return grado;
    }

    public String getColorHex() {
        return colorHex;
    }

    public String getIcono() {
        return icono;
    }

    public Integer getTotalTemas() {
        return totalTemas;
    }

    public Integer getTotalEjercicios() {
        return totalEjercicios;
    }

    public Integer getProgreso() {
        return progreso;
    }

    public String getEstado() {
        return estado;
    }

    public String getSiguienteTema() {
        return siguienteTema;
    }

    public String getRecomendacion() {
        return recomendacion;
    }
}
