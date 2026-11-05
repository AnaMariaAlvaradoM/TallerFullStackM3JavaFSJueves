# 🔧 Fase 4 · Calidad, refactor y entrega

**Taller Módulo 3 · Llave Maestra** · Se publica: jueves 5 de noviembre · **Entrega final: fecha por confirmar**

---

## 📨 Lo que pide Hernán

> Les tengo una noticia: voy a pedirle un crédito al banco para comprar más herramientas, y me pidieron mostrarles el sistema.
>
> El banco va a mandar a **su propio ingeniero** a revisarlo. Me dijo que lo primero que abre es el **README**, que va a intentar **entrar sin permiso** y que quiere ver el código **ordenado**.
>
> Yo no sé de eso. Lo que sí sé es que si él no lo puede instalar ni entender sin llamarlos a ustedes, para el banco el sistema no existe.
>
> Ah, y me pidieron un **video corto** mostrando cómo funciona.

---

## 🎯 Qué vas a construir

Lo mismo que hicimos con Colmena en la Clase 4, aplicado a Llave Maestra:

| En clase (Colmena) | Tú (Llave Maestra) |
|---|---|
| Matriz de ataques: token adulterado, token vencido, ruta escrita a mano | Tu backend **rechaza** todo token que no salió de él (401) |
| Ninguna respuesta trae contraseñas ni detalles del servidor | Lo mismo en Llave Maestra |
| Refactor: controladores que solo hablan con servicios (SOLID) | Controladores sin repositorios, servicios con interfaz, sin `System.out` |
| `loadComponent`, `track`, `loading="lazy"`, `ng build` sin advertencias | Lo mismo en tu frontend |
| `@Tag` y `@Operation` en Swagger, README profesional | Swagger documentado y **el README de tu proyecto** |
| Demo por rol para el portafolio | **Video** de máximo 3 minutos |

Esta fase casi no tiene funciones nuevas: es la que hace que **otra persona pueda confiar en tu trabajo**.

---

## 🔒 1 · Seguridad final

### Lo que el ingeniero del banco va a intentar

| # | Ataque | Qué debe responder tu backend |
|---|---|---|
| 1 | Toma el token de Pedro, le cambia los permisos por todos y lo usa | `401`: la firma ya no coincide |
| 2 | Toma el token de Pedro y cambia el dueño (`sub`) por `admin@llavemaestra.co` | `401` |
| 3 | Fabrica un token con todos los permisos, firmado con **otra** clave | `401` |
| 4 | Manda un token con `"alg": "none"` (sin firma) | `401` |
| 5 | Usa un token bien firmado pero **vencido** | `401` |
| 6 | Usa un token bien firmado de una cuenta que **no existe** | `401` |
| 7 | Escribe a mano la URL de un endpoint que no le corresponde | `403` (ya lo tienes desde la Fase 1) |

Si tu `JwtService` verifica la firma con `verifyWith(...)` y tu `JwtFilter` busca a la persona en la base de datos en cada petición, los seis primeros ya están cubiertos. Esta fase lo **demuestra**.

### La clave del JWT

```properties
jwt.secret=${JWT_SECRET:llave-maestra-clave-secreta-de-desarrollo-2026}
```

- `${JWT_SECRET:...}` significa: usa la variable de entorno `JWT_SECRET`; si no existe, usa el valor de desarrollo.
- **¿Por qué importa?** Quien lea la clave en un repo público puede fabricar tokens con todos los permisos, y tu backend los aceptaría como buenos.
- El valor de desarrollo debe tener **al menos 32 caracteres**. La contraseña de la base de datos sigue la misma idea: `${DB_PASSWORD:postgres}`.

### Lo que ninguna respuesta debe traer

| Dónde | Qué no debe aparecer |
|---|---|
| `GET /api/usuarios`, `GET /api/alquileres`, `GET /api/alquileres/mios` | Ningún campo `password`, `clave` o `hash`, ni una contraseña cifrada (`$2a$…`) |
| `POST /api/auth/login` | Solo `{ "token": "..." }`. El token tampoco lleva la contraseña |
| Login con clave equivocada **y** login con un email que no existe | El **mismo** `401` con el **mismo** mensaje. Si son distintos, cualquiera averigua qué emails están registrados |
| Un JSON mal armado o un id que no es número (`/api/alquileres/abc/entregar`) | `400` con `{ codigo, mensaje, fecha }`, sin `trace`, sin `Exception`, sin líneas `at com.…` |

---

## 🧹 2 · Refactor (SOLID) y limpieza

### Backend

