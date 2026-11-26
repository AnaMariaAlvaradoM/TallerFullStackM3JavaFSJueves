import { Component, OnInit, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { mensajeDeError } from '../../core/errores';
import { HerramientasService } from '../../services/herramientas.service';

@Component({
  selector: 'app-herramienta-form',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './herramienta-form.html',
})
export class HerramientaForm implements OnInit {
  id = input<string | null>(null);

  private herramientasService = inject(HerramientasService);
  private router = inject(Router);

  nombre = '';
  categoria = 'ELECTRICA';
  precioDia: number | string = '';
  deposito: number | string = '';
  unidades: number | string = '';
  descripcion = '';
  imagenUrl = '';

  errorHerramienta = signal<string | null>(null);
  guardando = signal<boolean>(false);

  ngOnInit(): void {
    const idVal = this.id();
    if (idVal) {
      this.herramientasService.buscarPorId(idVal).subscribe({
        next: (h) => {
          this.nombre = h.nombre;
          this.categoria = h.categoria;
          this.precioDia = h.precioDia;
          this.deposito = h.deposito;
          this.unidades = h.unidades;
          this.descripcion = h.descripcion || '';
          this.imagenUrl = h.imagenUrl || '';
        },
        error: (err) => {
          this.errorHerramienta.set(mensajeDeError(err));
        },
      });
    }
  }

  onSubmit(): void {
    const payload = {
      nombre: this.nombre,
      categoria: this.categoria,
      precioDia: Number(this.precioDia),
      deposito: Number(this.deposito),
      unidades: Number(this.unidades),
      descripcion: this.descripcion || null,
      imagenUrl: this.imagenUrl || null,
    };

    this.guardando.set(true);
    this.errorHerramienta.set(null);

    const idVal = this.id();
    const peticion = idVal
      ? this.herramientasService.actualizar(idVal, payload)
      : this.herramientasService.crear(payload);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.router.navigateByUrl('/herramientas');
      },
      error: (err) => {
        this.guardando.set(false);
        this.errorHerramienta.set(mensajeDeError(err));
      },
    });
  }
}
