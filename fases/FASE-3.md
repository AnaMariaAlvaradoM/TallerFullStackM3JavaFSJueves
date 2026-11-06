# 🔧 Fase 3 · La vista del negocio + revisión entre pares

**Taller Módulo 3 · Llave Maestra** · Se publica: jueves 29 de octubre · **Checkpoint: domingo 8 de noviembre, 11:59 p. m.**

---

## 📨 Lo que pide Hernán

> Ya mis clientes separan solos, pero yo sigo manejando el negocio en el cuaderno.
>
> Necesito ver **todos** los alquileres, marcar cuándo **entregué** una herramienta y cuándo me la **devolvieron**. Quiero saber **cuánto llevo facturado** este mes, cuáles herramientas **se alquilan más** y, sobre todo, cuáles están **vencidas**: Jorge todavía no me devuelve la pulidora.
>
> Y que los clientes nuevos **se registren solos**, sin que yo les cree la cuenta. Eso sí: que nadie se pueda volver administrador desde el registro.
>
> Ah, y quiero agregar herramientas nuevas y corregir precios sin llamarlos a ustedes.

---

## 🎯 Qué vas a construir

Lo mismo que hicimos con Colmena en la Clase 3, aplicado a Llave Maestra:

| En clase (Colmena) | Tú (Llave Maestra) |
|---|---|
| Panel con reservas de hoy, ingresos y ocupación | **Panel** con facturado del mes, activos, **vencidos** y más alquiladas |
| Agenda por día con cancelación | **Alquileres**: todos, con filtro por estado, **Entregar** y **Recibir devolución** |
| Registro público de miembros (siempre `USER`) | **Registro** público de clientes (siempre `CLIENTE`) |
| Formulario de espacios (crear y editar) | **Formulario de herramientas** (crear y editar) |
| Revisión de un Pull Request en vivo | **Revisas el Pull Request de un compañero** |

---

## 📐 Las reglas del negocio

### Cómo avanza un alquiler

```text
RESERVADO ──entregar──▶ ENTREGADO ──devolver──▶ DEVUELTO
    │
    └──cancelar──▶ CANCELADO   (Fase 2)
```

| # | Regla | Respuesta |
|---|---|---|
| 1 | Solo se **entrega** lo que está `RESERVADO` | `409` si no |
| 2 | Solo se **devuelve** lo que está `ENTREGADO` (no se devuelve lo que no se entregó) | `409` si no |
| 3 | El alquiler no existe | `404` |
| 4 | Entregar y devolver es trabajo de Hernán (`ALQUILERES_GESTIONAR`) | `403` para un cliente |

### Qué cuenta en el panel

| Dato | Cómo se calcula |
|---|---|
| `ingresosMes` | Suma de `valorTotal` de los alquileres **creados** desde el día 1 del mes en curso, **sin los cancelados** |
| `alquileresActivos` | Cuántos alquileres están en `RESERVADO` o `ENTREGADO` |
| `vencidos` | Los alquileres `ENTREGADO` cuya `fechaFin` **ya pasó** (uno que termina hoy todavía no está vencido) |
| `masAlquiladas` | Cuántas veces se ha alquilado cada herramienta, **sin contar cancelados**, de mayor a menor |

**¿Por qué lo calcula el backend y no Angular?** Angular tendría que descargar **todos** los alquileres para sumarlos; la facturación es un dato **sensible** (solo `REPORTES_VER`); y es una **regla de negocio**: si cambia (por ejemplo, contar solo lo devuelto), cambia en un solo lugar.

### El registro

- Es **público**: no pide token.
- Siempre crea un **CLIENTE**, aunque el JSON traiga `"rol": "ADMIN"`. El DTO del registro **no tiene** campo `rol`: lo que no está en el contrato no se puede inyectar.
- Un email que ya existe responde `409`. Datos inválidos responden `400`.

---

## 📋 Contrato del backend (`api/`)

### 1 · Endpoints nuevos

