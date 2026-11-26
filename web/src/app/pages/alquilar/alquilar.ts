import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { Herramienta, HerramientasService } from '../../services/herramientas.service';
import { AlquileresService } from '../../services/alquileres.service';
import { calcularDias, validarAlquiler } from '../../core/validaciones';
import { mensajeDeError } from '../../core/errores';

@Component({
  selector: 'app-alquilar',
  imports: [RouterLink],
  templateUrl: './alquilar.html',
  styleUrl: './alquilar.css',
})
export class Alquilar implements OnInit {
  // Llega de la URL gracias a withComponentInputBinding()
  id = input.required<string>();

  private herramientas = inject(HerramientasService);
  private alquileres = inject(AlquileresService);
  private router = inject(Router);

  herramienta = signal<Herramienta | null>(null);
  errorCarga = signal<string | null>(null);
  errores = signal<string[]>([]);
  errorServidor = signal<string | null>(null);
  enviando = signal(false);
  dias = signal(0);

  valor = computed(() => this.dias() * (this.herramienta()?.precioDia ?? 0));

  // Fecha local en formato AAAA-MM-DD (lo que entrega un <input type="date">)
  hoy = new Date().toLocaleDateString('en-CA');

  ngOnInit() {
    this.herramientas.obtener(Number(this.id())).subscribe({
      next: (h) => this.herramienta.set(h),
      error: (error: HttpErrorResponse) => this.errorCarga.set(mensajeDeError(error)),
    });
  }

  pesos(valor: number): string {
    return `$ ${valor.toLocaleString('es-CO')}`;
  }

  private leerFechas(form: HTMLFormElement) {
    const datos = new FormData(form);
    return {
      fechaInicio: String(datos.get('fechaInicio') ?? ''),
      fechaFin: String(datos.get('fechaFin') ?? ''),
    };
  }

  // (input) del <form>: recalcula días y valor antes de enviar
  actualizar(evento: Event) {
    const { fechaInicio, fechaFin } = this.leerFechas(evento.currentTarget as HTMLFormElement);
    this.dias.set(calcularDias(fechaInicio, fechaFin));
  }

  reservar(evento: Event) {
    evento.preventDefault();
    this.errorServidor.set(null);

    const { fechaInicio, fechaFin } = this.leerFechas(evento.target as HTMLFormElement);

    const errores = validarAlquiler({ fechaInicio, fechaFin }, this.hoy);
    this.errores.set(errores);
    if (errores.length > 0) return;

    this.enviando.set(true);
    this.alquileres
      .crear({ herramientaId: Number(this.id()), fechaInicio, fechaFin })
      .subscribe({
        next: () => this.router.navigate(['/mis-alquileres']),
        error: (error: HttpErrorResponse) => {
          this.errorServidor.set(mensajeDeError(error));
          this.enviando.set(false);
        },
      });
  }
}