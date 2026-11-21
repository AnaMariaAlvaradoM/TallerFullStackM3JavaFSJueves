import { Component, OnInit, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { mensajeDeError } from '../../core/errores';
import { pesos } from '../../core/formato';
import { calcularDias, validarAlquiler } from '../../core/validaciones';
import { AlquileresService } from '../../services/alquileres.service';
import { Herramienta, HerramientasService } from '../../services/herramientas.service';

@Component({
  selector: 'app-alquilar',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './alquilar.html',
})
export class Alquilar implements OnInit {
  id = input.required<string>();

  private herramientasService = inject(HerramientasService);
  private alquileresService = inject(AlquileresService);
  private router = inject(Router);

  herramienta = signal<Herramienta | null>(null);
  fechaInicio = '';
  fechaFin = '';
  errores = signal<string[]>([]);
  errorAlquiler = signal<string | null>(null);
  enviando = signal<boolean>(false);

  formatearPesos = pesos;

  ngOnInit(): void {
    this.herramientasService.buscarPorId(this.id()).subscribe({
      next: (h) => this.herramienta.set(h),
      error: (err) => this.errorAlquiler.set(mensajeDeError(err)),
    });
  }

  dias(): number {
    return calcularDias(this.fechaInicio, this.fechaFin);
  }

  valorTotal(): number {
    const precio = this.herramienta()?.precioDia ?? 0;
    return this.dias() * precio;
  }

  onSubmit(): void {
    const ahora = new Date();
    const a = ahora.getFullYear();
    const m = String(ahora.getMonth() + 1).padStart(2, '0');
    const d = String(ahora.getDate()).padStart(2, '0');
    const hoy = `${a}-${m}-${d}`;

    const fallos = validarAlquiler({ fechaInicio: this.fechaInicio, fechaFin: this.fechaFin }, hoy);
    this.errores.set(fallos);
    if (fallos.length > 0) {
      return;
    }

    this.enviando.set(true);
    this.errorAlquiler.set(null);

    this.alquileresService
      .crear({
        herramientaId: Number(this.id()),
        fechaInicio: this.fechaInicio,
        fechaFin: this.fechaFin,
      })
      .subscribe({
        next: () => {
          this.enviando.set(false);
          this.router.navigateByUrl('/mis-alquileres');
        },
        error: (err) => {
          this.enviando.set(false);
          this.errorAlquiler.set(mensajeDeError(err));
        },
      });
  }
}
