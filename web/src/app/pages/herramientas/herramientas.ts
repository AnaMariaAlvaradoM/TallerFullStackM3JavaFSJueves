import { Component, computed, inject, signal } from '@angular/core';
import { Herramienta, HerramientasService, NOMBRE_TIPO, TipoHerramienta } from '../../services/herramientas.service';
import { AuthService } from '../../core/auth.service';
import { pesos } from '../../core/formato';
import { SiPuede } from '../../shared/si-puede';

type Filtro = TipoHerramienta | 'TODOS';

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

  nombreTipo = NOMBRE_TIPO;
  pesos = pesos;

  gestiona = computed(() => this.authService.puede('HERRAMIENTAS_GESTIONAR'));
  inactivos = computed(() => this.herramientas().filter(e => !e.activa).length);

  filtros: { valor: Filtro; texto: string }[] = [
    { valor: 'TODOS', texto: 'Todos' },
    { valor: 'ELECTRICA', texto: 'Eléctrica' },
    { valor: 'CONSTRUCCION', texto: 'Construcción' },
    { valor: 'JARDIN', texto: 'Jardín' },
    { valor: 'MEDICION', texto: 'Medición' }
  ];
  filtro = signal<Filtro>('TODOS');

  herramientasFiltrados = computed(() =>
    this.filtro() === 'TODOS'
      ? this.herramientas()
      : this.herramientas().filter(e => e.categoria === this.filtro())
  );

  constructor() {
    this.herramientasService.cargar();
  }

  alternarEstado(herramienta: Herramienta) {
    this.herramientasService.cambiarEstado(herramienta, !herramienta.activa);
  }
}
