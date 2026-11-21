import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { pesos } from '../../core/formato';
import { Herramienta, HerramientasService } from '../../services/herramientas.service';
import { SiPuede } from '../../shared/si-puede';

@Component({
  selector: 'app-herramientas',
  standalone: true,
  imports: [RouterLink, SiPuede],
  templateUrl: './herramientas.html',
})
export class Herramientas implements OnInit {
  herramientasService = inject(HerramientasService);

  formatearPesos = pesos;

  ngOnInit(): void {
    this.herramientasService.cargar();
  }

  cambiarEstado(herramienta: Herramienta): void {
    this.herramientasService.cambiarEstado(herramienta, !herramienta.activa);
  }
}
