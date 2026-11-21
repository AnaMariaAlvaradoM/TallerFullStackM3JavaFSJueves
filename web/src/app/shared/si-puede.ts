import { Directive, TemplateRef, ViewContainerRef, effect, inject, input } from '@angular/core';
import { AuthService } from '../core/auth.service';
import { Permiso } from '../core/permisos';


@Directive({
  selector: '[appSiPuede]',
})
export class SiPuede {
  private authService = inject(AuthService);
  private plantilla = inject(TemplateRef);
  private contenedor = inject(ViewContainerRef);

  appSiPuede = input.required<Permiso>();
  


  private dibujado = false;

  constructor() {
    effect(() => {
      const puede = this.authService.puede(this.appSiPuede());

      if (puede && !this.dibujado) {
        this.contenedor.createEmbeddedView(this.plantilla);
        this.dibujado = true;
      } else if (!puede && this.dibujado) {
        this.contenedor.clear();
        this.dibujado = false;
      }
    });
  }
}