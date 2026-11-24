import { Routes } from '@angular/router';
import { authGuard } from './core/auth-guard';
import { permisoGuard } from './core/permiso-guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'herramientas'
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then(m => m.Login)
  },
  {
    path: 'herramientas',
    loadComponent: () => import('./pages/herramientas/herramientas').then(m => m.Herramientas),
    canActivate: [authGuard, permisoGuard('HERRAMIENTAS_VER')]
  },
  {
    path: 'clientes',
    loadComponent: () => import('./pages/clientes/clientes').then(m => m.Clientes),
    canActivate: [authGuard, permisoGuard('CLIENTES_GESTIONAR')]
  },
  {
    path: 'sin-acceso',
    loadComponent: () => import('./pages/sin-acceso/sin-acceso').then(m => m.SinAcceso),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: 'herramientas'
  }
];
