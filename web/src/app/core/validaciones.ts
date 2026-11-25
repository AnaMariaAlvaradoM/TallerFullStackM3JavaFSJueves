export interface RangoFechas {
  fechaInicio?: string | null;
  fechaFin?: string | null;
}

export interface DatosCuentaValidacion {
  nombre?: string | null;
  email?: string | null;
  password?: string | null;
}

function parsearDiaUtc(fechaIso: string): number | null {
  const match = /^(\d{4})-(\d{2})-(\d{2})$/.exec(fechaIso.trim());
  if (!match) {
    return null;
  }

  const anio = Number(match[1]);
  const mes = Number(match[2]);
  const dia = Number(match[3]);

  const fecha = new Date(Date.UTC(anio, mes - 1, dia));
  if (
    fecha.getUTCFullYear() !== anio ||
    fecha.getUTCMonth() !== mes - 1 ||
    fecha.getUTCDate() !== dia
  ) {
    return null;
  }

  return Math.floor(Date.UTC(anio, mes - 1, dia) / 86_400_000);
}

export function calcularDias(fechaInicio?: string | null, fechaFin?: string | null): number {
  if (!fechaInicio || !fechaFin) {
    return 0;
  }

  const inicioDia = parsearDiaUtc(fechaInicio);
  const finDia = parsearDiaUtc(fechaFin);

  if (inicioDia === null || finDia === null || finDia < inicioDia) {
    return 0;
  }

  return finDia - inicioDia + 1;
}

export function validarAlquiler(rango: RangoFechas, hoy: string): string[] {
  const errores: string[] = [];
  const inicioTexto = (rango.fechaInicio ?? '').trim();
  const finTexto = (rango.fechaFin ?? '').trim();

  if (!inicioTexto) {
    errores.push('La fecha de inicio es obligatoria.');
  }
  if (!finTexto) {
    errores.push('La fecha de fin es obligatoria.');
  }
  if (errores.length > 0) {
    return errores;
  }

  const inicioDia = parsearDiaUtc(inicioTexto);
  const finDia = parsearDiaUtc(finTexto);
  const hoyDia = parsearDiaUtc(hoy);

  if (inicioDia === null || finDia === null) {
    errores.push('Las fechas deben tener el formato AAAA-MM-DD.');
    return errores;
  }

  if (finDia < inicioDia) {
    errores.push('La fecha de fin no puede ser anterior a la fecha de inicio.');
    return errores;
  }

  if (hoyDia !== null && inicioDia < hoyDia) {
    errores.push('La fecha de inicio no puede ser anterior a hoy.');
    return errores;
  }

  const dias = finDia - inicioDia + 1;
  if (dias > 15) {
    errores.push('El alquiler no puede superar los 15 días seguidos.');
    return errores;
  }

  return errores;
}

export function validarCuenta(datos: DatosCuentaValidacion, confirmacion?: string | null): string[] {
  const errores: string[] = [];
  const nombre = (datos.nombre ?? '').trim();
  const email = (datos.email ?? '').trim();
  const password = datos.password ?? '';
  const conf = confirmacion ?? '';

  if (!nombre) {
    errores.push('El nombre es obligatorio.');
  } else if (nombre.length > 80) {
    errores.push('El nombre no puede superar los 80 caracteres.');
  }

  const emailValido = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  if (!email || !emailValido) {
    errores.push('Ingresa un correo electrónico válido.');
  }

  if (password.length < 8) {
    errores.push('La contraseña debe tener al menos 8 caracteres.');
  }

  if (password !== conf) {
    errores.push('La confirmación de contraseña no coincide.');
  }

  return errores;
}