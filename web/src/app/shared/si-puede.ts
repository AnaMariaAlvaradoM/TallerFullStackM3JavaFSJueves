import { Directive,effect,inject,input,TemplateRef,ViewContainerRef } from "@angular/core";
import { AuthService } from "../core/auth.service";
import { Permiso } from "../core/permisos";


// Hace lo mismo que puede(permiso), decide si un btón pertence a la experiencia
// Crea o retira un fragmento de la plantilla, y entrega a botones y contenido visible

@Directive({
    selector: '[appSiPuede]', //cualquier elemento se activa con ese atributo
})

export class SiPuede{
private authService = inject(AuthService);
private plantilla = inject(TemplateRef);
private contenedor = inject(ViewContainerRef);

permiso = input.required<Permiso>({alias: 'appSiPuede'});
private dibujado = false;

    constructor(){
        effect(() => {
            
            const puede = this.authService.puede(this.permiso());
            if(puede && !this.dibujado){
                this.contenedor.createEmbeddedView(this.plantilla); //dibuja el contenido
                this.dibujado = true;
            } else if(!puede && this.dibujado){
                this.contenedor.clear(); //lo quita del dom
                this.dibujado = false;
            }
        })
    }
}
