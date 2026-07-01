import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { ProgresoResumen, ProgresoService } from './progreso.service';

@Component({
  selector: 'app-progreso',
  imports: [CommonModule, RouterLink],
  templateUrl: './progreso.html',
  styleUrl: './progreso.css',
})
export class Progreso implements OnInit {
  private readonly authService = inject(AuthService);

  usuarioId = 1;

  progreso = signal<ProgresoResumen | null>(null);
  cargando = signal<boolean>(false);
  error = signal<string>('');

  totalTemas = computed(() => this.progreso()?.totalTemas ?? 0);
  promedioAcierto = computed(() => this.progreso()?.promedioAcierto ?? 0);
  ejerciciosCorrectos = computed(() => this.progreso()?.ejerciciosCorrectos ?? 0);
  logrosTotales = computed(() => this.progreso()?.logrosTotales ?? 0);
  detalleTemas = computed(() => this.progreso()?.detalleTemas ?? []);

  constructor(private progresoService: ProgresoService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    const param = Number(this.route.snapshot.paramMap.get('usuarioId'));
    this.usuarioId = Number.isFinite(param) && param > 0 ? param : this.authService.getUserId();
    this.cargarProgreso();
  }

  cargarProgreso(): void {
    this.error.set('');
    this.cargando.set(true);

    this.progresoService.obtenerProgreso(this.usuarioId).subscribe({
      next: (data) => {
        this.progreso.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('No se pudo cargar el progreso. Verifica que el backend esté corriendo.');
        this.cargando.set(false);
      },
    });
  }

  trackByTema(_: number, tema: { tema: string }): string {
    return tema.tema;
  }

  formatearPorcentaje(valor: number): string {
    return `${valor.toFixed(1)}%`;
  }
}
