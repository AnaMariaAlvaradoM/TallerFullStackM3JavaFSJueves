# 🔧 Llave Maestra · Taller Full Stack · Módulo 3

**Java Full Stack Developer · Dev Senior Code · jueves**

---

## ¿Qué es esto?

**Llave Maestra** es una alquiladora (ficticia) de herramientas en Cajicá. Su dueño, Hernán Rojas, lo anota todo en un cuaderno y ya le pasó de todo: alquiló la misma mezcladora dos veces, un cliente canceló lo que no era suyo y otro vio lo que les cobra a los demás.

Durante el Módulo 3 vas a construir su sistema completo, **backend en Spring Boot y frontend en Angular**, por fases:

- En clase, la profe construye **Colmena** (reservas de un coworking).
- Tú aplicas **el mismo concepto** a Llave Maestra. Mismo patrón, otro negocio: así aprendes a transferir, no a copiar.

### La nota se calcula sola

Cada vez que haces `git push`, GitHub corre las **pruebas automáticas** del taller sobre tu código y pone tu nota en tu Pull Request.

### ¿Qué es una prueba automática?

**Definición técnica:** código que ejecuta tu código con datos conocidos y compara el resultado con el esperado.

**En la vida real:** el control de calidad de una fábrica: cada pieza pasa por la misma revisión, sin excepciones y sin favoritismos.

### Los tres tipos de prueba de este taller

- **Backend:** levantan **tu** API con una base de datos en memoria (no tocan tu PostgreSQL) y le hacen peticiones HTTP, igual que Swagger o Postman. Revisan códigos (`200`, `401`, `403`, `409`…) y respuestas.
- **Frontend:** crean tus componentes y servicios de Angular, simulan el backend y revisan lo que se ve en pantalla.
- **Estructura:** leen tus archivos y revisan la arquitectura (permisos con `hasAuthority`, rutas con `loadComponent`, nada de Angular viejo).

---

## Las fases

| Fase | Se publica | Tema | Enunciado | Checkpoint |
|---|---|---|---|---|
| 1 | Jue 22 oct | Base y control de acceso | [`fases/FASE-1.md`](fases/FASE-1.md) | Mié 28 oct · 11:59 p. m. |
| 2 | Jue 22 oct | Alquilar con reglas | [`fases/FASE-2.md`](fases/FASE-2.md) | Mié 4 nov · 11:59 p. m. |
| 3 | Jue 29 oct | La vista del negocio + revisión entre pares | Se publica después de la Clase 3 | Dom 8 nov |
| 4 | Jue 5 nov | Calidad, refactor y entrega | Se publica después de la Clase 4 | **Dom 15 nov** |

Todas las fases se trabajan **en este mismo repo** y se entregan en **el mismo Pull Request**.

---

## Antes de empezar

| Herramienta | Versión | Cómo verificar |
|---|---|---|
| Java (JDK) | **17 o superior** | `java -version` |
| Node.js | **24 LTS** (24.15 o superior) | `node -v` |
| Angular CLI | **22** | `ng version` |
| PostgreSQL + pgAdmin | Cualquiera reciente | Abre pgAdmin |
| Git | Cualquiera reciente | `git --version` |

No necesitas instalar Maven: el backend trae `mvnw` (el *wrapper* de Maven).

---

## Paso 1 · Fork (tu copia del repo)

**Definición técnica:** un *fork* es una copia de un repositorio en tu propia cuenta de GitHub, conectada con el original.

**En la vida real:** fotocopiar el cuaderno de la profe. Escribes en tu fotocopia; el original no cambia.

1. Inicia sesión en GitHub.
2. En la página de este repo, clic en **Fork** (arriba a la derecha).
3. Deja el nombre como está y clic en **Create fork**.

**Cómo saber que quedó bien:** arriba a la izquierda dice **tu-usuario / nombre-del-repo** y debajo *forked from …*.

---

## Paso 2 · Clone y primer arranque

### Bajar tu fork

1. En **tu fork** (no en el de la profe), clic en el botón verde **Code** y copia la URL HTTPS.
2. En VS Code abre la terminal (**Terminal → New Terminal**) y ve a tu carpeta de proyectos.
3. Ejecuta (cambia la URL por la tuya):

```bash
git clone https://github.com/TU-USUARIO/NOMBRE-DEL-REPO.git
cd NOMBRE-DEL-REPO
code .
```

### Instalar el frontend

