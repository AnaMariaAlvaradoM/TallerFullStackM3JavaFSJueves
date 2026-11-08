// Las mismas reglas de fechas del backend, para avisar ANTES de enviar.
// Las fechas son textos 'AAAA-MM-DD', como los entrega un <input type="date">.

export const MAXIMO_DIAS = 15;

const MS_POR_DIA = 24 * 60 * 60 * 1000;

// '2026-11-10' -> número de día. Con Date.UTC no hay líos de zona horaria ni de horario de verano.
function numeroDeDia(fecha: string): number {
  const [anio, mes, dia] = fecha.split('-').map(Number);
  return Date.UTC(anio, mes - 1, dia) / MS_POR_DIA;
}

// Días contando el primero y el último. 0 si falta una fecha o están invertidas.
export function calcularDias(fechaInicio: string, fechaFin: string): number {
  if (!fechaInicio || !fechaFin) return 0;
  const dias = numeroDeDia(fechaFin) - numeroDeDia(fechaInicio) + 1;
  return dias > 0 ? dias : 0;
}

// Devuelve la lista de errores; vacía si se puede enviar
export function validarAlquiler(datos: { fechaInicio: string; fechaFin: string }, hoy: string): string[] {
  const errores: string[] = [];

  // 1. Las dos fechas son obligatorias (si falta alguna, no se revisa lo demás)
  if (!datos.fechaInicio) errores.push('Elige la fecha de inicio.');
  if (!datos.fechaFin) errores.push('Elige la fecha de fin.');
  if (errores.length > 0) return errores;

  // 2. Fin antes de inicio
  if (numeroDeDia(datos.fechaFin) < numeroDeDia(datos.fechaInicio)) {
    errores.push('La fecha de fin no puede ser anterior a la de inicio.');
  }

  // 3. Inicio antes de hoy (hoy sí se puede)
  if (numeroDeDia(datos.fechaInicio) < numeroDeDia(hoy)) {
    errores.push('La fecha de inicio ya pasó: elige hoy o una fecha futura.');
  }

  // 4. Más de 15 días (15 exactos sí se puede)
  if (calcularDias(datos.fechaInicio, datos.fechaFin) > MAXIMO_DIAS) {
    errores.push(`El alquiler máximo es de ${MAXIMO_DIAS} días.`);
  }

  return errores;
}

// Hoy como 'AAAA-MM-DD' en la hora local (igual que un <input type="date">)
export function fechaDeHoy(): string {
  const hoy = new Date();
  const mes = String(hoy.getMonth() + 1).padStart(2, '0');
  const dia = String(hoy.getDate()).padStart(2, '0');
  return `${hoy.getFullYear()}-${mes}-${dia}`;
}
