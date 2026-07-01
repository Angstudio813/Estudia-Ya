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
}