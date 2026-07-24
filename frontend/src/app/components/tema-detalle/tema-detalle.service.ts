import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/api-base';

export interface EjercicioResumen {
  id: number;
  pregunta: string;
  dificultad: string;
  generadoPorIA: boolean;
}

export interface TemaDetalle {
  id: number;
  nombre: string;
  descripcion: string;
  orden: number;
  cursoId: number;
  cursoNombre: string;
  cursoColor: string;
  totalEjercicios: number;
  ejerciciosResueltos: number;
  porcentajeAcierto: number;
  necesitaRefuerzo: boolean;
  ejercicios: EjercicioResumen[];
}

@Injectable({
  providedIn: 'root',
})
export class TemaDetalleService {
  private readonly apiUrl = `${API_BASE_URL}/api/temas`;

  constructor(private http: HttpClient) {}

  obtenerDetalle(temaId: number, usuarioId?: number): Observable<TemaDetalle> {
    let params = new HttpParams();
    if (usuarioId != null) {
      params = params.set('usuarioId', usuarioId.toString());
    }
    return this.http.get<TemaDetalle>(`${this.apiUrl}/${temaId}`, {
      params,
    });
  }
}
