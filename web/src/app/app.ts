import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { DESCRIPCION_PERMISO, NOMBRE_ROL, Permiso } from './core/permisos';
import { AuthService } from './core/auth.service';

interface EnlaceMenu{
  ruta: string;
  texto: string;
  permiso: Permiso;
}

const MENU: EnlaceMenu[] = [
  {ruta: '/herramientas', texto: 'Herramientas', permiso: 'HERRAMIENTAS_VER'},
  {ruta: '/mis-herramientas', texto: 'Mis Herramientas', permiso:'ALQUILERES_VER_PROPIOS'},
  {ruta: '/cliente',texto:'Clientes', permiso:'CLIENTES_GESTIONAR'}
];

@Component({
  imports: [RouterOutlet,RouterLink,RouterLinkActive],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})

export class App {
  authservice = inject(AuthService);
  descripcionPermiso = DESCRIPCION_PERMISO;

  enlaces= computed(()=> MENU.filter(enlace => this.authservice.puede(enlace.permiso)));
  nombreRol = computed(()=>{
    const rol = this.authservice.rol();
    return rol? NOMBRE_ROL[rol] : '';
  });

}
