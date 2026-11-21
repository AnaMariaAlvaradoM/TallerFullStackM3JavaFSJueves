import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-sin-acceso',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="contenedor py-12 text-center">
      <h1 class="text-3xl font-bold text-red-600 mb-4">Acceso Denegado</h1>
      <p class="text-gray-600 mb-6">No tienes los permisos necesarios para acceder a esta sección.</p>
      <a routerLink="/herramientas" class="btn btn-primario">Volver al catálogo</a>
    </div>
  `,
})
export class SinAcceso {}
