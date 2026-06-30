import { Routes } from '@angular/router';

import { Home } from './components/home/home';
import { PlanEstudio } from './components/plan-estudio/plan-estudio';
import { LogrosRetos } from './components/logros-retos/logros-retos';

export const routes: Routes = [
  {
    path: '',
    component: Home
  },
  {
    path: 'plan-estudio',
    component: PlanEstudio
  },
  {
    path: 'logros-retos',
    component: LogrosRetos
  },
  {
    path: '**',
    redirectTo: '',
    pathMatch: 'full'
  }
];
