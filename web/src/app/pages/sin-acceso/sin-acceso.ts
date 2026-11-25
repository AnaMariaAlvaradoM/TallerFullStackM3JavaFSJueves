import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-sin-acceso',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section class="card">
      <h1>Sin acceso</h1>
      <p>No tienes permisos para ver esta sección.</p>
      <a routerLink="/herramientas" class="btn-primario">Volver a herramientas</a>
    </section>
  `
})
export class SinAcceso {}