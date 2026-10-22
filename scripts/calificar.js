// Adaptador Full Stack (JUnit + Vitest + node:test): corre las pruebas de cada parte del taller
// y calcula la nota (0.0 a 5.0). La lista de partes sale de .github/calificacion.json
// Uso local:   npm run calificar          → todo lo publicado
//              npm run calificar -- 1     → solo la Fase 1
//              npm run calificar -- 1.5   → solo la parte 1.5
// En GitHub Actions además genera resultado.json y el resumen del run.

const fs = require("node:fs");
const path = require("node:path");
const { spawnSync } = require("node:child_process");

const RAIZ = path.join(__dirname, "..");
const API = path.join(RAIZ, "api");
const WEB = path.join(RAIZ, "web");
const CONFIG = JSON.parse(fs.readFileSync(path.join(RAIZ, ".github", "calificacion.json"), "utf8"));
const EN_GITHUB = process.env.GITHUB_ACTIONS === "true";
const EN_WINDOWS = process.platform === "win32";
const TEMPORAL = path.join(RAIZ, ".calificacion-tmp");
const MAX_FALLOS_EN_CONSOLA = 2;
const TIEMPO_ANGULAR_MS = 240000;
const TIEMPO_MAVEN_MS = 20 * 60 * 1000;

// ---------------------------------------------------------------- utilidades

