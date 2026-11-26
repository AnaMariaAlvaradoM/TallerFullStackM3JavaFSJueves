# Llave Maestra

## Qué es

**Llave Maestra** es una aplicación web full stack para la gestión operativa de una alquiladora de herramientas en Cajicá, administrada por Hernán Rojas. Permite controlar el catálogo de herramientas, su disponibilidad y retiro, la administración de clientes, el registro y cancelación de alquileres con validación de fechas, y la operación diaria con control de acceso basado en roles.

## Tecnologías

- **Backend:** Java 17+, Spring Boot, Spring Security, JWT (JSON Web Tokens), Spring Data JPA, Hibernate, PostgreSQL (`llave_db`) y H2 para pruebas.
- **Frontend:** Angular 22, TypeScript, HTML5, CSS3 (componentes standalone, lazy loading con `loadComponent`, directivas de permisos con `hasAuthority`).
- **Documentación y herramientas:** Swagger / OpenAPI, Maven Wrapper (`mvnw`), Node.js / npm, Git.

## Variables de entorno

- `JWT_SECRET`: Clave secreta utilizada por el backend en Spring Boot para firmar y validar los tokens JWT de autenticación.
- `DB_PASSWORD`: Contraseña de conexión a la base de datos PostgreSQL.
- `SPRING_DATASOURCE_URL`: URL de conexión a la base de datos PostgreSQL (por ejemplo, `jdbc:postgresql://localhost:5432/llave_db`).
- `SPRING_DATASOURCE_USERNAME`: Usuario de PostgreSQL (por ejemplo, `postgres`).
- `SPRING_DATASOURCE_PASSWORD`: Contraseña del datasource de PostgreSQL.
- `SERVER_PORT`: Puerto donde se ejecuta la API de Spring Boot (por defecto `8080`).

## Usuarios de prueba

- **Hernán Rojas (Administrador):** `hernan@llavemaestra.co` — Contraseña: `llave123`
- **Pedro (Cliente):** `pedro@correo.com` — Contraseña: `llave123`

## Cómo levantarlo

1. Crea una base de datos vacía llamada `llave_db` en PostgreSQL y configura las variables o credenciales en `api/src/main/resources/application.properties`.
2. Para levantar el backend desde la carpeta `api/`, ejecuta `./mvnw spring-boot:run` (en Windows `mvnw.cmd spring-boot:run`). La API queda disponible en `http://localhost:8080` y Swagger en `http://localhost:8080/swagger-ui.html`.
3. Para levantar el frontend desde la carpeta `web/`, instala las dependencias con `npm install` y inicia la aplicación con `ng serve -o` (o `npm start`), disponible en `http://localhost:4200`.
4. Para ejecutar las pruebas automáticas desde la raíz del repositorio, ejecuta `npm run calificar`.

## Roles y permisos

- **Administrador (`ADMIN`):** Gestión completa de herramientas (crear, editar, retirar), administración de clientes (listar, bloquear y desbloquear), gestión operativa de alquileres (entregar, devolver) y acceso al resumen del negocio.
- **Cliente (`CLIENTE`):** Consulta del catálogo de herramientas y disponibilidad, creación de alquileres propios, consulta de sus alquileres y cancelación de alquileres propios.
- **Autoridades (`hasAuthority`):** Cada endpoint del backend y ruta/elemento de interfaz en Angular valida permisos granulares mediante autoridades en el token JWT.