import { Directive, TemplateRef, ViewContainerRef, effect, inject, input } from '@angular/core';
import { AuthService } from '../core/auth.service';
import { Permiso } from '../core/permisos';

// Directiva estructural: dibuja su contenido SOLO si la persona tiene el permiso.
// Uso: <button *appSiPuede="'ESPACIOS_GESTIONAR'">Desactivar</button>
@Directive({
  selector: '[appSiPuede]',
})
export class SiPuede {
  private authService = inject(AuthService);
  private plantilla = inject(TemplateRef);
  private contenedor = inject(ViewContainerRef);

  permiso = input.required<Permiso>({ alias: 'appSiPuede' });

  private dibujado = false;

  constructor() {
    effect(() => {
      const puede = this.authService.puede(this.permiso());

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