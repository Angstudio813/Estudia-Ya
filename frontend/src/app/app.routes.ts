import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { Home } from './features/home/home';
import { PlanEstudio } from './features/study/plan-estudio/plan-estudio';
import { LogrosRetos } from './features/study/logros-retos/logros-retos';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'home', component: Home },
  { path: 'plan-estudio', component: PlanEstudio },
  { path: 'logros-retos', component: LogrosRetos },
  { path: '**', redirectTo: 'login' }
];