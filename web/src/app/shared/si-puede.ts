import { Directive, TemplateRef, ViewContainerRef, effect, inject, input } from '@angular/core';

import { AuthService } from '../core/auth.service';
import { Permiso } from '../core/permisos';

@Directive({
  selector: '[appSiPuede]',
  standalone: true
})
export class SiPuede {
  private readonly auth = inject(AuthService);
  private readonly templateRef = inject(TemplateRef<unknown>);
  private readonly viewContainer = inject(ViewContainerRef);

  readonly appSiPuede = input.required<Permiso>();
  private mostrada = false;

  constructor() {
    effect(() => {
      const permisoRequerido = this.appSiPuede();
      const permitido = this.auth.puede(permisoRequerido);

      if (permitido && !this.mostrada) {
        this.viewContainer.createEmbeddedView(this.templateRef);
        this.mostrada = true;
      } else if (!permitido && this.mostrada) {
        this.viewContainer.clear();
        this.mostrada = false;
      }
    });
  }
}