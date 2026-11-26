import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { validarCuenta } from '../../core/validaciones';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section class="card auth-card">
      <h1>Crear cuenta</h1>
      <p class="subtitulo">Regístrate como cliente para alquilar herramientas.</p>

      <form (submit)="onSubmit($event)" novalidate>
        <div class="campo">
          <label for="reg-nombre">Nombre completo</label>
          <input id="reg-nombre" name="nombre" type="text" maxlength="80" required />
        </div>

        <div class="campo">
          <label for="reg-email">Correo electrónico</label>
          <input id="reg-email" name="email" type="email" required />
        </div>

        <div class="campo">
          <label for="reg-password">Contraseña</label>
          <input id="reg-password" name="password" type="password" minlength="8" required />
        </div>

        <div class="campo">
          <label for="reg-confirmacion">Confirmar contraseña</label>
          <input id="reg-confirmacion" name="confirmacion" type="password" minlength="8" required />
        </div>

        @if (erroresValidacion().length > 0) {
          <ul class="lista-errores" data-testid="errores-registro">
            @for (err of erroresValidacion(); track err) {
              <li>{{ err }}</li>
            }
          </ul>
        }

        @if (errorBackend()) {
          <p class="error-texto" data-testid="error-registro">{{ errorBackend() }}</p>
        }

        <button type="submit" [disabled]="auth.cargando()">
          {{ auth.cargando() ? 'Registrando...' : 'Registrarse' }}
        </button>
      </form>

      <p class="pie-enlace">
        ¿Ya tienes cuenta?
        <a routerLink="/login">Inicia sesión</a>
      </p>
    </section>
  `
})
export class Registro {
  readonly auth = inject(AuthService);
  readonly erroresValidacion = signal<string[]>([]);
  readonly errorBackend = signal<string | null>(null);

  onSubmit(event: Event): void {
    event.preventDefault();
    this.erroresValidacion.set([]);
    this.errorBackend.set(null);

    const form = event.target as HTMLFormElement;
    const data = new FormData(form);

    const nombre = String(data.get('nombre') ?? '').trim();
    const email = String(data.get('email') ?? '').trim();
    const password = String(data.get('password') ?? '');
    const confirmacion = String(data.get('confirmacion') ?? '');

    const errores = validarCuenta({ nombre, email, password }, confirmacion);
    if (errores.length > 0) {
      this.erroresValidacion.set(errores);
      return;
    }

    this.auth.registrarse({ nombre, email, password }).subscribe({
      error: () => {
        this.errorBackend.set(this.auth.error());
      }
    });
  }
}