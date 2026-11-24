import { Component, inject, OnInit } from '@angular/core';
import { HerramientasService } from '../../services/herramientas.service';
import { pesos } from '../../core/formato';
import { SiPuede } from '../../shared/si-puede';

@Component({
  selector: 'app-herramientas',
  standalone: true,
  imports: [SiPuede],
  templateUrl: './herramientas.html'
})
export class Herramientas implements OnInit {
  herramientasService = inject(HerramientasService);
  formatoPesos = pesos;

  ngOnInit() {
    this.herramientasService.cargar();
  }
}