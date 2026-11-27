import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth-service';
import { SiPuede } from './shared/si-puede';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, SiPuede],
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  readonly auth = inject(AuthService);
}
