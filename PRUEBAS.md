# Matriz de pruebas · Alquiler de herramientas

Completa una fila por caso (mínimo 12). Escribe el **Esperado ANTES de probar** y el **Obtenido** después, en Swagger o en la app.
Ejemplo de una fila llena: `| 1 | Alquiler válido de 3 días | Pedro | Taladro, del +2 al +4 | 201 · 3 días · $ 75.000 | 201 · igual |`

| # | Regla o caso | Como quién | Datos | Esperado | Obtenido |
|---|---|---|---|---|---|
| 1 |  |  |  |  |  |
| 2 |  |  |  |  |  |
| 3 |  |  |  |  |  |
| 4 |  |  |  |  |  |
| 5 |  |  |  |  |  |
| 6 |  |  |  |  |  |
| 7 |  |  |  |  |  |
| 8 |  |  |  |  |  |
| 9 |  |  |  |  |  |
| 10 |  |  |  |  |  |
| 11 |  |  |  |  |  |
| 12 |  |  |  |  |  |
  

# Pruebas — Fase 2: Alquilar con reglas

| # | Caso / Escenario | Usuario / Permiso | Esperado | Obtenido |
| :--- | :--- | :--- | :--- | :--- |
| 1 | Alquiler válido de 1 día (hoy) | Cliente con `ALQUILERES_CREAR` (ej. Pedro) | `201 Created`, estado `RESERVADO`, `dias = 1`, `valorTotal = precioDia * 1` | |
| 2 | Alquiler válido de 15 días exactos | Cliente con `ALQUILERES_CREAR` | `201 Created`, `dias = 15`, `valorTotal = precioDia * 15` | |
| 3 | Alquiler de 16 días (excede el límite) | Cliente con `ALQUILERES_CREAR` | `400 Bad Request` con `{ codigo: 400, mensaje, fecha }` | |
| 4 | `fechaInicio` en el pasado (`ayer`) | Cliente con `ALQUILERES_CREAR` | `400 Bad Request` con `{ codigo: 400, mensaje, fecha }` | |
| 5 | `fechaFin` anterior a `fechaInicio` | Cliente con `ALQUILERES_CREAR` | `400 Bad Request` con `{ codigo: 400, mensaje, fecha }` | |
| 6 | Faltan campos obligatorios (`herramientaId`, `fechaInicio` o `fechaFin`) | Cliente con `ALQUILERES_CREAR` | `400 Bad Request` con `{ codigo: 400, mensaje, fecha }` | |
| 7 | Herramienta inexistente (`id = 99999`) | Cliente con `ALQUILERES_CREAR` | `404 Not Found` con `{ codigo: 404, mensaje, fecha }` | |
| 8 | Herramienta retirada (`activa = false`) | Cliente con `ALQUILERES_CREAR` | `409 Conflict` con `{ codigo: 409, mensaje, fecha }` | |
| 9 | Cliente supera el máximo de 3 alquileres activos | Cliente con 3 alquileres `RESERVADO`/`ENTREGADO` | `409 Conflict` con `{ codigo: 409, mensaje, fecha }` | |
| 10 | Cruce de fechas sin unidades disponibles | Alquiler en fechas ocupadas por `>= unidades` | `409 Conflict` con `{ codigo: 409, mensaje, fecha }` | |
| 11 | Listar alquileres propios (`GET /api/alquileres/mios`) | Cliente con `ALQUILERES_VER_PROPIOS` | `200 OK` devolviendo solo los alquileres del usuario del token | |
| 12 | Cancelar alquiler futuro propio en `RESERVADO` | Dueño con `ALQUILERES_CANCELAR_PROPIOS` (`fechaInicio > hoy`) | `200 OK` con estado `CANCELADO` | |
| 13 | Intentar cancelar alquiler de otro usuario sin permiso de gestión | Otro usuario (ej. Diana o Hernán sin `ALQUILERES_GESTIONAR`) | `403 Forbidden` con `{ codigo: 403, mensaje, fecha }` | |
| 14 | Intentar cancelar alquiler que empieza hoy o ya empezó / no está `RESERVADO` | Dueño del alquiler | `409 Conflict` con `{ codigo: 409, mensaje, fecha }` | |