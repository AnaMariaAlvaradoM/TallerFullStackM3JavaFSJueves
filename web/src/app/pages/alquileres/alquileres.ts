import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { pesos } from '../../core/formato';
import { Alquiler, AlquileresService } from '../../services/alquileres.service';

@Component({
  selector: 'app-alquileres',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './alquileres.html',
})
export class Alquileres implements OnInit {
  alquileresService = inject(AlquileresService);

  formatearPesos = pesos;

  ngOnInit(): void {
    this.alquileresService.cargarTodos();
  }

  alCambiarFiltro(evento: Event): void {
    const valor = (evento.target as HTMLSelectElement).value;
    this.alquileresService.cargarTodos(valor || undefined);
  }

  entregar(alquiler: Alquiler): void {
    this.alquileresService.entregar(alquiler);
  }

  devolver(alquiler: Alquiler): void {
    this.alquileresService.devolver(alquiler);
  }
}
