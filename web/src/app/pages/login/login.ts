import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
})
export class Login {
  auth = inject(AuthService);

  email = '';
  password = '';

  onSubmit(): void {
    if (this.email && this.password) {
      this.auth.iniciarSesion({
        email: this.email,
        password: this.password,
      });
    }
  }
}
