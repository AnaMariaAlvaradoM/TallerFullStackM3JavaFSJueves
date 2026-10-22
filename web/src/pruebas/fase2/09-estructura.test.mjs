// Pruebas de estructura de la Fase 2: rutas, capas y la matriz de pruebas.
import { it } from 'node:test';
import assert from 'node:assert/strict';
import { APP, JAVA, archivosCon, leer, leerJava, leerRaiz } from '../utilidades-estructura.mjs';

it('las rutas de alquilar y mis alquileres piden su permiso', () => {
  const rutas = leer('app.routes.ts');
  assert.match(rutas, /path:\s*'herramientas\/:id\/alquilar'[\s\S]*?permisoGuard\(\s*'ALQUILERES_CREAR'\s*\)[\s\S]*?loadComponent/,
    "Falta la ruta 'herramientas/:id/alquilar' con permisoGuard('ALQUILERES_CREAR') y loadComponent");
  assert.match(rutas, /path:\s*'mis-alquileres'[\s\S]*?permisoGuard\(\s*'ALQUILERES_VER_PROPIOS'\s*\)[\s\S]*?loadComponent/,
    "Falta la ruta 'mis-alquileres' con permisoGuard('ALQUILERES_VER_PROPIOS') y loadComponent");
});

it('el :id de la URL llega como input (withComponentInputBinding)', () => {
  assert.match(leer('app.config.ts'), /withComponentInputBinding\s*\(\s*\)/, 'app.config.ts debería tener provideRouter(routes, withComponentInputBinding())');
});

it('la página Alquilar valida con validarAlquiler antes de enviar', () => {
  assert.match(leer('pages/alquilar/alquilar.ts'), /validarAlquiler\s*\(/, 'alquilar.ts debería usar validarAlquiler(...) de core/validaciones.ts');
});

it('el menú tiene el enlace a Mis alquileres', () => {
  assert.match(leer('app.ts') + leer('app.html'), /mis-alquileres/, 'El menú debería tener un enlace a /mis-alquileres con el permiso ALQUILERES_VER_PROPIOS');
});

it('nada de window.confirm ni alert: las confirmaciones van en la página', () => {
  assert.ok(leer('pages/mis-alquileres/mis-alquileres.ts'), 'Aún no existe pages/mis-alquileres/mis-alquileres.ts para revisar');
  const usan = archivosCon(APP, '.ts', /\b(window\.)?(confirm|alert)\s*\(/);
  assert.deepEqual(usan, [], `Cambia confirm()/alert() por una confirmación dentro de la página en: ${usan.join(', ')}`);
});

it('la regla "solo cancelas lo tuyo" vive en el servicio, no en el controlador', () => {
  const servicio = archivosCon(`${JAVA}/service`, '.java', /AccessDeniedException/);
  assert.ok(servicio.length > 0, 'La verificación de dueño (AccessDeniedException) debería estar en un servicio (service/impl)');
  assert.match(leerJava('controller/AlquilerController.java'), /hasAnyAuthority\(/, 'AlquilerController.cancelar debería usar hasAnyAuthority(...) con los dos permisos de cancelar');
});

it('PRUEBAS.md tiene al menos 12 casos con resultado esperado y obtenido', () => {
  const matriz = leerRaiz('PRUEBAS.md');
  assert.ok(matriz, 'Falta PRUEBAS.md en la raíz del repo');
  const filas = matriz.split(/\r?\n/).filter((linea) => /^\|\s*\d+\s*\|/.test(linea));
  const completas = filas.filter((fila) => {
    const celdas = fila.split('|').slice(1, -1).map((c) => c.trim());
    return celdas.length >= 6 && celdas.every((c) => c.length > 0);
  });
  assert.ok(completas.length >= 12, `PRUEBAS.md debería tener 12 filas completas (todas las columnas llenas). Encontré ${completas.length}`);
});
