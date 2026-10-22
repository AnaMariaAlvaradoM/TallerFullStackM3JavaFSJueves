import { calcularDias, validarAlquiler } from '../../app/core/validaciones';

const HOY = '2026-11-10';

describe('2.5 · Validación del formulario (validaciones.ts)', () => {
  it('calcularDias cuenta el primer y el último día', () => {
    expect(calcularDias('2026-11-10', '2026-11-12'), "calcularDias('2026-11-10', '2026-11-12') debería ser 3").toBe(3);
    expect(calcularDias('2026-11-10', '2026-11-10'), 'Inicio y fin el mismo día = 1 día').toBe(1);
  });

  it('calcularDias funciona al cambiar de mes y de año', () => {
    expect(calcularDias('2026-02-27', '2026-03-02'), 'Del 27 de febrero al 2 de marzo de 2026 son 4 días').toBe(4);
    expect(calcularDias('2026-12-30', '2027-01-02'), 'Del 30 de diciembre al 2 de enero son 4 días').toBe(4);
  });

  it('calcularDias devuelve 0 si falta una fecha o están invertidas', () => {
    expect(calcularDias('', '2026-11-12'), 'Sin fecha de inicio, 0 días').toBe(0);
    expect(calcularDias('2026-11-12', '2026-11-10'), 'Con las fechas invertidas, 0 días').toBe(0);
  });

  it('un alquiler válido no tiene errores', () => {
    expect(validarAlquiler({ fechaInicio: '2026-11-12', fechaFin: '2026-11-14' }, HOY), 'Fechas válidas → lista vacía []').toEqual([]);
  });

  it('empezar hoy y alquilar 15 días exactos es válido', () => {
    expect(validarAlquiler({ fechaInicio: HOY, fechaFin: '2026-11-24' }, HOY), 'Del 10 al 24 son 15 días: es el límite, sin errores (revisa > frente a >=)').toEqual([]);
  });

  it('pide las dos fechas', () => {
    expect(validarAlquiler({ fechaInicio: '', fechaFin: '' }, HOY).length, 'Sin fechas debería haber 2 errores (inicio y fin)').toBe(2);
    expect(validarAlquiler({ fechaInicio: '2026-11-12', fechaFin: '' }, HOY).length, 'Sin fecha de fin debería haber 1 error').toBe(1);
  });

  it('detecta la fecha de fin antes de la de inicio', () => {
    expect(validarAlquiler({ fechaInicio: '2026-11-14', fechaFin: '2026-11-12' }, HOY).length, 'Fin antes de inicio debería dar 1 error').toBe(1);
  });

  it('detecta una fecha de inicio que ya pasó', () => {
    expect(validarAlquiler({ fechaInicio: '2026-11-09', fechaFin: '2026-11-11' }, HOY).length, 'Empezar ayer debería dar 1 error').toBe(1);
  });

  it('detecta más de 15 días y lo dice en el mensaje', () => {
    const errores = validarAlquiler({ fechaInicio: '2026-11-10', fechaFin: '2026-11-25' }, HOY);
    expect(errores.length, 'Del 10 al 25 son 16 días: debería dar 1 error').toBe(1);
    expect(errores[0], 'El mensaje debería mencionar el máximo (15)').toContain('15');
  });
});
