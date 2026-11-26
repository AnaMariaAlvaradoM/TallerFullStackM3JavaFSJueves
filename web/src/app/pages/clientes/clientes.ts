import { Component, OnInit, inject } from '@angular/core';

import { AuthService } from '../../core/auth.service';
import { Usuario, UsuariosService } from '../../services/usuarios.service';

@Component({
  selector: 'app-clientes',
  standalone: true,
  template: `
    <section class="pagina">
      <header class="encabezado-pagina">
        <div>
          <h1>Clientes</h1>
          <p class="subtitulo">Administra las cuentas de acceso a Llave Maestra.</p>
        </div>
      </header>

      @if (servicio.cargando()) {
        <p>Cargando clientes...</p>
      }

      @if (servicio.error()) {
        <p class="error-texto">{{ servicio.error() }}</p>
      }

      <div class="lista-elementos">
        @for (u of servicio.usuarios(); track u.id) {
          <article class="card fila-elemento" data-testid="cliente">
            <div>
              <h2>{{ u.nombre }}</h2>
              <p class="meta">{{ u.email }} · Rol: {{ u.rol }}</p>
              <p class="estado-item">Estado: {{ u.activo ? 'Activo' : 'Bloqueado' }}</p>
            </div>

            <div class="acciones">
              @if (!esCuentaPropia(u)) {
                <button
                  type="button"
                  class="btn-secundario"
                  data-testid="boton-estado"
                  (click)="alternarEstado(u)"
                >
                  {{ u.activo ? 'Bloquear' : 'Desbloquear' }}
                </button>
              }
            </div>
          </article>
        }
      </div>
    </section>
  `
})
export class Clientes implements OnInit {
  readonly servicio = inject(UsuariosService);
  private readonly auth = inject(AuthService);

  ngOnInit(): void {
    this.servicio.cargar();
  }

  esCuentaPropia(usuario: Usuario): boolean {
    const token = this.auth.token();
    if (!token) {
      return false;
    }
    try {
      const partes = token.split('.');
      if (partes.length !== 3) {
        return false;
      }
      const base64 = partes[1].replace(/-/g, '+').replace(/_/g, '/');
      const json = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      const payload = JSON.parse(json) as { sub?: string };
      return (payload.sub ?? '').toLowerCase() === usuario.email.toLowerCase();
    } catch {
      return false;
    }
  }

  alternarEstado(usuario: Usuario): void {
    this.servicio.cambiarEstado(usuario, !usuario.activo);
  }
}