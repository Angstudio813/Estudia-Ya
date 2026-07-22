import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PlanEstudioService, PlanEstudioResponse, CursoInscrito } from './plan-estudio.service';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-plan-estudio',
  imports: [CommonModule, FormsModule],
  templateUrl: './plan-estudio.html',
  styleUrl: './plan-estudio.css',
})
export class PlanEstudio implements OnInit {
  private readonly authService = inject(AuthService);

  cursosDisponibles = signal<CursoInscrito[]>([]);
  cursosSeleccionados = signal<Set<number>>(new Set());
  horasDisponiblesPorDia: number = 2;
  diasDisponibles: number = 5;

  horario = signal<{ [dia: string]: string[] } | null>(null);
  cargando = signal<boolean>(false);
  cargandoCursos = signal<boolean>(false);
  error = signal<string>('');

  constructor(private planEstudioService: PlanEstudioService) {}

  ngOnInit(): void {
    this.cargarCursos();
  }

  cargarCursos(): void {
    this.cargandoCursos.set(true);
    const usuarioId = this.authService.getUserId();

    this.planEstudioService.listarCursos(usuarioId).subscribe({
      next: (cursos) => {
        this.cursosDisponibles.set(cursos);
        this.cargandoCursos.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('No se pudieron cargar tus cursos.');
        this.cargandoCursos.set(false);
      }
    });
  }

  toggleCurso(cursoId: number): void {
    const actual = new Set(this.cursosSeleccionados());
    if (actual.has(cursoId)) {
      actual.delete(cursoId);
    } else {
      actual.add(cursoId);
    }
    this.cursosSeleccionados.set(actual);
  }

  cursoSeleccionado(cursoId: number): boolean {
    return this.cursosSeleccionados().has(cursoId);
  }

  get diasDelHorario(): string[] {
    const h = this.horario();
    return h ? Object.keys(h) : [];
  }

  generarPlan(): void {
    this.error.set('');
    this.horario.set(null);

    const cursos = this.cursosDisponibles()
      .filter(c => this.cursosSeleccionados().has(c.id))
      .map(c => c.nombre);

    if (cursos.length === 0) {
      this.error.set('Selecciona al menos un curso.');
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
