# Bitácora de uso de IA

Registra cada vez que uses una IA en el taller. **Una respuesta de IA no es evidencia; la verificación sí.**

| Fecha | Fase | Qué intentaba resolver | Prompt que usé | Qué acepté o rechacé | Cómo lo comprobé | Qué aprendí |
|---|---|---|---|---|---|---|
|  |  |  |  |  |  |  |
# Bitácora de uso de IA — Fase 1 (Llave Maestra)

## 1. Alcance trabajado en la Fase 1
- Backend en Spring Boot (`com.devsenior.llavemaestra.api`) y frontend en Angular (`web`).
- Configuración de seguridad, permisos/roles, manejo de errores en formato `{ codigo, mensaje, fecha }` y vistas/flujos base de la fase.

## 2. Consultas y apoyo recibido
- *Diseño y estructura:* orientación paso a paso sobre capas/archivos y contratos esperados.
- *Depuración y ajustes:* corrección de imports, tipos, dependencias y errores de compilación detectados en el editor o consola.

## 3. Cambios y decisiones revisadas por mí
- Adaptación de los ejemplos al paquete y estructura real del repositorio (`com.devsenior.llavemaestra.api` y estructura de carpetas en `web/src/app`).
- Revisión del código pegado para evitar duplicados, mantener la coherencia con la Fase 1 y asegurar que compilara sin errores.

## 4. Validación realizada
- Revisión de errores en el editor (`PROBLEMS` / consola de compilación).
- Pruebas funcionales en la aplicación / endpoints y ejecución de `npm run calificar -- 1`.