import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/api-base';

export interface AsistenteIARespuesta {
  usuarioId: number;
  nombreUsuario: string;
  mensajePrincipal: string;
  temasRefuerzo: string[];
  recomendaciones: string[];
}

export interface AsistenteChatRespuesta {
  respuesta: string;
}

@Injectable({
  providedIn: 'root',
})
export class AsistenteIAService {
  private readonly apiUrl = `${API_BASE_URL}/api/asistente-ia`;

  constructor(private http: HttpClient) {}

  obtenerAsistencia(usuarioId: number): Observable<AsistenteIARespuesta> {
    return this.http.get<AsistenteIARespuesta>(`${this.apiUrl}/${usuarioId}`, {
      withCredentials: true,
    });
  }

enviarPregunta(usuarioId: number, pregunta: string): Observable<AsistenteChatRespuesta> {
    const params = new HttpParams().set('pregunta', pregunta);

    return this.http.post<AsistenteChatRespuesta>(`${this.apiUrl}/${usuarioId}/chat`, {}, {
      params,
      withCredentials: true,
    });
  }
}