# 🔧 Fase 1 · Base y control de acceso

**Taller Módulo 3 · Llave Maestra** · Se publica: jueves 22 de octubre · **Checkpoint: miércoles 28 de octubre, 11:59 p. m.**

---

## 📨 El correo de Hernán

> **De:** Hernán Rojas · hernan@llavemaestra.co
> **Asunto:** Sistema de alquiler para Llave Maestra
>
> Tengo una alquiladora de herramientas hace seis años y lo anoto todo en un cuaderno. Este mes alquilé la misma mezcladora a dos clientes, mi ayudante canceló un alquiler que no era de quien llamó, y un cliente vio lo que les cobro a los demás.
>
> Lo primero: que cada persona entre con su cuenta y vea **solo lo que le corresponde**. Mis clientes ven el catálogo. Yo veo todo, retiro las herramientas que están en reparación y **bloqueo** al que no devuelve.

---

## 🎯 Qué vas a construir

Lo mismo que hicimos con Colmena en la Clase 1, aplicado a Llave Maestra:

| En clase (Colmena) | Tú (Llave Maestra) |
|---|---|
| `Permiso` + `Rol` (ADMIN, USER) | `Permiso` + `Rol` (ADMIN, CLIENTE) |
| Espacios: el miembro ve los activos | Herramientas: el cliente ve las activas |
| Desactivar un espacio | Retirar una herramienta |
| Miembros: desactivar cuentas | Clientes: bloquear cuentas |
| `permisoGuard`, `*appSiPuede`, menú por permisos | Lo mismo, con los permisos de Llave Maestra |

El código de Colmena es tu **referencia**, no tu plantilla: si lo copias sin cambiar el negocio, las pruebas lo notan.

---

## 🧭 La matriz de permisos

| Permiso | ADMIN | CLIENTE |
|---|---|---|
| `HERRAMIENTAS_VER` | ✅ | ✅ |
| `HERRAMIENTAS_GESTIONAR` | ✅ | |
| `ALQUILERES_CREAR` | | ✅ |
| `ALQUILERES_VER_PROPIOS` | | ✅ |
| `ALQUILERES_CANCELAR_PROPIOS` | | ✅ |
| `ALQUILERES_VER_TODOS` | ✅ | |
| `ALQUILERES_GESTIONAR` | ✅ | |
| `CLIENTES_GESTIONAR` | ✅ | |
| `REPORTES_VER` | ✅ | |

Los permisos de alquileres y reportes se usan en las fases 2 y 3, pero **ya** van en `Permiso.java`, en `Rol.java` y en el token.

---

## 📋 Contrato del backend (`api/`)

Las pruebas usan estos nombres **tal cual**. El paquete base es `com.devsenior.llavemaestra`: no lo cambies.

### 1 · Archivos que revisan las pruebas de estructura

| Archivo | Qué debe tener |
|---|---|
| `model/Permiso.java` | `public enum Permiso` con los 9 permisos de la matriz |
| `model/Rol.java` | `public enum Rol` con `ADMIN(...)` y `CLIENTE(...)`, cada uno con su paquete de permisos |
| `controller/` | Al menos 3 controladores, con `@PreAuthorize("hasAuthority('...')")`. **Nada de `hasRole`** |
| `config/JwtFilter.java` | Verifica `isEnabled()` (o `isActivo()`) antes de autenticar |

### 2 · Datos sembrados (`config/DataInitializer.java`)

Clave de **todas** las cuentas: `llave123`.

| Nombre | Email | Rol | Activo |
|---|---|---|---|
| Hernán Rojas | `admin@llavemaestra.co` | ADMIN | sí |
| Pedro Salazar | `pedro@correo.com` | CLIENTE | sí |
| Diana Mejía | `diana@correo.com` | CLIENTE | sí |
| Jorge Pinzón | `jorge@correo.com` | CLIENTE | **no** |

| Nombre | `categoria` | `precioDia` | `deposito` | `unidades` | `activa` |
|---|---|---|---|---|---|
| Taladro percutor | `ELECTRICA` | 25000 | 100000 | 3 | sí |
| Pulidora angular | `ELECTRICA` | 22000 | 80000 | 2 | sí |
| Mezcladora de concreto | `CONSTRUCCION` | 60000 | 300000 | 1 | sí |
| Andamio tubular | `CONSTRUCCION` | 15000 | 120000 | 4 | sí |
| Guadañadora | `JARDIN` | 35000 | 150000 | 2 | sí |
| Nivel láser | `MEDICION` | 30000 | 200000 | 1 | **no** |

