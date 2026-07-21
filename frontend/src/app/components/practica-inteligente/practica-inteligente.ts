import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { PracticaInteligenteDTO, PracticaInteligenteService } from './practica-inteligente.service';

type FiltroDificultad = 'all' | 'FACIL' | 'MEDIO' | 'DIFICIL';

interface PracticaVista extends PracticaInteligenteDTO {
  respondida: boolean;
  seleccionada: string | null;
  correcta: boolean | null;
  feedbackVisible: boolean;
  feedbackTitulo: string;
  feedbackTexto: string;
}

@Component({
  selector: 'app-practica-inteligente',
  imports: [CommonModule, RouterLink],
  templateUrl: './practica-inteligente.html',
  styleUrl: './practica-inteligente.css',
})
export class PracticaInteligente implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly practicaInteligenteService = inject(PracticaInteligenteService);

  practicas = signal<PracticaVista[]>([]);
  filtroActivo = signal<FiltroDificultad>('all');
  cargando = signal<boolean>(false);
  generandoIA = signal<boolean>(false);
  error = signal<string>('');
  cursoId = signal<number | null>(null);
  temaId = signal<number | null>(null);

  usuarioId = computed(() => this.authService.getUserId());

  practicasVisibles = computed(() => {
    const filtro = this.filtroActivo();
    return this.practicas().filter((practica) => filtro === 'all' || practica.dificultad === filtro);
  });

  totalRespondidas = computed(() => this.practicas().filter((practica) => practica.respondida).length);
  totalCorrectas = computed(() => this.practicas().filter((practica) => practica.correcta === true).length);
  xpTotal = computed(() => {
    return this.practicas().reduce((acumulado, practica) => {
      if (practica.correcta === true) {
        return acumulado + (practica.xp ?? 0);
      }
      return acumulado;
    }, 0);
  });
  progresoSesion = computed(() => {
    const total = this.practicas().length || 1;
    return Math.round((this.totalRespondidas() / total) * 100);
  });
  estadoSesion = computed(() =>
    this.totalRespondidas() === this.practicas().length && this.practicas().length > 0
      ? 'Completada'
      : 'En curso'
  );
  sinResultados = computed(() => this.practicas().length > 0 && this.practicasVisibles().length === 0);

  ngOnInit(): void {
    const cursoIdParam = Number(this.route.snapshot.queryParamMap.get('cursoId'));
    const temaIdParam = Number(this.route.snapshot.queryParamMap.get('temaId'));

    if (Number.isFinite(cursoIdParam) && cursoIdParam > 0) {
      this.cursoId.set(cursoIdParam);
    }
    if (Number.isFinite(temaIdParam) && temaIdParam > 0) {
      this.temaId.set(temaIdParam);
    }

    this.generarPracticaIA();
  }

  generarPracticaIA(): void {
    this.error.set('');
    this.generandoIA.set(true);
    this.practicas.set([]);

    const tid = this.temaId();
    const cid = this.cursoId();
    const uid = this.usuarioId();

    let request$;

    if (tid != null) {
      request$ = this.practicaInteligenteService.generarIA(uid, tid, 5);
    } else if (cid != null) {
      request$ = this.practicaInteligenteService.generarCursoIA(uid, cid, 3);
    } else {
      this.error.set('Selecciona un curso o tema desde Mis Cursos para generar ejercicios con IA.');
      this.generandoIA.set(false);
      return;
    }

    request$.subscribe({
      next: (data) => {
        if (data.length === 0) {
          this.error.set('No se pudieron generar ejercicios. Verifica que el tema tenga contenido.');
          this.generandoIA.set(false);
          return;
        }
        this.practicas.set(
          data.map((practica) => ({
            ...practica,
            respondida: false,
            seleccionada: null,
            correcta: null,
            feedbackVisible: false,
            feedbackTitulo: 'Resultado',
            feedbackTexto: practica.explicacion || 'Revisa el procedimiento y vuelve a intentarlo.',
          })),
        );
        this.generandoIA.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('Error al generar ejercicios con IA. Verifica que el backend esté corriendo.');
        this.generandoIA.set(false);
      },
    });
  }

  setFiltro(filtro: FiltroDificultad): void {
    this.filtroActivo.set(filtro);
  }

  responder(practicaId: number, opcion: string): void {
    const practica = this.practicas().find((p) => p.id === practicaId);
    if (!practica || practica.respondida) {
      return;
    }

    this.practicaInteligenteService.responder(this.usuarioId(), practicaId, opcion).subscribe({
      next: (response) => {
        this.practicas.update((practicas) =>
          practicas.map((p) => {
            if (p.id !== practicaId) {
              return p;
            }
            return {
              ...p,
              respondida: true,
              seleccionada: opcion,
              correcta: response.esCorrecta,
              feedbackVisible: true,
              feedbackTitulo: response.esCorrecta ? '¡Correcto!' : `Respuesta correcta: ${response.respuestaCorrecta}`,
              feedbackTexto: p.explicacion || 'Revisa el procedimiento y vuelve a intentarlo.',
            };
          }),
        );
      },
      error: (err) => {
        console.error(err);
        this.practicas.update((practicas) =>
          practicas.map((p) => {
            if (p.id !== practicaId) {
              return p;
            }
            return {
              ...p,
              respondida: true,
              seleccionada: opcion,
              correcta: opcion === p.respuestaCorrecta,
              feedbackVisible: true,
              feedbackTitulo: opcion === p.respuestaCorrecta ? '¡Correcto!' : `Respuesta correcta: ${p.respuestaCorrecta}`,
              feedbackTexto: p.explicacion || 'Revisa el procedimiento y vuelve a intentarlo.',
            };
          }),
        );
      },
    });
  }

  trackById(_: number, practica: PracticaVista): number {
    return practica.id;
  }
}
