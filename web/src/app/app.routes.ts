import { Routes } from '@angular/router';
import { authGuard } from './core/auth-guard';
import { permisoGuard } from './core/permiso-guard';

export const routes: Routes = [

    {
        path: 'login',
        title: 'Iniciar sesión · Llave',
        loadComponent: () => import('./pages/login/login').then(m => m.Login)
      },

      {
        path: 'herramientas',
        title: 'Herramientas . Llave',
        canActivate: [authGuard, permisoGuard ('HERRAMIENTAS_VER')],
        loadComponent: () => import('./pages/herramientas/herramientas').then(m => m.Herramientas)
      },

      {
        path: 'clientes',
        title: 'Clientes . Llave',
        canActivate: [authGuard, permisoGuard ('CLIENTES_GESTIONAR')],
        loadComponent: () => import('./pages/clientes/clientes').then(m => m.Clientes)
      },

      {
        path: 'sin-acceso',
        title: 'Sin acceso . Llave',
        canActivate: [authGuard],
        loadComponent: () => import('./pages/sin-acceso/sin-acceso').then(m => m.SinAcceso)
      },


      { path: '', pathMatch: 'full', redirectTo: 'herramientas' },
      { path: '**', redirectTo: 'herramientas' }
 
];
