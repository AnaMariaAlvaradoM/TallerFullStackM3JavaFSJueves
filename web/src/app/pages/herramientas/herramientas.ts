import { Component, computed, inject, signal } from '@angular/core';
import { Herramienta, HerramientasService, NOMBRE_CATEGORIA, TipoCategoria } from '../../services/herramientas.service';
import { AuthService } from '../../core/auth.service';
import { pesos } from '../../core/formato';
import { SiPuede } from '../../shared/si-puede';
import { RouterLink } from '@angular/router';

type Filtro = TipoCategoria | 'TODOS';

@Component({
  imports: [SiPuede, RouterLink],
  selector: 'app-herramientas',
  styleUrl: './herramientas.css',
  templateUrl: './herramientas.html',
})


export class Herramientas {
  private herramientasService = inject(HerramientasService);
  private authService = inject(AuthService);

  herramienta = this.herramientasService.herramientas;
  cargando = this.herramientasService.cargando;
  error = this.herramientasService.error;

  nombreCategoria = NOMBRE_CATEGORIA;
  pesos = pesos;

  gestiona = computed(() => this.authService.puede('HERRAMIENTAS_GESTIONAR'));
  inactivos = computed(() => this.herramienta().filter(e => !e.activa).length);

  filtros: { valor: Filtro ;texto: string }[] = [
  { valor: 'TODOS', texto: 'Todos' },
  { valor: 'ELECTRICA', texto: 'Eléctrica' },
  { valor: 'CONSTRUCCION', texto: 'Construcción' },
  { valor: 'JARDIN', texto: 'Jardín' },
  { valor: 'MEDICION', texto: 'Medición' }
];

  filtro = signal<Filtro>('TODOS');

  herramientasFiltradas = computed(() =>
    this.filtro() === 'TODOS'
      ? this.herramienta()
      : this.herramienta().filter(e => e.categoria === this.filtro())
  );

  constructor() {
    this.herramientasService.cargar();
  }

  alternarEstado(herramienta: Herramienta) {
    this.herramientasService.cambiarEstado(herramienta, !herramienta.activa);
  }
}
