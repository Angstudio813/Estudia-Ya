import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  GestionUsuariosService,
  NivelEducativo,
  Usuario,
  UsuarioPayload,
} from './gestion-usuarios.service';

interface UsuarioForm {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
  nivel: NivelEducativo;
  grado: number;
}

@Component({
  selector: 'app-gestion-usuarios',
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-usuarios.html',
  styleUrl: './gestion-usuarios.css',
})
export class GestionUsuarios {
  private readonly usuariosService = inject(GestionUsuariosService);

  protected readonly usuarios = signal<Usuario[]>([]);
  protected readonly cargando = signal(false);
  protected readonly guardando = signal(false);
  protected readonly error = signal('');
  protected readonly mensaje = signal('');
  protected readonly busqueda = signal('');
  protected readonly usuarioEditandoId = signal<number | null>(null);
  protected readonly eliminandoId = signal<number | null>(null);
  protected readonly niveles: NivelEducativo[] = ['PRIMARIA', 'SECUNDARIA'];

  protected form: UsuarioForm = this.formularioVacio();

  protected readonly modoEdicion = computed(() => this.usuarioEditandoId() !== null);
  protected readonly usuariosFiltrados = computed(() => {
    const termino = this.busqueda().trim().toLowerCase();
    const usuarios = this.usuarios();

    if (!termino) {
      return usuarios;
    }

    return usuarios.filter((usuario) => {
      const texto = `${usuario.nombre} ${usuario.apellido} ${usuario.email}`.toLowerCase();
      return texto.includes(termino);
    });
  });

  constructor() {
    this.cargarUsuarios();
  }

  protected get gradosDisponibles(): number[] {
    return this.form.nivel === 'PRIMARIA' ? [1, 2, 3, 4, 5, 6] : [1, 2, 3, 4, 5];
  }

  protected cargarUsuarios(): void {
    this.cargando.set(true);
    this.error.set('');

    this.usuariosService.listar().subscribe({
      next: (usuarios) => {
        this.usuarios.set(usuarios);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar los usuarios.');
        this.cargando.set(false);
      },
    });
  }

  protected guardarUsuario(): void {
    this.error.set('');
    this.mensaje.set('');

    if (!this.formularioValido()) {
      return;
    }

    const payload = this.construirPayload();
    const usuarioId = this.usuarioEditandoId();
    const request = usuarioId
      ? this.usuariosService.actualizar(usuarioId, payload)
      : this.usuariosService.crear(payload);

    this.guardando.set(true);
    request.subscribe({
      next: (usuario) => {
        this.actualizarLista(usuario);
        this.mensaje.set(usuarioId ? 'Usuario actualizado.' : 'Usuario creado.');
        this.nuevoUsuario();
        this.guardando.set(false);
      },
      error: (response) => {
        this.error.set(response?.error?.message ?? 'No se pudo guardar el usuario.');
        this.guardando.set(false);
      },
    });
  }

  protected editarUsuario(usuario: Usuario): void {
    this.usuarioEditandoId.set(usuario.id);
    this.form = {
      nombre: usuario.nombre,
      apellido: usuario.apellido,
      email: usuario.email,
      password: '',
      nivel: usuario.nivel,
      grado: usuario.grado,
    };
    this.error.set('');
    this.mensaje.set('');
  }

  protected nuevoUsuario(): void {
    this.usuarioEditandoId.set(null);
    this.form = this.formularioVacio();
  }

  protected eliminarUsuario(usuario: Usuario): void {
    const confirmar = window.confirm(`Eliminar a ${usuario.nombre} ${usuario.apellido}?`);
    if (!confirmar) {
      return;
    }

    this.eliminandoId.set(usuario.id);
    this.error.set('');
    this.mensaje.set('');

    this.usuariosService.eliminar(usuario.id).subscribe({
      next: () => {
        this.usuarios.update((usuarios) => usuarios.filter((item) => item.id !== usuario.id));
        if (this.usuarioEditandoId() === usuario.id) {
          this.nuevoUsuario();
        }
        this.mensaje.set('Usuario eliminado.');
        this.eliminandoId.set(null);
      },
      error: () => {
        this.error.set('No se pudo eliminar el usuario.');
        this.eliminandoId.set(null);
      },
    });
  }

  protected cambiarNivel(): void {
    if (!this.gradosDisponibles.includes(this.form.grado)) {
      this.form.grado = 1;
    }
  }

  protected trackByUsuario(_: number, usuario: Usuario): number {
    return usuario.id;
  }

  protected iniciales(usuario: Usuario): string {
    const nombre = usuario.nombre?.trim().charAt(0) ?? '';
    const apellido = usuario.apellido?.trim().charAt(0) ?? '';
    return `${nombre}${apellido}`.toUpperCase() || 'US';
  }

  protected nivelTexto(nivel: NivelEducativo): string {
    return nivel === 'PRIMARIA' ? 'Primaria' : 'Secundaria';
  }

  protected fechaTexto(fecha: string | null): string {
    if (!fecha) {
      return '-';
    }

    return new Date(fecha).toLocaleDateString('es-PE');
  }

  private formularioVacio(): UsuarioForm {
    return {
      nombre: '',
      apellido: '',
      email: '',
      password: '',
      nivel: 'SECUNDARIA',
      grado: 3,
    };
  }

  private formularioValido(): boolean {
    if (!this.form.nombre.trim() || !this.form.apellido.trim() || !this.form.email.trim()) {
      this.error.set('Completa nombre, apellido y email.');
      return false;
    }
    if (!this.form.email.includes('@')) {
      this.error.set('Ingresa un email valido.');
      return false;
    }
    if (!this.modoEdicion() && !this.form.password.trim()) {
      this.error.set('La contrasena es obligatoria para usuarios nuevos.');
      return false;
    }
    if (!this.gradosDisponibles.includes(this.form.grado)) {
      this.error.set('Selecciona un grado valido.');
      return false;
    }

    return true;
  }

  private construirPayload(): UsuarioPayload {
    const payload: UsuarioPayload = {
      nombre: this.form.nombre.trim(),
      apellido: this.form.apellido.trim(),
      email: this.form.email.trim().toLowerCase(),
      nivel: this.form.nivel,
      grado: this.form.grado,
    };

    if (this.form.password.trim()) {
      payload.password = this.form.password.trim();
    }

    return payload;
  }

  private actualizarLista(usuario: Usuario): void {
    this.usuarios.update((usuarios) => {
      const existe = usuarios.some((item) => item.id === usuario.id);
      if (!existe) {
        return [...usuarios, usuario].sort((a, b) => a.id - b.id);
      }

      return usuarios.map((item) => (item.id === usuario.id ? usuario : item));
    });
  }
}
