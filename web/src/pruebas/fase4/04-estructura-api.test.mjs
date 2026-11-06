// 4.4 · Refactor del backend: cada capa hace lo suyo (SOLID) y no queda nada de depuración.
import { it } from 'node:test';
import assert from 'node:assert/strict';
import { join, basename } from 'node:path';
import { JAVA, archivos, archivosCon, contenido, leerJava, leerRaiz } from '../utilidades-estructura.mjs';

const CONTROLADORES = join(JAVA, 'controller');
const SERVICIOS = join(JAVA, 'service');

function controladores() {
  const lista = archivos(CONTROLADORES, '.java');
  assert.ok(lista.length >= 5, 'No encuentro tus controladores en api/.../controller/ (deberían ser al menos 5)');
  return lista;
}

it('ningún controlador usa un repositorio: solo habla con servicios', () => {
  controladores();
  const culpables = archivosCon(CONTROLADORES, '.java', /\.repository\.|Repository\b/);
  assert.deepEqual(culpables, [], 'Estos controladores usan un repositorio directamente. El controlador recibe la petición y llama al SERVICIO; el servicio es el que habla con el repositorio');
});

it('los controladores dependen de interfaces, no de implementaciones', () => {
  controladores();
  const culpables = archivosCon(CONTROLADORES, '.java', /\.service\.impl\.|\w+ServiceImpl\b/);
  assert.deepEqual(culpables, [], 'Estos controladores usan una clase ...ServiceImpl. Deberían recibir la interfaz (AlquilerService), no la implementación (principio D de SOLID)');
});

it('cada servicio de negocio tiene su interfaz y su implementación', () => {
  const impls = archivos(join(SERVICIOS, 'impl'), '.java');
  const esperados = ['AlquilerService', 'AuthService', 'HerramientaService', 'ReporteService', 'UsuarioService'];
  for (const servicio of esperados) {
    const interfaz = leerJava(`service/${servicio}.java`);
    assert.ok(interfaz, `Falta la interfaz service/${servicio}.java`);
    assert.match(interfaz, new RegExp(`public\\s+interface\\s+${servicio}\\b`), `service/${servicio}.java debería ser una interfaz (public interface ${servicio})`);
    const impl = leerJava(`service/impl/${servicio}Impl.java`);
    assert.ok(impl, `Falta service/impl/${servicio}Impl.java`);
    assert.match(impl, new RegExp(`implements\\s+[\\w\\s,]*\\b${servicio}\\b`), `${servicio}Impl debería decir implements ${servicio}`);
    assert.match(impl, /@Service\b/, `${servicio}Impl debería tener @Service`);
  }
  assert.ok(impls.length >= esperados.length, 'Faltan implementaciones en service/impl/');
});

it('cada endpoint (menos login y registro) pide un permiso con @PreAuthorize', () => {
  const faltan = [];
  const metodo = /((?:@\w+(?:\((?:[^()]|\([^()]*\))*\))?\s*)+)public\s+[^;{=]*?\b(\w+)\s*\(/g;
  for (const ruta of controladores()) {
    const nombre = basename(ruta);
    if (nombre === 'AuthController.java') continue;
    const codigo = contenido(ruta);
    const inicioClase = codigo.search(/\bclass\s/);
    if (/@PreAuthorize/.test(codigo.slice(0, inicioClase))) continue;
    for (const m of codigo.slice(inicioClase).matchAll(metodo)) {
      if (/@(Get|Post|Put|Patch|Delete|Request)Mapping/.test(m[1]) && !/@PreAuthorize/.test(m[1])) {
        faltan.push(`${nombre} → ${m[2]}`);
      }
    }
  }
  assert.deepEqual(faltan, [], 'Estos endpoints no piden ningún permiso: si alguien escribe la URL a mano, entra. Ponles @PreAuthorize("hasAuthority(\'...\')")');
});

it('no quedan System.out ni printStackTrace en el backend', () => {
  controladores();
  const culpables = archivosCon(JAVA, '.java', /System\.(out|err)\.|\.printStackTrace\s*\(/);
  assert.deepEqual(culpables, [], 'Quita los System.out.println y printStackTrace de estos archivos: en producción ensucian el registro y pueden mostrar datos');
});

it('la clave del JWT sale de una variable de entorno, con un valor de desarrollo', () => {
  const propiedades = leerRaiz('api/src/main/resources/application.properties');
  assert.ok(propiedades, 'Falta api/src/main/resources/application.properties');
  const linea = propiedades.split(/\r?\n/).find((l) => /^\s*jwt\.secret\s*=/.test(l));
  assert.ok(linea, 'Falta la propiedad jwt.secret en application.properties');
  assert.match(linea, /=\s*\$\{[A-Z][A-Z0-9_]*:[^}]{32,}\}\s*$/,
    'jwt.secret debería tomar la clave de una variable de entorno con un valor de desarrollo de al menos 32 caracteres: jwt.secret=${JWT_SECRET:clave-de-desarrollo-...}');
  assert.match(propiedades, /password\s*=\s*\$\{[A-Z_]+:/, 'La contraseña de la base de datos también debería venir de una variable de entorno: spring.datasource.password=${DB_PASSWORD:postgres}');
});
