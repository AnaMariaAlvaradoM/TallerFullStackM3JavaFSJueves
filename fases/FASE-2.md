# 🔧 Fase 2 · Alquilar con reglas

**Taller Módulo 3 · Llave Maestra** · Se publica: jueves 22 de octubre · **Checkpoint: miércoles 4 de noviembre, 11:59 p. m.**

---

## 📨 Lo que pide Hernán

> Ahora sí: quiero que mis clientes **separen las herramientas solos**. Pero que no vuelva a pasar lo de la mezcladora: el sistema no puede alquilar más unidades de las que tengo.
>
> No alquilo por más de **15 días seguidos**, y nadie puede tener más de **3 alquileres activos** a la vez. Cada cliente ve y cancela **solo lo suyo**, y solo mientras no se lo haya entregado.
>
> Y antes de abrirlo a todos mis clientes, **demuéstrenme** que las reglas funcionan.

---

## 🎯 Qué vas a construir

Lo mismo que hicimos con Colmena en la Clase 2, aplicado a Llave Maestra:

| En clase (Colmena) | Tú (Llave Maestra) |
|---|---|
| Reserva por horas, bloques de 30 min | Alquiler por **días**, máximo 15 |
| No se cruzan dos reservas del mismo espacio | No se cruzan **más alquileres que unidades** |
| — | Máximo **3 alquileres activos** por cliente |
| `validarReserva(...)` en `core/validaciones.ts` | `validarAlquiler(...)` en `core/validaciones.ts` |
| Reservar · Mis reservas | Alquilar · Mis alquileres |
| Matriz de pruebas de la reserva | `PRUEBAS.md` del alquiler |

---

## 📐 Las reglas del negocio

### Cómo se cuentan los días

Se cuentan **el primero y el último**: del 10 al 12 son **3 días**. Si inicio y fin son el mismo día, es **1 día**.

### Qué es un alquiler "activo"

Uno en estado `RESERVADO` o `ENTREGADO`. Un alquiler `CANCELADO` o `DEVUELTO` ya no ocupa la herramienta.

### Cuándo dos alquileres "se cruzan"

Cuando comparten al menos un día: uno del 10 al 12 y otro del 12 al 14 **sí** se cruzan (el día 12). Uno del 10 al 12 y otro del 13 al 15, **no**.

### Las reglas al alquilar

| # | Regla | Respuesta |
|---|---|---|
| 1 | Faltan `herramientaId`, `fechaInicio` o `fechaFin` | `400` |
| 2 | La herramienta no existe | `404` |
| 3 | La herramienta está retirada (`activa = false`) | `409` |
| 4 | La fecha de fin es anterior a la de inicio | `400` |
| 5 | La fecha de inicio ya pasó (hoy **sí** se puede) | `400` |
| 6 | Más de 15 días (15 exactos **sí** se puede) | `400` |
| 7 | El cliente ya tiene 3 alquileres activos | `409` |
| 8 | Los alquileres activos que se cruzan ya son tantos como las unidades | `409` |

Todos los errores responden `{ codigo, mensaje, fecha }` con un mensaje claro para el cliente.

### Las reglas al cancelar

| # | Regla | Respuesta |
|---|---|---|
| 1 | El alquiler no existe | `404` |
| 2 | No es tuyo (y no tienes `ALQUILERES_GESTIONAR`) | `403` |
| 3 | Ya no está en `RESERVADO` (ya se entregó, se devolvió o se canceló) | `409` |
| 4 | Empieza hoy o ya empezó | `409` |

---

## 📋 Contrato del backend (`api/`)

### 1 · Endpoints nuevos

| Método | Ruta | Permiso | Respuesta |
|---|---|---|---|
| POST | `/api/alquileres` | `ALQUILERES_CREAR` | `201` con el alquiler creado |
| GET | `/api/alquileres/mios` | `ALQUILERES_VER_PROPIOS` | `200` con los alquileres de quien pide |
| PATCH | `/api/alquileres/{id}/cancelar` | `ALQUILERES_CANCELAR_PROPIOS` **o** `ALQUILERES_GESTIONAR` | `200` con el alquiler en `CANCELADO` |

El cuerpo del `POST` es solo esto:

```json
{ "herramientaId": 1, "fechaInicio": "2026-11-10", "fechaFin": "2026-11-12" }
```

### 2 · Lo que responde un alquiler

`id`, `herramientaId`, `herramientaNombre`, `clienteId`, `clienteNombre`, `fechaInicio`, `fechaFin`, `dias`, `valorTotal`, `deposito`, `estado`, `creadoEn`.

