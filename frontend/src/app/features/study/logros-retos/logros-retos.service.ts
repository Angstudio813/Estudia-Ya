import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { UsuarioProgreso } from '../../../shared/interfaces/logro-reto';

@Injectable({
  providedIn: 'root'
})
export class LogrosRetosService {
  private readonly apiUrl = 'http://localhost:8080/gamificacion';

  constructor(private http: HttpClient) {}

  obtenerProgreso(usuarioId: number): Observable<UsuarioProgreso> {
  return this.http
    .get<UsuarioProgreso>(`${this.apiUrl}/${usuarioId}`)
    .pipe(
      catchError(error => {
        console.error('Error al obtener progreso', error);
        return throwError(() => error);
      })
    );
}

completarReto(usuarioId: number, reto: string): Observable<UsuarioProgreso> {
  const params = new URLSearchParams({
    usuarioId: usuarioId.toString(),
    reto
  });

  return this.http
    .post<UsuarioProgreso>(
      `${this.apiUrl}/reto?${params.toString()}`,
      {}
    )
    .pipe(
      catchError(error => {
        console.error('Error al completar reto', error);
        return throwError(() => error);
      })
    );
}
}