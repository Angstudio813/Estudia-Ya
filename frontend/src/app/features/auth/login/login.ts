import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgClass } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth.service';
import { environment } from '../../../../environment';

interface LoginResponse {
  token: string;
  type: string;
  usuarioId: number;
  nombre: string;
  apellido: string;
  email: string;
  rol: string;
  nivel: string;
  grado: number;
  xpTotal?: number;
  nivelJuego?: number;
  rachaActual?: number;
  rachaMasAlta?: number;
}

@Component({
  selector: 'app-login',
  imports: [FormsModule, NgClass, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly loginUrl = `${environment.apiUrl}/api/auth/login`;
  private readonly dashboardUrl = '/inicio';

  email = '';
  password = '';
  verPassword = false;
  cargando = false;
  mensajeError = '';
  mensajeOk = '';

  login(): void {
    if (!this.email || !this.password) {
      this.mensajeError = 'Ingresa tu email y contrasena.';
      this.mensajeOk = '';
      return;
    }

    this.cargando = true;
    this.mensajeError = '';
    this.mensajeOk = '';

    this.http.post<LoginResponse>(
      this.loginUrl,
      {
        email: this.email,
        password: this.password
      },
      { withCredentials: true }
    ).subscribe({
      next: (respuesta) => {
        this.guardarToken(respuesta);
        this.authService.saveProfile(respuesta);
        this.router.navigateByUrl(this.dashboardUrl);
      },
      error: (error: HttpErrorResponse) => {
        this.mensajeError = this.obtenerMensajeError(error);
        this.cargando = false;
      }
    });
  }

  private guardarToken(respuesta: LoginResponse): void {
    this.authService.saveSession(respuesta);
  }

  private obtenerMensajeError(error: HttpErrorResponse): string {
    if (typeof error.error === 'string' && error.error.trim()) {
      return error.error;
    }

    const detalle = error.error?.detail ?? error.error?.message;
    if (typeof detalle === 'string' && detalle.trim()) {
      return detalle;
    }

    return 'Email o contrasena incorrectos.';
  }
}
