import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { UsuarioResponse } from '../../core/models/usuario.model';
import { AuthService } from '../../core/services/auth.service';
import { UsuarioService } from '../../core/services/usuario.service';

@Component({
  selector: 'app-usuario-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './usuario-list.component.html',
  styleUrls: ['./usuario-list.component.css'],
})
export class UsuarioListComponent implements OnInit {
  private readonly usuarioService = inject(UsuarioService);
  public authService = inject(AuthService);

  // Signals para el estado reactivo
  usuarios = signal<UsuarioResponse[]>([]);
  cargando = signal<boolean>(true);
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.usuarioService.listarUsuarios().subscribe({
      next: (data) => {
        this.usuarios.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar los usuarios', err);
        this.cargando.set(false);
        this.errorMessage.set('No se pudo cargar la lista de usuarios.');
      },
    });
  }

  cambiarEstado(id: number, estadoActual: boolean): void {
    this.errorMessage.set(null);
    const nuevoEstado = !estadoActual;

    this.usuarioService.cambiarEstado(id, { activo: nuevoEstado }).subscribe({
      next: (usuarioActualizado) => {
        // Actualizamos la signal localmente de forma reactiva
        this.usuarios.update((lista) => lista.map((u) => (u.id === id ? usuarioActualizado : u)));
      },
      error: (err) => {
        console.error('Error al cambiar el estado del usuario', err);
        // Si el backend responde con 409 Conflict (auto-bloqueo), capturamos el mensaje
        if (err.status === 409 && err.error?.message) {
          this.errorMessage.set(err.error.message);
        } else {
          this.errorMessage.set('Ocurrió un error al intentar cambiar el estado del usuario.');
        }
      },
    });
  }
}
