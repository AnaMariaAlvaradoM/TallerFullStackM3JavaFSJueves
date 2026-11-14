import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Alquiler, AlquileresService, sePuedeCancelar } from '../../services/alquileres.service';
import { pesos } from '../../core/formato';
import { Alquilar } from '../alquilar/alquilar';
const formatoDia = new Intl.DateTimeFormat('es-CO', {weekday:'short',day: 'numeric', month:'short'})
@Component({
  imports: [RouterLink],
  selector: 'app-mis-alquileres',
  styleUrl: './mis-alquileres.css',
  templateUrl: './mis-alquileres.html',
})
export class MisAlquileres {
  private alquilerService = inject(AlquileresService);

  alquileres = this.alquilerService.alquileres;
  cargando = this.alquilerService.cargando;
  error = this.alquilerService.error;

  pesos = pesos;

  porConfirmar = signal<number | null>(null);

  constructor(){
    this.alquilerService.cargarMios();
  }

  dia(fecha: string): string{
    return formatoDia.format( new Date(`${fecha}T00:00`))
  }

  sePuedeCancelar(alquiler:Alquiler): boolean{
    return sePuedeCancelar(alquiler,new Date())
  }

  confirmarCancelacion(alquiler:Alquiler){
    this.alquilerService.cancelar(alquiler);
    this.porConfirmar.set(null);
  }
}
