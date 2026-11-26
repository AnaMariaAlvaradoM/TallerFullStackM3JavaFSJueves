import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { AuthRequest, AuthResponse } from '../models/auth.model';

@Injectable({
  providedIn: 'root',
})
class AuthService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/auth';

  private readonly _currentUser = signal<AuthResponse | null>(this.obtenerUsuarioAlCargar());
  public currentUser = this._currentUser.asReadonly();

  public isLoggedIn = signal<boolean>(this.tokenExiste());

  private tokenExiste(): boolean {
    return !!localStorage.getItem('token');
  }

  private obtenerUsuarioAlCargar(): AuthResponse | null {
    const usuarioGuardado = localStorage.getItem('usuario');
    return usuarioGuardado ? JSON.parse(usuarioGuardado) : null;
  }

  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('usuario', JSON.stringify(response));

        this._currentUser.set(response);
        this.isLoggedIn.set(true);
      }),
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    this._currentUser.set(null);
    this.isLoggedIn.set(false);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  tienePermiso(permiso: string): boolean {
    const user = this._currentUser();
    return user ? user.authorities.includes(permiso) : false;
  }
}

export { AuthService };
