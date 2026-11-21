export function calcularDias(fechaInicio: string, fechaFin: string): number {
  if (!fechaInicio || !fechaFin) return 0;
  const [a1, m1, d1] = fechaInicio.split('-').map(Number);
  const [a2, m2, d2] = fechaFin.split('-').map(Number);
  if (!a1 || !m1 || !d1 || !a2 || !m2 || !d2) return 0;
  const utc1 = Date.UTC(a1, m1 - 1, d1);
  const utc2 = Date.UTC(a2, m2 - 1, d2);
  if (utc2 < utc1) return 0;
  const msPorDia = 24 * 60 * 60 * 1000;
  return Math.round((utc2 - utc1) / msPorDia) + 1;
}

export function validarAlquiler(
  datos: { fechaInicio: string; fechaFin: string },
  hoy: string
): string[] {
  const errores: string[] = [];
  if (!datos.fechaInicio) {
    errores.push('Falta la fecha de inicio');
  }
  if (!datos.fechaFin) {
    errores.push('Falta la fecha de fin');
  }
  if (errores.length > 0) return errores;

  const [ah, mh, dh] = hoy.split('-').map(Number);
  const [a1, m1, d1] = datos.fechaInicio.split('-').map(Number);
  const [a2, m2, d2] = datos.fechaFin.split('-').map(Number);
  const utcHoy = Date.UTC(ah, mh - 1, dh);
  const utcInicio = Date.UTC(a1, m1 - 1, d1);
  const utcFin = Date.UTC(a2, m2 - 1, d2);

  if (utcFin < utcInicio) {
    errores.push('La fecha de fin no puede ser anterior a la de inicio');
  }
  if (utcInicio < utcHoy) {
    errores.push('La fecha de inicio ya pasó');
  }

  const dias = calcularDias(datos.fechaInicio, datos.fechaFin);
  if (dias > 15) {
    errores.push('El alquiler no puede superar los 15 días máximos permitidos');
  }

  return errores;
}

export function validarCuenta(
  datos: { nombre?: string; email?: string; password?: string },
  confirmacion?: string
): string[] {
  const errores: string[] = [];
  if (!datos.nombre || datos.nombre.trim() === '') {
    errores.push('El nombre es obligatorio');
  }
  if (!datos.email || !datos.email.includes('@')) {
    errores.push('El email debe tener un formato válido con @');
  }
  if (!datos.password || datos.password.length < 8) {
    errores.push('La contraseña debe tener al menos 8 caracteres');
  }
  if (datos.password !== confirmacion) {
    errores.push('La confirmación de la contraseña no coincide');
  }
  return errores;
}
