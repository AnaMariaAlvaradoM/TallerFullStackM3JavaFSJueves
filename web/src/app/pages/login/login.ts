import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/auth.service';

@Component({
  imports: [],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
  private authService = inject(AuthService);

  error = this.authService.error;
  cargando = this.authService.cargando;

  ingresar(evento: SubmitEvent) {
    evento.preventDefault();
    const datos = new FormData(evento.target as HTMLFormElement);

    this.authService.iniciarSesion({
      email: String(datos.get('email') ?? '').trim(),
      password: String(datos.get('password') ?? '')
    });
  }
}