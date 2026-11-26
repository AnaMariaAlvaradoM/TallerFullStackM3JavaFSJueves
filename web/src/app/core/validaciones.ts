const MAX_DIAS = 15;
const MS_POR_DIA = 24 * 60 * 60 * 1000;

/** Convierte 'AAAA-MM-DD' en un número de día (sin zonas horarias). */
function aNumeroDeDia(fecha: string): number {
  const [anio, mes, dia] = fecha.split('-').map(Number);
  return Date.UTC(anio, mes - 1, dia) / MS_POR_DIA;
}

/** Días contando el primero y el último. 0 si falta una fecha o están invertidas. */
export function calcularDias(fechaInicio: string, fechaFin: string): number {
  if (!fechaInicio || !fechaFin) return 0;

  const inicio = aNumeroDeDia(fechaInicio);
  const fin = aNumeroDeDia(fechaFin);

  if (fin < inicio) return 0;
  return fin - inicio + 1;
}

/** Lista de errores; vacía si se puede enviar. */
export function validarAlquiler(
  { fechaInicio, fechaFin }: { fechaInicio: string; fechaFin: string },
  hoy: string
): string[] {
  const errores: string[] = [];

  // 1. Un error por cada fecha que falte (si falta alguna, no revisa lo demás)
  if (!fechaInicio) errores.push('La fecha de inicio es obligatoria');
  if (!fechaFin) errores.push('La fecha de fin es obligatoria');
  if (errores.length > 0) return errores;

  const inicio = aNumeroDeDia(fechaInicio);
  const fin = aNumeroDeDia(fechaFin);

  // 2. Fin antes de inicio
  if (fin < inicio) {
    errores.push('La fecha de fin no puede ser anterior a la de inicio');
  }

  // 3. Inicio antes de hoy
  if (inicio < aNumeroDeDia(hoy)) {
    errores.push('No puedes alquilar en una fecha que ya pasó');
  }

  // 4. Más de 15 días
  if (calcularDias(fechaInicio, fechaFin) > MAX_DIAS) {
    errores.push(`El alquiler es máximo de ${MAX_DIAS} días`);
  }

  return errores;
}