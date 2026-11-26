import { Routes } from '@angular/router';
import { authGuard } from './core/auth-guard';
import { permisoGuard } from './core/permiso-guard';

export const routes: Routes = [
  {
    path: 'login',
    title: 'Iniciar sesión · Colmena',
    loadComponent: () => import('./pages/login/login').then(m => m.Login)
  },
  /* {
    path: 'registro',
    title: 'Crear cuenta · LlaveMaestra',
    loadComponent: () => import('./pages/registro/registro').then(m => m.Registro)
  },
  {
    path: 'panel',
    title: 'Panel · LlaveMaestra',
    canActivate: [authGuard, permisoGuard('REPORTES_VER')],
    loadComponent: () => import('./pages/panel/panel').then(m => m.Panel)
  }, */
  {
    path: 'herramientas',
    title: 'Espacios · LlaveMaestra',
    canActivate: [authGuard, permisoGuard('HERRAMIENTAS_VER')],
    loadComponent: () => import('./pages/herramientas/herramientas').then(m => m.Herramientas)
  },
  /* {
    path: 'espacios/nuevo',
    title: 'Nuevo espacio · LlaveMaestra',
    canActivate: [authGuard, permisoGuard('HERRAMIENTAS_GESTIONAR')],
    loadComponent: () => import('./pages/espacio-form/espacio-form').then(m => m.EspacioForm)
  },
  {
    path: 'espacios/:id/editar',
    title: 'Editar espacio · LlaveMaestra',
    canActivate: [authGuard, permisoGuard('HERRAMIENTAS_GESTIONAR')],
    loadComponent: () => import('./pages/espacio-form/espacio-form').then(m => m.EspacioForm)
  },
  {
    path: 'espacios/:id/reservar',
    title: 'Reservar · LlaveMaestra',
    canActivate: [authGuard, permisoGuard('ALQUILERES_CREAR')],
    loadComponent: () => import('./pages/reservar/reservar').then(m => m.Reservar)
  },
  {
    path: 'mis-reservas',
    title: 'Mis reservas · LlaveMaestra',
    canActivate: [authGuard, permisoGuard('ALQUILERES_VER_PROPIOS')],
    loadComponent: () => import('./pages/mis-reservas/mis-reservas').then(m => m.MisReservas)
  },
  {
    path: 'agenda',
    title: 'Agenda · LlaveMaestra',
    canActivate: [authGuard, permisoGuard('ALQUILERES_VER_TODOS')],
    loadComponent: () => import('./pages/agenda/agenda').then(m => m.Agenda)
  }, */
    {
    path: 'herramientas/:id/alquilar',
    title: 'Alquilar · LlaveMaestra',
    canActivate: [authGuard, permisoGuard('ALQUILERES_CREAR')],
    loadComponent: () => import('./pages/alquilar/alquilar').then(m => m.Alquilar)
  },
  {
    path: 'mis-alquileres',
    title: 'Mis alquileres · LlaveMaestra',
    canActivate: [authGuard, permisoGuard('ALQUILERES_VER_PROPIOS')],
    loadComponent: () => import('./pages/mis-alquileres/mis-alquileres').then(m => m.MisAlquileres)
  },
  {
    path: 'clientes',
    title: 'Clientes · LlaveMaestra',
    canActivate: [authGuard, permisoGuard('CLIENTES_GESTIONAR')],
    loadComponent: () => import('./pages/clientes/clientes').then(m => m.Clientes)
  },
  {
    path: 'sin-acceso',
    title: 'Sin acceso · LlaveMaestra',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/sin-acceso/sin-acceso').then(m => m.SinAcceso)
  },
    { path: '', pathMatch: 'full', redirectTo: 'herramientas' },
  { path: '**', redirectTo: 'herramientas' }
];