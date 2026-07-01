import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, signal } from '@angular/core';
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
  imports: [CommonModule],
  templateUrl: './practica-inteligente.html',
  styleUrl: './practica-inteligente.css',
})
export class PracticaInteligente implements OnInit {
  usuarioId = 1;

  practicas = signal<PracticaVista[]>([]);
  filtroActivo = signal<FiltroDificultad>('all');
  cargando = signal<boolean>(false);
  error = signal<string>('');

  practicasVisibles = computed(() => {
    const filtro = this.filtroActivo();
    return this.practicas().filter((practica) => filtro === 'all' || practica.dificultad === filtro);
  });

  totalRespondidas = computed(() => this.practicas().filter((practica) => practica.respondida).length);
  totalCorrectas = computed(() => this.practicas().filter((practica) => practica.correcta).length);
  xpTotal = computed(() => this.practicas().reduce((acumulado, practica) => acumulado + (practica.xp ?? 0), 0));
  progresoSesion = computed(() => {
    const total = this.practicas().length || 1;
    return Math.round((this.totalRespondidas() / total) * 100);
  });
  estadoSesion = computed(() => (this.totalRespondidas() === this.practicas().length && this.practicas().length > 0 ? 'Completada' : 'En curso'));
  sinResultados = computed(() => this.practicas().length > 0 && this.practicasVisibles().length === 0);

  constructor(private practicaInteligenteService: PracticaInteligenteService) {}

  ngOnInit(): void {
    this.cargarPracticas();
  }

  cargarPracticas(): void {
    this.error.set('');
    this.cargando.set(true);

    this.practicaInteligenteService.listar(this.usuarioId).subscribe({
      next: (data) => {
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
        this.cargando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('No se pudieron cargar los ejercicios. Verifica que el backend esté corriendo.');
        this.cargando.set(false);
      },
    });
  }

  setFiltro(filtro: FiltroDificultad): void {
    this.filtroActivo.set(filtro);
  }

  responder(practicaId: number, opcion: string): void {
    this.practicas.update((practicas) =>
      practicas.map((practica) => {
        if (practica.id !== practicaId || practica.respondida) {
          return practica;
        }

        const correcta = opcion === practica.respuestaCorrecta;

        return {
          ...practica,
          respondida: true,
          seleccionada: opcion,
          correcta,
          feedbackVisible: true,
          feedbackTitulo: correcta ? 'Correcto' : `Respuesta correcta: ${practica.respuestaCorrecta}`,
          feedbackTexto: practica.explicacion || 'Revisa el procedimiento y vuelve a intentarlo.',
        };
      }),
    );
  }

  trackById(_: number, practica: PracticaVista): number {
    return practica.id;
  }
}