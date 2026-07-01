import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-study-layout',
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './study-layout.html',
  styleUrl: './study-layout.css',
})
export class StudyLayout {
  private readonly authService = inject(AuthService);

  protected readonly perfil = computed(() => this.authService.getProfile());
  protected readonly nombreUsuario = computed(() => this.authService.getDisplayName());
  protected readonly iniciales = computed(() => this.obtenerIniciales());
  protected readonly gradoTexto = computed(() => this.obtenerGradoTexto());

  protected readonly nivelXp = 'Nivel 8 - Explorador';
  protected readonly xpTexto = '620 / 1000 XP';

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
