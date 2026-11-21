import { Component, computed, inject } from '@angular/core';
import { Usuario, UsuariosService } from '../../services/usuarios.service';
import { AuthService } from '../../core/auth.service';
import { NOMBRE_ROL } from '../../core/permisos';
import { fechaCorta } from '../../core/formato';

@Component({
  imports: [],
  selector: 'app-clientes',
  styleUrl: './clientes.css',
  templateUrl: './clientes.html',
})
export class Clientes {

  private usuariosService = inject(UsuariosService);
  private authService = inject(AuthService);

  usuarios = this.usuariosService.usuarios;
  cargando = this.usuariosService.cargando;
  error = this.usuariosService.error;

  nombreRol = NOMBRE_ROL;
  fechaCorta = fechaCorta;

  activos = computed(() => this.usuarios().filter(u => u.activo).length);
  administradores = computed(() => this.usuarios().filter(u => u.rol === 'ADMIN').length);

  constructor() {
    this.usuariosService.cargar();
  }

  
  esYo(usuario: Usuario): boolean {
    return usuario.email === this.authService.email();
  }

  alternarEstado(usuario: Usuario) {
    this.usuariosService.cambiarEstado(usuario, !usuario.activo);
  }

}
