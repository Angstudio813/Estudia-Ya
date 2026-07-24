import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/api-base';

export interface TemaResumen {
  id: number;
  nombre: string;
  descripcion: string;
  orden: number;
  totalEjercicios: number;
  ejerciciosResueltos: number;
  porcentajeAcierto: number;
  necesitaRefuerzo: boolean;
  estado: string;
}

export interface CursoDetalle {
  id: number;
  nombre: string;
  descripcion: string;
  nivel: string;
  grado: number;
  colorHex: string;
  icono: string;
  totalTemas: number;
  totalEjercicios: number;
  progreso: number;
  estado: string;
  temas: TemaResumen[];
}

@Injectable({
  providedIn: 'root',
})
export class CursoDetalleService {
  private readonly apiUrl = `${API_BASE_URL}/api/cursos`;

  constructor(private http: HttpClient) {}

  obtenerDetalle(cursoId: number, usuarioId?: number): Observable<CursoDetalle> {
    let params = new HttpParams();
    if (usuarioId != null) {
      params = params.set('usuarioId', usuarioId.toString());
    }
    return this.http.get<CursoDetalle>(`${this.apiUrl}/${cursoId}`, {
      params,
    });
  }
}
