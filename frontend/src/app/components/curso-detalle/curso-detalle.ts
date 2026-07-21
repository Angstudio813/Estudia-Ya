import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { CursoDetalle, CursoDetalleService, TemaResumen } from './curso-detalle.service';

@Component({
  selector: 'app-curso-detalle',
  imports: [CommonModule, RouterLink],
  templateUrl: './curso-detalle.html',
  styleUrl: './curso-detalle.css',
})
export class CursoDetalleComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly authService = inject(AuthService);
  private readonly cursoDetalleService = inject(CursoDetalleService);

  curso = signal<CursoDetalle | null>(null);
  cargando = signal(true);
  error = signal('');

  usuarioId = computed(() => this.authService.getUserId());

  ngOnInit(): void {
    const cursoId = Number(this.route.snapshot.paramMap.get('cursoId'));
    if (!Number.isFinite(cursoId) || cursoId <= 0) {
      this.error.set('Curso no válido.');
      this.cargando.set(false);
      return;
    }
    this.cargarCurso(cursoId);
  }

  cargarCurso(cursoId: number): void {
    this.cargando.set(true);
    this.error.set('');

    this.cursoDetalleService.obtenerDetalle(cursoId, this.usuarioId()).subscribe({
      next: (data) => {
        this.curso.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('No se pudo cargar el curso. Verifica que el backend esté corriendo.');
        this.cargando.set(false);
      },
    });
  }

  iconoCurso(nombre: string): string {
    const lower = nombre.toLowerCase();
    if (lower.includes('mat')) return 'fa-solid fa-square-root-variable';
    if (lower.includes('comunic')) return 'fa-solid fa-language';
    if (lower.includes('ciencia')) return 'fa-solid fa-flask-vial';
    return 'fa-solid fa-book-open';
  }

  trackByTema(_: number, tema: TemaResumen): number {
    return tema.id;
  }

  estadoColor(estado: string): string {
    switch (estado) {
      case 'Completado': return '#16a34a';
      case 'En progreso': return '#2563eb';
      case 'Por iniciar': return '#94a3b8';
      case 'Sin ejercicios': return '#f59e0b';
      default: return '#94a3b8';
    }
  }
}
