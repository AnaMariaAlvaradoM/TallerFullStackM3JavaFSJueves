import { Directive, effect, inject, input, TemplateRef, ViewContainerRef } from '@angular/core';
import { AuthService } from '../core/auth.service';
import { Permiso } from '../core/permisos';

@Directive({
  selector: '[appSiPuede]',
  standalone: true
})
export class SiPuede {
  appSiPuede = input.required<Permiso | string>();

  private auth = inject(AuthService);
  private tpl = inject(TemplateRef<unknown>);
  private vcr = inject(ViewContainerRef);
  private visible = false;

  constructor() {
    effect(() => {
      const permiso = this.appSiPuede();
      const puede = this.auth.puede(permiso as Permiso);
      if (puede && !this.visible) {
        this.vcr.createEmbeddedView(this.tpl);
        this.visible = true;
      } else if (!puede && this.visible) {
        this.vcr.clear();
        this.visible = false;
      }
    });
  }
}
