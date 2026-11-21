import { Routes } from '@angular/router';
import { authGuard } from './core/auth-guard';
import { permisoGuard } from './core/permiso-guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'herramientas',
    pathMatch: 'full',
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then((m) => m.Login),
  },
  {
    path: 'registro',
    loadComponent: () => import('./pages/registro/registro').then((m) => m.Registro),
  },
  {
    path: 'herramientas',
    loadComponent: () => import('./pages/herramientas/herramientas').then((m) => m.Herramientas),
    canActivate: [authGuard],
  },
  {
    path: 'herramientas/nueva',
    loadComponent: () => import('./pages/herramienta-form/herramienta-form').then((m) => m.HerramientaForm),
    canActivate: [authGuard, permisoGuard('HERRAMIENTAS_GESTIONAR')],
  },
  {
    path: 'herramientas/:id/editar',
    loadComponent: () => import('./pages/herramienta-form/herramienta-form').then((m) => m.HerramientaForm),
    canActivate: [authGuard, permisoGuard('HERRAMIENTAS_GESTIONAR')],
  },
  {
    path: 'herramientas/:id/alquilar',
    loadComponent: () => import('./pages/alquilar/alquilar').then((m) => m.Alquilar),
    canActivate: [authGuard, permisoGuard('ALQUILERES_CREAR')],
  },
  {
    path: 'clientes',
    loadComponent: () => import('./pages/clientes/clientes').then((m) => m.Clientes),
    canActivate: [authGuard, permisoGuard('CLIENTES_GESTIONAR')],
  },
  {
    path: 'mis-alquileres',
    loadComponent: () => import('./pages/mis-alquileres/mis-alquileres').then((m) => m.MisAlquileres),
    canActivate: [authGuard, permisoGuard('ALQUILERES_VER_PROPIOS')],
  },
  {
    path: 'panel',
    loadComponent: () => import('./pages/panel/panel').then((m) => m.Panel),
    canActivate: [authGuard, permisoGuard('REPORTES_VER')],
  },
  {
    path: 'alquileres',
    loadComponent: () => import('./pages/alquileres/alquileres').then((m) => m.Alquileres),
    canActivate: [authGuard, permisoGuard('ALQUILERES_VER_TODOS')],
  },
  {
    path: 'sin-acceso',
    loadComponent: () => import('./pages/sin-acceso/sin-acceso').then((m) => m.SinAcceso),
  },
  {
    path: '**',
    redirectTo: 'herramientas',
  },
];