Las categorías son un enum: `ELECTRICA`, `CONSTRUCCION`, `JARDIN`, `MEDICION`. Agrega `descripcion` e `imagenUrl` (un enlace de `loremflickr.com`) a cada herramienta.

### 3 · El token

`POST /api/auth/login` con `{ "email", "password" }` responde `200` con `{ "token": "..." }`.

El payload del JWT trae estos *claims*:

| Claim | Ejemplo |
|---|---|
| `sub` | `pedro@correo.com` |
| `nombre` | `Pedro Salazar` |
| `rol` | `CLIENTE` |
| `permisos` | `["HERRAMIENTAS_VER", "ALQUILERES_CREAR", ...]` |
| `exp` | la fecha de vencimiento |

Clave incorrecta, email que no existe o cuenta bloqueada → `401` con `{ codigo, mensaje, fecha }`.

### 4 · Endpoints

| Método | Ruta | Permiso | Respuesta |
|---|---|---|---|
| POST | `/api/auth/login` | Público | `200` token |
| GET | `/api/herramientas` | `HERRAMIENTAS_VER` | ADMIN: todas · CLIENTE: solo las activas |
| GET | `/api/herramientas/{id}` | `HERRAMIENTAS_VER` | `200` o `404` |
| PATCH | `/api/herramientas/{id}/estado` | `HERRAMIENTAS_GESTIONAR` | Cuerpo `{ "activa": false }` · `200` o `404` |
| GET | `/api/usuarios` | `CLIENTES_GESTIONAR` | Las cuentas, **sin** contraseña |
| PATCH | `/api/usuarios/{id}/estado` | `CLIENTES_GESTIONAR` | Cuerpo `{ "activo": false }` · `200`, `404` o `409` |

Cada herramienta se responde con: `id`, `nombre`, `categoria`, `descripcion`, `precioDia`, `deposito`, `unidades`, `imagenUrl`, `activa`.

Cada cuenta se responde con: `id`, `nombre`, `email`, `rol`, `activo`, `creadoEn`.

### 5 · Reglas

- Sin token → `401`. Con token pero sin el permiso → `403`.
- El administrador **no** puede bloquearse a sí mismo → `409` con mensaje.
- Si Hernán bloquea a Diana, el token que ella ya tenía deja de servir (`401`) en la siguiente petición.
- Todo error responde `{ codigo, mensaje, fecha }` (el manejador ya viene listo).

---

## 📋 Contrato del frontend (`web/src/app/`)

### 6 · Permisos y sesión

| Archivo | Exporta | Qué hace |
|---|---|---|
| `core/permisos.ts` | `type Rol`, `type Permiso` | Los mismos nombres del backend |
| `core/errores.ts` | `mensajeDeError(error)` | Traduce un `HttpErrorResponse` a texto (lo calificamos en la Fase 2) |
| `core/auth.service.ts` | clase `AuthService` | La sesión (abajo) |

`AuthService` guarda el token en `localStorage` con la clave **`llave_token`** y lo lee al crearse. Tiene:

| Miembro | Tipo | Qué hace |
|---|---|---|
| `sesionActiva()` | señal | `true` si hay token y no ha vencido (`exp`) |
| `nombre()`, `rol()`, `permisos()`, `token()`, `error()`, `cargando()` | señales | Salen del token o del estado del login |
| `puede(permiso)` | método | `true` si el token trae ese permiso |
| `iniciarSesion({ email, password })` | método | `POST` al login, guarda el token y navega a `/herramientas`. Si falla, `error()` trae el mensaje del backend |
| `cerrarSesion()` | método | Borra el token y navega a `/login` |

Lee el payload del token como **UTF-8** (Hernán tiene tilde), igual que en Colmena.

### 7 · Interceptor, guards y directiva

| Archivo | Exporta |
|---|---|
| `core/jwt-interceptor.ts` | `jwtInterceptor`: agrega `Authorization: Bearer <token>` si hay sesión |
| `core/auth-guard.ts` | `authGuard`: sin sesión → `/login` |
| `core/permiso-guard.ts` | `permisoGuard(permiso)`: sin el permiso → `/sin-acceso` |
| `shared/si-puede.ts` | Directiva `SiPuede`, selector `[appSiPuede]`; reacciona si cambia la sesión |

En `app.config.ts`: `provideHttpClient(withInterceptors([jwtInterceptor]))`.

### 8 · Servicios

