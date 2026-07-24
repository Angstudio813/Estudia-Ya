import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/api-base';

export interface CursoInscrito {
  id: number;
  nombre: string;
  nivel: string;
  colorHex: string;
  icono: string;
}

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
  private readonly apiUrl = `${API_BASE_URL}/api/plan-estudio`;
  private readonly cursosUrl = `${API_BASE_URL}/api/mis-cursos`;

  constructor(private http: HttpClient) {}

  listarCursos(usuarioId: number): Observable<CursoInscrito[]> {
    return this.http.get<CursoInscrito[]>(`${this.cursosUrl}?usuarioId=${usuarioId}`);
  }

  generarPlan(request: PlanEstudioRequest): Observable<PlanEstudioResponse> {
    return this.http.post<PlanEstudioResponse>(`${this.apiUrl}/generar`, request, {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    });
  }
}