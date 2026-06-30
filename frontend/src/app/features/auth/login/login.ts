import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface LoginResponse {
  token: string;
  type: string;
}

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  private readonly http = inject(HttpClient);
  private readonly loginUrl = 'http://localhost:8080/api/auth/login';
  private readonly dashboardUrl = 'http://localhost:8080/';

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
        window.location.href = this.dashboardUrl;
      },
      error: () => {
        this.mensajeError = 'Email o contrasena incorrectos.';
        this.cargando = false;
      }
    });
  }

  private guardarToken(respuesta: LoginResponse): void {
    if (typeof localStorage === 'undefined') {
      return;
    }

    localStorage.setItem('estudiaya_token', respuesta.token);
    localStorage.setItem('estudiaya_token_type', respuesta.type);
  }
}
