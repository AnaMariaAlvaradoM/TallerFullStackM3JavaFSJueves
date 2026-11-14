

export interface DatosAlquiler {
    herramientaId:string,
    fechaInicio: string,
    fechaFin: string;
}


 function  dia(fecha: string): Date{
        return new Date(fecha);
    }

    

export function hoyComoTexto(): string {
    const hoy = new Date();
    const mes = String(hoy.getMonth()+1).padStart(2,'0');
    const dia = String(hoy.getDate()).padStart(2,'0');
    return `${hoy.getFullYear()}-${mes}-${dia}`;
}



export function calcularDias(fechaInicio: string, fechaFin: string): number{
    if(!fechaInicio || !fechaFin){
        return 0;
    }

    if(fechaFin < fechaInicio){
        return 0;
    }
    const dias = ((Number(dia(fechaFin)) - Number(dia(fechaInicio)))/(1000 * 60 * 60 * 24))+1;

    return dias;
}

   

export function validarAlquiler( { fechaInicio, fechaFin }: { fechaInicio: string; fechaFin: string }, hoy: string): string[]{
    const errores: string[] = [];

    if(!fechaInicio){
        errores.push('Escoge fecha de inicio del alquiler');
    }
    if(!fechaFin){
        errores.push('Escoge fecha de fin del alquiler');
    }
    if(errores.length >0){
        return errores;
    }

    if(dia(fechaFin)<=dia(fechaInicio)){
        errores.push('La fecha de fin debe ser posterior a la fecha de inicio del alquiler');
    }
    if(calcularDias(fechaInicio,fechaFin) >15){
        errores.push('Solo se pueden alquilar herramientas por máximo 15 días');
    }
    if(fechaInicio < hoy){
        errores.push('"No se puede hacer un alquiler en un día que ya pasó"')
    }

    return errores;
}


