import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html'
})
export class Login {
  authService = inject(AuthService);
  email = '';
  password = '';

  onSubmit() {
    if (this.email && this.password) {
      this.authService.iniciarSesion({ email: this.email, password: this.password });
    }
  }
}