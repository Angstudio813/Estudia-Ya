import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/api-base';

export interface ProgresoTema {
  tema: string;
  ejerciciosIntentados: number;
  ejerciciosCorrectos: number;
  porcentajeAcierto: number;
  necesitaRefuerzo: boolean;
}

export interface ProgresoResumen {
  usuarioId: number;
  totalTemas: number;
  promedioAcierto: number;
  temasEnRefuerzo: number;
  ejerciciosCorrectos: number;
  logrosTotales: number;
  detalleTemas: ProgresoTema[];
}

@Injectable({
  providedIn: 'root',
})
export class ProgresoService {
  private readonly apiUrl = `${API_BASE_URL}/progreso`;

  constructor(private http: HttpClient) {}

  obtenerProgreso(usuarioId: number): Observable<ProgresoResumen> {
    return this.http.get<ProgresoResumen>(`${this.apiUrl}/${usuarioId}`);
  }
}