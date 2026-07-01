import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/api-base';

export type NivelEducativo = 'PRIMARIA' | 'SECUNDARIA';

export interface Usuario {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  nivel: NivelEducativo;
  grado: number;
  xpTotal: number;
  nivelJuego: number;
  rachaActual: number;
  rachaMasAlta: number;
  fechaRegistro: string | null;
  ultimoAcceso: string | null;
}

export interface UsuarioPayload {
  nombre: string;
  apellido: string;
  email: string;
  nivel: NivelEducativo;
  grado: number;
  password?: string;
}

@Injectable({
  providedIn: 'root',
})
export class GestionUsuariosService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${API_BASE_URL}/api/usuarios`;

  listar(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiUrl, { withCredentials: true });
  }

  crear(payload: UsuarioPayload): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, payload, { withCredentials: true });
  }

  actualizar(id: number, payload: UsuarioPayload): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiUrl}/${id}`, payload, { withCredentials: true });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }
}
