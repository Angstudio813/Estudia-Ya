import { Component, inject, signal } from '@angular/core';
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

  email = signal('');
  password = signal('');
  verPassword = false;
  cargando = signal(false);
  mensajeError = signal('');
  mensajeOk = signal('');

  login(): void {
    if (!this.email() || !this.password()) {
      this.mensajeError.set('Ingresa tu email y contrasena.');
      this.mensajeOk.set('');
      return;
    }

    this.cargando.set(true);
    this.mensajeError.set('');
    this.mensajeOk.set('');

    const timer = setTimeout(() => {
      this.cargando.set(false);
      this.mensajeError.set('Credenciales invalidas.');
      this.email.set('');
      this.password.set('');
    }, 6000);

    this.http.post<LoginResponse>(
      this.loginUrl,
      { email: this.email(), password: this.password() }
    ).subscribe({
      next: (respuesta) => {
        clearTimeout(timer);
        this.guardarToken(respuesta);
        this.authService.saveProfile(respuesta);
        this.router.navigateByUrl(this.dashboardUrl);
      },
      error: (error: HttpErrorResponse) => {
        clearTimeout(timer);
        this.mensajeError.set(this.obtenerMensajeError(error));
        this.cargando.set(false);
        this.email.set('');
        this.password.set('');
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
