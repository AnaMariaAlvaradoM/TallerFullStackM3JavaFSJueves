# Matriz de pruebas · Alquiler de herramientas

Completa una fila por caso (mínimo 12). Escribe el **Esperado ANTES de probar** y el **Obtenido** después, en Swagger o en la app.
Ejemplo de una fila llena: `| 1 | Alquiler válido de 3 días | Pedro | Taladro, del +2 al +4 | 201 · 3 días · $ 75.000 | 201 · igual |`

> Las fechas se escriben relativas a hoy: `+60` = dentro de 60 días, `-1` = ayer, `hoy` = la fecha del día.
> Probado contra la API real (`http://localhost:8080`, PostgreSQL) el 5 de noviembre de 2026, con los usuarios sembrados (clave `llave123`).
> Al empezar, Pedro tenía 1 alquiler activo sembrado (Taladro, +5 a +7) y Diana ninguno activo.

| # | Regla o caso | Como quién | Datos | Esperado | Obtenido |
|---|---|---|---|---|---|
| 1 | Alquiler válido de 3 días (normal) | Pedro | Taladro percutor, del +60 al +62 | 201 · 3 días · $ 75.000 · RESERVADO | 201 · 3 días · $ 75.000 · RESERVADO · cliente Pedro Salazar |
| 2 | Inicio y fin el mismo día = 1 día (límite) | Pedro | Andamio tubular, del +70 al +70 | 201 · 1 día · $ 15.000 | 201 · 1 día · $ 15.000 |
| 3 | 15 días exactos sí se puede (límite) | Diana | Andamio tubular, del +80 al +94 | 201 · 15 días · $ 225.000 | 201 · 15 días · $ 225.000 |
| 4 | Regla 6: más de 15 días | Diana | Andamio tubular, del +80 al +95 (16 días) | 400 · mensaje con "15 días" | 400 · "El alquiler máximo es de 15 días (pediste 16)" |
| 5 | Regla 4: fin antes de inicio | Pedro | Taladro, inicio +62, fin +60 | 400 · fechas invertidas | 400 · "La fecha de fin no puede ser anterior a la fecha de inicio" |
| 6 | Regla 5: inicio en el pasado | Pedro | Taladro, del -1 al +1 | 400 · fecha pasada | 400 · "La fecha de inicio ya pasó: elige hoy o una fecha futura" |
| 7 | Regla 1: falta fechaInicio | Pedro | `{ herramientaId, fechaFin: +65 }` | 400 · "Falta la fecha de inicio" | 400 · "Falta la fecha de inicio" |
| 8 | Regla 2: herramienta que no existe | Pedro | herramientaId 999999, del +60 al +61 | 404 · herramienta inexistente | 404 · "No existe la herramienta con id 999999" |
| 9 | Regla 3: herramienta retirada | Pedro | Nivel láser (activa = false), del +60 al +61 | 409 · herramienta retirada | 409 · "La herramienta Nivel láser está retirada y no se puede alquilar" |
| 10 | Mezcladora libre (1 unidad) | Diana | Mezcladora de concreto, del +100 al +102 | 201 · 3 días · $ 180.000 | 201 · 3 días · $ 180.000 |
| 11 | Regla 8: se cruza con otro (comparten el día +100) | Diana | Mezcladora, del +100 al +100 (ya alquilada del +100 al +102) | 409 · sin unidades | 409 · "No quedan unidades de Mezcladora de concreto para esas fechas" |
| 12 | El día justo después de otro alquiler (límite) | Diana | Mezcladora, del +103 al +104 (el otro termina el +102) | 201 · 2 días · $ 120.000 | 201 · 2 días · $ 120.000 |
| 13 | Regla 7: cuarto alquiler activo | Pedro (ya con 3 activos: sembrado + casos 1 y 2) | Guadañadora, del +110 al +111 | 409 · máximo 3 activos | 409 · "Ya tienes 3 alquileres activos: no puedes tener más de 3 a la vez" |
| 14 | El administrador no alquila | Hernán | Taladro, del +60 al +61 | 403 (no tiene ALQUILERES_CREAR) | 403 · "No tienes permiso para realizar esta acción" |
| 15 | Sin token | Nadie | Taladro, del +60 al +61, sin `Authorization` | 401 | 401 · "Debes iniciar sesión para continuar" |
| 16 | Cancelar: no es tuyo | Diana | Cancelar el alquiler del caso 1 (de Pedro) | 403 | 403 · "No tienes permiso para realizar esta acción" |
| 17 | Cancelar lo propio, RESERVADO y futuro | Pedro | Cancelar el alquiler del caso 1 | 200 · estado CANCELADO | 200 · CANCELADO |
| 18 | Cancelar dos veces | Pedro | Cancelar otra vez el alquiler del caso 1 | 409 · ya no está RESERVADO | 409 · "Este alquiler está CANCELADO: solo se puede cancelar un alquiler RESERVADO" |
| 19 | Mis alquileres sin el permiso | Hernán | GET /api/alquileres/mios | 403 (no tiene ALQUILERES_VER_PROPIOS) | 403 · "No tienes permiso para realizar esta acción" |
| 20 | El JSON intenta mandar cliente y valor | Pedro | Taladro +120 a +121 con `clienteId` de Diana, `valorTotal: 1`, `deposito: 0` | 201 · cliente Pedro · $ 50.000 · depósito $ 100.000 | 201 · cliente Pedro Salazar · $ 50.000 (el backend ignora lo que manda el JSON) |
| 21 | Empieza hoy: se crea pero no se cancela | Pedro | Andamio, del hoy al hoy; luego cancelar | 201 al crear · 409 al cancelar | 201 al crear · 409 "Ya no se puede cancelar: el alquiler empieza hoy o ya empezó" |