| # | Regla | Por qué |
|---|---|---|
| 1 | **Ningún controlador importa un repositorio** | El controlador recibe la petición y llama al servicio. La regla de negocio vive en un solo lugar (S) |
| 2 | Los controladores reciben la **interfaz** (`AlquilerService`), nunca la implementación (`AlquilerServiceImpl`) | Dependes de lo que se promete, no de cómo se hace (D) |
| 3 | `AlquilerService`, `AuthService`, `HerramientaService`, `ReporteService` y `UsuarioService` son `public interface` en `service/`, cada una con su `...Impl` con `@Service` en `service/impl/` | Mismo motivo |
| 4 | Cada endpoint, menos login y registro, lleva `@PreAuthorize` (en el método o en la clase) | Si alguien escribe la URL a mano, el backend decide |
| 5 | Sin `System.out.println` ni `printStackTrace()` en `api/src/main` | Ensucian el registro del servidor y pueden mostrar datos |

### Frontend

| # | Regla | Por qué |
|---|---|---|
| 1 | **Todas** las rutas con `loadComponent`, y `app.routes.ts` no importa las páginas arriba | Cada página se descarga cuando se visita, no al abrir la app |
| 2 | Cada `@for` con `track` del campo que no se repite (`track a.id`), nunca `track $index` | Angular reutiliza los elementos en vez de redibujar toda la lista |
| 3 | Las imágenes que vienen del backend (`[src]="h.imagenUrl"`) llevan `loading="lazy"` | El navegador no las descarga hasta que están cerca de la pantalla |
| 4 | Toda `<img>` tiene `alt` (vacío si es decorativa) | Accesibilidad |
| 5 | Ninguna página usa `HttpClient`: eso lo hacen los servicios | Igual que controlador → servicio en Spring |
| 6 | Sin `console.log` ni `debugger` en `web/src/app` | Cualquiera los ve en la consola del navegador |
| 7 | `ng build` termina **sin errores y sin advertencias** (incluidas las de presupuesto: CSS de un componente de más de 4 kB) | Es lo que se sube a producción |

> **Tip:** si `ng build` te dice que el CSS de un componente pasa de 4 kB, mueve lo que se repite (botones, tarjetas, avisos) a `src/styles.css`.

---

## 📚 3 · Documentación

### Swagger

- Título **"Llave Maestra API"** y una descripción, en `OpenApiConfig` (ya viene así: no lo borres).
- El botón **Authorize** (esquema `bearer`).
- Cada controlador con `@Tag(name = "...")`: así Swagger no los agrupa como `alquiler-controller`.
- **Cada** endpoint con `@Operation(summary = "...")`.

```java
@RestController
@Tag(name = "Alquileres", description = "Crear, consultar, cancelar, entregar y devolver alquileres")
@RequestMapping("/api/alquileres")
public class AlquilerController {

    @PreAuthorize("hasAuthority('ALQUILERES_GESTIONAR')")
    @Operation(summary = "Entregar una herramienta", description = "RESERVADO → ENTREGADO. 409 si no está reservado.")
    @PatchMapping("/{id}/entregar")
    public ResponseEntity<AlquilerResponse> entregar(@PathVariable Long id) { ... }
}
```

Los imports son `io.swagger.v3.oas.annotations.Operation` e `io.swagger.v3.oas.annotations.tags.Tag`.

### El README de tu proyecto

**Reemplaza** el `README.md` de la raíz (el de las instrucciones del taller) por el README de **tu** proyecto. Las instrucciones no se pierden: siguen en `fases/` y en el repo de la profe. Así, cuando alguien abra tu repo en GitHub, ve tu proyecto y no un enunciado.

Debe empezar con `# ... Llave Maestra ...` y tener estas **8 secciones** (`##`), con estos títulos:

| # | Sección | Qué lleva |
|---|---|---|
| 1 | `## Qué es` | Un párrafo: el problema de Hernán y qué resuelve el sistema |
| 2 | `## Tecnologías` | Spring Boot, Angular, PostgreSQL, JWT (y lo demás que uses) |
| 3 | `## Cómo levantarlo` | Crear `llave_db`, el comando del backend (`mvnw`), `npm install` y `ng serve` |
| 4 | `## Variables de entorno` | `JWT_SECRET`, `DB_USER` y `DB_PASSWORD`: para qué es cada una y su valor por defecto |
| 5 | `## Usuarios de prueba` | Los 4 emails y la clave `llave123` |
| 6 | `## Roles y permisos` | `ADMIN`, `CLIENTE` y los 9 permisos |
| 7 | `## Decisiones técnicas` | Al menos 3 decisiones, una por viñeta, con su **por qué** |
| 8 | `## Demo` | El enlace a tu video |

