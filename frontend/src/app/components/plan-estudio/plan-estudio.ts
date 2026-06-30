import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlanEstudioService, PlanEstudioResponse } from './plan-estudio.service';

@Component({
  selector: 'app-plan-estudio',
  imports: [CommonModule, FormsModule],
  templateUrl: './plan-estudio.html',
  styleUrl: './plan-estudio.css',
})
export class PlanEstudio {
  cursosTexto: string = '';
  horasDisponiblesPorDia: number = 2;
  diasDisponibles: number = 5;

  horario = signal<{ [dia: string]: string[] } | null>(null);
  cargando = signal<boolean>(false);
  error = signal<string>('');

  constructor(private planEstudioService: PlanEstudioService) {}

  get diasDelHorario(): string[] {
    const h = this.horario();
    return h ? Object.keys(h) : [];
  }

  generarPlan(): void {
    this.error.set('');
    this.horario.set(null);

    const cursos = this.cursosTexto
      .split(',')
      .map(c => c.trim())
      .filter(c => c.length > 0);

    if (cursos.length === 0) {
      this.error.set('Ingresa al menos un curso.');
      return;
    }

    this.cargando.set(true);

    this.planEstudioService.generarPlan({
      cursos,
      horasDisponiblesPorDia: this.horasDisponiblesPorDia,
      diasDisponibles: this.diasDisponibles
    }).subscribe({
      next: (response: PlanEstudioResponse) => {
        this.horario.set(response.horario);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('No se pudo generar el plan. Verifica que el backend esté corriendo.');
        this.cargando.set(false);
      }
    });
  }
}