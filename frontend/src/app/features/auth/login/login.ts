import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth.service';

interface LoginResponse {
  token: string;
  type: string;
  usuarioId: number;
  nombre: string;
  apellido: string;
  email: string;
  nivel: string;
  grado: number;
}

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly loginUrl = 'http://localhost:8080/api/auth/login';
  private readonly dashboardUrl = '/';

  email = 'carlos@estudiaya.pe';
  password = '123456';
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
      error: () => {
        this.mensajeError = 'Email o contrasena incorrectos.';
        this.cargando = false;
      }
    });
  }

  private guardarToken(respuesta: LoginResponse): void {
    this.authService.saveSession(respuesta);
  }
}
