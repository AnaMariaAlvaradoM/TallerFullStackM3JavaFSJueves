import { Component, computed, inject } from '@angular/core';
import { RouterOutlet, RouterLinkWithHref, RouterLink, RouterLinkActive } from '@angular/router';
import { DESCRIPCION_PERMISO, NOMBRE_ROL, Permiso } from './core/permisos';
import { AuthService } from './core/auth.service';

interface EnlaceMenu {
  ruta: string;
  texto: string;
  permiso: Permiso;
  testId: string;
}

// El menú completo: cada enlace dice qué permiso necesita para verse
const MENU: EnlaceMenu[] = [
  { ruta: '/herramientas', texto: 'Herramientas', permiso: 'HERRAMIENTAS_VER', testId: 'menu-herramientas' },
  { ruta: '/usuarios', texto: 'Clientes', permiso: 'CLIENTES_GESTIONAR', testId: 'menu-clientes' }
];

@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  authService = inject(AuthService);

  descripcionPermiso = DESCRIPCION_PERMISO;

  enlaces = computed(() => MENU.filter(enlace => this.authService.puede(enlace.permiso)));

  nombreRol = computed(() => {
    const rol = this.authService.rol();
    return rol ? NOMBRE_ROL[rol] : '';
  });
}
