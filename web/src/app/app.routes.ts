import { Routes } from '@angular/router';

import { authGuard } from './core/auth-guard';
import { permisoGuard } from './core/permiso-guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'herramientas',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then((m) => m.Login)
  },
  {
    path: 'registro',
    loadComponent: () => import('./pages/registro/registro').then((m) => m.Registro)
  },
  {
    path: 'herramientas',
    canActivate: [authGuard, permisoGuard('HERRAMIENTAS_VER')],
    loadComponent: () => import('./pages/herramientas/herramientas').then((m) => m.Herramientas)
  },
  {
    path: 'herramientas/nueva',
    canActivate: [authGuard, permisoGuard('HERRAMIENTAS_GESTIONAR')],
    loadComponent: () =>
      import('./pages/herramienta-form/herramienta-form').then((m) => m.HerramientaForm)
  },
  {
    path: 'herramientas/:id/editar',
    canActivate: [authGuard, permisoGuard('HERRAMIENTAS_GESTIONAR')],
    loadComponent: () =>
      import('./pages/herramienta-form/herramienta-form').then((m) => m.HerramientaForm)
  },
  {
    path: 'herramientas/:id/alquilar',
    canActivate: [authGuard, permisoGuard('ALQUILERES_CREAR')],
    loadComponent: () => import('./pages/alquilar/alquilar').then((m) => m.Alquilar)
  },
  {
    path: 'mis-alquileres',
    canActivate: [authGuard, permisoGuard('ALQUILERES_VER_PROPIOS')],
    loadComponent: () => import('./pages/mis-alquileres/mis-alquileres').then((m) => m.MisAlquileres)
  },
  {
    path: 'clientes',
    canActivate: [authGuard, permisoGuard('CLIENTES_GESTIONAR')],
    loadComponent: () => import('./pages/clientes/clientes').then((m) => m.Clientes)
  },
  {
    path: 'panel',
    canActivate: [authGuard, permisoGuard('REPORTES_VER')],
    loadComponent: () => import('./pages/panel/panel').then((m) => m.Panel)
  },
  {
    path: 'alquileres',
    canActivate: [authGuard, permisoGuard('ALQUILERES_VER_TODOS')],
    loadComponent: () => import('./pages/alquileres/alquileres').then((m) => m.Alquileres)
  },
  {
    path: 'sin-acceso',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/sin-acceso/sin-acceso').then((m) => m.SinAcceso)
  },
  {
    path: '**',
    redirectTo: 'herramientas'
  }
];