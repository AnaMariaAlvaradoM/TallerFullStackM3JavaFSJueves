import { Directive, effect, inject, input, TemplateRef, ViewContainerRef } from '@angular/core';
import { AuthService } from '../core/auth.service';
import { Permiso } from '../core/permisos';

// Directiva estructural: <button *appSiPuede="'HERRAMIENTAS_GESTIONAR'">
// Si la sesión tiene ese permiso, dibuja el elemento; si no, lo quita del DOM.
@Directive({
  selector: '[appSiPuede]',
})
export class SiPuede {
  readonly appSiPuede = input.required<Permiso>();

  private readonly plantilla = inject(TemplateRef);
  private readonly contenedor = inject(ViewContainerRef);
  private readonly auth = inject(AuthService);
  private dibujado = false;

  constructor() {
    // effect() se vuelve a ejecutar solo cuando cambia una señal que lee (los permisos de la sesión)
    effect(() => {
      const puede = this.auth.puede(this.appSiPuede());
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