| Método | Ruta | Permiso | Respuesta |
|---|---|---|---|
| GET | `/api/alquileres` | `ALQUILERES_VER_TODOS` | `200` con todos los alquileres (más recientes primero) |
| GET | `/api/alquileres?estado=ENTREGADO` | `ALQUILERES_VER_TODOS` | `200` solo con los de ese estado |
| PATCH | `/api/alquileres/{id}/entregar` | `ALQUILERES_GESTIONAR` | `200` con el alquiler en `ENTREGADO` |
| PATCH | `/api/alquileres/{id}/devolver` | `ALQUILERES_GESTIONAR` | `200` con el alquiler en `DEVUELTO` |
| GET | `/api/reportes/resumen` | `REPORTES_VER` | `200` con el resumen |
| POST | `/api/auth/registro` | Pública | `201` con `{ "token": "..." }` |
| POST | `/api/herramientas` | `HERRAMIENTAS_GESTIONAR` | `201` con la herramienta creada |
| PUT | `/api/herramientas/{id}` | `HERRAMIENTAS_GESTIONAR` | `200` con la herramienta editada |

### 2 · El resumen

```json
{
  "ingresosMes": 1250000,
  "alquileresActivos": 7,
  "vencidos": [ { "id": 3, "herramientaNombre": "Pulidora angular", "clienteNombre": "Jorge Pinzón", "estado": "ENTREGADO", "...": "los mismos campos de un alquiler" } ],
  "masAlquiladas": [ { "herramienta": "Taladro percutor", "alquileres": 9 } ]
}
```

### 3 · El registro

```json
{ "nombre": "Camilo Torres", "email": "camilo@correo.com", "password": "obra2026segura" }
```

| Campo | Regla (`400` si no se cumple) |
|---|---|
| `nombre` | Obligatorio, máximo 80 caracteres |
| `email` | Obligatorio, con formato de email |
| `password` | Obligatoria, mínimo 8 caracteres |

Responde `201` con el token de la cuenta nueva, igual que el login.

### 4 · Crear y editar herramientas

```json
{ "nombre": "Compresor 50 L", "categoria": "ELECTRICA", "descripcion": "Para pintar y limpiar", "precioDia": 20000, "deposito": 120000, "unidades": 2, "imagenUrl": "https://..." }
```

| Campo | Regla (`400` si no se cumple) |
|---|---|
| `nombre` | Obligatorio, máximo 80 caracteres. Repetido: `409` |
| `categoria` | Obligatoria: `ELECTRICA`, `CONSTRUCCION`, `JARDIN` o `MEDICION` |
| `precioDia` | Mínimo 1.000 |
| `deposito` | Mínimo 0 |
| `unidades` | Entre 1 y 50 |
| `descripcion`, `imagenUrl` | Opcionales, máximo 300 caracteres |

Una herramienta nueva entra **activa**. Editar no cambia si está activa: para eso ya existe `PATCH /api/herramientas/{id}/estado`. Editar una que no existe: `404`.

### 5 · Tus datos sembrados

Agrega en `DataInitializer` el alquiler que Jorge no devolvió: la **Pulidora angular** a nombre de **Jorge Pinzón**, en estado `ENTREGADO`, del día `hoy - 10` al día `hoy - 3`. Es el vencido que Hernán debe ver en su panel.

> **Ojo:** `DataInitializer` solo siembra si la base está vacía. Para verlo en tu PostgreSQL, borra las tablas o crea la base de nuevo. Las pruebas usan una base en memoria y siempre lo siembran.

---

## 📋 Contrato del frontend (`web/src/app/`)

### 6 · Servicios

| Archivo · clase | Miembro nuevo | Qué hace |
|---|---|---|
| `services/reportes.service.ts` · `ReportesService` | `resumen(): Observable<Resumen>` | `GET /api/reportes/resumen` |
| `services/alquileres.service.ts` · `AlquileresService` | `cargarTodos(estado?)` | `GET /api/alquileres` (con `?estado=` si llega) y llena `alquileres()` |
| | `entregar(alquiler)`, `devolver(alquiler)` | `PATCH .../entregar` o `.../devolver` y reemplazan ese alquiler en la lista. Si falla, `error()` trae el mensaje |
| `services/herramientas.service.ts` · `HerramientasService` | `crear(datos)`, `actualizar(id, datos)` | `POST` y `PUT`; devuelven el `Observable` para que la página decida |
| `core/auth.service.ts` · `AuthService` | `registrarse({ nombre, email, password })` | `POST /api/auth/registro`, guarda el token y entra, igual que el login |

