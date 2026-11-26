# 🔑 Llave Maestra — Sistema de Alquiler de Herramientas

> Sistema web Full Stack para que Hernán gestione el alquiler de sus herramientas industriales.  
> Backend en Spring Boot · Frontend en Angular · Base de datos PostgreSQL · Autenticación JWT

---

## Qué es

Hernán es dueño de un taller y alquila herramientas industriales (taladros, pulidoras, mezcladoras, etc.) a clientes de la zona. Hasta ahora llevaba todo en cuadernos y calculaba los cobros a mano. Con **Llave Maestra** puede:

- Publicar su catálogo de herramientas con precio por día y depósito.
- Dejar que sus clientes consulten la disponibilidad y reserven en línea.
- Ver en tiempo real qué herramientas están reservadas, entregadas o vencidas.
- Consultar el facturado del mes y bloquear clientes problemáticos.

Los clientes solo ven y reservan; Hernán administra el ciclo completo (reservar → entregar → devolver).

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Java 21 · Spring Boot 3 · Spring Security · Spring Data JPA |
| Autenticación | JWT (jjwt) firmado con HMAC-SHA256 |
| Base de datos | PostgreSQL 16 (producción) · H2 en memoria (pruebas) |
| Frontend | Angular 22 · Standalone components · Signals · Tailwind CSS |
| Documentación | Springdoc OpenAPI 2 (Swagger UI) |
| Pruebas | JUnit 5 · Vitest |

---

## Cómo levantarlo

### Requisitos previos
- Java 21+
- Node.js 22.22.3+ o 24+
- PostgreSQL 16+ corriendo localmente

### 1. Base de datos

```sql
-- En psql o pgAdmin:
CREATE DATABASE llave_db;
```

### 2. Backend

```bash
# Dentro de la carpeta api/
cp .env.example .env        # o crea api/.env con tus valores (ver Variables de entorno)
./mvnw spring-boot:run      # Linux/Mac
mvnw.cmd spring-boot:run    # Windows
```

La API queda disponible en **http://localhost:8080**  
Swagger UI: **http://localhost:8080/swagger-ui.html**

### 3. Frontend

```bash
# Dentro de la carpeta web/
npm install
ng serve
```

La app queda disponible en **http://localhost:4200**

---

## Variables de entorno

Crea el archivo `api/.env` con las siguientes variables:

| Variable | Para qué sirve | Valor por defecto |
|---|---|---|
| `JWT_SECRET` | Clave para firmar y verificar los tokens JWT. Mín. 32 caracteres. | `llave-maestra-clave-secreta-de-desarrollo-2026-segura` |
| `DB_USERNAME` | Usuario de PostgreSQL | `postgres` |
| `DB_PASSWORD` | Contraseña de PostgreSQL | `postgres` |
| `URL` | URL JDBC completa de la base de datos | `jdbc:postgresql://localhost:5432/llave_db` |

> **Importante:** nunca subas el archivo `.env` a un repositorio público. El `.gitignore` ya lo excluye.

Ejemplo de `api/.env`:
```properties
URL=jdbc:postgresql://localhost:5432/llave_db
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=llave-maestra-clave-secreta-de-desarrollo-2026-segura
```

---

## Usuarios de prueba

| Rol | Email | Contraseña |
|---|---|---|
| ADMIN (Hernán) | `admin@llavemaestra.co` | `llave123` |
| CLIENTE | `pedro@correo.com` | `llave123` |
| CLIENTE | `diana@correo.com` | `llave123` |
| CLIENTE | `jorge@correo.com` | `llave123` |

---

## Roles y permisos

| Rol | Permisos |
|---|---|
| `ADMIN` | `HERRAMIENTAS_VER` · `HERRAMIENTAS_GESTIONAR` · `ALQUILERES_VER_TODOS` · `ALQUILERES_GESTIONAR` · `ALQUILERES_CANCELAR_PROPIOS` · `CLIENTES_GESTIONAR` · `REPORTES_VER` |
| `CLIENTE` | `HERRAMIENTAS_VER` · `ALQUILERES_CREAR` · `ALQUILERES_VER_PROPIOS` · `ALQUILERES_CANCELAR_PROPIOS` |

**Los 9 permisos del sistema:**
1. `HERRAMIENTAS_VER` — Consultar el catálogo
2. `HERRAMIENTAS_GESTIONAR` — Crear, editar, activar/retirar herramientas
3. `ALQUILERES_CREAR` — Reservar una herramienta
4. `ALQUILERES_VER_PROPIOS` — Ver los alquileres propios
5. `ALQUILERES_VER_TODOS` — Ver todos los alquileres del sistema
6. `ALQUILERES_GESTIONAR` — Entregar y devolver herramientas
7. `ALQUILERES_CANCELAR_PROPIOS` — Cancelar alquiler propio (estado RESERVADO)
8. `CLIENTES_GESTIONAR` — Activar o bloquear clientes
9. `REPORTES_VER` — Ver el panel ejecutivo con facturado y estadísticas

---

## Decisiones técnicas

- **JWT verificado con firma HMAC-SHA256 en cada petición:** el `JwtFilter` extrae el email del token y busca al usuario en la base de datos antes de autenticar. Así, un token adulterado (firma cambiada), un token vencido o un token de una cuenta que ya no existe, todos dan `401`. Esta verificación cubre los 6 ataques de la Fase 4 sin código adicional.

- **Un solo mensaje de error para credenciales incorrectas:** tanto "email no existe" como "contraseña incorrecta" devuelven exactamente el mismo `401 { "mensaje": "Email o contraseña incorrectos" }`. Revelar cuál de los dos falló permitiría a un atacante descubrir qué cuentas existen (ataque de enumeración de usuarios).

- **Control de disponibilidad por unidades, no por herramienta:** el sistema cuenta cuántos alquileres activos (`RESERVADO` o `ENTREGADO`) se superponen con las fechas pedidas y los compara con `unidades`. Esto permite que Hernán tenga dos martillos y los alquile en paralelo sin conflicto, reflejando con exactitud la realidad del inventario.

- **Separación estricta de capas (SOLID):** los controladores solo conocen la interfaz del servicio (ej. `AlquilerService`), nunca la implementación (`AlquilerServiceImpl`) ni los repositorios. Las implementaciones viven en `service/impl/`. Esto hace que cambiar la lógica de negocio no afecte a los controladores, y facilita escribir pruebas unitarias con mocks.

- **Lazy loading de componentes en Angular:** todas las rutas usan `loadComponent` para cargar cada página solo cuando el usuario la visita. Esto reduce el bundle inicial de la app y acelera el primer pintado.

---

## Demo

> 📹 **[Ver video demo en YouTube](https://www.youtube.com/watch?v=dQw4w9WgXcQ)** *(pendiente de enlace definitivo — actualizar con el video real)*

El video muestra:
1. Login como Pedro y como Hernán (menús distintos por rol)
2. Intento de alquiler rechazado por solapamiento de fechas
3. Pedro cancela uno de sus alquileres
4. Hernán revisa el panel: facturado del mes y herramientas vencidas
5. Hernán bloquea a un cliente y ese cliente ya no puede entrar

---

<sub>Proyecto Final · Módulo 3 · DevSenior Code · 2026</sub>
