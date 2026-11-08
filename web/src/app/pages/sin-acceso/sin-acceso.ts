import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

// A donde llega quien intenta entrar a una página sin tener el permiso
@Component({
  selector: 'app-sin-acceso',
  imports: [RouterLink],
  templateUrl: './sin-acceso.html',
})
export class SinAcceso {}