### 3 · Lo que decide el backend (nunca el JSON)

- **El cliente sale del token**, aunque el JSON traiga un `clienteId`.
- `dias`, `valorTotal = dias × precioDia` y `deposito` (el de la herramienta) los calcula el backend, aunque el JSON traiga otros valores.
- Un alquiler nuevo queda en `RESERVADO`. Los estados son un enum: `RESERVADO`, `ENTREGADO`, `DEVUELTO`, `CANCELADO`.

### 4 · Dónde va cada regla

- La regla "solo cancelas lo tuyo" va en el **servicio** (lanza `AccessDeniedException`), no en el controlador: depende del dato, no solo del permiso.
- El controlador de cancelar usa `@PreAuthorize("hasAnyAuthority('ALQUILERES_CANCELAR_PROPIOS', 'ALQUILERES_GESTIONAR')")`.

### 5 · Tus datos sembrados

Puedes sembrar alquileres de ejemplo, pero **Pedro y Diana pueden tener como máximo 1 alquiler activo cada uno** y ninguno con fechas a más de 200 días de hoy. Las pruebas crean sus propios alquileres en fechas lejanas.

---

## 📋 Contrato del frontend (`web/src/app/`)

### 6 · `core/validaciones.ts`

Las **mismas** reglas de fechas del backend, para avisar antes de enviar.

| Función | Qué hace |
|---|---|
| `calcularDias(fechaInicio, fechaFin): number` | Días contando el primero y el último. `0` si falta una fecha o están invertidas |
| `validarAlquiler({ fechaInicio, fechaFin }, hoy): string[]` | Lista de errores; vacía si se puede enviar |

Las fechas son textos `'AAAA-MM-DD'` (lo que entrega un `<input type="date">`). `hoy` también.

`validarAlquiler` revisa, en este orden:

1. Un error por cada fecha que falte (si falta alguna, no revisa lo demás).
2. Fin antes de inicio.
3. Inicio antes de `hoy`.
4. Más de 15 días: el mensaje debe decir **15**.

**Pista:** para no pelear con las zonas horarias, convierte `'2026-11-10'` en un número de día con `Date.UTC(anio, mes - 1, dia)`.

### 7 · `core/errores.ts`

`mensajeDeError(error: HttpErrorResponse): string`:

| Caso | Devuelve |
|---|---|
| `status` 0 (backend apagado) | Un mensaje que mencione el **servidor** |
| `status` 403 | Un mensaje que mencione el **permiso** |
| El backend mandó `{ mensaje }` | Ese mensaje, tal cual |
| Cualquier otro | Un mensaje genérico (nunca vacío) |

### 8 · Servicios

`services/alquileres.service.ts` · clase `AlquileresService`:

| Miembro | Qué hace |
|---|---|
| `crear(datos): Observable<Alquiler>` | `POST /api/alquileres` con `{ herramientaId, fechaInicio, fechaFin }`. Devuelve el `Observable` para que la página decida qué hacer |
| `cargarMios()` | `GET /api/alquileres/mios` y llena `alquileres()` |
| `cancelar(alquiler)` | `PATCH /api/alquileres/{id}/cancelar` y reemplaza ese alquiler en la lista. Si falla, `error()` trae el mensaje |
| `alquileres()`, `cargando()`, `error()` | Señales |

En `HerramientasService` agrega `obtener(id: number): Observable<Herramienta>` (`GET /api/herramientas/{id}`).

### 9 · Página Alquilar

`pages/alquilar/alquilar.ts` · clase `Alquilar` · selector `app-alquilar`.

- Input `id = input.required<string>()`: llega de la URL gracias a `withComponentInputBinding()` en `app.config.ts`.
- Al iniciar, pide la herramienta y muestra su nombre en `data-testid="nombre-herramienta"`.
- Un `<form>` con `(submit)` y dos campos: `name="fechaInicio"` y `name="fechaFin"` (`type="date"`). Lee los datos con `FormData`, como el login.
- Escucha `(input)` en el `<form>` para mostrar, **antes de enviar**, los días y el valor en `data-testid="resumen-valor"` (por ejemplo: *3 días · $ 75.000*).
- Al enviar, primero `validarAlquiler(...)`. Si hay errores, los muestra como `<li>` dentro de `data-testid="errores-formulario"` y **no** envía nada.
- Si todo está bien, envía `{ herramientaId: <número>, fechaInicio, fechaFin }`.
- Si el backend responde bien, navega a `/mis-alquileres`. Si responde error, muestra su mensaje en `data-testid="error-alquiler"`.

### 10 · Página Mis alquileres

