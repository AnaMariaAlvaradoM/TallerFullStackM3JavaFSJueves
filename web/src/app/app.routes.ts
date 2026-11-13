import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { permissionGuard } from './core/guards/permission.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'herramientas',
    loadComponent: () =>
      import('./features/herramientas/herramienta-list.component').then(
        (m) => m.HerramientaListComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'usuarios',
    loadComponent: () =>
      import('./features/usuarios/usuario-list.component').then((m) => m.UsuarioListComponent),
    canActivate: [authGuard, permissionGuard],
    data: { permiso: 'CLIENTES_GESTIONAR' },
  },
  {
    path: '',
    redirectTo: 'herramientas',
    pathMatch: 'full',
  },
];
