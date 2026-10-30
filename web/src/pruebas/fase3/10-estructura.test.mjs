// Pruebas de estructura de la Fase 3: rutas, menú y dónde vive cada regla.
import { it } from 'node:test';
import assert from 'node:assert/strict';
import { leer, leerJava } from '../utilidades-estructura.mjs';

function ruta(rutas, path) {
  const inicio = rutas.search(new RegExp(`path:\\s*'${path.replace(/[/:]/g, (c) => '\\' + c)}'`));
  if (inicio < 0) return '';
  const fin = rutas.indexOf('}', inicio);
  return rutas.slice(inicio, fin);
}

it('las rutas de Hernán piden su permiso y usan loadComponent', () => {
  const rutas = leer('app.routes.ts');
  const esperadas = [
    ['panel', 'REPORTES_VER'],
    ['alquileres', 'ALQUILERES_VER_TODOS'],
    ['herramientas/nueva', 'HERRAMIENTAS_GESTIONAR'],
    ['herramientas/:id/editar', 'HERRAMIENTAS_GESTIONAR'],
  ];
  for (const [path, permiso] of esperadas) {
    const bloque = ruta(rutas, path);
    assert.ok(bloque, `Falta la ruta '${path}' en app.routes.ts`);
    assert.match(bloque, new RegExp(`permisoGuard\\(\\s*'${permiso}'\\s*\\)`), `La ruta '${path}' debería tener permisoGuard('${permiso}')`);
    assert.match(bloque, /loadComponent/, `La ruta '${path}' debería usar loadComponent`);
  }
});

it('la ruta de registro es pública', () => {
  const bloque = ruta(leer('app.routes.ts'), 'registro');
  assert.ok(bloque, "Falta la ruta 'registro' en app.routes.ts");
  assert.doesNotMatch(bloque, /canActivate/, "La ruta 'registro' es pública: no lleva canActivate (quien se registra todavía no tiene sesión)");
});

it('el menú tiene Panel y Alquileres', () => {
  const menu = leer('app.ts') + leer('app.html');
  assert.match(menu, /\/panel\b/, 'El menú debería tener un enlace a /panel con el permiso REPORTES_VER');
  assert.match(menu, /\/alquileres\b/, 'El menú debería tener un enlace a /alquileres con el permiso ALQUILERES_VER_TODOS');
});

it('el registro no acepta rol: RegistroRequest no tiene ese campo', () => {
  const dto = leerJava('dto/RegistroRequest.java');
  assert.ok(dto, 'Falta api/.../dto/RegistroRequest.java (el DTO de entrada del registro)');
  assert.doesNotMatch(dto, /\b[Rr]ol\b/, 'RegistroRequest no debería tener un campo rol: lo que no está en el contrato no se puede inyectar');
});

it('el panel muestra lo que calcula el backend: Angular no suma', () => {
  const panel = leer('pages/panel/panel.ts');
  assert.ok(panel, 'Falta pages/panel/panel.ts');
  assert.doesNotMatch(panel, /\.reduce\s*\(/, 'panel.ts no debería sumar con reduce: los ingresos los calcula el backend (GET /api/reportes/resumen)');
  assert.match(leerJava('controller/ReporteController.java'), /hasAuthority\(\s*'REPORTES_VER'\s*\)/, "ReporteController debería proteger el resumen con hasAuthority('REPORTES_VER')");
});

it('entregar y devolver exigen ALQUILERES_GESTIONAR', () => {
  const controlador = leerJava('controller/AlquilerController.java');
  for (const accion of ['entregar', 'devolver']) {
    const donde = controlador.indexOf(`"/{id}/${accion}"`);
    assert.ok(donde >= 0, `Falta en AlquilerController el endpoint @PatchMapping("/{id}/${accion}")`);
    const alrededor = controlador.slice(Math.max(0, donde - 250), donde + 250);
    assert.match(alrededor, /hasAuthority\(\s*'ALQUILERES_GESTIONAR'\s*\)/,
      `En AlquilerController, PATCH /{id}/${accion} debería llevar @PreAuthorize("hasAuthority('ALQUILERES_GESTIONAR')")`);
  }
});
