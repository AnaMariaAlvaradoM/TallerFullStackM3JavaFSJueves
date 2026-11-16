import { Routes } from '@angular/router';
import { authGuard } from './core/auth-guard';
import { permisoGuard } from './core/permiso-guard';

export const routes: Routes = [
  {
    path: 'login',
    title: 'Iniciar sesión · Llave Maestra',
    loadComponent: () => import('./pages/login/login').then((m) => m.Login),
  },
  {
    path: 'herramientas',
    title: 'Herramientas · LLave Maestra',
    canActivate: [authGuard, permisoGuard('HERRAMIENTAS_VER')],
    loadComponent: () => import('./pages/herramientas/herramientas').then((m) => m.Herramientas),
  },
  {
    path: 'usuarios',
    title: 'Clientes · LLave Maestra',
    canActivate: [authGuard, permisoGuard('CLIENTES_GESTIONAR')],
    loadComponent: () => import('./pages/clientes/clientes').then((m) => m.Clientes),
  },
  {
    path: 'sin-acceso',
    title: 'Sin acceso · LLave Maestra',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/sin-acceso/sin-acceso').then((m) => m.SinAcceso),
  },
  { path: '', pathMatch: 'full', redirectTo: 'herramientas' },
  { path: '**', redirectTo: 'herramientas' },
];
