# Matriz de pruebas · Alquiler de herramientas

Completa una fila por caso (mínimo 12). Escribe el **Esperado ANTES de probar** y el **Obtenido** después, en Swagger o en la app.
Ejemplo de una fila llena: `| 1 | Alquiler válido de 3 días | Pedro | Taladro, del +2 al +4 | 201 · 3 días · $ 75.000 | 201 · igual |`

| # | Regla o caso | Como quién | Datos | Esperado | Obtenido |
|---|---|---|---|---|---|
| 1 | Login exitoso con credenciales válidas | Hernán (ADMIN) | admin@llavemaestra.co / admin123 | 200 OK con token JWT y rol ADMIN | 200 OK con token JWT y rol ADMIN |
| 2 | Rechazo de login con contraseña incorrecta | Anónimo | admin@llavemaestra.co / claveErrada | 401 Unauthorized · Credenciales inválidas | 401 Unauthorized · Credenciales inválidas |
| 3 | Bloqueo de acceso no autenticado al catálogo | Anónimo | GET /api/herramientas | 401 Unauthorized | 401 Unauthorized |
| 4 | Consulta de catálogo con sesión activa | Pedro (CLIENTE) | GET /api/herramientas | 200 OK con lista de herramientas disponibles | 200 OK con lista de herramientas disponibles |
| 5 | Creación de alquiler válido de 3 días | Pedro (CLIENTE) | Taladro percutor, inicio: hoy+2, fin: hoy+4 | 201 Created · 3 días · $ 75.000 | 201 Created · 3 días · $ 75.000 |
| 6 | Rechazo de alquiler con fecha fin anterior a inicio | Pedro (CLIENTE) | Taladro percutor, inicio: hoy+5, fin: hoy+2 | 400 Bad Request · Fin antes de inicio | 400 Bad Request · Fin antes de inicio |
| 7 | Rechazo de alquiler de más de 15 días | Pedro (CLIENTE) | Taladro percutor, inicio: hoy+1, fin: hoy+20 | 400 Bad Request · Máximo 15 días permitidos | 400 Bad Request · Máximo 15 días permitidos |
| 8 | Cancelar alquiler propio en estado RESERVADO | Pedro (CLIENTE) | PATCH /api/alquileres/{id}/cancelar en reserva futura | 200 OK · estado CANCELADO | 200 OK · estado CANCELADO |
| 9 | Impedir cancelar alquiler ajeno | Jorge (CLIENTE) | PATCH /api/alquileres/{idPedro}/cancelar | 403 Forbidden · Acceso denegado | 403 Forbidden · Acceso denegado |
| 10 | Entregar herramienta de alquiler RESERVADO | Hernán (ADMIN) | PATCH /api/alquileres/{id}/entregar | 200 OK · estado pasa a ENTREGADO | 200 OK · estado pasa a ENTREGADO |
| 11 | Recibir devolución de alquiler ENTREGADO | Hernán (ADMIN) | PATCH /api/alquileres/{id}/devolver | 200 OK · estado pasa a DEVUELTO | 200 OK · estado pasa a DEVUELTO |
| 12 | Consultar resumen ejecutivo y métricas de panel | Hernán (ADMIN) | GET /api/reportes/resumen | 200 OK con ingresos, activos, ranking y vencidos | 200 OK con ingresos, activos, ranking y vencidos |
| 13 | Rechazo a cliente que intenta consultar resumen | Pedro (CLIENTE) | GET /api/reportes/resumen | 403 Forbidden · Requiere REPORTES_VER | 403 Forbidden · Requiere REPORTES_VER |
| 14 | Registro de nueva cuenta cliente | Anónimo | POST /api/auth/registro { nombre, email, password } | 201 Created con token de sesión iniciada | 201 Created con token de sesión iniciada |
