// 4.5 · Optimización y limpieza del frontend.
import { it } from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { relative } from 'node:path';
import { APP, RAIZ, archivos, archivosCon, leer } from '../utilidades-estructura.mjs';

function nombre(ruta) {
  return relative(RAIZ, ruta).split('\\').join('/');
}

function plantillas() {
  const lista = archivos(APP, '.html');
  assert.ok(lista.length >= 8, 'No encuentro las plantillas .html de tus páginas en web/src/app/');
  return lista;
}

it('todas las páginas se cargan con loadComponent (lazy loading)', () => {
  const rutas = leer('app.routes.ts');
  assert.ok(rutas, 'Falta web/src/app/app.routes.ts');
  assert.doesNotMatch(rutas, /\bcomponent\s*:/, 'app.routes.ts todavía tiene rutas con component: … Cámbialas por loadComponent: () => import(...).then(m => m.Pagina)');
  assert.doesNotMatch(rutas, /import\s*\{[^}]*\}\s*from\s*'\.\/pages\//, 'app.routes.ts no debería importar las páginas arriba: así se cargan todas al abrir la app. Usa loadComponent');
  const cargas = rutas.match(/loadComponent/g) || [];
  assert.ok(cargas.length >= 10, `Encontré ${cargas.length} rutas con loadComponent; el taller tiene al menos 10 páginas`);
});

it('no quedan console.log ni debugger en el frontend', () => {
  plantillas();
  const culpables = archivosCon(APP, '.ts', /\bconsole\.(log|debug|info|warn|error)\s*\(|\bdebugger\b/);
  assert.deepEqual(culpables, [], 'Quita los console.log y debugger de estos archivos: en producción cualquiera los ve en la consola del navegador');
});

it('cada @for tiene un track con identidad (no $index)', () => {
  const malos = [];
  for (const ruta of plantillas()) {
    const html = readFileSync(ruta, 'utf8');
    for (const m of html.matchAll(/@for\s*\(([\s\S]*?)\)\s*\{/g)) {
      if (!/;\s*track\s+/.test(m[1]) || /track\s+\$index\b/.test(m[1])) malos.push(`${nombre(ruta)}: @for (${m[1].trim()})`);
    }
  }
  assert.deepEqual(malos, [], 'Estos @for no tienen un track con identidad. Usa track a.id (o el campo que no se repite): Angular reutiliza los elementos en vez de redibujarlos');
});

it('las imágenes que vienen del backend cargan en diferido (loading="lazy")', () => {
  const malos = [];
  for (const ruta of plantillas()) {
    const html = readFileSync(ruta, 'utf8');
    for (const m of html.matchAll(/<img\b[^>]*>/g)) {
      if (/\[src\]/.test(m[0]) && !/loading\s*=\s*"lazy"/.test(m[0])) malos.push(`${nombre(ruta)}: ${m[0]}`);
    }
  }
  assert.deepEqual(malos, [], 'Estas imágenes vienen de una URL del backend y no tienen loading="lazy": el navegador las descarga aunque no estén en pantalla');
});

it('todas las imágenes tienen texto alternativo (alt)', () => {
  const malos = [];
  for (const ruta of plantillas()) {
    const html = readFileSync(ruta, 'utf8');
    for (const m of html.matchAll(/<img\b[^>]*>/g)) {
      if (!/\s\[?alt\]?\s*=/.test(m[0])) malos.push(`${nombre(ruta)}: ${m[0]}`);
    }
  }
  assert.deepEqual(malos, [], 'Estas imágenes no tienen alt. Si es decorativa, alt=""; si muestra algo, descríbelo');
});

it('las páginas no llaman al backend: eso lo hacen los servicios', () => {
  const paginas = archivos(`${APP}/pages`, '.ts');
  assert.ok(paginas.length >= 10, 'No encuentro tus páginas en web/src/app/pages/');
  const culpables = archivosCon(`${APP}/pages`, '.ts', /\bHttpClient\b/);
  assert.deepEqual(culpables, [], 'Estas páginas usan HttpClient directamente. La página muestra y reacciona; el servicio habla con el backend (igual que controlador → servicio en Spring)');
});
