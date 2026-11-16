import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-sin-acceso',
  imports: [RouterLink],
  template: `
    <section class="sin-acceso">
      <p class="codigo">403</p>
      <h1 class="titulo-pagina">Esta sección no está disponible para tu cuenta</h1>
      <p class="subtitulo">Si crees que deberías tener acceso, habla con la administración del coworking.</p>
      <a routerLink="/espacios" class="btn btn-primario">Volver a espacios</a>
    </section>
  `,
  styles: `
    .sin-acceso {
      display: grid;
      justify-items: center;
      gap: var(--espacio-sm);
      padding: var(--espacio-lg) 0;
      text-align: center;
    }

    .codigo {
      font-size: 5rem;
      font-weight: 800;
      line-height: 1;
      color: var(--miel);
    }

    a {
      text-decoration: none;
    }
  `,
})
export class SinAcceso {}