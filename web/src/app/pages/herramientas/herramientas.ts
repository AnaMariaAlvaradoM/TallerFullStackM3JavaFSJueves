import { Component, computed, inject, signal } from '@angular/core';
import { AuthService } from '../../core/auth.service';
import { pesos } from '../../core/formato';
import { SiPuede } from '../../shared/si-puede';
import { Categoria, Herramienta, HerramientasService, NOMBRE_CATEGORIA } from '../../services/herramientas.service';

type Filtro = Categoria | 'TODAS';

@Component({
  imports: [SiPuede],
  selector: 'app-herramientas',
  styleUrl: './herramientas.css',
  templateUrl: './herramientas.html',
})
export class Herramientas {
  private herramientasService = inject(HerramientasService);
  private authService = inject(AuthService);

  herramientas = this.herramientasService.herramientas;
  cargando = this.herramientasService.cargando;
  error = this.herramientasService.error;

  nombreCategoria = NOMBRE_CATEGORIA;
  pesos = pesos;

  gestiona = computed(() => this.authService.puede('HERRAMIENTAS_GESTIONAR'));
  retiradas = computed(() => this.herramientas().filter(h => !h.activa).length);

  filtros: { valor: Filtro; texto: string }[] = [
    { valor: 'TODAS', texto: 'Todas' },
    { valor: 'ELECTRICA', texto: 'Eléctricas' },
    { valor: 'CONSTRUCCION', texto: 'Construcción' },
    { valor: 'JARDIN', texto: 'Jardín' },
    { valor: 'MEDICION', texto: 'Medición' }
  ];
  filtro = signal<Filtro>('TODAS');

  herramientasFiltradas = computed(() =>
    this.filtro() === 'TODAS'
      ? this.herramientas()
      : this.herramientas().filter(h => h.categoria === this.filtro())
  );

  constructor() {
    this.herramientasService.cargar();
  }

  alternarEstado(herramienta: Herramienta) {
    this.herramientasService.cambiarEstado(herramienta, !herramienta.activa);
  }
}