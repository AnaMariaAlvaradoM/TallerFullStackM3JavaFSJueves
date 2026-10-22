// Pruebas de estructura: leen tus archivos (sin comentarios) y revisan la arquitectura.
import { it } from 'node:test';
import assert from 'node:assert/strict';
import { APP, JAVA, archivos, archivosCon, contenido, leer, leerJava } from '../utilidades-estructura.mjs';

const PERMISOS = ['HERRAMIENTAS_VER', 'HERRAMIENTAS_GESTIONAR', 'ALQUILERES_CREAR', 'ALQUILERES_VER_PROPIOS',
  'ALQUILERES_CANCELAR_PROPIOS', 'ALQUILERES_VER_TODOS', 'ALQUILERES_GESTIONAR', 'CLIENTES_GESTIONAR', 'REPORTES_VER'];

it('Permiso.java es un enum con los 9 permisos del negocio', () => {
  const codigo = leerJava('model/Permiso.java');
  assert.match(codigo, /enum\s+Permiso\b/, 'Falta api/.../model/Permiso.java con "public enum Permiso"');
  for (const permiso of PERMISOS) {
    assert.match(codigo, new RegExp(`\\b${permiso}\\b`), `Permiso.java debería tener ${permiso}`);
  }
});

it('Rol.java agrupa permisos en ADMIN y CLIENTE', () => {
  const codigo = leerJava('model/Rol.java');
  assert.match(codigo, /enum\s+Rol\b/, 'Falta api/.../model/Rol.java con "public enum Rol"');
  assert.match(codigo, /\bADMIN\s*\(/, 'Rol.java debería declarar ADMIN(...) con su paquete de permisos');
  assert.match(codigo, /\bCLIENTE\s*\(/, 'Rol.java debería declarar CLIENTE(...) con su paquete de permisos');
});

it('los controladores autorizan por permiso con hasAuthority, no con hasRole', () => {
  const controladores = archivos(`${JAVA}/controller`, '.java').map(contenido);
  assert.ok(controladores.length >= 3, 'Debería haber al menos 3 controladores en api/.../controller');
  assert.ok(controladores.some((c) => /@PreAuthorize\(\s*"hasAuthority\(/.test(c)), 'Usa @PreAuthorize("hasAuthority(\'...\')") en tus controladores');
  const conRol = archivosCon(`${JAVA}/controller`, '.java', /hasRole\s*\(|hasAnyRole\s*\(/);
  assert.deepEqual(conRol, [], `En Llave Maestra se autoriza por permiso: cambia hasRole por hasAuthority en ${conRol.join(', ')}`);
});

it('el JwtFilter revisa que la cuenta siga activa', () => {
  const filtro = leerJava('config/JwtFilter.java');
  assert.ok(filtro, 'Falta api/.../config/JwtFilter.java');
  assert.match(filtro, /isEnabled\s*\(\s*\)|isActivo\s*\(\s*\)/, 'JwtFilter debería verificar isEnabled() (o isActivo()) antes de autenticar');
});

it('las rutas cargan sus páginas con loadComponent', () => {
  const rutas = leer('app.routes.ts');
  for (const ruta of ['login', 'herramientas', 'clientes', 'sin-acceso']) {
    assert.match(rutas, new RegExp(`path:\\s*'${ruta}'[\\s\\S]*?loadComponent`), `La ruta '${ruta}' debería usar loadComponent`);
  }
});

it('app.config registra el interceptor con withInterceptors', () => {
  assert.match(leer('app.config.ts'), /withInterceptors\(\s*\[\s*jwtInterceptor\s*\]\s*\)/, 'app.config.ts debería tener provideHttpClient(withInterceptors([jwtInterceptor]))');
});

it('la página de herramientas usa *appSiPuede para lo que exige permiso', () => {
  assert.match(leer('pages/herramientas/herramientas.html'), /\*appSiPuede=/, 'herramientas.html debería usar *appSiPuede="\'HERRAMIENTAS_GESTIONAR\'" en el botón de retirar');
});

it('sin Angular viejo: nada de *ngFor, *ngIf, @Input() ni NgModule', () => {
  assert.ok(leer('pages/herramientas/herramientas.html'), 'Aún no existe pages/herramientas/herramientas.html para revisar');
  const html = archivosCon(APP, '.html', /\*ng(For|If)\b/);
  const ts = archivosCon(APP, '.ts', /@Input\s*\(|@NgModule\s*\(/);
  assert.deepEqual([...html, ...ts], [], `Cambia por @for/@if, input() y componentes standalone en: ${[...html, ...ts].join(', ')}`);
});
