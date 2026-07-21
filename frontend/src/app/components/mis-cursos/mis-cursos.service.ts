import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
// ¡Esta es la línea que te faltaba para quitar el error rojo!
import { API_BASE_URL } from '../../core/api-base';

export interface MisCurso {
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
  siguienteTema: string;
  recomendacion: string;
}

@Injectable({
  providedIn: 'root'
})
export class MisCursosService {
  private readonly http = inject(HttpClient);
  // Ahora usamos la variable global correctamente
  private readonly baseUrl = `${API_BASE_URL}/api/mis-cursos`;

  listarCursos(usuarioId: number): Observable<MisCurso[]> {
    return this.http.get<MisCurso[]>(`${this.baseUrl}?usuarioId=${usuarioId}`, { withCredentials: true });
  }
}