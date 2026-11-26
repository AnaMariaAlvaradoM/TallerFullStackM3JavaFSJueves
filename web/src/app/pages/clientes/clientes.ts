import { Component, OnInit, inject } from '@angular/core';
import { Usuario, UsuariosService } from '../../services/usuarios.service';

@Component({
  selector: 'app-clientes',
  standalone: true,
  templateUrl: './clientes.html',
})
export class Clientes implements OnInit {
  usuariosService = inject(UsuariosService);

  ngOnInit(): void {
    this.usuariosService.cargar();
  }

  cambiarEstado(usuario: Usuario): void {
    this.usuariosService.cambiarEstado(usuario, !usuario.activo);
  }
}
