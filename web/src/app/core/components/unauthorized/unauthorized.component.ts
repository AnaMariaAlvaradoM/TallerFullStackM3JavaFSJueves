import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-[80vh] flex flex-col items-center justify-center text-center px-4">
      <h1 class="text-6xl font-extrabold text-red-600 mb-4">403</h1>
      <h2 class="text-2xl font-bold text-gray-800 mb-2">Acceso Denegado</h2>
      <p class="text-gray-600 max-w-md mb-6">
        No cuentas con los permisos necesarios (autoridades requeridas) para acceder a esta sección
        del sistema.
      </p>
      <a
        routerLink="/herramientas"
        class="bg-blue-600 hover:bg-blue-700 text-white font-medium px-6 py-2.5 rounded-md transition-colors"
      >
        Volver al Catálogo
      </a>
    </div>
  `,
})
export class UnauthorizedComponent {}
