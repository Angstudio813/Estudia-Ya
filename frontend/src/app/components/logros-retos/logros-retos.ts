import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LogrosRetosService, UsuarioProgreso } from './logros-retos.service';
import { AuthService } from '../../core/auth.service';

interface RetoActivo {
  texto: string;
  pts: number;
}

@Component({
  selector: 'app-logros-retos',
  imports: [CommonModule, FormsModule],
  templateUrl: './logros-retos.html',
  styleUrl: './logros-retos.css',
})
export class LogrosRetos implements OnInit {
  private readonly authService = inject(AuthService);
  usuarioId: number = 1;

  progreso = signal<UsuarioProgreso | null>(null);
  cargando = signal<boolean>(false);
  error = signal<string>('');
  completandoIndex = signal<number | null>(null);

  poolRetos: RetoActivo[] = [
    { texto: 'Completar 1 ejercicio', pts: 10 },
    { texto: 'Estudiar 1 día', pts: 15 },
    { texto: 'Practicar 3 veces', pts: 20 },
    { texto: 'Leer un tema', pts: 25 },
    { texto: 'Resolver 5 preguntas', pts: 30 },
    { texto: 'Estudiar 2 horas', pts: 35 },
    { texto: 'Repasar tema difícil', pts: 40 },
    { texto: 'Practicar matemáticas', pts: 45 },
    { texto: 'Resolver examen corto', pts: 50 },
  ];

  retosActivos = signal<RetoActivo[]>([]);

  puntos = computed(() => this.progreso()?.puntos ?? 0);
  nivel = computed(() => Math.floor(this.puntos() / 50) + 1);
  racha = computed(() => this.progreso()?.logros.length ?? 0);

  rango = computed(() => {
    const n = this.nivel();
    if (n >= 8) return 'Diamante';
    if (n >= 5) return 'Oro';
    if (n >= 3) return 'Plata';
    return 'Bronce';
  });

  progresoPorcentaje = computed(() => {
    const p = this.puntos();
    const n = this.nivel();
    return Math.min((p % (n * 50)) / (n * 50) * 100, 100);
  });

  insignias = computed(() => {
    const p = this.puntos();
    const r = this.racha();
    const n = this.nivel();
    const lista: { clase: string; icono: string; texto: string }[] = [];

    if (p >= 50) lista.push({ clase: 'bronze', icono: 'fa-medal', texto: 'Principiante' });
    if (r >= 5) lista.push({ clase: 'silver', icono: 'fa-fire', texto: 'Constante' });
    if (p >= 150 && n >= 3) lista.push({ clase: 'gold', icono: 'fa-rocket', texto: 'Dedicado' });
    if (p >= 300 && n >= 5) lista.push({ clase: 'gold', icono: 'fa-brain', texto: 'Experto' });
    if (p >= 500 && n >= 7 && r >= 10) lista.push({ clase: 'gold', icono: 'fa-crown', texto: 'Maestro' });
    if (p >= 800 && n >= 10 && r >= 15) lista.push({ clase: 'gold', icono: 'fa-gem', texto: 'Legendario' });
    if (r >= 20) lista.push({ clase: 'gold', icono: 'fa-trophy', texto: 'Imparable' });
    if (p >= 400 && r >= 8) lista.push({ clase: 'silver', icono: 'fa-bolt', texto: 'Velocista' });

    return lista;
  });

  constructor(private logrosRetosService: LogrosRetosService) {}

  ngOnInit(): void {
    this.usuarioId = this.authService.getUserId();
    this.iniciarRetos();
    this.cargarProgreso();
  }

  iniciarRetos(): void {
    const retos: RetoActivo[] = [];
    for (let i = 0; i < 3; i++) {
      retos.push(this.elegirRetoAleatorio());
    }
    this.retosActivos.set(retos);
  }

  elegirRetoAleatorio(): RetoActivo {
    const posibles = this.poolRetos.filter(r => r.pts <= this.nivel() * 20);
    const lista = posibles.length > 0 ? posibles : this.poolRetos;
    return { ...lista[Math.floor(Math.random() * lista.length)] };
  }

  cargarProgreso(): void {
    this.error.set('');
    this.cargando.set(true);

    this.logrosRetosService.obtenerProgreso(this.usuarioId).subscribe({
      next: (data) => {
        this.progreso.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('No se pudo cargar el progreso. Verifica que el backend esté corriendo.');
        this.cargando.set(false);
      }
    });
  }

  completarReto(index: number): void {
    const reto = this.retosActivos()[index];
    this.completandoIndex.set(index);
    this.error.set('');

    this.logrosRetosService.completarReto(this.usuarioId, reto.texto).subscribe({
      next: (data) => {
        this.progreso.set(data);

        const nuevos = [...this.retosActivos()];
        nuevos.splice(index, 1);
        nuevos.push(this.elegirRetoAleatorio());
        this.retosActivos.set(nuevos);

        this.completandoIndex.set(null);
      },
      error: (err) => {
        console.error(err);
        this.error.set('No se pudo completar el reto. Verifica que el backend esté corriendo.');
        this.completandoIndex.set(null);
      }
    });
  }
}