| Archivo | Clase | Métodos y señales |
|---|---|---|
| `services/herramientas.service.ts` | `HerramientasService` | `herramientas()`, `cargando()`, `error()`, `cargar()`, `cambiarEstado(herramienta, activa)` |
| `services/usuarios.service.ts` | `UsuariosService` | `usuarios()`, `cargando()`, `error()`, `cargar()`, `cambiarEstado(usuario, activo)` |

`cambiarEstado` hace el `PATCH` y reemplaza ese elemento en la lista con la respuesta.

### 9 · Páginas y rutas

| Ruta | Página (archivo · clase) | Guards |
|---|---|---|
| `login` | `pages/login/login.ts` · `Login` | — |
| `herramientas` | `pages/herramientas/herramientas.ts` · `Herramientas` | `authGuard`, `permisoGuard('HERRAMIENTAS_VER')` |
| `clientes` | `pages/clientes/clientes.ts` · `Clientes` | `authGuard`, `permisoGuard('CLIENTES_GESTIONAR')` |
| `sin-acceso` | `pages/sin-acceso/sin-acceso.ts` · `SinAcceso` | `authGuard` |

Todas con `loadComponent`. La ruta vacía redirige a `herramientas`.

### 10 · Los `data-testid`

| Dónde | `data-testid` | Qué es |
|---|---|---|
| Login | `error-login` | El mensaje cuando el login falla |
| Herramientas | `herramienta` | Cada tarjeta |
| Herramientas | `nombre-herramienta` | El nombre, dentro de la tarjeta |
| Herramientas | `precio-dia` | El precio por día con formato de pesos (`$ 25.000`; usa `pesos()` de `core/formato.ts`) |
| Herramientas | `boton-estado` | Retirar / volver a ofrecer. **Solo** con `*appSiPuede="'HERRAMIENTAS_GESTIONAR'"` |
| Clientes | `cliente` | Cada fila |
| Clientes | `boton-estado` | Bloquear / desbloquear, dentro de la fila (no en la tuya) |
| Menú (`app.html`) | `menu-herramientas`, `menu-clientes` | Cada enlace, solo si tiene el permiso |
| Barra (`app.html`) | `nombre-usuario`, `cerrar-sesion` | El nombre del token y el botón de salir |

---

## ✅ Cómo saber que vas bien

```bash
npm run calificar -- 1
```

La Fase 1 tiene **10 partes y 58 pruebas**:

| Parte | Qué revisa | Pruebas |
|---|---|---|
| 1.1 | Backend · Login y token | 7 |
| 1.2 | Backend · Catálogo | 5 |
| 1.3 | Backend · Retirar herramientas | 4 |
| 1.4 | Backend · Clientes y bloqueo | 6 |
| 1.5 | Angular · Sesión | 7 |
| 1.6 | Angular · Interceptor, guards y rutas | 6 |
| 1.7 | Angular · `*appSiPuede` | 4 |
| 1.8 | Angular · Herramientas | 5 |
| 1.9 | Angular · Clientes y menú | 6 |
| 1.10 | Estructura | 8 |

### En el navegador

- **Como Pedro:** ve 5 herramientas, sin botones de gestión, y el menú solo tiene Herramientas. Escribir `/clientes` a mano lo lleva a Sin acceso.
- **Como Hernán:** ve 6 herramientas (el Nivel láser como retirada), puede retirarlas y en Clientes bloquea a cualquiera menos a sí mismo.
- **Como Jorge:** no puede entrar.

### En Swagger

Repite lo mismo **sin Angular**: Pedro con su token recibe `403` al retirar una herramienta. Si eso no pasa, tu seguridad está solo en la interfaz.

---

## 🤖 Uso de IA en esta fase

- **Nivel permitido: 0 · Tutor.** Pregúntale a la IA para entender un error o un concepto; no le pidas que escriba tus clases.
- Desconfía de lo que te dé con `hasRole`, `@Input()`, `*ngIf` o `NgModule`: es la forma vieja.
- Escribe tu primera entrada en [`BITACORA-IA.md`](../BITACORA-IA.md).

---

## 📤 Entrega

1. `git add .` → `git commit -m "Fase 1"` → `git push`.
2. Si aún no lo tienes, abre **tu único Pull Request** con tu nombre completo como título (README, Paso 6).
3. Revisa la nota en el comentario del PR.

**Checkpoint:** miércoles 28 de octubre, 11:59 p. m. Ese día la profe revisa que tu PR exista y que la Fase 1 esté en marcha.

---

<sub>© 2026 Ana Alvarado · Educadora Tech & Desarrolladora Full Stack · Todos los derechos reservados · linkedin.com/in/ana-alvarado-instructora-full-stack</sub>
