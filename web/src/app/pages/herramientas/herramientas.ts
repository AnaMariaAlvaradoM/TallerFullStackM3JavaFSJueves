import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HerramientasService } from '../../services/herramientas.service';
import { SiPuede } from '../../shared/si-puede';
import { pesos } from '../../core/formato';

@Component({
  selector: 'app-herramientas',
  imports: [SiPuede, RouterLink],
  templateUrl: './herramientas.html',
  styleUrl: './herramientas.css',
})
export class Herramientas implements OnInit {
  readonly servicio = inject(HerramientasService);
  readonly pesos = pesos; // para usar pesos(...) en el HTML

  ngOnInit(): void {
    this.servicio.cargar();
  }
}
