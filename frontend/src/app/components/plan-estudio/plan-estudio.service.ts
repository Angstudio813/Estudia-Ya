import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PlanEstudioRequest {
  cursos: string[];
  horasDisponiblesPorDia: number;
  diasDisponibles: number;
}

export interface PlanEstudioResponse {
  horario: { [dia: string]: string[] };
}

@Injectable({
  providedIn: 'root'
})
export class PlanEstudioService {
  private readonly apiUrl = 'http://localhost:8080/plan-estudio';

  constructor(private http: HttpClient) {}

  generarPlan(request: PlanEstudioRequest): Observable<PlanEstudioResponse> {
    return this.http.post<PlanEstudioResponse>(`${this.apiUrl}/generar`, request);
  }
}