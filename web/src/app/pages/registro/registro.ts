import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { validarCuenta } from '../../core/validaciones';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './registro.html',
})
export class Registro {
  authService = inject(AuthService);
  private router = inject(Router);

  nombre = '';
  email = '';
  password = '';
  confirmacion = '';
  errores = signal<string[]>([]);

  onSubmit(): void {
    const fallos = validarCuenta(
      { nombre: this.nombre, email: this.email, password: this.password },
      this.confirmacion
    );
    this.errores.set(fallos);
    if (fallos.length > 0) {
      return;
    }

    this.authService.registrarse({
      nombre: this.nombre,
      email: this.email,
      password: this.password,
    });
  }
}
