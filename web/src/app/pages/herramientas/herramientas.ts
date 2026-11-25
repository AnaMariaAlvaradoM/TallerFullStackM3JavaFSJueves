import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { pesos } from '../../core/formato';
import { Herramienta, HerramientasService } from '../../services/herramientas.service';
import { SiPuede } from '../../shared/si-puede';

@Component({
  selector: 'app-herramientas',
  standalone: true,
  imports: [RouterLink, SiPuede],
  template: `
    <section class="pagina">
      <header class="encabezado-pagina">
        <div>
          <h1>Herramientas</h1>
          <p class="subtitulo">Catálogo disponible en Llave Maestra.</p>
        </div>

        <a
          *appSiPuede="'HERRAMIENTAS_GESTIONAR'"
          routerLink="/herramientas/nueva"
          class="btn-primario"
          data-testid="boton-nueva-herramienta"
        >
          Nueva herramienta
        </a>
      </header>

      @if (servicio.cargando()) {
        <p>Cargando herramientas...</p>
      }

      @if (servicio.error()) {
        <p class="error-texto">{{ servicio.error() }}</p>
      }

      <div class="grid-tarjetas">
        @for (h of servicio.herramientas(); track h.id) {
          <article class="card tarjeta-herramienta" data-testid="herramienta">
            @if (h.imagenUrl) {
              <img
                [src]="h.imagenUrl"
                [alt]="h.nombre"
                loading="lazy"
                class="imagen-herramienta"
              />
            }

            <div class="cuerpo-tarjeta">
              <div class="fila-superior">
                <h2 data-testid="nombre-herramienta">{{ h.nombre }}</h2>
                <span class="badge">{{ h.categoria }}</span>
              </div>

              @if (h.descripcion) {
                <p class="descripcion">{{ h.descripcion }}</p>
              }

              <p class="precio" data-testid="precio-dia">{{ pesos(h.precioDia) }} / día</p>
              <p class="meta">Depósito: {{ pesos(h.deposito) }} · Unidades: {{ h.unidades }}</p>
              <p class="estado-item">Estado: {{ h.activa ? 'Activa' : 'Retirada' }}</p>

              <div class="acciones">
                @if (h.activa) {
                  <a
                    *appSiPuede="'ALQUILERES_CREAR'"
                    [routerLink]="['/herramientas', h.id, 'alquilar']"
                    class="btn-primario"
                    data-testid="boton-alquilar"
                  >
                    Alquilar
                  </a>
                }

                <a
                  *appSiPuede="'HERRAMIENTAS_GESTIONAR'"
                  [routerLink]="['/herramientas', h.id, 'editar']"
                  class="btn-secundario"
                  data-testid="boton-editar"
                >
                  Editar
                </a>

                <button
                  *appSiPuede="'HERRAMIENTAS_GESTIONAR'"
                  type="button"
                  class="btn-secundario"
                  data-testid="boton-estado"
                  (click)="alternarEstado(h)"
                >
                  {{ h.activa ? 'Retirar' : 'Activar' }}
                </button>
              </div>
            </div>
          </article>
        }
      </div>
    </section>
  `
})
export class Herramientas implements OnInit {
  readonly servicio = inject(HerramientasService);
  readonly pesos = pesos;

  ngOnInit(): void {
    this.servicio.cargar();
  }

  alternarEstado(herramienta: Herramienta): void {
    this.servicio.cambiarEstado(herramienta, !herramienta.activa);
  }
}