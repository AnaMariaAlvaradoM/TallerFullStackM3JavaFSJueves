import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { pesos } from '../../core/formato';
import { Alquiler, AlquileresService } from '../../services/alquileres.service';

@Component({
  selector: 'app-mis-alquileres',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './mis-alquileres.html',
})
export class MisAlquileres implements OnInit {
  alquileresService = inject(AlquileresService);

  alquilerParaCancelar = signal<number | null>(null);
  formatearPesos = pesos;

  ngOnInit(): void {
    this.alquileresService.cargarMios();
  }

  hoy(): string {
    const fecha = new Date();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');
    return `${fecha.getFullYear()}-${mes}-${dia}`;
  }

  puedeCancelar(a: Alquiler): boolean {
    return a.estado === 'RESERVADO' && a.fechaInicio > this.hoy();
  }

  pedirCancelacion(a: Alquiler): void {
    this.alquilerParaCancelar.set(a.id);
  }

  confirmarCancelacion(a: Alquiler): void {
    this.alquileresService.cancelar(a);
    this.alquilerParaCancelar.set(null);
  }
}
