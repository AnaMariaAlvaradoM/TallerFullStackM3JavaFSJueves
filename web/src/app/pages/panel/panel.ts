import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { mensajeDeError } from '../../core/errores';
import { pesos } from '../../core/formato';
import { ReportesService, Resumen } from '../../services/reportes.service';

@Component({
  selector: 'app-panel',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './panel.html',
})
export class Panel implements OnInit {
  private reportesService = inject(ReportesService);

  resumen = signal<Resumen | null>(null);
  cargando = signal<boolean>(true);
  error = signal<string | null>(null);
  formatearPesos = pesos;

  ngOnInit(): void {
    this.reportesService.resumen().subscribe({
      next: (r) => {
        this.resumen.set(r);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(mensajeDeError(err));
        this.cargando.set(false);
      },
    });
  }
}
