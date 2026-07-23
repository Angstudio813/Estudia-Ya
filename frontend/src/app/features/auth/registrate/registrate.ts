import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgClass } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { environment } from '../../../../environment';

@Component({
  selector: 'app-registrate',
  imports: [FormsModule, NgClass, RouterLink],
  templateUrl: './registrate.html',
  styleUrl: './registrate.scss'
})
export class Registrate {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly registroUrl = `${environment.apiUrl}/api/auth/registro`;

  nombre = '';
  apellido = '';
  email = '';
  password = '';
  verPassword = false;
  nivel: 'PRIMARIA' | 'SECUNDARIA' | '' = '';
  grado: number | null = null;
  cargando = false;
  mensajeError = '';
  mensajeOk = '';

  get gradosDisponibles(): number[] {
    if (this.nivel === 'PRIMARIA') {
      return [1, 2, 3, 4, 5, 6];
    }
    if (this.nivel === 'SECUNDARIA') {
      return [1, 2, 3, 4, 5];
    }
    return [];
  }

  onNivelChange(): void {
    this.grado = null;
  }

  registrar(): void {
    if (!this.nombre || !this.apellido || !this.email || !this.password || !this.nivel || !this.grado) {
      this.mensajeError = 'Completa todos los campos.';
      this.mensajeOk = '';
      return;
    }

    this.cargando = true;
    this.mensajeError = '';
    this.mensajeOk = '';

    this.http.post(
      this.registroUrl,
      {
        nombre: this.nombre,
        apellido: this.apellido,
        email: this.email,
        password: this.password,
        nivel: this.nivel,
        grado: this.grado
      }
    ).subscribe({
      next: () => {
        this.mensajeOk = 'Cuenta creada correctamente. Ya puedes iniciar sesion.';
        this.mensajeError = '';
        this.cargando = false;
        setTimeout(() => this.router.navigateByUrl('/login'), 2000);
      },
      error: (error: HttpErrorResponse) => {
        this.mensajeError = this.obtenerMensajeError(error);
        this.cargando = false;
      }
    });
  }

  private obtenerMensajeError(error: HttpErrorResponse): string {
    if (typeof error.error === 'string' && error.error.trim()) {
      return error.error;
    }

    const detalle = error.error?.message;
    if (typeof detalle === 'string' && detalle.trim()) {
      return detalle;
    }

    return 'No se pudo crear la cuenta. Intenta de nuevo.';
  }
}
