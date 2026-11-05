import { Routes } from '@angular/router';
import { authGuard } from './core/auth-guard';
import { permisoGuard } from './core/permiso-guard';

// Cada página se carga solo cuando se necesita (loadComponent = carga perezosa).
// canActivate: los guards que deben dejar pasar, en orden (primero sesión, luego permiso).
export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'herramientas' },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then((m) => m.Login),
  },
  {
    path: 'herramientas',
    canActivate: [authGuard, permisoGuard('HERRAMIENTAS_VER')],
    loadComponent: () => import('./pages/herramientas/herramientas').then((m) => m.Herramientas),
  },
  {
    path: 'herramientas/:id/alquilar',
    canActivate: [authGuard, permisoGuard('ALQUILERES_CREAR')],
    loadComponent: () => import('./pages/alquilar/alquilar').then((m) => m.Alquilar),
  },
  {
    path: 'mis-alquileres',
    canActivate: [authGuard, permisoGuard('ALQUILERES_VER_PROPIOS')],
    loadComponent: () => import('./pages/mis-alquileres/mis-alquileres').then((m) => m.MisAlquileres),
  },
  {
    path: 'clientes',
    canActivate: [authGuard, permisoGuard('CLIENTES_GESTIONAR')],
    loadComponent: () => import('./pages/clientes/clientes').then((m) => m.Clientes),
  },
  {
    path: 'sin-acceso',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/sin-acceso/sin-acceso').then((m) => m.SinAcceso),
  },
  { path: '**', redirectTo: 'herramientas' },
];
