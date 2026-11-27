import { Component, inject, OnInit, signal } from '@angular/core';
import { AlquileresService } from '../../services/alquileres.service';
import { Alquiler, EstadoAlquiler } from '../../core/modelos';
import { fechaDeHoy } from '../../core/validaciones';
import { fechaCorta, pesos } from '../../core/formato';

const ETIQUETAS: Record<EstadoAlquiler, string> = {
  RESERVADO: 'Reservado',
  ENTREGADO: 'Entregado',
  DEVUELTO: 'Devuelto',
  CANCELADO: 'Cancelado',
};

@Component({
  selector: 'app-mis-alquileres',
  templateUrl: './mis-alquileres.html',
  styleUrl: './mis-alquileres.css',
})
export class MisAlquileres implements OnInit {
  readonly servicio = inject(AlquileresService);
  readonly pesos = pesos;
  readonly fechaCorta = fechaCorta;
  readonly etiquetas = ETIQUETAS;
  private readonly hoy = fechaDeHoy();

  // El id del alquiler que está esperando confirmación (null = ninguno)
  readonly porConfirmar = signal<number | null>(null);

  ngOnInit(): void {
    this.servicio.cargarMios();
  }

  // Misma regla del backend: solo lo RESERVADO que empieza DESPUÉS de hoy.
  // Las fechas 'AAAA-MM-DD' se pueden comparar como texto.
  sePuedeCancelar(alquiler: Alquiler): boolean {
    return alquiler.estado === 'RESERVADO' && alquiler.fechaInicio > this.hoy;
  }

  pedirConfirmacion(alquiler: Alquiler): void {
    this.porConfirmar.set(alquiler.id);
  }

  confirmarCancelacion(alquiler: Alquiler): void {
    this.servicio.cancelar(alquiler);
    this.porConfirmar.set(null);
  }

  desistir(): void {
    this.porConfirmar.set(null);
  }
}
