# Bitácora de uso de IA · Llave Maestra
h
Registro de interacciones y apoyo de inteligencia artificial durante el desarrollo del proyecto.

| Fecha | Fase / Tema | Herramienta IA | Prompt / Objetivo | Qué se generó o decidió | Verificación realizada | Estado |
|---|---|---|---|---|---|---|
| 2026-11-26 | Fase 1 · Control de acceso y sesión | Gemini | Revisar estructura de autenticación, roles y protección de rutas en el proyecto | Se definió el flujo de login, autoridades y guardias manteniendo el patrón de la aplicación | Ejecución de pruebas de la fase y revisión en navegador | En revisión |
| 2026-11-26 | Fase 2 · Reglas de alquiler y fechas | Gemini | Analizar validaciones de fechas, disponibilidad y cancelación de alquileres | Se estructuraron las reglas de negocio para evitar solapamientos y validar propiedad del alquiler | Pruebas con el calificador del taller y revisión de respuestas HTTP | En progreso |
| 2026-11-26 | Fase 3 · Servicios y vistas de negocio | Gemini | Guiar conexión de servicios Angular y operaciones de herramientas y alquileres | Se organizaron los métodos de servicio y componentes standalone siguiendo las convenciones del repo | Verificación con `npm run calificar` y pruebas en entorno local | En progreso |
| 2026-11-26 | Fase 4 · Documentación y calidad | Gemini | Preparar el `README.md` del proyecto y la bitácora de uso de IA | Se redactó la documentación propia de Llave Maestra y el registro tabular con 7 columnas | Ejecución de `npm run calificar -- 4.7` y `npm run calificar -- 4.8` | Completado |