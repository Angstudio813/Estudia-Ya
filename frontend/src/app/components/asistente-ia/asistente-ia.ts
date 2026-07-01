import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AsistenteIARespuesta, AsistenteIAService } from './asistente-ia.service';

@Component({
  selector: 'app-asistente-ia',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './asistente-ia.html',
  styleUrl: './asistente-ia.css',
})
export class AsistenteIA implements OnInit {
  usuarioId = 1;

  cargaInicial = signal<boolean>(false);
  enviando = signal<boolean>(false);
  error = signal<string>('');
  pregunta = '';
  respuestaChat = signal<string | null>(null);
  preguntaRealizada = signal<string | null>(null);
  asistencia = signal<AsistenteIARespuesta | null>(null);

  temasRefuerzo = computed(() => this.asistencia()?.temasRefuerzo ?? []);
  recomendaciones = computed(() => this.asistencia()?.recomendaciones ?? []);
  saludo = computed(() => this.asistencia()?.mensajePrincipal ?? 'Analizando tu perfil académico y preparando tus recomendaciones...');
  nombre = computed(() => this.asistencia()?.nombreUsuario ?? 'estudiante');
  saludoUsuario = computed(() => `Hola, ${this.nombre()}`);

  constructor(private asistenteIAService: AsistenteIAService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    const param = Number(this.route.snapshot.paramMap.get('usuarioId') ?? '1');
    this.usuarioId = Number.isFinite(param) && param > 0 ? param : 1;
    this.cargarAsistencia();
  }

  cargarAsistencia(): void {
    this.error.set('');
    this.cargaInicial.set(true);

    this.asistenteIAService.obtenerAsistencia(this.usuarioId).subscribe({
      next: (data) => {
        this.asistencia.set(data);
        this.cargaInicial.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('No pudimos conectar con el tutor de IA en este momento.');
        this.cargaInicial.set(false);
      },
    });
  }

  setSugerencia(texto: string): void {
    this.pregunta = texto;
  }

  enviarPregunta(): void {
    const texto = this.pregunta.trim();

    if (!texto) {
      return;
    }

    this.enviando.set(true);
    this.error.set('');
    this.preguntaRealizada.set(texto);
    this.respuestaChat.set(null);

    this.asistenteIAService.enviarPregunta(this.usuarioId, texto).subscribe({
      next: (data) => {
        this.respuestaChat.set(data.respuesta);
        this.enviando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('No se pudo enviar la pregunta. Verifica que el backend esté corriendo.');
        this.enviando.set(false);
      },
    });
  }
}