import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/api-base';

export interface Logro {
  id: number;
  nombre: string;
  descripcion: string;
  icono: string;
  tipo: 'RACHA' | 'EJERCICIOS' | 'CURSO' | 'ESPECIAL';
  fechaDesbloqueado: string;
}

export interface UsuarioProgreso {
  puntos: number;
  logros: Logro[];
}

@Injectable({
  providedIn: 'root'
})
export class LogrosRetosService {
  private readonly apiUrl = `${API_BASE_URL}/api/gamificacion`;

  constructor(private http: HttpClient) {}

  obtenerProgreso(usuarioId: number): Observable<UsuarioProgreso> {
    return this.http.get<UsuarioProgreso>(`${this.apiUrl}/${usuarioId}`);
  }

  completarReto(usuarioId: number, reto: string): Observable<UsuarioProgreso> {
    const params = new HttpParams()
      .set('usuarioId', usuarioId.toString())
      .set('reto', reto);

    return this.http.post<UsuarioProgreso>(`${this.apiUrl}/reto`, {}, {
      params,
    });
  }
}