// 4.8 · Bitácora de IA: qué le pediste, qué aceptaste y cómo lo comprobaste.
import { it } from 'node:test';
import assert from 'node:assert/strict';
import { leerRaiz } from '../utilidades-estructura.mjs';

function filas() {
  const texto = leerRaiz('BITACORA-IA.md');
  assert.ok(texto, 'Falta BITACORA-IA.md en la raíz del repo');
  return texto
    .split(/\r?\n/)
    .filter((l) => /^\s*\|/.test(l))
    .map((l) => l.trim().replace(/^\||\|$/g, '').split('|').map((c) => c.trim()))
    .filter((celdas) => !celdas.every((c) => /^:?-{3,}:?$/.test(c) || c === ''))
    .filter((celdas) => !/^fecha$/i.test(celdas[0]));
}

it('tiene al menos 4 entradas', () => {
  const llenas = filas().filter((c) => c.some((x) => x !== ''));
  assert.ok(llenas.length >= 4, `BITACORA-IA.md tiene ${llenas.length} entrada(s); deberían ser al menos 4 (una fila por cada vez que usaste IA)`);
});

it('cada entrada tiene las 7 columnas llenas', () => {
  const llenas = filas().filter((c) => c.some((x) => x !== ''));
  assert.ok(llenas.length >= 1, 'BITACORA-IA.md todavía no tiene entradas');
  const incompletas = llenas
    .filter((c) => c.length < 7 || c.slice(0, 7).some((x) => x === ''));
  assert.equal(incompletas.length, 0, `${incompletas.length} entrada(s) de BITACORA-IA.md tienen columnas vacías. Llena las 7: fecha, fase, qué intentabas, prompt, qué aceptaste o rechazaste, cómo lo comprobaste y qué aprendiste`);
});

it('al menos una entrada es de la Fase 4', () => {
  const deLaCuatro = filas().filter((c) => /\b4\b/.test(c[1] || ''));
  assert.ok(deLaCuatro.length >= 1, 'BITACORA-IA.md debería tener al menos una entrada de la Fase 4 (por ejemplo, el borrador del README o el refactor)');
});
