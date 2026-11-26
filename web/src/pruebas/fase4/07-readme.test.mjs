// 4.7 · El README de TU proyecto: lo primero que abre quien lo revisa.
import { it } from 'node:test';
import assert from 'node:assert/strict';
import { leerRaiz } from '../utilidades-estructura.mjs';

function sinTildes(texto) {
  return texto.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
}

function readme() {
  const texto = leerRaiz('README.md');
  assert.ok(texto.trim(), 'Falta README.md en la raíz del repo');
  assert.doesNotMatch(texto, /Taller Full Stack · Módulo 3/, 'README.md todavía es el del taller. Reemplázalo por el README de TU proyecto (las instrucciones del taller siguen en fases/)');
  return texto;
}

// El contenido de la sección "## ..." cuyo título contiene alguna de las palabras
function seccion(...palabras) {
  const texto = readme();
  const partes = texto.split(/^##\s+/m).slice(1);
  const encontrada = partes.find((p) => {
    const titulo = sinTildes(p.split(/\r?\n/)[0]);
    return palabras.some((palabra) => titulo.includes(sinTildes(palabra)));
  });
  assert.ok(encontrada, `Falta en README.md una sección "## ${palabras[0]}"`);
  return encontrada;
}

it('1 · empieza con el nombre del proyecto y explica qué es', () => {
  const texto = readme();
  const titulo = (texto.match(/^#\s+(.+)$/m) || [])[1] || '';
  assert.match(titulo, /Llave Maestra/i, 'El título (# ...) del README debería tener el nombre del proyecto: Llave Maestra');
  const que = seccion('Qué es');
  assert.ok(que.replace(/\s+/g, ' ').length > 120, 'La sección "Qué es" debería explicar en un párrafo el problema de Hernán y qué resuelve el sistema');
});

it('2 · dice con qué tecnologías está hecho', () => {
  const tec = sinTildes(seccion('Tecnologías'));
  for (const t of ['spring boot', 'angular', 'postgresql', 'jwt']) {
    assert.ok(tec.includes(t), `La sección "Tecnologías" debería mencionar ${t}`);
  }
});

it('3 · explica cómo levantarlo, con los comandos', () => {
  const pasos = seccion('Cómo levantarlo', 'Cómo ejecutarlo', 'Instalación');
  assert.match(pasos, /mvnw/, 'La sección "Cómo levantarlo" debería tener el comando del backend (./mvnw spring-boot:run o mvnw.cmd spring-boot:run)');
  assert.match(pasos, /ng serve|npm start/, 'La sección "Cómo levantarlo" debería tener el comando del frontend (ng serve o npm start)');
  assert.match(pasos, /npm (install|ci)/, 'La sección "Cómo levantarlo" debería decir que primero se instalan las dependencias (npm install)');
  assert.match(pasos, /llave_db/, 'La sección "Cómo levantarlo" debería decir qué base de datos crear (llave_db)');
});

it('4 · documenta las variables de entorno', () => {
  const variables = seccion('Variables de entorno');
  assert.match(variables, /JWT_SECRET/, 'La sección "Variables de entorno" debería explicar JWT_SECRET');
  assert.match(variables, /DB_PASSWORD/, 'La sección "Variables de entorno" debería explicar DB_PASSWORD');
});

it('5 · trae los usuarios de prueba', () => {
  const usuarios = seccion('Usuarios de prueba');
  for (const email of ['admin@llavemaestra.co', 'pedro@correo.com', 'diana@correo.com', 'jorge@correo.com']) {
    assert.ok(usuarios.includes(email), `La sección "Usuarios de prueba" debería tener ${email}`);
  }
  assert.match(usuarios, /llave123/, 'La sección "Usuarios de prueba" debería decir la contraseña de prueba');
});

it('6 · explica los roles y permisos', () => {
  const permisos = seccion('Roles y permisos', 'Permisos');
  for (const p of ['ADMIN', 'CLIENTE', 'HERRAMIENTAS_VER', 'HERRAMIENTAS_GESTIONAR', 'ALQUILERES_CREAR', 'ALQUILERES_VER_PROPIOS',
    'ALQUILERES_CANCELAR_PROPIOS', 'ALQUILERES_VER_TODOS', 'ALQUILERES_GESTIONAR', 'CLIENTES_GESTIONAR', 'REPORTES_VER']) {
    assert.ok(permisos.includes(p), `La sección "Roles y permisos" debería mencionar ${p}`);
  }
});

it('7 · cuenta al menos 3 decisiones técnicas', () => {
  const decisiones = seccion('Decisiones');
  const items = decisiones.split(/\r?\n/).filter((l) => /^\s*(?:[-*]|\d+\.)\s+\S/.test(l) || /^###\s+\S/.test(l));
  assert.ok(items.length >= 3, `La sección "Decisiones técnicas" debería tener al menos 3 decisiones (una por viñeta o por ###). Encontré ${items.length}`);
});

it('8 · enlaza el video de la demo', () => {
  const demo = seccion('Demo');
  assert.match(demo, /https?:\/\/\S+/, 'La sección "Demo" debería tener el enlace a tu video (YouTube, Drive o Loom)');
});
