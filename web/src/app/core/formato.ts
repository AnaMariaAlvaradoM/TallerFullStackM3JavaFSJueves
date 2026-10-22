const formatoPesos = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0
});

// 25000 -> "$ 25.000"
export function pesos(valor: number): string {
  return formatoPesos.format(valor);
}

const formatoFecha = new Intl.DateTimeFormat('es-CO', {
  day: 'numeric',
  month: 'short',
  year: 'numeric',
  timeZone: 'UTC'
});

// '2026-11-10' -> "10 de nov de 2026" (se lee como fecha, sin correr el día por la zona horaria)
export function fechaCorta(fecha: string): string {
  return formatoFecha.format(new Date(fecha.slice(0, 10) + 'T00:00:00Z'));
}
