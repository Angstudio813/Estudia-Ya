import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { PracticaInteligenteService, PracticaInteligenteDTO } from '../practica-inteligente/practica-inteligente.service';
import { TemaDetalleService, TemaDetalle } from './tema-detalle.service';

interface PracticaVista extends PracticaInteligenteDTO {
  respondida: boolean;
  seleccionada: string | null;
  correcta: boolean | null;
  feedbackVisible: boolean;
  feedbackTitulo: string;
  feedbackTexto: string;
}

@Component({
  selector: 'app-tema-detalle',
  imports: [CommonModule, RouterLink],
  templateUrl: './tema-detalle.html',
  styleUrl: './tema-detalle.css',
})
export class TemaDetalleComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly authService = inject(AuthService);
  private readonly temaDetalleService = inject(TemaDetalleService);
  private readonly practicaService = inject(PracticaInteligenteService);

  tema = signal<TemaDetalle | null>(null);
  practicas = signal<PracticaVista[]>([]);
  cargando = signal(true);
  error = signal('');
  modoPractica = signal(false);
  generandoIA = signal(false);

  usuarioId = computed(() => this.authService.getUserId());

  totalRespondidas = computed(() => this.practicas().filter(p => p.respondida).length);
  totalCorrectas = computed(() => this.practicas().filter(p => p.correcta === true).length);
  xpGanado = computed(() => {
    return this.practicas().reduce((sum, p) => p.correcta === true ? sum + (p.xp ?? 0) : sum, 0);
  });

  ngOnInit(): void {
    const temaId = Number(this.route.snapshot.paramMap.get('temaId'));
    if (!Number.isFinite(temaId) || temaId <= 0) {
      this.error.set('Tema no válido.');
      this.cargando.set(false);
      return;
    }
    this.cargarTema(temaId);
  }

  cargarTema(temaId: number): void {
    this.cargando.set(true);
    this.error.set('');

    this.temaDetalleService.obtenerDetalle(temaId, this.usuarioId()).subscribe({
      next: (data) => {
        this.tema.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('No se pudo cargar el tema.');
        this.cargando.set(false);
      },
    });
  }

  iniciarPractica(): void {
    const t = this.tema();
    if (!t) return;

    this.modoPractica.set(true);
    this.generandoIA.set(true);
    this.error.set('');

    this.practicaService.generarIA(this.usuarioId(), t.id, 5).subscribe({
      next: (ejercicios) => {
        if (ejercicios.length === 0) {
          this.error.set('No se pudieron generar ejercicios. Intenta de nuevo.');
          this.generandoIA.set(false);
          return;
        }
        this.practicas.set(
          ejercicios.map((e) => ({
            ...e,
            respondida: false,
            seleccionada: null,
            correcta: null,
            feedbackVisible: false,
            feedbackTitulo: 'Resultado',
            feedbackTexto: e.explicacion || 'Revisa el procedimiento.',
          }))
        );
        this.generandoIA.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('Error al generar ejercicios con IA.');
        this.generandoIA.set(false);
      },
    });
  }

  responder(practicaId: number, opcion: string): void {
    const practica = this.practicas().find((p) => p.id === practicaId);
    if (!practica || practica.respondida) return;

    this.practicaService.responder(this.usuarioId(), practicaId, opcion).subscribe({
      next: (response) => {
        this.practicas.update((ps) =>
          ps.map((p) =>
            p.id !== practicaId
              ? p
              : {
                  ...p,
                  respondida: true,
                  seleccionada: opcion,
                  correcta: response.esCorrecta,
                  feedbackVisible: true,
                  feedbackTitulo: response.esCorrecta
                    ? '¡Correcto!'
                    : `Respuesta correcta: ${response.respuestaCorrecta}`,
                  feedbackTexto: p.explicacion || 'Revisa el procedimiento.',
                }
          )
        );
      },
      error: () => {
        this.practicas.update((ps) =>
          ps.map((p) =>
            p.id !== practicaId
              ? p
              : {
                  ...p,
                  respondida: true,
                  seleccionada: opcion,
                  correcta: opcion === p.respuestaCorrecta,
                  feedbackVisible: true,
                  feedbackTitulo:
                    opcion === p.respuestaCorrecta
                      ? '¡Correcto!'
                      : `Respuesta correcta: ${p.respuestaCorrecta}`,
                  feedbackTexto: p.explicacion || 'Revisa el procedimiento.',
                }
          )
        );
      },
    });
  }

  volverAlCurso(): void {
    this.modoPractica.set(false);
    this.practicas.set([]);
    const t = this.tema();
    if (t) {
      this.cargarTema(t.id);
    }
  }

  getEstadoTema(t: TemaDetalle): string {
    if (t.totalEjercicios === 0) return 'Sin ejercicios';
    if (t.ejerciciosResueltos >= t.totalEjercicios && t.porcentajeAcierto >= 70) return 'Completado';
    if (t.ejerciciosResueltos > 0) return 'En progreso';
    return 'Por iniciar';
  }

  trackById(_: number, p: PracticaVista): number {
    return p.id;
  }
}
