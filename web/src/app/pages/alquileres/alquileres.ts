import { Component, OnInit, inject } from '@angular/core';

import { pesos } from '../../core/formato';
import { Alquiler, AlquileresService } from '../../services/alquileres.service';

@Component({
  selector: 'app-alquileres',
  standalone: true,
  template: `
    <section class="pagina">
      <header class="encabezado-pagina">
        <div>
          <h1>Alquileres</h1>
          <p class="subtitulo">Control de reservas, entregas y devoluciones.</p>
        </div>

        <div class="campo-filtro">
          <label for="filtro-estado">Estado</label>
          <select
            id="filtro-estado"
            data-testid="filtro-estado"
            (change)="onCambiarEstado($event)"
          >
            <option value="">Todos</option>
            <option value="RESERVADO">RESERVADO</option>
            <option value="ENTREGADO">ENTREGADO</option>
            <option value="DEVUELTO">DEVUELTO</option>
            <option value="CANCELADO">CANCELADO</option>
          </select>
        </div>
      </header>

      @if (servicio.cargando()) {
        <p>Cargando alquileres...</p>
      }

      @if (servicio.error()) {
        <p class="error-texto">{{ servicio.error() }}</p>
      }

      <div class="lista-elementos">
        @for (a of servicio.alquileres(); track a.id) {
          <article class="card fila-alquiler" data-testid="alquiler">
            <div>
              <h2>{{ a.herramientaNombre }}</h2>
              <p class="meta">Cliente: {{ a.clienteNombre }}</p>
              <p class="meta">
                Del {{ a.fechaInicio }} al {{ a.fechaFin }} ({{ a.dias }} días) ·
                Total: {{ pesos(a.valorTotal) }}
              </p>
              <p class="estado-item" data-testid="estado-alquiler">{{ a.estado }}</p>
            </div>

            <div class="acciones">
              @if (a.estado === 'RESERVADO') {
                <button
                  type="button"
                  class="btn-primario"
                  data-testid="boton-entregar"
                  (click)="entregar(a)"
                >
                  Entregar
                </button>
              }

              @if (a.estado === 'ENTREGADO') {
                <button
                  type="button"
                  class="btn-secundario"
                  data-testid="boton-devolver"
                  (click)="devolver(a)"
                >
                  Recibir devolución
                </button>
              }
            </div>
          </article>
        } @empty {
          @if (!servicio.cargando()) {
            <p class="vacio" data-testid="sin-alquileres">No hay alquileres para mostrar.</p>
          }
        }
      </div>
    </section>
  `
})
export class Alquileres implements OnInit {
  readonly servicio = inject(AlquileresService);
  readonly pesos = pesos;

  ngOnInit(): void {
    this.servicio.cargarTodos();
  }

  onCambiarEstado(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.servicio.cargarTodos(select.value || undefined);
  }

  entregar(alquiler: Alquiler): void {
    this.servicio.entregar(alquiler);
  }

  devolver(alquiler: Alquiler): void {
    this.servicio.devolver(alquiler);
  }
}