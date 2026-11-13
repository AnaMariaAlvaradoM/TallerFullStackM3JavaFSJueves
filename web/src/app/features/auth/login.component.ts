import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  errorMessage = signal<string | null>(null);
  cargando = signal<boolean>(false);

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(4)]],
  });

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.cargando.set(true);
    this.errorMessage.set(null);

    const { email, password } = this.loginForm.value;

    this.authService.login({ email, password }).subscribe({
      next: () => {
        this.cargando.set(false);
        // Redirigir al catálogo de herramientas tras un inicio de sesión exitoso
        void this.router.navigate(['/herramientas']);
      },
      error: (err) => {
        console.error('Error en el login', err);
        this.cargando.set(false);
        if (err.status === 401 || err.status === 403) {
          this.errorMessage.set(
            'Credenciales inválidas. Por favor verifique su correo y contraseña.',
          );
        } else {
          this.errorMessage.set('Ocurrió un error al intentar iniciar sesión con el servidor.');
        }
      },
    });
  }
}
