// 4.6 · El build de producción termina limpio: sin errores y sin pasarse del presupuesto.
import { before, it } from 'node:test';
import assert from 'node:assert/strict';
import { spawnSync } from 'node:child_process';
import { existsSync, rmSync } from 'node:fs';
import { join } from 'node:path';
import { APP, RAIZ, archivos } from '../utilidades-estructura.mjs';

const WEB = join(RAIZ, 'web');
const SALIDA = join(RAIZ, '.calificacion-tmp', 'build-web');
let resultado = { status: 1, stdout: '', stderr: '' };

before(() => {
  const ng = join(WEB, 'node_modules', '@angular', 'cli', 'bin', 'ng.js');
  assert.ok(existsSync(ng), 'No encuentro Angular CLI: ejecuta npm install dentro de web/');
  rmSync(SALIDA, { recursive: true, force: true });
  resultado = spawnSync(process.execPath, [ng, 'build', '--configuration', 'production', '--output-path', SALIDA], {
    cwd: WEB,
    encoding: 'utf8',
    timeout: 220000,
    maxBuffer: 32 * 1024 * 1024,
    env: { ...process.env, NG_CLI_ANALYTICS: 'false', FORCE_COLOR: '0', CI: 'true' },
  });
});

function salida() {
  return `${resultado.stdout || ''}\n${resultado.stderr || ''}`.replace(/\x1b\[[0-9;]*m/g, '');
}

function primeras(lineas, patron) {
  return salida().split(/\r?\n/).filter((l) => patron.test(l)).slice(0, lineas).join(' | ');
}

function hayPaginas() {
  const paginas = archivos(join(APP, 'pages'), '.ts');
  assert.ok(paginas.length >= 10, 'Todavía no encuentro tus páginas en web/src/app/pages/: esta parte compila el frontend completo del taller');
}

it('ng build termina sin errores', () => {
  hayPaginas();
  assert.equal(resultado.status, 0, `ng build falló: ${primeras(4, /ERROR|error/) || salida().slice(-400)}`);
});

it('ng build no tiene advertencias de presupuesto ni de otro tipo', () => {
  hayPaginas();
  assert.equal(resultado.status, 0, 'Primero haz que ng build termine sin errores');
  const advertencias = primeras(4, /WARNING|budget|exceeded/i);
  assert.equal(advertencias, '', `ng build terminó con advertencias: ${advertencias}`);
});
