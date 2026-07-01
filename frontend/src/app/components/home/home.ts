import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { ProgresoResumen, ProgresoService } from '../progreso/progreso.service';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private readonly authService = inject(AuthService);
  private readonly progresoService = inject(ProgresoService);

  protected readonly progreso = signal<ProgresoResumen | null>(null);
  protected readonly cargando = signal(true);
  protected readonly ejerciciosCompletados = computed(() => this.progreso()?.ejerciciosCorrectos ?? 0);
  protected readonly logrosDesbloqueados = computed(() => this.progreso()?.logrosTotales ?? 0);
  protected readonly promedioGeneral = computed(() => Math.round(this.progreso()?.promedioAcierto ?? 0));
  protected readonly temasRefuerzo = computed(() => this.progreso()?.temasEnRefuerzo ?? 0);
  protected readonly totalTemas = computed(() => this.progreso()?.totalTemas ?? 0);
  protected readonly rachaActual = computed(() => this.authService.getProfile()?.rachaActual ?? 0);
  protected readonly misionTitulo = computed(() => this.temasRefuerzo() > 0 ? 'Refuerza tus temas pendientes' : 'Empieza tu primera practica');
  protected readonly misionSubtitulo = computed(() => this.temasRefuerzo() > 0 ? 'La IA detecto temas que conviene repasar' : 'Aun no hay actividad registrada para esta cuenta');
  protected readonly cursoProgreso = computed(() => Math.min(100, Math.max(0, this.promedioGeneral())));
  protected readonly resumenActividad = computed(() => this.totalTemas() > 0 ? `${this.totalTemas()} temas con seguimiento` : 'Sin actividad registrada');
  protected readonly resumenLogros = computed(() => this.logrosDesbloqueados() > 0 ? `${this.logrosDesbloqueados()} logros desbloqueados` : 'Sin logros todavia');
  protected readonly nombreUsuario = computed(() => this.authService.getDisplayName());
  protected readonly saludo = computed(() => this.obtenerSaludo());

  constructor() {
    this.cargarProgreso();
  }

  private cargarProgreso(): void {
    this.progresoService.obtenerProgreso(this.authService.getUserId()).subscribe({
      next: (progreso) => {
        this.progreso.set(progreso);
        this.cargando.set(false);
      },
      error: () => {
        this.progreso.set(null);
        this.cargando.set(false);
      },
    });
  }

  private obtenerSaludo(): string {
    const hora = new Date().getHours();

    if (hora < 12) {
      return 'Buenos días';
    }

    if (hora < 19) {
      return 'Buenas tardes';
    }

    return 'Buenas noches';
  }
}
