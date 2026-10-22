// Ayudas para las pruebas de estructura. No es un archivo de pruebas.
import { existsSync, readFileSync, readdirSync, statSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { join, relative } from 'node:path';

export const APP = fileURLToPath(new URL('../app', import.meta.url));
export const RAIZ = fileURLToPath(new URL('../../../', import.meta.url));
export const JAVA = join(RAIZ, 'api/src/main/java/com/devsenior/llavemaestra');

function sinComentarios(codigo) {
  return codigo
    .replace(/<!--[\s\S]*?-->/g, '')
    .replace(/\/\*[\s\S]*?\*\//g, '')
    .replace(/(^|[^:])\/\/.*$/gm, '$1');
}

function leerRuta(ruta) {
  return existsSync(ruta) ? sinComentarios(readFileSync(ruta, 'utf8')) : '';
}

export function leer(rutaDentroDeApp) {
  return leerRuta(join(APP, rutaDentroDeApp));
}

export function leerJava(rutaDentroDelPaquete) {
  return leerRuta(join(JAVA, rutaDentroDelPaquete));
}

export function leerRaiz(ruta) {
  return existsSync(join(RAIZ, ruta)) ? readFileSync(join(RAIZ, ruta), 'utf8') : '';
}

function recorrer(carpeta) {
  if (!existsSync(carpeta)) return [];
  return readdirSync(carpeta).flatMap((nombre) => {
    const ruta = join(carpeta, nombre);
    return statSync(ruta).isDirectory() ? recorrer(ruta) : [ruta];
  });
}

export function archivosCon(carpeta, extension, patron) {
  return recorrer(carpeta)
    .filter((ruta) => ruta.endsWith(extension))
    .filter((ruta) => patron.test(sinComentarios(readFileSync(ruta, 'utf8'))))
    .map((ruta) => relative(RAIZ, ruta).split('\\').join('/'));
}

export function archivos(carpeta, extension) {
  return recorrer(carpeta).filter((ruta) => ruta.endsWith(extension));
}

export function contenido(ruta) {
  return leerRuta(ruta);
}
