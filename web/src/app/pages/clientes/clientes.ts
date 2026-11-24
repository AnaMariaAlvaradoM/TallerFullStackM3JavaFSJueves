import { Component, inject, OnInit } from '@angular/core';
import { UsuariosService } from '../../services/usuarios.service';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [],
  templateUrl: './clientes.html'
})
export class Clientes implements OnInit {
  usuariosService = inject(UsuariosService);
  authService = inject(AuthService);

  ngOnInit() {
    this.usuariosService.cargar();
  }
}