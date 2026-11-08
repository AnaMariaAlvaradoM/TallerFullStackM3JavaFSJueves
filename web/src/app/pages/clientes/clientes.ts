import { Component, inject, OnInit } from '@angular/core';
import { UsuariosService } from '../../services/usuarios.service';
import { AuthService } from '../../core/auth.service';
import { fechaCorta } from '../../core/formato';

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.html',
  styleUrl: './clientes.css',
})
export class Clientes implements OnInit {
  readonly servicio = inject(UsuariosService);
  readonly auth = inject(AuthService);
  readonly fechaCorta = fechaCorta;

  ngOnInit(): void {
    this.servicio.cargar();
  }
}
