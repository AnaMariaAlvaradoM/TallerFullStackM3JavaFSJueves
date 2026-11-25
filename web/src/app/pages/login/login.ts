import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink],
  template: `
    <section class="card auth-card">
      <h1>Iniciar sesión</h1>
      <p class="subtitulo">Ingresa con tu cuenta de Llave Maestra.</p>

      <form (submit)="onSubmit($event)" novalidate>
        <div class="campo">
          <label for="login-email">Correo electrónico</label>
          <input id="login-email" name="email" type="email" required autocomplete="email" />
        </div>

        <div class="campo">
          <label for="login-password">Contraseña</label>
          <input id="login-password" name="password" type="password" required autocomplete="current-password" />
        </div>

        @if (errorLocal() || auth.error()) {
          <p class="error-texto" data-testid="error-login">
            {{ errorLocal() || auth.error() }}
          </p>
        }

        <button type="submit" [disabled]="auth.cargando()">
          {{ auth.cargando() ? 'Ingresando...' : 'Entrar' }}
        </button>
      </form>

      <p class="pie-enlace">
        ¿Aún no tienes cuenta?
        <a routerLink="/registro" data-testid="enlace-registro">Crear cuenta</a>
      </p>
    </section>
  `
})
export class Login {
  readonly auth = inject(AuthService);
  readonly errorLocal = signal<string | null>(null);

  onSubmit(event: Event): void {
    event.preventDefault();
    this.errorLocal.set(null);

    const form = event.target as HTMLFormElement;
    const data = new FormData(form);
    const email = String(data.get('email') ?? '').trim();
    const password = String(data.get('password') ?? '');

    if (!email || !password) {
      this.errorLocal.set('Debes ingresar tu correo y contraseña.');
      return;
    }

    this.auth.iniciarSesion({ email, password });
  }
}