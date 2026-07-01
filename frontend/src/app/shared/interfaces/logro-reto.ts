export interface Logro {
  id: number;
  nombre: string;
  descripcion: string;
  icono: string;
  tipo: 'RACHA' | 'EJERCICIOS' | 'CURSO' | 'ESPECIAL';
  fechaDesbloqueado: string;
}

export interface UsuarioProgreso {
  puntos: number;
  logros: Logro[];
}