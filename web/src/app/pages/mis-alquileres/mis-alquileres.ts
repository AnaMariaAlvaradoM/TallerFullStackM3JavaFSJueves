import { Component, OnInit, inject, signal } from '@angular/core';

import { pesos } from '../../core/formato';
import { Alquiler, AlquileresService } from '../../services/alquileres.service';

function fechaHoyIso(): string {
  const ahora = new Date();
  const y = ahora.getFullYear();
  const m = String(ahora.getMonth() + 1).padStart(2, '0');
  const d = String(ahora.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

@Component({
  selector: 'app-mis-alquileres',
  standalone: true,
  template: `
    <section class="pagina">
      <header class="encabezado-pagina">
        <div>
          <h1>Mis alquileres</h1>
          <p class="subtitulo">Consulta tus reservas y cancela las que aún no inician.</p>
        </div>
      </header>

      @if (servicio.cargando()) {
        <p>Cargando tus alquileres...</p>
      }

      @if (servicio.error()) {
        <p class="error-texto">{{ servicio.error() }}</p>
      }

      <div class="lista-elementos">
        @for (a of servicio.alquileres(); track a.id) {
          <article class="card fila-alquiler" data-testid="alquiler">
            <div>
              <h2>{{ a.herramientaNombre }}</h2>
              <p class="meta">
                Del {{ a.fechaInicio }} al {{ a.fechaFin }} ({{ a.dias }} días)
              </p>
              <p class="meta">
                Total: {{ pesos(a.valorTotal) }} · Depósito: {{ pesos(a.deposito) }}
              </p>
              <p class="estado-item" data-testid="estado-alquiler">{{ a.estado }}</p>
            </div>

            <div class="acciones">
              @if (puedeCancelar(a)) {
                @if (confirmandoId() === a.id) {
                  <div class="confirmacion" data-testid="confirmar-cancelacion">
                    <span>¿Cancelar este alquiler?</span>
                    <button
                      type="button"
                      class="btn-peligro"
                      (click)="confirmarCancelacion(a)"
                    >
                      Sí, cancelar
                    </button>
                    <button
                      type="button"
                      class="btn-secundario"
                      (click)="cancelarConfirmacion()"
                    >
                      No
                    </button>
                  </div>
                } @else {
                  <button
                    type="button"
                    class="btn-secundario"
                    data-testid="boton-cancelar"
                    (click)="solicitarConfirmacion(a.id)"
                  >
                    Cancelar
                  </button>
                }
              }
            </div>
          </article>
        } @empty {
          @if (!servicio.cargando()) {
            <p class="vacio" data-testid="sin-alquileres">No tienes alquileres registrados.</p>
          }
        }
      </div>
    </section>
  `
})
export class MisAlquileres implements OnInit {
  readonly servicio = inject(AlquileresService);
  readonly pesos = pesos;
  readonly confirmandoId = signal<number | null>(null);

  ngOnInit(): void {
    this.servicio.cargarMios();
  }

  puedeCancelar(alquiler: Alquiler): boolean {
    return alquiler.estado === 'RESERVADO' && alquiler.fechaInicio > fechaHoyIso();
  }

  solicitarConfirmacion(id: number): void {
    this.confirmandoId.set(id);
  }

  cancelarConfirmacion(): void {
    this.confirmandoId.set(null);
  }

  confirmarCancelacion(alquiler: Alquiler): void {
    this.servicio.cancelar(alquiler);
    this.confirmandoId.set(null);
  }
}