`pages/mis-alquileres/mis-alquileres.ts` · clase `MisAlquileres` · selector `app-mis-alquileres`.

| `data-testid` | Qué es |
|---|---|
| `alquiler` | Cada alquiler (con nombre de la herramienta, fechas, días, valor y depósito) |
| `estado-alquiler` | El estado, dentro de cada alquiler |
| `boton-cancelar` | **Solo** si está en `RESERVADO` y empieza después de hoy |
| `confirmar-cancelacion` | Aparece al hacer clic en Cancelar; al confirmarlo, se cancela |
| `sin-alquileres` | El mensaje cuando la lista está vacía (`@empty`) |

Nada de `window.confirm()` ni `alert()`: la confirmación va dentro de la página.

### 11 · Herramientas, rutas y menú

- En cada herramienta **activa**, un enlace `data-testid="boton-alquilar"` a `/herramientas/{id}/alquilar`, **solo** con `*appSiPuede="'ALQUILERES_CREAR'"`.
- Rutas nuevas, con `loadComponent`:

| Ruta | Guards |
|---|---|
| `herramientas/:id/alquilar` | `authGuard`, `permisoGuard('ALQUILERES_CREAR')` |
| `mis-alquileres` | `authGuard`, `permisoGuard('ALQUILERES_VER_PROPIOS')` |

- En el menú, el enlace `data-testid="menu-mis-alquileres"` con el permiso `ALQUILERES_VER_PROPIOS`.

---

## 🧪 `PRUEBAS.md`: demuéstraselo a Hernán

Llena la matriz que está en la raíz del repo: **mínimo 12 casos**, todas las columnas llenas.

1. Escribe primero el **Esperado** de cada caso (sin probar).
2. Pruébalo **en Swagger** (sin Angular) y escribe el **Obtenido**.
3. Incluye casos normales, **límites** (15 días exactos, el día justo después de otro alquiler) y errores, como Pedro, como Diana y como Hernán.

Si un Obtenido no coincide con el Esperado, encontraste un error: corrígelo y vuelve a probar.

---

## ✅ Cómo saber que vas bien

```bash
npm run calificar -- 2
```

La Fase 2 tiene **9 partes y 58 pruebas**:

| Parte | Qué revisa | Pruebas |
|---|---|---|
| 2.1 | Backend · Crear alquiler (cálculo, dueño y permisos) | 6 |
| 2.2 | Backend · Reglas de fechas | 5 |
| 2.3 | Backend · Disponibilidad (retirada, unidades, máximo 3) | 6 |
| 2.4 | Backend · Mis alquileres y cancelar | 6 |
| 2.5 | Angular · `validaciones.ts` | 9 |
| 2.6 | Angular · `mensajeDeError` y `AlquileresService` | 7 |
| 2.7 | Angular · Página Alquilar | 6 |
| 2.8 | Angular · Mis alquileres y botón Alquilar | 6 |
| 2.9 | Estructura y `PRUEBAS.md` | 7 |

### En el navegador

- Pedro alquila el taladro 3 días y ve *3 días · $ 75.000* antes de enviar.
- Si escoge 16 días, el formulario le avisa sin llamar al backend.
- Si la mezcladora ya está alquilada en esas fechas, ve el mensaje del backend.
- En Mis alquileres cancela el suyo (con confirmación); lo que empieza hoy ya no tiene botón.
- Hernán no ve "Alquilar" ni "Mis alquileres".

---

## 🤖 Uso de IA en esta fase

- **Nivel permitido: 1 · Revisor.** Arma tú la matriz de `PRUEBAS.md` y **después** pídele a la IA que la revise y proponga casos que te faltan.
- Prompt sugerido: *"Estas son las reglas de alquiler de una herramienta [pega las reglas]. Esta es mi matriz de pruebas [pégala]. ¿Qué casos límite o de error me faltan? No escribas código."*
- Prueba en Swagger cada caso que te proponga antes de agregarlo, y regístralo en [`BITACORA-IA.md`](../BITACORA-IA.md).

---

## 📤 Entrega

1. `git add .` → `git commit -m "Fase 2"` → `git push`.
2. Tu Pull Request se actualiza solo: **no abras otro**.
3. Revisa la nota en el comentario del PR.

**Checkpoint:** miércoles 4 de noviembre, 11:59 p. m. Ese día la profe revisa que la Fase 2 esté en marcha y que `PRUEBAS.md` exista.

---

<sub>© 2026 Ana Alvarado · Educadora Tech & Desarrolladora Full Stack · Todos los derechos reservados · linkedin.com/in/ana-alvarado-instructora-full-stack</sub>
