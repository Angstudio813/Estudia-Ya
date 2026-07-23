import { Routes } from '@angular/router';

import { Home } from './components/home/home';
import { PlanEstudio } from './components/plan-estudio/plan-estudio';
import { LogrosRetos } from './components/logros-retos/logros-retos';
import { PracticaInteligente } from './components/practica-inteligente/practica-inteligente';
import { AsistenteIA } from './components/asistente-ia/asistente-ia';
import { Progreso } from './components/progreso/progreso';
import { MisCursos } from './components/mis-cursos/mis-cursos';
import { GestionUsuarios } from './components/gestion-usuarios/gestion-usuarios';
import { Login } from './features/auth/login/login';
import { Registrate } from './features/auth/registrate/registrate';
import { authGuard, adminGuard } from './core/auth.guard';
import { StudyLayout } from './components/study-layout/study-layout';
import { CursoDetalleComponent } from './components/curso-detalle/curso-detalle';
import { TemaDetalleComponent } from './components/tema-detalle/tema-detalle';

export const routes: Routes = [
  {
    path: 'login',
    component: Login
  },
  {
    path: 'registrate',
    component: Registrate
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'inicio',
    component: StudyLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: Home,
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'mis-cursos',
    component: StudyLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: MisCursos,
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'mis-cursos/:usuarioId',
    component: StudyLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: MisCursos,
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'cursos/:cursoId',
    component: StudyLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: CursoDetalleComponent,
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'temas/:temaId',
    component: StudyLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: TemaDetalleComponent,
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'gestion-usuarios',
    component: StudyLayout,
    canActivate: [authGuard, adminGuard],
    children: [
      {
        path: '',
        component: GestionUsuarios,
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'plan-estudio',
    component: StudyLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: PlanEstudio,
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'progreso/:usuarioId',
    component: StudyLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: Progreso,
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'progreso',
    component: StudyLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: Progreso,
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'plan-estuio',
    redirectTo: 'plan-estudio',
    pathMatch: 'full'
  },
  {
    path: 'logros-retos',
    component: StudyLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: LogrosRetos,
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'practica-inteligente',
    component: StudyLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: PracticaInteligente,
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'asistente-ia/:usuarioId',
    component: StudyLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: AsistenteIA,
        pathMatch: 'full'
      }
    ]
  },
  {
    path: 'asistente-ia',
    component: StudyLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: AsistenteIA,
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'login',
    pathMatch: 'full'
  }
];
