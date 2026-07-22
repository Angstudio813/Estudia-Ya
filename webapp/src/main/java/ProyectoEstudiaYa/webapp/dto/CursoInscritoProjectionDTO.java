package ProyectoEstudiaYa.webapp.dto;

public interface CursoInscritoProjectionDTO {
    Long getCursoId();
    String getNombre();
    String getDescripcion();
    String getNivel();
    Integer getGrado();
    String getColorHex();
    String getIcono();
    Integer getPorcentajeCompletado();
    Long getTotalTemas();
    Long getTotalEjercicios();
    Long getSiguienteTemaOrden();
    String getSiguienteTemaNombre();
}
