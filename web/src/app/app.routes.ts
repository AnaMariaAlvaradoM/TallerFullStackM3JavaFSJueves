import { Routes } from '@angular/router';


export const routes: Routes = [
    {
        path:'login',
        title: 'Iniciar sesión · Llave Maestra',
        loadComponent: () => import('./pages/login/login').then(m => m.Login)
    },

    {
        path: 'herramientas',
        title: 'Herramientas · LlaveMaestra',
        loadComponent: () => import('./pages/herramientas/herramientas').then(m =>m.Herramientas)
    },
    {
        path: 'clientes',
        title: 'Clientes · LlaveMaestra',
        loadComponent: () => import('./pages/clientes/clientes').then(m =>m.Clientes)
    },
    {
        path: 'sin-acceso',
        title: 'sin acceso · LlaveMaestra',
        loadComponent: () => import('./pages/sin-acceso/sin-acceso').then(m =>m.SinAcceso)
    }
];
