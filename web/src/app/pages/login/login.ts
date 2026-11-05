import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  readonly auth = inject(AuthService);

  // Lee los campos del formulario con FormData (por su atributo name)
  iniciar(evento: Event): void {
    evento.preventDefault(); // evita que el navegador recargue la página
    const datos = new FormData(evento.target as HTMLFormElement);
    this.auth.iniciarSesion({
      email: String(datos.get('email') ?? '').trim(),
      password: String(datos.get('password') ?? ''),
    });
  }
}