La prueba revisa que estén las secciones y lo mínimo de cada una. **Que se entienda y que funcione** lo revisa la profe (8 % de la nota): alguien que nunca vio el proyecto debe poder levantarlo siguiendo solo tu README.

### Bitácora de IA

`BITACORA-IA.md` con **al menos 4 entradas**, con las 7 columnas llenas, y al menos una de la Fase 4 (por ejemplo, el borrador del README).

---

## 🎥 4 · Video demo

Máximo **3 minutos**, con tu voz o con subtítulos. Súbelo a YouTube (oculto), Google Drive (con enlace público) o Loom, y pon el enlace en la sección `## Demo` de tu README.

Debe mostrar estos **5 momentos**:

1. Login como **Pedro** y como **Hernán**: cada uno ve un menú distinto.
2. Un alquiler que el sistema **rechaza** por una regla (por ejemplo, Diana pide la mezcladora en las fechas de Pedro) y el mensaje que ve.
3. Pedro **cancela** uno de sus alquileres.
4. Hernán en el **panel**: facturado del mes y la pulidora **vencida** de Jorge.
5. Hernán **bloquea** a un cliente, y ese cliente ya no puede entrar.

El video no se califica solo: lo revisa la profe (8 % de la nota).

---

## ✅ Cómo saber que vas bien

```bash
npm run calificar -- 4
```

La Fase 4 tiene **8 partes y 42 pruebas**:

| Parte | Qué revisa | Pruebas |
|---|---|---|
| 4.1 | Backend · Tokens que no se dejan engañar | 6 |
| 4.2 | Backend · Sin datos sensibles | 6 |
| 4.3 | Backend · Swagger documentado | 5 |
| 4.4 | Refactor del backend (SOLID) | 6 |
| 4.5 | Angular · Optimización y limpieza | 6 |
| 4.6 | Angular · Build de producción (tarda un poco más: compila todo el frontend) | 2 |
| 4.7 | README del proyecto | 8 |
| 4.8 | Bitácora de IA | 3 |

### Antes de entregar, pruébalo como el ingeniero del banco

- Clona tu repo en **otra carpeta** y levántalo siguiendo **solo** tu README. Si tuviste que hacer algo que el README no dice, agrégalo.
- Abre Swagger: cada grupo tiene nombre y cada endpoint dice qué hace.
- Copia tu token desde el navegador, cámbiale una letra del medio en https://jwt.io y úsalo en Swagger: debe dar `401`.
- `ng build` en `web/`: sin advertencias.

---

## 🤖 Uso de IA en esta fase

- **Nivel permitido: 2 · Copiloto.** La IA puede escribir un **borrador** del README o proponer un refactor. Tú lo lees, lo corriges y lo pruebas.
- Prompt sugerido para el README: *"Este es mi proyecto Llave Maestra: [pega los usuarios, la tabla de permisos y los comandos que usas]. Arma un README con estas 8 secciones: [pega la tabla de arriba]. Usa solo los datos que te doy; si te falta algo, pregúntame."*
- Prompt sugerido para el refactor: *"Este es mi AlquilerController [pégalo]. ¿Qué responsabilidades tiene que deberían estar en el servicio? No reescribas el archivo: dime qué mover y por qué."*
- **Verifica todo:** la IA suele inventar comandos (`mvn` en vez de `mvnw`), permisos que no existen o puertos equivocados.
- **Nunca** le pegues tu `JWT_SECRET` real ni contraseñas.
- Registra lo que usaste en [`BITACORA-IA.md`](../BITACORA-IA.md).

---

## 📤 Entrega final

1. En tu fork, **Sync fork → Update branch**, y luego `git pull` en tu computador (así te llegan esta fase y sus pruebas). Hazlo **antes** de reemplazar el README.
2. `git add .` → `git commit -m "Fase 4"` → `git push`.
3. Tu Pull Request se actualiza solo: **no abras otro**.

**Entrega final:** la profe confirma la fecha pronto. Mientras tanto, cada `push` actualiza tu nota.

### Cómo queda la nota del taller

| Componente | Peso |
|---|---|
| Pruebas automáticas (las 4 fases) | **70 %** |
| README que levanta sin ayuda | 8 % |
| Video demo con los 5 momentos | 8 % |
| `PRUEBAS.md` | 4 % |
| Revisión entre pares (dada y respondida) | 4 % |
| Bitácora de IA | 3 % |
| Se ve profesional en computador y en celular | 3 % |

Recuerda: la nota se calcula sobre las fases publicadas. Al salir la Fase 4, tu nota baja hasta que la completes. Es normal.

---

<sub>© 2026 Ana Alvarado · Educadora Tech & Desarrolladora Full Stack · Todos los derechos reservados · linkedin.com/in/ana-alvarado-instructora-full-stack</sub>
