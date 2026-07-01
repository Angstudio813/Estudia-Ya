export interface PlanEstudioRequest {
  cursos: string[];
  horasDisponiblesPorDia: number;
  diasDisponibles: number;
}

export interface PlanEstudioResponse {
  horario: {
    [dia: string]: string[];
  };
}