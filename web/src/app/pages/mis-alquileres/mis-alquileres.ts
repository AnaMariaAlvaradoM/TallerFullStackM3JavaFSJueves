import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Alquiler, AlquileresService } from '../../services/alquileres.service';
import { pesos } from '../../core/formato';

@Component({
  selector: 'app-mis-alquileres',
  imports: [RouterLink],
  templateUrl: './mis-alquileres.html',
  styleUrl: './mis-alquileres.css',
})
export class MisAlquileres {
  private alquileresService = inject(AlquileresService);

  alquileres = this.alquileresService.alquileres;
  cargando = this.alquileresService.cargando;
  error = this.alquileresService.error;

  pesos = pesos;

  // Fecha local AAAA-MM-DD; las fechas ISO se comparan bien como texto
  hoy = new Date().toLocaleDateString('en-CA');

  // id del alquiler que está pidiendo confirmación (null si ninguno)
  confirmando = signal<number | null>(null);

  constructor() {
    this.alquileresService.cargarMios();
  }

  puedeCancelar(a: Alquiler): boolean {
    return a.estado === 'RESERVADO' && a.fechaInicio > this.hoy;
  }

  pedirConfirmacion(a: Alquiler) {
    this.confirmando.set(a.id);
  }

  descartar() {
    this.confirmando.set(null);
  }

  confirmarCancelacion(a: Alquiler) {
    this.alquileresService.cancelar(a);
    this.confirmando.set(null);
  }
}