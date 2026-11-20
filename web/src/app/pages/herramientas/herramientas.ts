import { Component, computed, inject, signal } from '@angular/core';
import { AuthService } from '../../core/auth.service';
import { pesos } from '../../core/formato';
import { SiPuede } from '../../Shared/si-puede';
import {
  Categoria,
  Herramienta,
  HerramientasService,
  NOMBRE_CATEGORIA
} from '../../services/herramientas.service';


type Filtro = Categoria | 'TODOS';

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
    { valor: 'TODOS', texto: 'Todas' },
    { valor: 'ELECTRICA', texto: 'Eléctrica' },
    { valor: 'CONSTRUCCION', texto: 'Construcción' },
    { valor: 'JARDIN', texto: 'Jardín' },
    { valor: 'MEDICION', texto: 'Medición' }
  ];

  filtro = signal<Filtro>('TODOS');

  herramientasFiltradas = computed(() =>
    this.filtro() === 'TODOS'
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
