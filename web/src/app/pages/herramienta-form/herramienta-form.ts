import { Component, OnInit, inject, input, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { mensajeDeError } from '../../core/errores';
import {
  CategoriaHerramienta,
  DatosHerramienta,
  Herramienta,
  HerramientasService
} from '../../services/herramientas.service';

@Component({
  selector: 'app-herramienta-form',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section class="card form-card">
      <h1>{{ esEdicion() ? 'Editar herramienta' : 'Nueva herramienta' }}</h1>

      @if (cargandoInicial()) {
        <p>Cargando datos de la herramienta...</p>
      } @else {
        <form (submit)="onSubmit($event)" novalidate>
          <div class="campo">
            <label for="hf-nombre">Nombre</label>
            <input
              id="hf-nombre"
              name="nombre"
              type="text"
              maxlength="80"
              required
              [value]="herramientaActual()?.nombre ?? ''"
            />
          </div>

          <div class="campo">
            <label for="hf-categoria">Categoría</label>
            <select id="hf-categoria" name="categoria" required [value]="herramientaActual()?.categoria ?? 'ELECTRICA'">
              <option value="ELECTRICA">ELECTRICA</option>
              <option value="CONSTRUCCION">CONSTRUCCION</option>
              <option value="JARDIN">JARDIN</option>
              <option value="MEDICION">MEDICION</option>
            </select>
          </div>

          <div class="campo">
            <label for="hf-precio">Precio por día</label>
            <input
              id="hf-precio"
              name="precioDia"
              type="number"
              min="1000"
              step="1"
              required
              [value]="herramientaActual()?.precioDia ?? ''"
            />
          </div>

          <div class="campo">
            <label for="hf-deposito">Depósito</label>
            <input
              id="hf-deposito"
              name="deposito"
              type="number"
              min="0"
              step="1"
              required
              [value]="herramientaActual()?.deposito ?? ''"
            />
          </div>

          <div class="campo">
            <label for="hf-unidades">Unidades</label>
            <input
              id="hf-unidades"
              name="unidades"
              type="number"
              min="1"
              max="50"
              step="1"
              required
              [value]="herramientaActual()?.unidades ?? 1"
            />
          </div>

          <div class="campo">
            <label for="hf-descripcion">Descripción</label>
            <input
              id="hf-descripcion"
              name="descripcion"
              type="text"
              maxlength="300"
              [value]="herramientaActual()?.descripcion ?? ''"
            />
          </div>

          <div class="campo">
            <label for="hf-imagen">URL de imagen</label>
            <input
              id="hf-imagen"
              name="imagenUrl"
              type="text"
              maxlength="300"
              [value]="herramientaActual()?.imagenUrl ?? ''"
            />
          </div>

          @if (errorMensaje()) {
            <p class="error-texto" data-testid="error-herramienta">{{ errorMensaje() }}</p>
          }

          <div class="acciones">
            <button type="submit" class="btn-primario" [disabled]="guardando()">
              {{ guardando() ? 'Guardando...' : 'Guardar' }}
            </button>
            <a routerLink="/herramientas" class="btn-secundario">Cancelar</a>
          </div>
        </form>
      }
    </section>
  `
})
export class HerramientaForm implements OnInit {
  private readonly herramientasService = inject(HerramientasService);
  private readonly router = inject(Router);

  readonly id = input<string>();
  readonly herramientaActual = signal<Herramienta | null>(null);
  readonly cargandoInicial = signal<boolean>(false);
  readonly guardando = signal<boolean>(false);
  readonly errorMensaje = signal<string | null>(null);

  esEdicion(): boolean {
    return Boolean(this.id());
  }

  ngOnInit(): void {
    const rawId = this.id();
    if (!rawId) {
      return;
    }

    const numId = Number(rawId);
    if (!Number.isFinite(numId) || numId <= 0) {
      this.errorMensaje.set('Identificador de herramienta no válido.');
      return;
    }

    this.cargandoInicial.set(true);
    this.herramientasService.obtener(numId).subscribe({
      next: (h) => {
        this.herramientaActual.set(h);
        this.cargandoInicial.set(false);
      },
      error: (err) => {
        this.errorMensaje.set(mensajeDeError(err));
        this.cargandoInicial.set(false);
      }
    });
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    this.errorMensaje.set(null);

    const form = event.target as HTMLFormElement;
    const data = new FormData(form);

    const nombre = String(data.get('nombre') ?? '').trim();
    const categoria = String(data.get('categoria') ?? 'ELECTRICA') as CategoriaHerramienta;
    const precioDia = Number(data.get('precioDia') ?? 0);
    const deposito = Number(data.get('deposito') ?? 0);
    const unidades = Number(data.get('unidades') ?? 0);
    const descripcion = String(data.get('descripcion') ?? '').trim();
    const imagenUrl = String(data.get('imagenUrl') ?? '').trim();

    const payload: DatosHerramienta = {
      nombre,
      categoria,
      precioDia,
      deposito,
      unidades,
      descripcion: descripcion || null,
      imagenUrl: imagenUrl || null
    };

    this.guardando.set(true);
    const rawId = this.id();
    const peticion$ = rawId
      ? this.herramientasService.actualizar(Number(rawId), payload)
      : this.herramientasService.crear(payload);

    peticion$.subscribe({
      next: () => {
        this.guardando.set(false);
        void this.router.navigate(['/herramientas']);
      },
      error: (err) => {
        this.errorMensaje.set(mensajeDeError(err));
        this.guardando.set(false);
      }
    });
  }
}