import { Component,inject,computed,signal } from '@angular/core';
import { SiPuede } from '../../shared/si-puede';
import { RouterLink } from '@angular/router';
import { Categoria, Herramienta, HerramientasService, NOMBRE_CATEGORIA } from '../../services/herramientas.service';
import { AuthService } from '../../core/auth.service';
import { pesos } from '../../core/formato';

type Filtro = Categoria | 'TODAS';
@Component({
  imports: [SiPuede,RouterLink],
  selector: 'app-herramientas',
  styleUrl: './herramientas.css',
  templateUrl: './herramientas.html',
})

export class Herramientas {
  private herramientasService = inject(HerramientasService);
  private authservice = inject(AuthService);

  herramientas = this.herramientasService.herramientas;
  cargando = this.herramientasService.cargando;
  error = this.herramientasService.error;

  nombreCategoria = NOMBRE_CATEGORIA;
  pesos = pesos

  gestiona = computed(()=>this.authservice.puede('HERRAMIENTAS_GESTIONAR'));
  inactivos = computed(()=> this.herramientas().filter(e => !e.activa).length);

  filtro = signal<Filtro>('TODAS')

  herramientasFiltradas = computed(() => 
  this.filtro() == 'TODAS'
  ? this.herramientas()
  : this.herramientas().filter(e => e.categoria == this.filtro())
);
  constructor(){
    this.herramientasService.cargar();
  }
  
  alternarEstado(herramienta: Herramienta){
    this.herramientasService.cambiarEstado(herramienta, !herramienta.activa);
  }
}