En la nueva ventana de VS Code, abre la terminal. Desde la raíz del repo (tarda de 1 a 3 minutos):

```bash
cd web
npm install
```

### Crear la base de datos

En pgAdmin crea una base **vacía** llamada `llave_db`. Si tu usuario o tu clave de PostgreSQL no son `postgres`, cámbialos en `api/src/main/resources/application.properties`.

### Levantar el backend

Abre **otra** terminal (el ícono **+** de la terminal). Desde la raíz del repo:

```bash
cd api
mvnw.cmd spring-boot:run
```

En macOS o Linux el comando es `./mvnw spring-boot:run`. La primera vez descarga dependencias: tarda unos minutos.

**Resultado esperado:** la terminal termina en `Started LlaveMaestraApplication` y `http://localhost:8080/swagger-ui.html` abre Swagger (todavía casi vacío).

### Levantar el frontend

En la terminal de `web`:

```bash
ng serve -o
```

**Resultado esperado:** el navegador abre `http://localhost:4200` con *"Llave Maestra · Taller del Módulo 3"*.

---

## Paso 3 · Resolver la fase

1. Abre el enunciado de la fase: [`fases/FASE-1.md`](fases/FASE-1.md).
2. El backend va en **`api/src/main/`**. El frontend va en **`web/src/app/`**.
3. Crea los componentes con el CLI, igual que en clase (`ng g c pages/... --skip-tests`).

### Lo que ya viene listo

| Dónde | Qué trae |
|---|---|
| `api/` | `pom.xml` con todas las dependencias, `application.properties`, la clase principal, Swagger, `ErrorResponse` y el manejador global de errores |
| `web/` | El proyecto Angular 22 creado, el sistema de diseño en `src/styles.css`, `core/api.ts` y `core/formato.ts` |

### Las tres reglas de oro

1. **Nombres exactos.** Rutas, permisos, archivos, clases, selectores y `data-testid` se escriben **tal cual** dice el enunciado. Las pruebas los buscan así.
2. **No toques las pruebas** ni los archivos de configuración (lista abajo).
3. **Lo moderno:** permisos con `hasAuthority`, Angular 22 con `input()`, `@for`, `@if` y componentes standalone.

### ¿Qué es un `data-testid`?

Es un atributo HTML que no cambia cómo se ve la página; sirve para que las pruebas encuentren un elemento sin depender del diseño. Ejemplo de la **forma** (no resuelve nada del taller):

```html
<p data-testid="saludo">Hola</p>
```

---

## Paso 4 · Ver tu nota en tu computador

Desde la **raíz** del repo (no dentro de `api` ni de `web`):

| Comando | Qué califica |
|---|---|
| `npm run calificar` | Todo lo publicado hasta hoy |
| `npm run calificar -- 1` | Solo la Fase 1 |
| `npm run calificar -- 1.5` | Solo la parte 1.5 (más rápido mientras corriges) |

No necesitas tener el backend corriendo: las pruebas levantan su propia copia con una base de datos en memoria. Sí necesitas haber hecho `npm install` dentro de `web`.

### Cómo leer el resultado

- ✅ todas las pruebas de esa parte pasan.
- 🟡 pasan algunas: debajo dice cuáles fallan y por qué.
- ❌ no pasa ninguna. Lee el aviso ⚠️: casi siempre dice el archivo y la línea.

---

## Paso 5 · Subir tu trabajo

Desde la raíz del repo, en este orden:

```bash
git add .
git commit -m "Fase 1"
git push
```

- `git add .` → mete en la caja todos los cambios.
- `git commit -m "..."` → cierra la caja y le pone una etiqueta.
- `git push` → envía la caja a **tu fork** en GitHub.

---

## Paso 6 · Abrir tu Pull Request (solo UNO en todo el módulo)

**Definición técnica:** un *Pull Request* (PR) es una solicitud para que revisen los cambios de tu fork contra el repo original.

**En la vida real:** radicar un documento en ventanilla: queda con número, se revisa y te responden ahí mismo.

1. En **tu fork**, clic en **Contribute → Open pull request**.
2. Revisa que diga: base repository **el de la profe** · base **main** ← head repository **el tuyo** · compare **main**.
3. **Título:** tu nombre completo (por ejemplo `Ana María Pérez Gómez`).
4. Clic en **Create pull request**.

Un solo PR para las 4 fases: cada `git push` lo actualiza solo. **No abras otro.**

---

## Paso 7 · Ver tu nota en GitHub

