import { Component, computed, inject, input, OnInit, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { HerramientasService } from '../../services/herramientas.service';
import { AlquileresService } from '../../services/alquileres.service';
import { Herramienta } from '../../core/modelos';
import { calcularDias, fechaDeHoy, validarAlquiler } from '../../core/validaciones';
import { mensajeDeError } from '../../core/errores';
import { pesos } from '../../core/formato';

@Component({
  selector: 'app-alquilar',
  imports: [RouterLink],
  templateUrl: './alquilar.html',
  styleUrl: './alquilar.css',
})
export class Alquilar implements OnInit {
  // Llega de la URL /herramientas/:id/alquilar gracias a withComponentInputBinding()
  readonly id = input.required<string>();

  private readonly herramientasService = inject(HerramientasService);
  private readonly alquileresService = inject(AlquileresService);
  private readonly router = inject(Router);

  readonly pesos = pesos;
  readonly hoy = fechaDeHoy();

  readonly herramienta = signal<Herramienta | null>(null);
  readonly fechaInicio = signal('');
  readonly fechaFin = signal('');
  readonly errores = signal<string[]>([]);
  readonly errorServidor = signal<string | null>(null);
  readonly enviando = signal(false);

  // computed(): se recalculan solos cada vez que cambian las fechas
  readonly dias = computed(() => calcularDias(this.fechaInicio(), this.fechaFin()));
  readonly valor = computed(() => this.dias() * (this.herramienta()?.precioDia ?? 0));

  ngOnInit(): void {
    this.herramientasService.obtener(Number(this.id())).subscribe({
      next: (h) => this.herramienta.set(h),
      error: (error: HttpErrorResponse) => this.errorServidor.set(mensajeDeError(error)),
    });
  }

  // Lee las fechas del formulario (por el atributo name de cada campo)
  private leerFormulario(formulario: HTMLFormElement): { fechaInicio: string; fechaFin: string } {
    const datos = new FormData(formulario);
    return {
      fechaInicio: String(datos.get('fechaInicio') ?? ''),
      fechaFin: String(datos.get('fechaFin') ?? ''),
    };
  }

  // Cada vez que la persona escribe, actualiza el resumen (días y valor) ANTES de enviar
  actualizar(evento: Event): void {
    const { fechaInicio, fechaFin } = this.leerFormulario(evento.currentTarget as HTMLFormElement);
    this.fechaInicio.set(fechaInicio);
    this.fechaFin.set(fechaFin);
  }

  alquilar(evento: Event): void {
    evento.preventDefault();
    const datos = this.leerFormulario(evento.currentTarget as HTMLFormElement);
    this.fechaInicio.set(datos.fechaInicio);
    this.fechaFin.set(datos.fechaFin);
    this.errorServidor.set(null);

    // 1. Primero validamos aquí: si hay errores, NO se llama al backend
    const errores = validarAlquiler(datos, this.hoy);
    this.errores.set(errores);
    if (errores.length > 0) return;

    // 2. Todo bien: enviamos solo herramienta y fechas
    this.enviando.set(true);
    this.alquileresService
      .crear({ herramientaId: Number(this.id()), fechaInicio: datos.fechaInicio, fechaFin: datos.fechaFin })
      .subscribe({
        next: () => {
          this.enviando.set(false);
          this.router.navigateByUrl('/mis-alquileres');
        },
        // 3. Si el backend dice que no (por ejemplo, sin unidades), mostramos su mensaje
        error: (error: HttpErrorResponse) => {
          this.enviando.set(false);
          this.errorServidor.set(mensajeDeError(error));
        },
      });
  }
}
