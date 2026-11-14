import { Component, computed, inject, input, OnInit, Signal, signal } from '@angular/core';
import { DiasAlquilada, Herramienta,HerramientasService } from '../../services/herramientas.service';
import { AlquileresService } from '../../services/alquileres.service';
import { Router, RouterLink } from '@angular/router';
import { calcularDias, hoyComoTexto, validarAlquiler } from '../../core/validaciones';
import { pesos } from '../../core/formato';
import { HttpErrorResponse } from '@angular/common/http';
import { mensajeDeError } from '../../core/errores';



@Component({
  imports: [RouterLink],
  selector: 'app-alquilar',
  styleUrl: './alquilar.css',
  templateUrl: './alquilar.html',
})
export class Alquilar implements OnInit {
  private herramientaService = inject(HerramientasService);
  private alquilerService = inject(AlquileresService)
  private router = inject(Router);

  id = input.required<string>();


  herramienta = signal<Herramienta | null>(null);
  errorCarga = signal<string | null>(null);
  alquiladas = signal<DiasAlquilada[]>([]);

  fechaInicio = signal('');
  fechaFin = signal('');
  errores = signal<string[]>([]);
  errorServidor = signal<string | null>(null);
  enviando = signal(false);

  
  hoy = hoyComoTexto();
  pesos = pesos;

  dias = computed(()=> calcularDias(this.fechaInicio(),this.fechaFin()));
  valor = computed(()=> Math.round(this.dias() * (this.herramienta()?.precioDia ??0)));
  
  ngOnInit(){
    this.herramientaService.obtener(Number(this.id())).subscribe({
      next: (herramienta) => this.herramienta.set(herramienta),
      error:(error: HttpErrorResponse) => this.errorCarga.set(mensajeDeError(error))
    });    
  }

  actualizar(evento: Event){
    const datos = new FormData(evento.currentTarget as HTMLFormElement);
    this.fechaInicio.set(String(datos.get('fechaInicio') ?? ''));
    this.fechaFin.set(String(datos.get('fechaFin') ?? ''));
  } 

  alquilar(evento:SubmitEvent){
    evento.preventDefault();
    const datos = new FormData(evento.target as HTMLFormElement);
    const alquiler = {
      herramientaId: Number(this.id()),
      fechaInicio: String(datos.get('fechaInicio')??''),
      fechaFin: String(datos.get('fechaFin')?? '')
    };

    this.errorServidor.set(null);
    this.errores.set(validarAlquiler({
      fechaInicio: alquiler.fechaInicio,
      fechaFin: alquiler.fechaFin
    }, hoyComoTexto()));

    if(this.errores().length > 0){
      return;
    }

    this.enviando.set(true);
    this.alquilerService.crear(alquiler).subscribe({
      next: () => {
        this.enviando.set(false);
        this.router.navigateByUrl('/mis-alquileres');
      },
      error: (error:HttpErrorResponse) => {
        this.enviando.set(false);
        this.errorServidor.set(mensajeDeError(error));
      }
    });
  }

  



}
