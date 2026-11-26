import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { mensajeDeError } from '../../core/errores';
import { pesos } from '../../core/formato';
import { calcularDias, validarAlquiler } from '../../core/validaciones';
import { AlquileresService } from '../../services/alquileres.service';
import { Herramienta, HerramientasService } from '../../services/herramientas.service';

function fechaHoyIso(): string {
  const ahora = new Date();
  const y = ahora.getFullYear();
  const m = String(ahora.getMonth() + 1).padStart(2, '0');
  const d = String(ahora.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

@Component({
  selector: 'app-alquilar',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section class="card form-card">
      <h1>Alquilar herramienta</h1>

      @if (cargandoHerramienta()) {
        <p>Cargando herramienta...</p>
      } @else if (herramienta(); as h) {
        <h2 data-testid="nombre-herramienta">{{ h.nombre }}</h2>
        <p class="meta">
          Precio por día: {{ pesos(h.precioDia) }} · Depósito: {{ pesos(h.deposito) }}
        </p>

        <form (submit)="onSubmit($event)" (input)="onInput($event)" novalidate>
          <div class="campo">
            <label for="alq-inicio">Fecha de inicio</label>
            <input id="alq-inicio" name="fechaInicio" type="date" required />
          </div>

          <div class="campo">
            <label for="alq-fin">Fecha de fin</label>
            <input id="alq-fin" name="fechaFin" type="date" required />
          </div>

          @if (resumenTexto(); as resumen) {
            <p class="resumen-valor" data-testid="resumen-valor">{{ resumen }}</p>
          }

          @if (erroresFormulario().length > 0) {
            <ul class="lista-errores" data-testid="errores-formulario">
              @for (err of erroresFormulario(); track err) {
                <li>{{ err }}</li>
              }
            </ul>
          }

          @if (errorAlquiler()) {
            <p class="error-texto" data-testid="error-alquiler">{{ errorAlquiler() }}</p>
          }

          <div class="acciones">
            <button type="submit" class="btn-primario" [disabled]="enviando()">
              {{ enviando() ? 'Reservando...' : 'Confirmar alquiler' }}
            </button>
            <a routerLink="/herramientas" class="btn-secundario">Volver</a>
          </div>
        </form>
      } @else {
        <p class="error-texto" data-testid="error-alquiler">
          {{ errorHerramienta() || 'No se pudo cargar la herramienta.' }}
        </p>
        <a routerLink="/herramientas" class="btn-secundario">Volver al catálogo</a>
      }
    </section>
  `
})
export class Alquilar implements OnInit {
  private readonly herramientasService = inject(HerramientasService);
  private readonly alquileresService = inject(AlquileresService);
  private readonly router = inject(Router);
  readonly pesos = pesos;

  readonly id = input.required<string>();
  readonly herramienta = signal<Herramienta | null>(null);
  readonly cargandoHerramienta = signal<boolean>(true);
  readonly errorHerramienta = signal<string | null>(null);

  readonly fechaInicioSel = signal<string>('');
  readonly fechaFinSel = signal<string>('');
  readonly erroresFormulario = signal<string[]>([]);
  readonly errorAlquiler = signal<string | null>(null);
  readonly enviando = signal<boolean>(false);

  readonly resumenTexto = computed<string | null>(() => {
    const h = this.herramienta();
    if (!h) {
      return null;
    }
    const dias = calcularDias(this.fechaInicioSel(), this.fechaFinSel());
    if (dias <= 0) {
      return null;
    }
    const total = dias * h.precioDia;
    return `${dias} días · ${pesos(total)}`;
  });

  ngOnInit(): void {
    const numId = Number(this.id());
    if (!Number.isFinite(numId) || numId <= 0) {
      this.errorHerramienta.set('Identificador de herramienta inválido.');
      this.cargandoHerramienta.set(false);
      return;
    }

    this.herramientasService.obtener(numId).subscribe({
      next: (h) => {
        this.herramienta.set(h);
        this.cargandoHerramienta.set(false);
      },
      error: (err) => {
        this.errorHerramienta.set(mensajeDeError(err));
        this.cargandoHerramienta.set(false);
      }
    });
  }

  onInput(event: Event): void {
    const form = (event.currentTarget as HTMLFormElement | null);
    if (!form) {
      return;
    }
    const data = new FormData(form);
    this.fechaInicioSel.set(String(data.get('fechaInicio') ?? ''));
    this.fechaFinSel.set(String(data.get('fechaFin') ?? ''));
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    this.erroresFormulario.set([]);
    this.errorAlquiler.set(null);

    const form = event.target as HTMLFormElement;
    const data = new FormData(form);
    const fechaInicio = String(data.get('fechaInicio') ?? '').trim();
    const fechaFin = String(data.get('fechaFin') ?? '').trim();

    this.fechaInicioSel.set(fechaInicio);
    this.fechaFinSel.set(fechaFin);

    const errores = validarAlquiler({ fechaInicio, fechaFin }, fechaHoyIso());
    if (errores.length > 0) {
      this.erroresFormulario.set(errores);
      return;
    }

    const herramientaId = Number(this.id());
    this.enviando.set(true);

    this.alquileresService.crear({ herramientaId, fechaInicio, fechaFin }).subscribe({
      next: () => {
        this.enviando.set(false);
        void this.router.navigate(['/mis-alquileres']);
      },
      error: (err) => {
        this.errorAlquiler.set(mensajeDeError(err));
        this.enviando.set(false);
      }
    });
  }
}