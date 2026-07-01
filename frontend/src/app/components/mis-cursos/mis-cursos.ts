import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { MisCurso, MisCursosService } from './mis-cursos.service';

@Component({
  selector: 'app-mis-cursos',
  imports: [CommonModule, RouterLink],
  template: `
    <div class="mis-cursos-page">
      <div class="hero-card">
        <div>
          <div class="eyebrow">Módulo de aprendizaje</div>
          <h1>Mis cursos</h1>
          <p>Revisa tu avance, prioriza el siguiente tema y decide qué practicar hoy.</p>
        </div>

        <div class="profile-pill">
          <div class="avatar">{{ iniciales() }}</div>
          <div>
            <div class="profile-name">{{ nombreUsuario() }}</div>
            <div class="profile-grade">{{ gradoTexto() }}</div>
          </div>
        </div>
      </div>

      <div *ngIf="cargando()" class="state-card">Cargando tus cursos...</div>
      <div *ngIf="!cargando() && error()" class="state-card error">{{ error() }}</div>
      <div *ngIf="!cargando() && !error() && cursos().length === 0" class="state-card">No hay cursos registrados.</div>

      <ng-container *ngIf="!cargando() && !error() && cursos().length > 0">
        <section class="focus-grid" *ngIf="cursoDestacado() as destacado">
          <div class="focus-card" [style.--accent]="destacado.colorHex || '#14506a'">
            <div class="focus-content">
              <div class="focus-tag">Plan de hoy</div>
              <h2>Continúa con {{ destacado.nombre }}</h2>
              <p>{{ destacado.recomendacion }}</p>
              <div class="focus-actions">
                <a routerLink="/practica-inteligente">Practicar ahora</a>
                <span>{{ destacado.siguienteTema }}</span>
              </div>
            </div>

            <div class="focus-score">
              <span>Avance</span>
              <strong>{{ destacado.progreso }}%</strong>
              <div class="focus-ring">
                <div class="focus-ring-fill" [style.height.%]="destacado.progreso || 0"></div>
              </div>
            </div>
          </div>
        </section>

        <div class="summary-grid">
          <div class="summary-card">
            <i class="fa-solid fa-layer-group"></i>
            <span>Cursos activos</span>
            <strong>{{ cursos().length }}</strong>
          </div>
          <div class="summary-card">
            <i class="fa-solid fa-book-open-reader"></i>
            <span>Temas disponibles</span>
            <strong>{{ totalTemas() }}</strong>
          </div>
          <div class="summary-card">
            <i class="fa-solid fa-bolt"></i>
            <span>Ejercicios listos</span>
            <strong>{{ totalEjercicios() }}</strong>
          </div>
        </div>

        <div class="filters">
          <button type="button" [class.active]="filtroEsActivo('all')" (click)="aplicarFiltro('all')">Todos</button>
          <button type="button" [class.active]="filtroEsActivo('En progreso')" (click)="aplicarFiltro('En progreso')">En progreso</button>
          <button type="button" [class.active]="filtroEsActivo('En buen ritmo')" (click)="aplicarFiltro('En buen ritmo')">Buen ritmo</button>
          <button type="button" [class.active]="filtroEsActivo('Por iniciar')" (click)="aplicarFiltro('Por iniciar')">Por iniciar</button>
        </div>

        <div class="courses-grid">
          <article *ngFor="let curso of cursosFiltrados(); trackBy: trackByCurso" class="course-card" [style.--accent]="curso.colorHex || '#534ab7'">
            <div class="course-icon">
              <i [class]="iconoCurso(curso.nombre)"></i>
            </div>

            <div class="course-body">
              <div class="course-head">
                <h2>{{ curso.nombre }}</h2>
                <span class="status-pill">{{ curso.estado }}</span>
              </div>
              <p>{{ curso.descripcion || 'Curso disponible para seguir aprendiendo.' }}</p>

              <div class="course-meta">
                <span>{{ curso.nivel }}</span>
                <span>{{ curso.grado }} grado</span>
                <span>{{ curso.totalTemas }} temas</span>
                <span>{{ curso.totalEjercicios }} ejercicios</span>
              </div>

              <div class="progress-block">
                <div class="progress-top">
                  <span>Avance</span>
                  <strong>{{ curso.progreso }}%</strong>
                </div>
                <div class="progress-track">
                  <div class="progress-fill" [style.width.%]="curso.progreso || 0"></div>
                </div>
              </div>

              <div class="course-plan">
                <div>
                  <span>Siguiente tema</span>
                  <strong>{{ curso.siguienteTema }}</strong>
                </div>
                <p>{{ curso.recomendacion }}</p>
              </div>

              <div class="course-actions">
                <div>
                  <span>Ritmo sugerido</span>
                  <strong>{{ curso.progreso >= 60 ? 'Consolidar' : 'Reforzar' }}</strong>
                </div>
                <a routerLink="/practica-inteligente">Abrir práctica</a>
              </div>
            </div>
          </article>
        </div>
      </ng-container>
    </div>
  `,
  styles: [`
    @import url('https://fonts.googleapis.com/css2?family=Sora:wght@400;600;700;800&display=swap');

    .mis-cursos-page {
      display: flex;
      flex-direction: column;
      gap: 18px;
    }

    .hero-card {
      display: flex;
      justify-content: space-between;
      gap: 16px;
      align-items: center;
    }

    .eyebrow {
      font-size: 12px;
      font-weight: 800;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: #67808c;
    }

    .hero-card h1 {
      margin: 6px 0 0;
      font-family: 'Sora', sans-serif;
      font-size: 34px;
      line-height: 1.1;
      color: #0f3648;
    }

    .hero-card p {
      margin: 8px 0 0;
      color: #5d7885;
    }

    .profile-pill {
      display: flex;
      align-items: center;
      gap: 12px;
      background: #fff;
      border: 1px solid #d7e3e9;
      border-radius: 16px;
      padding: 14px 16px;
    }

    .avatar {
      width: 48px;
      height: 48px;
      border-radius: 14px;
      display: grid;
      place-items: center;
      background: linear-gradient(135deg, #14506a, #1f8f6a);
      color: #fff;
      font-weight: 800;
    }

    .profile-name {
      font-weight: 800;
      color: #0f3648;
    }

    .profile-grade {
      color: #67808c;
      font-size: 14px;
    }

    .state-card {
      background: #fff;
      border: 1px solid #d7e3e9;
      border-radius: 18px;
      padding: 20px;
      color: #0f3648;
    }

    .state-card.error {
      border-color: #f3b4b4;
      background: #fff6f6;
      color: #9d2f2f;
    }

    .focus-grid {
      display: grid;
    }

    .focus-card {
      display: flex;
      justify-content: space-between;
      gap: 18px;
      padding: 22px;
      border-radius: 20px;
      background: linear-gradient(135deg, var(--accent, #14506a), #1f8f6a);
      color: #fff;
    }

    .focus-tag {
      font-size: 12px;
      font-weight: 800;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: rgba(255, 255, 255, 0.78);
    }

    .focus-card h2 {
      margin: 8px 0 0;
      font-family: 'Sora', sans-serif;
      font-size: 28px;
    }

    .focus-card p {
      margin: 8px 0 0;
      color: rgba(255, 255, 255, 0.9);
    }

    .focus-actions {
      display: flex;
      gap: 12px;
      align-items: center;
      margin-top: 16px;
    }

    .focus-actions a {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 11px 16px;
      border-radius: 12px;
      background: #fff;
      color: #14506a;
      text-decoration: none;
      font-weight: 800;
    }

    .focus-actions span {
      color: rgba(255, 255, 255, 0.9);
    }

    .focus-score {
      min-width: 120px;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
    }

    .focus-score strong {
      font-family: 'Sora', sans-serif;
      font-size: 30px;
    }

    .focus-ring {
      width: 74px;
      height: 74px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.2);
      display: grid;
      place-items: end center;
      overflow: hidden;
    }

    .focus-ring-fill {
      width: 100%;
      background: rgba(255, 255, 255, 0.85);
      border-radius: 50% 50% 0 0;
    }

    .summary-grid,
    .filters,
    .courses-grid {
      display: grid;
      gap: 14px;
    }

    .summary-grid {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }

    .summary-card {
      background: #fff;
      border: 1px solid #d7e3e9;
      border-radius: 16px;
      padding: 18px;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .summary-card i {
      font-size: 20px;
      color: #14506a;
    }

    .summary-card span {
      color: #67808c;
    }

    .summary-card strong {
      font-family: 'Sora', sans-serif;
      font-size: 28px;
      color: #0f3648;
    }

    .filters {
      grid-template-columns: repeat(auto-fit, minmax(150px, max-content));
    }

    .filters button {
      border: 1px solid #d7e3e9;
      background: #fff;
      color: #14506a;
      border-radius: 999px;
      padding: 10px 16px;
      font-weight: 700;
      cursor: pointer;
    }

    .filters button.active {
      background: #14506a;
      color: #fff;
      border-color: #14506a;
    }

    .courses-grid {
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    }

    .course-card {
      background: #fff;
      border: 1px solid #d7e3e9;
      border-radius: 18px;
      padding: 18px;
      display: flex;
      gap: 14px;
    }

    .course-icon {
      width: 44px;
      height: 44px;
      border-radius: 14px;
      background: #edf3f6;
      color: #14506a;
      display: grid;
      place-items: center;
      flex: 0 0 auto;
    }

    .course-body {
      flex: 1;
    }

    .course-head {
      display: flex;
      justify-content: space-between;
      gap: 12px;
      align-items: flex-start;
    }

    .course-head h2 {
      margin: 0;
      font-family: 'Sora', sans-serif;
      font-size: 20px;
      color: #0f3648;
    }

    .status-pill {
      border-radius: 999px;
      padding: 6px 10px;
      background: #edf3f6;
      color: #14506a;
      font-size: 12px;
      font-weight: 800;
    }

    .course-body p {
      margin: 8px 0 0;
      color: #5d7885;
    }

    .course-meta {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-top: 12px;
    }

    .course-meta span {
      background: #f3f7f9;
      color: #5d7885;
      border-radius: 999px;
      padding: 6px 10px;
      font-size: 12px;
    }

    .progress-block {
      margin-top: 14px;
    }

    .progress-top,
    .course-actions,
    .course-plan {
      display: flex;
      justify-content: space-between;
      gap: 12px;
      align-items: center;
    }

    .progress-top span,
    .course-plan span,
    .course-actions span {
      color: #67808c;
      font-size: 13px;
    }

    .progress-top strong {
      color: #0f3648;
    }

    .progress-track {
      margin-top: 8px;
      height: 10px;
      background: #e5edf1;
      border-radius: 999px;
      overflow: hidden;
    }

    .progress-fill {
      height: 100%;
      background: linear-gradient(90deg, #ed6a3b, #1f8f6a);
      border-radius: inherit;
    }

    .course-plan {
      margin-top: 14px;
      align-items: flex-start;
    }

    .course-plan strong {
      display: block;
      margin-top: 4px;
      color: #0f3648;
    }

    .course-plan p {
      margin: 0;
      max-width: 220px;
    }

    .course-actions {
      margin-top: 16px;
      align-items: flex-end;
    }

    .course-actions strong {
      display: block;
      color: #0f3648;
    }

    .course-actions a {
      text-decoration: none;
      background: #14506a;
      color: #fff;
      padding: 10px 14px;
      border-radius: 12px;
      font-weight: 800;
    }

    @media (max-width: 760px) {
      .hero-card,
      .focus-card,
      .course-card,
      .course-plan,
      .course-actions {
        flex-direction: column;
        align-items: stretch;
      }

      .summary-grid {
        grid-template-columns: 1fr;
      }

      .course-head {
        flex-direction: column;
      }
    }
  `]
})
export class MisCursos {
  private readonly misCursosService = inject(MisCursosService);
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);

  protected readonly cursos = signal<MisCurso[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal('');
  protected readonly filtroActivo = signal('all');

  protected readonly perfil = computed(() => this.authService.getProfile());
  protected readonly nombreUsuario = computed(() => this.authService.getDisplayName());
  protected readonly iniciales = computed(() => {
    const perfil = this.perfil();
    if (!perfil) {
      return 'ES';
    }

    const nombre = perfil.nombre?.trim().charAt(0) ?? '';
    const apellido = perfil.apellido?.trim().charAt(0) ?? '';
    return `${nombre}${apellido}`.toUpperCase() || 'ES';
  });
  protected readonly gradoTexto = computed(() => {
    const perfil = this.perfil();
    if (!perfil) {
      return 'Estudiante';
    }

    const nivel = perfil.nivel === 'PRIMARIA' ? 'Primaria' : 'Secundaria';
    return `${perfil.grado}ro ${nivel}`;
  });
  protected readonly usuarioId = computed(() => {
    const routeUsuarioId = Number(this.route.snapshot.paramMap.get('usuarioId'));
    return Number.isFinite(routeUsuarioId) && routeUsuarioId > 0 ? routeUsuarioId : this.authService.getUserId();
  });
  protected readonly cursosFiltrados = computed(() => {
    const filtro = this.filtroActivo();
    const cursos = this.cursos();

    if (filtro === 'all') {
      return cursos;
    }

    return cursos.filter((curso) => curso.estado === filtro);
  });
  protected readonly cursoDestacado = computed(() => this.cursosFiltrados()[0] ?? this.cursos()[0] ?? null);
  protected readonly totalTemas = computed(() => this.cursos().reduce((acumulado, curso) => acumulado + (curso.totalTemas ?? 0), 0));
  protected readonly totalEjercicios = computed(() => this.cursos().reduce((acumulado, curso) => acumulado + (curso.totalEjercicios ?? 0), 0));

  constructor() {
    this.cargarCursos();
  }

  cargarCursos(): void {
    const usuarioId = this.usuarioId();
    this.cargando.set(true);
    this.error.set('');

    this.misCursosService.listarCursos(usuarioId).subscribe({
      next: (cursos) => {
        this.cursos.set(cursos);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar tus cursos.');
        this.cargando.set(false);
      }
    });
  }

  aplicarFiltro(filtro: string): void {
    this.filtroActivo.set(filtro);
  }

  trackByCurso(_: number, curso: MisCurso): number {
    return curso.id;
  }

  iconoCurso(nombre: string): string {
    const lower = nombre.toLowerCase();
    if (lower.includes('mat')) {
      return 'fa-solid fa-square-root-variable';
    }
    if (lower.includes('comunic')) {
      return 'fa-solid fa-language';
    }
    if (lower.includes('ciencia')) {
      return 'fa-solid fa-flask-vial';
    }
    return 'fa-solid fa-book-open';
  }

  filtroEsActivo(valor: string): boolean {
    return this.filtroActivo() === valor;
  }
}
