package ProyectoEstudiaYa.webapp.dto;

import java.util.Map;

public class PlanEstudioResponseDTO {

    private Map<String, String[]> horario;

    public PlanEstudioResponseDTO() {
    }

    public PlanEstudioResponseDTO(Map<String, String[]> horario) {
        this.horario = horario;
    }

    public Map<String, String[]> getHorario() {
        return horario;
    }

    public void setHorario(Map<String, String[]> horario) {
        this.horario = horario;
    }
}