A los pocos minutos de cada `push` (la calificación tarda entre 3 y 6 minutos):

- En tu PR aparece un **comentario** con la nota y una tabla por parte.
- El PR tiene una **etiqueta** `Nota X.X`.
- El enlace **"Ver qué falta por corregir"** muestra el detalle de cada prueba.

> [!IMPORTANT]
> **La nota se calcula sobre las fases publicadas.** Cuando sale una fase nueva, tu nota baja hasta que la completes: es normal.

> [!WARNING]
> **Fecha límite del taller:** domingo 15 de noviembre de 2026, 11:59 p. m. Después de esa hora los `push` ya no cambian la nota. Una primera entrega después del cierre queda con la etiqueta `⏰ Fuera de plazo`.

### La nota del taller

| Componente | Peso |
|---|---|
| Pruebas automáticas (la nota del PR) | 70 % |
| Revisión de la profe: README, video demo, `PRUEBAS.md`, revisión entre pares, bitácora de IA y diseño | 30 % |

---

## Paso 8 · Cuando se publica una fase nueva

Tu fork no se actualiza solo:

1. En **tu fork** en GitHub, clic en **Sync fork → Update branch**.
2. En tu computador, desde la raíz del repo:

```bash
git pull
```

3. Abre el nuevo `fases/FASE-N.md` y sigue con el Paso 3.

Si **Sync fork** dice que hay conflictos, es porque modificaste un archivo que no debías. Pide ayuda en la tutoría.

---

## Archivos que NO debes modificar

- `api/src/test/` (todas las pruebas del backend)
- `web/src/pruebas/` (todas las pruebas del frontend)
- `scripts/`, `fases/` y `.github/`
- `package.json` (el de la raíz)
- `api/pom.xml`, `api/mvnw`, `api/mvnw.cmd` y `api/.mvn/`
- `web/package.json`, `web/package-lock.json`, `web/angular.json` y los `web/tsconfig*.json`

**Si los modificas:** tu PR queda con la etiqueta `⚠️ Revisar` y la profe lo revisa a mano. La nota se calcula igual, **con las pruebas originales**: cambiarlas no sube la nota.

---

## Errores frecuentes

### `java`, `ng` o `npm` no se reconoce como comando
Falta instalarlo, o la terminal se abrió antes de instalarlo: ciérrala y ábrela.

### "The Angular CLI requires a minimum Node.js version…"
Tu Node es viejo. Instala Node 24 LTS desde nodejs.org.

### "No encuentro Angular: ejecuta npm install dentro de la carpeta web/"
Te faltó `cd web` y `npm install`.

### "./mvnw: Permission denied" (macOS o Linux)
Ejecuta una sola vez `chmod +x api/mvnw`.

### "Tu backend no compiló: api/…/Archivo.java, línea N…"
Hay un error de Java en ese archivo y esa línea. Mientras el backend no compile, **ninguna** prueba de backend puede correr.

### "Tu aplicación no arrancó: …"
Compila, pero Spring no logra levantarla. El mensaje dice la causa: casi siempre es una clase sin `@Service`/`@Repository` o un método de repositorio con un campo que no existe.

### "POST /api/auth/login con … debería responder 200 (¿sembraste ese usuario…?)"
El login todavía no funciona o los usuarios no están en tu `DataInitializer` con la clave `llave123`.

### "No compiló: No encuentro el archivo web/src/app/…"
El archivo no existe con **ese nombre exacto** o está en otra carpeta. Revisa mayúsculas, guiones y la ruta.

### "…existe, pero no exporta …"
La clase se llama distinto. En Angular 22 es `Herramientas`, no `HerramientasComponent`. Revisa también que diga `export`.

### "Angular no conoce <app-…>"
Usas un componente en una plantilla sin agregarlo al `imports` del componente que lo usa.

### "Please tell me who you are" al hacer commit
Git no sabe tu nombre. Ejecuta una sola vez (con tus datos):

```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu-correo@ejemplo.com"
```

### Error 403 al hacer `git push`
Clonaste el repo de la profe y no tu fork. Repite el Paso 2 con la URL de **tu** fork.

### El backend no arranca: "Port 8080 was already in use"
Ya tienes otro backend corriendo (¿Colmena?). Detenlo con `Ctrl+C` en su terminal.

---

<sub>© 2026 Ana Alvarado · Educadora Tech & Desarrolladora Full Stack · Todos los derechos reservados · linkedin.com/in/ana-alvarado-instructora-full-stack</sub>
