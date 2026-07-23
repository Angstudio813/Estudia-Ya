import { Component, computed, inject } from '@angular/core';
import { NgIf } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-study-layout',
  imports: [NgIf, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './study-layout.html',
  styleUrl: './study-layout.css',
})
export class StudyLayout {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly perfil = computed(() => this.authService.getProfile());
  protected readonly nombreUsuario = computed(() => this.authService.getDisplayName());
  protected readonly iniciales = computed(() => this.obtenerIniciales());
  protected readonly gradoTexto = computed(() => this.obtenerGradoTexto());
  protected readonly nivelXp = computed(() => `Nivel ${this.perfil()?.nivelJuego ?? 1} - Explorador`);
  protected readonly xpTexto = computed(() => `${this.perfil()?.xpTotal ?? 0} / 1000 XP`);
  protected readonly xpPorcentaje = computed(() => Math.min(100, Math.max(0, ((this.perfil()?.xpTotal ?? 0) / 1000) * 100)));
  protected readonly esAdmin = computed(() => this.authService.isAdmin());

  protected cerrarSesion(): void {
    this.authService.clearSession();
    this.router.navigateByUrl('/login');
  }

  private obtenerIniciales(): string {
    const perfil = this.authService.getProfile();

    if (!perfil) {
      return 'ES';
    }

    const nombre = perfil.nombre?.trim().charAt(0) ?? '';
    const apellido = perfil.apellido?.trim().charAt(0) ?? '';
    return `${nombre}${apellido}`.toUpperCase() || 'ES';
  }

  private obtenerGradoTexto(): string {
    const perfil = this.authService.getProfile();

    if (!perfil) {
      return 'Estudiante';
    }

    const nivel = perfil.nivel === 'PRIMARIA' ? 'Primaria' : 'Secundaria';
    return `${perfil.grado}ro ${nivel}`;
  }
}
