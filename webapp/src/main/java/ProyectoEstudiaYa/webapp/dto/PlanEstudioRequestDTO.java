package ProyectoEstudiaYa.webapp.dto;

import java.util.List;

public class PlanEstudioRequestDTO {

    private List<String> cursos;
    private int horasDisponiblesPorDia;
    private int diasDisponibles;

    public List<String> getCursos() {
        return cursos;
    }

    public void setCursos(List<String> cursos) {
        this.cursos = cursos;
    }

    public int getHorasDisponiblesPorDia() {
        return horasDisponiblesPorDia;
    }

    public void setHorasDisponiblesPorDia(int horasDisponiblesPorDia) {
        this.horasDisponiblesPorDia = horasDisponiblesPorDia;
    }

    public int getDiasDisponibles() {
        return diasDisponibles;
    }

    public void setDiasDisponibles(int diasDisponibles) {
        this.diasDisponibles = diasDisponibles;
    }
}