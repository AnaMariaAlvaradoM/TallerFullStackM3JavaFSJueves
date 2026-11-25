import { Component, OnInit, inject, signal } from '@angular/core';

import { mensajeDeError } from '../../core/errores';
import { pesos } from '../../core/formato';
import { ReportesService, Resumen } from '../../services/reportes.service';

@Component({
  selector: 'app-panel',
  standalone: true,
  template: `
    <section class="pagina">
      <header class="encabezado-pagina">
        <div>
          <h1>Panel del negocio</h1>
          <p class="subtitulo">Resumen operativo y financiero de Llave Maestra.</p>
        </div>
      </header>

      @if (cargando()) {
        <p>Cargando resumen...</p>
      }

      @if (errorMensaje()) {
        <p class="error-texto">{{ errorMensaje() }}</p>
      }

      @if (resumen(); as r) {
        <div class="grid-metricas">
          <article class="card metrica">
            <h2>Ingresos del mes</h2>
            <p class="valor-metrica" data-testid="ingresos-mes">{{ pesos(r.ingresosMes) }}</p>
          </article>

          <article class="card metrica">
            <h2>Alquileres activos</h2>
            <p class="valor-metrica" data-testid="alquileres-activos">{{ r.alquileresActivos }}</p>
          </article>
        </div>

        <section class="card bloque-panel">
          <h2>Alquileres vencidos</h2>
          @for (v of r.vencidos; track v.id) {
            <article class="fila-vencido" data-testid="vencido">
              <strong>{{ v.herramientaNombre }}</strong>
              <span> · Cliente: {{ v.clienteNombre }}</span>
              <span> · Venció el {{ v.fechaFin }}</span>
            </article>
          } @empty {
            <p class="vacio" data-testid="sin-vencidos">No hay herramientas vencidas.</p>
          }
        </section>

        <section class="card bloque-panel">
          <h2>Herramientas más alquiladas</h2>
          <ul class="lista-ranking">
            @for (item of r.masAlquiladas; track item.herramienta) {
              <li data-testid="mas-alquilada">
                <span>{{ item.herramienta }}</span>
                <strong>{{ item.alquileres }}</strong>
              </li>
            }
          </ul>
        </section>
      }
    </section>
  `
})
export class Panel implements OnInit {
  private readonly reportesService = inject(ReportesService);
  readonly pesos = pesos;

  readonly resumen = signal<Resumen | null>(null);
  readonly cargando = signal<boolean>(true);
  readonly errorMensaje = signal<string | null>(null);

  ngOnInit(): void {
    this.reportesService.resumen().subscribe({
      next: (data) => {
        this.resumen.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.errorMensaje.set(mensajeDeError(err));
        this.cargando.set(false);
      }
    });
  }
}