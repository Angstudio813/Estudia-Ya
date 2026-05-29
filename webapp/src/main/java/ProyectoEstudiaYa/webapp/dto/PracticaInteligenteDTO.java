package ProyectoEstudiaYa.webapp.dto;

public class PracticaInteligenteDTO {

    private Long id;
    private String pregunta;
    private String opcionA;
    private String opcionB;
    private String opcionC;
    private String opcionD;
    private String dificultad;
    private Boolean generadoPorIA;
    private Long temaId;
    private String temaNombre;
    private String cursoNombre;
    private String respuestaCorrecta;
    private String explicacion;
    private String habilidad;
    private String recomendacion;
    private Integer xp;

    public PracticaInteligenteDTO() {
    }

    public PracticaInteligenteDTO(Long id, String pregunta, String opcionA, String opcionB,
            String opcionC, String opcionD, String dificultad, Boolean generadoPorIA,
            Long temaId, String temaNombre, String cursoNombre) {
        this(id, pregunta, opcionA, opcionB, opcionC, opcionD, dificultad, generadoPorIA,
                temaId, temaNombre, cursoNombre, "", "", "Refuerzo", "Resuelve y revisa la explicacion.", 10);
    }

    public PracticaInteligenteDTO(Long id, String pregunta, String opcionA, String opcionB,
            String opcionC, String opcionD, String dificultad, Boolean generadoPorIA,
            Long temaId, String temaNombre, String cursoNombre, String respuestaCorrecta,
            String explicacion, String habilidad, String recomendacion, Integer xp) {
        this.id = id;
        this.pregunta = pregunta;
        this.opcionA = opcionA;
        this.opcionB = opcionB;
        this.opcionC = opcionC;
        this.opcionD = opcionD;
        this.dificultad = dificultad;
        this.generadoPorIA = generadoPorIA;
        this.temaId = temaId;
        this.temaNombre = temaNombre;
        this.cursoNombre = cursoNombre;
        this.respuestaCorrecta = respuestaCorrecta;
        this.explicacion = explicacion;
        this.habilidad = habilidad;
        this.recomendacion = recomendacion;
        this.xp = xp;
    }

    public Long getId() {
        return id;
    }

    public String getPregunta() {
        return pregunta;
    }

    public String getOpcionA() {
        return opcionA;
    }

    public String getOpcionB() {
        return opcionB;
    }

    public String getOpcionC() {
        return opcionC;
    }

    public String getOpcionD() {
        return opcionD;
    }

    public String getDificultad() {
        return dificultad;
    }

    public Boolean getGeneradoPorIA() {
        return generadoPorIA;
    }

    public Long getTemaId() {
        return temaId;
    }

    public String getTemaNombre() {
        return temaNombre;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public String getExplicacion() {
        return explicacion;
    }

    public String getHabilidad() {
        return habilidad;
    }

    public String getRecomendacion() {
        return recomendacion;
    }

    public Integer getXp() {
        return xp;
    }
}