### 7 · `core/validaciones.ts`

`validarCuenta({ nombre, email, password }, confirmacion): string[]`: un error por cada regla que falle (nombre vacío, email sin formato, contraseña de menos de 8, confirmación distinta). Lista vacía = se puede enviar.

### 8 · Páginas

**Panel** · `pages/panel/panel.ts` · clase `Panel` · ruta `panel`

| `data-testid` | Qué es |
|---|---|
| `ingresos-mes` | El facturado del mes, con `pesos(...)` |
| `alquileres-activos` | El número de activos |
| `vencido` | Cada vencido (herramienta y cliente) |
| `sin-vencidos` | El mensaje cuando no hay vencidos (`@empty`) |
| `mas-alquilada` | Cada fila del ranking, en el orden en que llega |

El panel **no suma nada**: muestra lo que responde el backend.

**Alquileres** · `pages/alquileres/alquileres.ts` · clase `Alquileres` · ruta `alquileres`

| `data-testid` | Qué es |
|---|---|
| `filtro-estado` | Un `<select>`: al cambiarlo, vuelve a pedir `GET /api/alquileres?estado=...` (el backend filtra) |
| `alquiler` | Cada alquiler, con herramienta, **cliente**, fechas y valor |
| `estado-alquiler` | El estado, dentro de cada alquiler |
| `boton-entregar` | **Solo** en lo `RESERVADO` |
| `boton-devolver` | **Solo** en lo `ENTREGADO` |
| `sin-alquileres` | El mensaje cuando la lista está vacía |

**Registro** · `pages/registro/registro.ts` · clase `Registro` · ruta `registro` (pública)

- Un `<form>` con `name="nombre"`, `name="email"`, `name="password"` y `name="confirmacion"`. Lee los datos con `FormData`, como el login.
- Antes de enviar, `validarCuenta(...)`. Si hay errores, los muestra como `<li>` dentro de `data-testid="errores-registro"` y **no** envía nada.
- Envía **solo** `{ nombre, email, password }`: ni la confirmación ni un rol.
- Si el backend responde error, muestra su mensaje en `data-testid="error-registro"`.
- En el login, un enlace `data-testid="enlace-registro"` a `/registro`.

**Formulario de herramientas** · `pages/herramienta-form/herramienta-form.ts` · clase `HerramientaForm` · rutas `herramientas/nueva` y `herramientas/:id/editar`

- Input `id = input<string>()`: no llega en `nueva`; en `editar`, carga la herramienta con `obtener(...)` y llena los campos.
- Campos: `name="nombre"`, `name="categoria"` (`<select>`), `name="precioDia"`, `name="deposito"`, `name="unidades"`, `name="descripcion"` y `name="imagenUrl"`.
- `precioDia`, `deposito` y `unidades` se envían como **números** (`Number(...)`).
- Sin `id` hace `POST`; con `id`, `PUT`. Si sale bien, vuelve a `/herramientas`. Si no, el mensaje del backend en `data-testid="error-herramienta"`.
- En la página Herramientas, con `*appSiPuede="'HERRAMIENTAS_GESTIONAR'"`: un enlace `data-testid="boton-nueva-herramienta"` a `/herramientas/nueva` y, en cada herramienta, `data-testid="boton-editar"` a `/herramientas/{id}/editar`.

### 9 · Rutas y menú

| Ruta | Guards |
|---|---|
| `registro` | Ninguno: es pública |
| `panel` | `authGuard`, `permisoGuard('REPORTES_VER')` |
| `alquileres` | `authGuard`, `permisoGuard('ALQUILERES_VER_TODOS')` |
| `herramientas/nueva` | `authGuard`, `permisoGuard('HERRAMIENTAS_GESTIONAR')` |
| `herramientas/:id/editar` | `authGuard`, `permisoGuard('HERRAMIENTAS_GESTIONAR')` |

Todas con `loadComponent`. En el menú: `data-testid="menu-panel"` (`REPORTES_VER`) y `data-testid="menu-alquileres"` (`ALQUILERES_VER_TODOS`).

---

## 🔍 Revisión entre pares

La profe te asigna el Pull Request de un compañero. Revísalo con esta lista y deja **un comentario** en su PR (pestaña **Files changed** → **Review changes** → **Comment**) que cubra los **6 puntos**:

