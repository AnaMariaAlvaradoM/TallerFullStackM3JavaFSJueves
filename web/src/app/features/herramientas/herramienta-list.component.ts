import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { HerramientaResponse } from '../../core/models/herramienta.model';
import { AuthService } from '../../core/services/auth.service';
import { HerramientaService } from '../../core/services/herramienta.service';

@Component({
  selector: 'app-herramienta-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './herramienta-list.component.html',
  styleUrls: ['./herramienta-list.component.css'],
})
export class HerramientaListComponent implements OnInit {
  private readonly herramientaService = inject(HerramientaService);
  public authService = inject(AuthService);

  // Signals para manejar el estado reactivo del componente
  herramientas = signal<HerramientaResponse[]>([]);
  cargando = signal<boolean>(true);

  ngOnInit(): void {
    this.cargarHerramientas();
  }

  cargarHerramientas(): void {
    this.herramientaService.listarHerramientas().subscribe({
      next: (data) => {
        this.herramientas.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error al cargar las herramientas', err);
        this.cargando.set(false);
      },
    });
  }

  cambiarEstado(id: number, estadoActual: boolean): void {
    const nuevoEstado = !estadoActual;
    this.herramientaService.cambiarEstado(id, { activa: nuevoEstado }).subscribe({
      next: (herramientaActualizada) => {
        // Actualizamos de forma reactiva la signal sin necesidad de volver a consultar todo el backend
        this.herramientas.update((lista) =>
          lista.map((h) => (h.id === id ? herramientaActualizada : h)),
        );
      },
      error: (err) => {
        console.error('Error al cambiar el estado de la herramienta', err);
      },
    });
  }
}
