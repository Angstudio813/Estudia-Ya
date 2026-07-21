import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/api-base';

export interface PracticaInteligenteDTO {
  id: number;
  pregunta: string;
  opcionA: string;
  opcionB: string;
  opcionC: string;
  opcionD: string;
  dificultad: 'FACIL' | 'MEDIO' | 'DIFICIL' | string;
  generadoPorIA: boolean;
  temaId: number | null;
  temaNombre: string;
  cursoNombre: string;
  respuestaCorrecta: string;
  explicacion: string;
  habilidad: string;
  recomendacion: string;
  xp: number;
}

export interface IntentoResponse {
  ejercicio: PracticaInteligenteDTO;
  esCorrecta: boolean;
  respuestaCorrecta: string;
}

@Injectable({
  providedIn: 'root',
})
export class PracticaInteligenteService {
  private readonly apiUrl = `${API_BASE_URL}/api/practica-inteligente`;

  constructor(private http: HttpClient) {}

  listar(usuarioId?: number): Observable<PracticaInteligenteDTO[]> {
    let params = new HttpParams();

    if (usuarioId != null) {
      params = params.set('usuarioId', usuarioId.toString());
    }

    return this.http.get<PracticaInteligenteDTO[]>(this.apiUrl, {
      params,
      withCredentials: true,
    });
  }

  listarPorCurso(cursoId: number): Observable<PracticaInteligenteDTO[]> {
    return this.http.get<PracticaInteligenteDTO[]>(`${this.apiUrl}/curso/${cursoId}`, {
      withCredentials: true,
    });
  }

  listarPorTema(temaId: number): Observable<PracticaInteligenteDTO[]> {
    return this.http.get<PracticaInteligenteDTO[]>(`${this.apiUrl}/tema/${temaId}`, {
      withCredentials: true,
    });
  }

  responder(usuarioId: number, ejercicioId: number, respuesta: string): Observable<IntentoResponse> {
    const params = new HttpParams()
      .set('usuarioId', usuarioId.toString())
      .set('ejercicioId', ejercicioId.toString())
      .set('respuesta', respuesta);

    return this.http.post<IntentoResponse>(`${this.apiUrl}/responder`, null, {
      params,
      withCredentials: true,
    });
  }

  generarIA(usuarioId: number, temaId: number, cantidad: number = 5): Observable<PracticaInteligenteDTO[]> {
    const params = new HttpParams()
      .set('usuarioId', usuarioId.toString())
      .set('temaId', temaId.toString())
      .set('cantidad', cantidad.toString());

    return this.http.post<PracticaInteligenteDTO[]>(`${this.apiUrl}/generar-ia`, null, {
      params,
      withCredentials: true,
    });
  }

  generarCursoIA(usuarioId: number, cursoId: number, cantidadPorTema: number = 3): Observable<PracticaInteligenteDTO[]> {
    const params = new HttpParams()
      .set('usuarioId', usuarioId.toString())
      .set('cursoId', cursoId.toString())
      .set('cantidadPorTema', cantidadPorTema.toString());

    return this.http.post<PracticaInteligenteDTO[]>(`${this.apiUrl}/generar-ia-curso`, null, {
      params,
      withCredentials: true,
    });
  }
}