| # | Qué revisas | Cómo lo compruebas |
|---|---|---|
| 1 | **Seguridad:** cada endpoint nuevo tiene `@PreAuthorize` con el permiso correcto | Busca `@PreAuthorize` en los controladores |
| 2 | **Reglas en el servicio:** entregar, devolver y el registro deciden en el servicio, no en el controlador | Lee el servicio y el controlador |
| 3 | **El registro no acepta rol:** `RegistroRequest` no tiene `rol` y el servicio pone `CLIENTE` | Lee el DTO y el servicio |
| 4 | **Nombres y responsabilidades:** cada archivo hace una sola cosa y su nombre lo dice | Recorre `controller/`, `service/`, `pages/` |
| 5 | **La interfaz:** muestra lo que puede hacer cada rol y se ve bien en el celular | Corre su proyecto o revisa sus `*appSiPuede` y su CSS |
| 6 | **Una mejora concreta:** qué cambiarías, en qué archivo y por qué | Escríbela con respeto y con el archivo y la línea |

Un buen comentario dice **qué viste**, **dónde** y **qué sugieres**. "Está bien" o "Está mal" no cuentan. Y responde con respeto el comentario que te dejen a ti: la revisión entre pares vale el **4 %** de la nota del taller (dada y respondida).

---

## ✅ Cómo saber que vas bien

```bash
npm run calificar -- 3
```

La Fase 3 tiene **10 partes y 56 pruebas**:

| Parte | Qué revisa | Pruebas |
|---|---|---|
| 3.1 | Backend · Todos los alquileres, entregar y devolver | 6 |
| 3.2 | Backend · Resumen del negocio | 6 |
| 3.3 | Backend · Registro público | 5 |
| 3.4 | Backend · Crear y editar herramientas | 5 |
| 3.5 | Angular · Servicios | 7 |
| 3.6 | Angular · Panel | 5 |
| 3.7 | Angular · Alquileres de Hernán | 5 |
| 3.8 | Angular · Registro y `validarCuenta` | 6 |
| 3.9 | Angular · Formulario de herramientas | 5 |
| 3.10 | Estructura de la Fase 3 | 6 |

### En el navegador

- Hernán entra y ve el Panel: el facturado del mes, los activos y la pulidora de Jorge en **vencidos**.
- En Alquileres filtra por `Reservado`, entrega uno y, en `Entregado`, recibe la devolución.
- Crea una herramienta nueva y Pedro la ve en el catálogo; corrige un precio con Editar.
- Desde el login, alguien nuevo crea su cuenta y entra como cliente; con un email repetido ve el mensaje del backend.
- Pedro no ve Panel, Alquileres, Nueva herramienta ni Editar.

---

## 🤖 Uso de IA en esta fase

- **Nivel permitido: 1 · Revisor.** Escribe tú el código y **después** pídele a la IA que revise un archivo.
- Prompt sugerido: *"Este es mi ReporteServiceImpl de un sistema de alquiler de herramientas [pégalo]. Las reglas son: [pega la tabla 'Qué cuenta en el panel']. ¿Qué casos no estoy cubriendo? No reescribas el código."*
- No le pidas a la IA que haga la revisión de tu compañero: la revisión es tuya.
- Registra lo que usaste en [`BITACORA-IA.md`](../BITACORA-IA.md).

---

## 📤 Entrega

1. En tu fork, **Sync fork → Update branch**, y luego `git pull` en tu computador (así te llegan esta fase y sus pruebas).
2. `git add .` → `git commit -m "Fase 3"` → `git push`.
3. Tu Pull Request se actualiza solo: **no abras otro**.

**Checkpoint:** domingo 8 de noviembre, 11:59 p. m. Ese día la profe revisa que la Fase 3 esté avanzada y que hayas dejado tu comentario de revisión.

Recuerda: la nota se calcula sobre las fases publicadas. Al salir la Fase 3, tu nota baja hasta que la completes. Es normal.

---

<sub>© 2026 Ana Alvarado · Educadora Tech & Desarrolladora Full Stack · Todos los derechos reservados · linkedin.com/in/ana-alvarado-instructora-full-stack</sub>