function sinColores(texto) {
  return texto.replace(/\x1b\[[0-9;]*m/g, "");
}

function desescapar(texto) {
  return texto
    .replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&quot;/g, '"')
    .replace(/&apos;/g, "'").replace(/&#39;/g, "'").replace(/&#10;/g, "\n").replace(/&amp;/g, "&");
}

function recortar(texto, maximo = 220) {
  const limpio = texto.replace(/\s+/g, " ").trim();
  return limpio.length > maximo ? limpio.slice(0, maximo) + "…" : limpio;
}

function contarTests(rutaTest) {
  const patron = new RegExp(CONFIG.patronTest, "gm");
  return (fs.readFileSync(path.join(RAIZ, rutaTest), "utf8").match(patron) || []).length;
}

function tipoDe(ejercicio) {
  if (ejercicio.test.endsWith(".java")) return "java";
  if (ejercicio.test.endsWith(".mjs")) return "node";
  return "angular";
}

// ---------------------------------------------------------------- backend (Maven + JUnit)

function traducirJava(mensaje) {
  let m;
  if ((m = mensaje.match(/^([\s\S]*?) ==> expected: <([\s\S]*?)> but was: <([\s\S]*?)>$/))) {
    const [, propio, esperado, recibido] = m;
    if (esperado === "true" || esperado === "false") return propio.trim();
    return `${propio.trim()} (esperaba ${recortar(esperado, 80)}, recibió ${recortar(recibido, 80)})`;
  }
  if ((m = mensaje.match(/^([\s\S]*?) ==> /))) return m[1].trim();
  return recortar(mensaje);
}

function causaRaiz(textoError) {
  const causas = [...textoError.matchAll(/Caused by: ([\w.$]+)(?::\s*([^\n]*))?/g)];
  const ultima = causas.length ? causas[causas.length - 1] : null;
  const texto = ultima ? `${ultima[1].split(".").pop()}: ${ultima[2] || ""}` : textoError.split("\n")[0];
  let m;
  if (/Unable to find a @SpringBootConfiguration/.test(textoError)) {
    return "No encuentro LlaveMaestraApplication en el paquete com.devsenior.llavemaestra (no cambies el paquete ni la clase principal).";
  }
  if ((m = textoError.match(/Could not resolve placeholder '([^']+)'/))) {
    return `Falta la propiedad "${m[1]}" en application.properties.`;
  }
  if ((m = textoError.match(/No qualifying bean of type '([^']+)'/))) {
    return `Spring no encuentra un bean de tipo ${m[1].split(".").pop()}: ¿le falta @Service, @Component o @Repository, o está fuera del paquete com.devsenior.llavemaestra?`;
  }
  if (/circular reference|BeanCurrentlyInCreationException/.test(textoError)) {
    return "Hay una dependencia circular entre tus beans (A necesita a B y B necesita a A). Revisa los constructores.";
  }
  if ((m = textoError.match(/Ambiguous mapping[^\n]*/))) {
    return `Dos métodos de tus controladores responden a la misma URL: ${recortar(m[0], 160)}`;
  }
  if ((m = textoError.match(/No property '([^']+)' found for type '([^']+)'/))) {
    return `Un método de tu repositorio busca por "${m[1]}", pero la entidad ${m[2]} no tiene ese campo: revisa el nombre del método.`;
  }
  return recortar(texto);
}

function humanizar(metodo) {
  return metodo
    .replace(/\(\)$/, "")
    .replace(/([a-záéíóúñ0-9])([A-ZÁÉÍÓÚÑ])/g, "$1 $2")
    .replace(/^./, (c) => c.toLowerCase())
    .toLowerCase();
}

function errorDeCompilacionJava(salida) {
  const lineas = sinColores(salida).split(/\r?\n/);
  for (const linea of lineas) {
    const m = linea.match(/\[ERROR\]\s+(?:.*?)(src[\\/]main[\\/]java[\\/][^:\s]+\.java):\[(\d+),\d+\]\s*(.*)$/);
    if (m) {
      const archivo = "api/" + m[1].split("\\").join("/");
      let detalle = m[3].trim();
      if (/cannot find symbol/.test(detalle)) detalle = "no encuentra un nombre (clase, método o variable): revisa mayúsculas e imports";
      if (/class (\w+) is public, should be declared in a file named/.test(detalle)) detalle = "el nombre de la clase no coincide con el nombre del archivo";
      if (/';' expected/.test(detalle)) detalle = "falta un punto y coma (;)";
      if (/package [\w.]+ does not exist/.test(detalle)) detalle = detalle.replace(/package ([\w.]+) does not exist/, "el paquete $1 no existe: revisa el import");
      return `${archivo}, línea ${m[2]}: ${detalle}`;
    }
  }
  return "";
}

// En Windows un .cmd se ejecuta a través de cmd.exe
function comandoMaven(argumentos) {
  if (process.env.CALIFICAR_MVN) return { comando: process.env.CALIFICAR_MVN, args: argumentos };
  return EN_WINDOWS
    ? { comando: "cmd.exe", args: ["/d", "/c", "mvnw.cmd", ...argumentos] }
    : { comando: "./mvnw", args: argumentos };
}

function correrBackend(ejercicios) {
  const resultados = new Map();
  if (ejercicios.length === 0) return resultados;

  const clases = ejercicios.map((e) => path.basename(e.test, ".java"));
  const reportes = path.join(API, "target", "surefire-reports");
  fs.rmSync(reportes, { recursive: true, force: true });

  if (!EN_GITHUB) {
    console.log("   ☕ Backend: compilando y levantando tu API con una base de datos en memoria…");
    console.log("      (la primera vez descarga dependencias: puede tardar varios minutos)");
  }
  const { comando, args } = comandoMaven([
    "-B", "-q", "test", `-Dtest=${clases.join(",")}`,
    "-Dsurefire.failIfNoSpecifiedTests=false", "-Dmaven.test.failure.ignore=true", "-DtrimStackTrace=false",
  ]);
  const proceso = spawnSync(comando, args, { cwd: API, encoding: "utf8", timeout: TIEMPO_MAVEN_MS, maxBuffer: 64 * 1024 * 1024 });
  const salida = (proceso.stdout || "") + "\n" + (proceso.stderr || "");

  let avisoGeneral = "";
  if (proceso.error && proceso.error.code === "ENOENT") {
    avisoGeneral = "No pude ejecutar Maven (mvnw). Revisa que estés en la carpeta del repo y que tengas Java 17 o superior (java -version).";
  } else if (proceso.error && proceso.error.code === "ETIMEDOUT") {
    avisoGeneral = "El backend tardó demasiado: revisa si tienes un ciclo infinito o algo que se queda esperando.";
  } else if (/COMPILATION ERROR|Compilation failure/.test(salida)) {
    const detalle = errorDeCompilacionJava(salida);
    avisoGeneral = `Tu backend no compiló${detalle ? `: ${detalle}` : ". Ejecuta mvnw compile dentro de api/ para ver el error."}`;
  } else if (/JAVA_HOME|java: (command )?not found|no se reconoce/i.test(salida)) {
    avisoGeneral = "No encuentro Java: instala el JDK 17 o superior y vuelve a abrir la terminal.";
  }

  for (const ejercicio of ejercicios) {
    const clase = path.basename(ejercicio.test, ".java");
    const paquete = ejercicio.test.split("/java/")[1].replace(/\.java$/, "").split("/").join(".");
    const archivo = path.join(reportes, `TEST-${paquete}.xml`);
    const total = contarTests(ejercicio.test);

    if (!fs.existsSync(archivo)) {
      resultados.set(ejercicio.id, {
        pasados: 0,
        fallidos: [],
        aviso: avisoGeneral || "No se pudieron correr las pruebas del backend: ejecuta mvnw test dentro de api/ para ver el error.",
      });
      continue;
    }

    const xml = fs.readFileSync(archivo, "utf8");
    const fallidos = [];
    let pasados = 0;
    let arranque = "";
    for (const caso of xml.matchAll(/<testcase\b[^>]*\bname="([^"]*)"[^>]*?(?:\/>|>([\s\S]*?)<\/testcase>)/g)) {
      const nombre = humanizar(desescapar(caso[1]));
      const cuerpo = caso[2] || "";
      const fallo = cuerpo.match(/<(failure|error)\b([^>]*)>([\s\S]*?)<\/\1>|<(failure|error)\b([^>]*)\/>/);
      if (!fallo) {
        if (!/<skipped/.test(cuerpo)) pasados++;
        continue;
      }
      const atributos = fallo[2] || fallo[5] || "";
      const mensaje = desescapar((atributos.match(/\bmessage="([^"]*)"/) || [])[1] || "");
      const detalle = desescapar(fallo[3] || "");
      if (/ApplicationContext|failure threshold|SpringBootConfiguration/.test(mensaje + detalle) || !nombre) {
        arranque = arranque || causaRaiz(detalle || mensaje);
        fallidos.push({ nombre, mensaje: "" });
      } else if (/TimeoutException|timed out after/.test(mensaje + detalle)) {
        fallidos.push({ nombre, mensaje: "Tiempo agotado: revisa si tienes un ciclo infinito." });
      } else {
        fallidos.push({ nombre, mensaje: traducirJava(mensaje || recortar(detalle)) });
      }
    }
    resultados.set(ejercicio.id, {
      pasados: Math.min(pasados, total),
      fallidos: arranque ? [] : fallidos,
      aviso: arranque ? `Tu aplicación no arrancó: ${arranque}` : "",
      clase,
    });
  }
  return resultados;
}

// ---------------------------------------------------------------- frontend (Angular + Vitest)

function buscarAngularCli() {
  try {
    return require.resolve("@angular/cli/bin/ng.js", { paths: [WEB] });
  } catch {
    return "";
  }
}
const NG = buscarAngularCli();

function rutaLegible(importacion) {
  const limpia = importacion.replace(/^(\.\.\/)+/, "");
  return limpia.startsWith("app/") ? `web/src/${limpia}.ts` : importacion;
}

function traducirAngular(mensaje) {
  let m;
  if ((m = mensaje.match(/Cannot find module '([^']+)'/)) || (m = mensaje.match(/Could not resolve "([^"]+)"/))) {
    return `No encuentro el archivo ${rutaLegible(m[1])}: ¿lo creaste con ese nombre exacto y en esa carpeta?`;
  }
  if ((m = mensaje.match(/Module '"([^"]+)"' has no exported member '([^']+)'/))) {
    return `El archivo ${rutaLegible(m[1])} existe, pero no exporta "${m[2]}": revisa el nombre exacto y que tenga export.`;
  }
  if ((m = mensaje.match(/No matching export in "([^"]+)" for import "([^"]+)"/))) {
    return `El archivo web/${m[1]} existe, pero no exporta "${m[2]}": revisa el nombre exacto (sin el sufijo Component).`;
  }
  if ((m = mensaje.match(/NG8001: '([^']+)' is not a known element/))) {
    return `Angular no conoce <${m[1]}>: agrega su clase al imports del componente que lo usa.`;
  }
  if ((m = mensaje.match(/NG8002: Can't bind to '([^']+)'/))) {
    return `Usas [${m[1]}] en un elemento o componente que no lo tiene (¿falta en imports?).`;
  }
  if ((m = mensaje.match(/NG0303: Can't set value of the '([^']+)' input/))) {
    return `El componente no tiene un input llamado "${m[1]}": revisa el nombre exacto (por ejemplo id = input.required<string>()).`;
  }
  if ((m = mensaje.match(/NG8004: No pipe found with name '([^']+)'/))) {
    return `Usas el pipe "${m[1]}" sin agregarlo al imports del componente.`;
  }
  if (/NG5002: @for loop must have a "track"/.test(mensaje)) {
    return "A tu @for le falta track (por ejemplo: track herramienta.id).";
  }
  if ((m = mensaje.match(/TS2339: Property '([^']+)' does not exist on type '([^']+)'/))) {
    return `"${m[1]}" no existe en ${m[2]}: revisa el nombre exacto.`;
  }
  if ((m = mensaje.match(/TS2345: Argument of type '([^']+)' is not assignable to parameter of type '([^']+)'/))) {
    return `Le pasas un ${m[1]} a algo que espera ${m[2]}: revisa los tipos (por ejemplo, el tipo Permiso).`;
  }
  if ((m = mensaje.match(/TS2554: Expected (\d+) arguments?, but got (\d+)/))) {
    return `Una función recibe ${m[2]} argumento(s) pero espera ${m[1]}: revisa la firma que pide el enunciado.`;
  }
  if (/No provider for/.test(mensaje)) {
    return "Falta un provider: tus servicios deberían usar @Service() o @Injectable({ providedIn: 'root' }).";
  }
  if (/Expected one matching request for criteria/.test(mensaje)) {
    const url = (mensaje.match(/"Match URL: ([^"]+)"/) || [])[1];
    return `No se hizo la petición HTTP esperada${url ? ` a ${url}` : ""}: revisa la URL y el método.`;
  }
  return recortar(mensaje);
}

function mensajeDeFallo(atributo) {
  const mensaje = desescapar(atributo);
  const propio = mensaje.match(/^(.*?): expected /s);
  const base = propio ? propio[1] : mensaje.split("\n")[0];
  return traducirAngular(base.trim());
}

function erroresDeCompilacion(salida) {
  const lineas = sinColores(salida).split(/\r?\n/);
  const errores = [];
  lineas.forEach((linea, i) => {
    const m = linea.match(/\[ERROR\]\s*(.+?)(\s*\[plugin [^\]]+\])?$/);
    if (!m) return;
    const donde = lineas.slice(i + 1, i + 5).map((l) => l.match(/(src\/app\/[\w./-]+:\d+)/)).find(Boolean);
    const texto = traducirAngular(m[1].trim()) + (donde ? ` (en web/${donde[1]})` : "");
    if (!errores.includes(texto)) errores.push(texto);
  });
  return errores;
}

function rutaIncluir(ejercicio) {
  return path.relative(path.join(WEB, "src"), path.join(RAIZ, ejercicio.test)).split(path.sep).join("/");
}

function ejecutarNg(ejercicios, reporte) {
  fs.rmSync(reporte, { force: true });
  const incluir = ejercicios.flatMap((e) => ["--include", rutaIncluir(e)]);
  return spawnSync(
    process.execPath,
    [NG, "test", "--watch=false", ...incluir, "--reporters=junit", `--output-file=${reporte}`],
    { cwd: WEB, encoding: "utf8", timeout: TIEMPO_ANGULAR_MS, maxBuffer: 64 * 1024 * 1024, env: { ...process.env, NG_CLI_ANALYTICS: "false", FORCE_COLOR: "0" } },
  );
}

function leerReporteAngular(xml, ejercicio, total) {
  const archivo = path.relative(WEB, path.join(RAIZ, ejercicio.test)).split(path.sep).join("/");
  const fallidos = [];
  let pasados = 0;
  for (const caso of xml.matchAll(/<testcase\b([^>]*?)(?:\/>|>([\s\S]*?)<\/testcase>)/g)) {
    const atributos = caso[1];
    const clase = desescapar((atributos.match(/\bclassname="([^"]*)"/) || [])[1] || "");
    if (clase !== archivo) continue;
    const nombre = desescapar((atributos.match(/\bname="([^"]*)"/) || [])[1] || "").split(" > ").pop();
    const cuerpo = caso[2] || "";
    const fallo = cuerpo.match(/<(?:failure|error)\b[^>]*\bmessage="([^"]*)"/);
    if (fallo) fallidos.push({ nombre, mensaje: mensajeDeFallo(fallo[1]) });
    else if (!/<skipped/.test(cuerpo)) pasados++;
  }
  return { pasados: Math.min(pasados, total), fallidos, aviso: "" };
}

function correrAngularUno(ejercicio, total) {
  const reporte = path.join(TEMPORAL, `junit-${ejercicio.id}.xml`);
  const proceso = ejecutarNg([ejercicio], reporte);
  if (proceso.error && proceso.error.code === "ETIMEDOUT") {
    return { pasados: 0, fallidos: [], aviso: "Tiempo agotado: revisa si tienes un ciclo infinito." };
  }
  if (!fs.existsSync(reporte)) {
    const errores = erroresDeCompilacion((proceso.stdout || "") + "\n" + (proceso.stderr || ""));
    const aviso = errores.length
      ? `No compiló: ${errores[0]}`
      : "No se pudieron correr las pruebas: ejecuta ng build dentro de web/ para ver el error.";
    return { pasados: 0, fallidos: [], aviso };
  }
  return leerReporteAngular(fs.readFileSync(reporte, "utf8"), ejercicio, total);
}

// Primero intenta todo junto (rápido). Si algo no compila, corre cada parte por separado
// para que un error en un archivo no le quite la nota a las demás partes.
function correrFrontend(ejercicios) {
  const resultados = new Map();
  if (ejercicios.length === 0) return resultados;
  if (!NG) {
    for (const e of ejercicios) resultados.set(e.id, { pasados: 0, fallidos: [], aviso: "No encuentro Angular: ejecuta npm install dentro de la carpeta web/." });
    return resultados;
  }
  fs.mkdirSync(TEMPORAL, { recursive: true });
  if (!EN_GITHUB) console.log("   🅰️  Frontend: compilando tus componentes y corriendo las pruebas…");
  const reporte = path.join(TEMPORAL, "junit-todo.xml");
  const proceso = ejecutarNg(ejercicios, reporte);
  const salioBien = fs.existsSync(reporte) && !(proceso.error && proceso.error.code === "ETIMEDOUT");
  if (salioBien) {
    const xml = fs.readFileSync(reporte, "utf8");
    for (const e of ejercicios) resultados.set(e.id, leerReporteAngular(xml, e, contarTests(e.test)));
    return resultados;
  }
  for (const e of ejercicios) {
    if (!EN_GITHUB) process.stdout.write(`      ⏳ ${e.id} ${e.titulo}…\r`);
    resultados.set(e.id, correrAngularUno(e, contarTests(e.test)));
    if (!EN_GITHUB) process.stdout.write(" ".repeat(70) + "\r");
  }
  return resultados;
}

// ---------------------------------------------------------------- estructura (node:test)

function primeraLineaDeError(lineas, desde) {
  for (let i = desde + 1; i < lineas.length && !/^(ok|not ok) \d+/.test(lineas[i]); i++) {
    const enLinea = lineas[i].match(/^\s+error: (['"])(.*)\1$/);
    if (enLinea) return enLinea[2];
    if (/^\s+error: \|-?$/.test(lineas[i])) return (lineas[i + 1] || "").trim();
  }
  return "";
}

function correrNode(ejercicio) {
  const total = contarTests(ejercicio.test);
  const proceso = spawnSync(process.execPath, ["--test-reporter=tap", path.join(RAIZ, ejercicio.test)], {
    cwd: WEB,
    encoding: "utf8",
    timeout: 30000,
  });
  const lineas = (proceso.stdout || "").split(/\r?\n/);
  const fallidos = [];
  let pasados = 0;
  lineas.forEach((linea, i) => {
    if (/^ok \d+ - /.test(linea)) pasados++;
    const fallo = linea.match(/^not ok \d+ - (.*)$/);
    if (fallo) fallidos.push({ nombre: fallo[1], mensaje: primeraLineaDeError(lineas, i) });
  });
  const aviso = proceso.error && proceso.error.code === "ETIMEDOUT" ? "Tiempo agotado." : "";
  return { pasados: Math.min(pasados, total), fallidos, aviso };
}

// ---------------------------------------------------------------- nota y reportes

function calcularNota(resultados) {
  const pasados = resultados.reduce((suma, r) => suma + r.pasados, 0);
  const total = resultados.reduce((suma, r) => suma + r.total, 0);
  const nota = total === 0 ? 0 : Math.round((pasados / total) * 50) / 10;
  return { pasados, total, nota };
}

function icono(r) {
  if (r.pasados === r.total) return "✅";
  return r.pasados > 0 ? "🟡" : "❌";
}

function imprimirEnConsola(resultados, resumen) {
  console.log(`\n${CONFIG.curso} · Resultados\n`);
  for (const r of resultados) {
    console.log(`${icono(r)} ${r.id.padEnd(4)} ${r.titulo.padEnd(36)} ${r.pasados}/${r.total} pruebas`);
    if (r.aviso) console.log(`     ⚠️  ${r.aviso}`);
    for (const f of r.fallidos.slice(0, MAX_FALLOS_EN_CONSOLA)) {
      console.log(`     ✗ ${f.nombre}`);
      if (f.mensaje) console.log(`       → ${f.mensaje}`);
    }
    const ocultos = r.fallidos.length - MAX_FALLOS_EN_CONSOLA;
    if (ocultos > 0) console.log(`     … y ${ocultos} prueba(s) más por corregir`);
  }
  console.log(`\n📊 Pruebas aprobadas: ${resumen.pasados}/${resumen.total}`);
  console.log(`🎯 Nota: ${resumen.nota.toFixed(1)} / 5.0\n`);
}

function escribirResumenGithub(resultados, resumen) {
  const filas = resultados.map((r) => `| ${icono(r)} | ${r.id} · ${r.titulo} | ${r.pasados}/${r.total} |`);
  const detalles = resultados
    .filter((r) => r.fallidos.length || r.aviso)
    .map((r) => {
      const items = r.fallidos.map((f) => `- ✗ ${f.nombre}${f.mensaje ? ` → ${f.mensaje}` : ""}`);
      if (r.aviso) items.unshift(`- ⚠️ ${r.aviso}`);
      return `**${r.id} · ${r.titulo}**\n${items.join("\n")}`;
    });
  const markdown = [
    `## 🎯 Nota: ${resumen.nota.toFixed(1)} / 5.0`,
    `Pruebas aprobadas: **${resumen.pasados}/${resumen.total}**`,
    "",
    "| Estado | Parte | Pruebas |",
    "|---|---|---|",
    ...filas,
    "",
    detalles.length ? "### Qué falta por corregir\n\n" + detalles.join("\n\n") : "### ¡Todo perfecto! 🎉",
  ].join("\n");
  if (process.env.GITHUB_STEP_SUMMARY) fs.appendFileSync(process.env.GITHUB_STEP_SUMMARY, markdown + "\n");
  const paraArtefacto = resultados.map(({ id, pasados, total }) => ({ id, pasados, total }));
  fs.writeFileSync(path.join(RAIZ, "resultado.json"), JSON.stringify({ ejercicios: paraArtefacto }));
}

// ---------------------------------------------------------------- principal

const filtro = process.argv[2];
const ejercicios = CONFIG.ejercicios.filter((e) => !filtro || e.id === filtro || e.id.startsWith(`${filtro}.`));
if (ejercicios.length === 0) {
  console.log(`No encontré "${filtro}". Las partes disponibles son: ${CONFIG.ejercicios.map((e) => e.id).join(", ")}`);
  process.exit(1);
}

const [mayor, menor] = process.versions.node.split(".").map(Number);
if (ejercicios.some((e) => tipoDe(e) === "angular") && (mayor < 22 || (mayor === 22 && menor < 22) || mayor === 23)) {
  console.log(`⚠️  Tienes Node ${process.versions.node}. Angular 22 necesita Node 22.22.3+ o 24.15+: instala Node 24 LTS desde nodejs.org.`);
}

console.log(`\nCalificando ${ejercicios.length} parte(s). Puede tardar un par de minutos.`);
const backend = correrBackend(ejercicios.filter((e) => tipoDe(e) === "java"));
const frontend = correrFrontend(ejercicios.filter((e) => tipoDe(e) === "angular"));

const resultados = ejercicios.map((e) => {
  const total = contarTests(e.test);
  const tipo = tipoDe(e);
  const r = tipo === "java" ? backend.get(e.id) : tipo === "angular" ? frontend.get(e.id) : correrNode(e);
  return { id: e.id, titulo: e.titulo, total, pasados: Math.min(r.pasados, total), fallidos: r.fallidos, aviso: r.aviso };
});

const resumen = calcularNota(resultados);
imprimirEnConsola(resultados, resumen);
if (EN_GITHUB) escribirResumenGithub(resultados, resumen);
fs.rmSync(TEMPORAL, { recursive: true, force: true });
process.exitCode = resumen.pasados === resumen.total ? 0 : 1;
