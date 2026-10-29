package com.devsenior.llavemaestra.pruebas.fase1;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// 1.3 · Retirar y reactivar herramientas (solo con HERRAMIENTAS_GESTIONAR)
class Fase1PermisosHerramientasTest extends PruebaApi {

    @Test
    void elClienteRecibe403AlRetirarUnaHerramienta() {
        long id = idHerramienta("Taladro percutor");
        Respuesta r = patch("/api/herramientas/" + id + "/estado", token(PEDRO), "{\"activa\":false}");
        assertEquals(403, r.estado(), "Pedro no tiene HERRAMIENTAS_GESTIONAR: debería recibir 403 (¿usaste @PreAuthorize(\"hasAuthority(...)\")?)");
        assertTrue(herramienta("Taladro percutor").path("activa").asBoolean(), "El taladro debería seguir activa después del intento de Pedro");
    }

    @Test
    void sinTokenNoSePuedeCambiarElEstado() {
        long id = idHerramienta("Taladro percutor");
        assertEquals(200, patch("/api/herramientas/" + id + "/estado", token(ADMIN), "{\"activa\":true}").estado(),
                "PATCH /api/herramientas/{id}/estado como administrador debería responder 200");
        assertEquals(401, patch("/api/herramientas/" + id + "/estado", null, "{\"activa\":false}").estado(),
                "PATCH /api/herramientas/{id}/estado sin token debería responder 401");
    }

    @Test
    void elAdministradorRetiraYReactivaUnaHerramienta() {
        long id = idHerramienta("Guadañadora");
        String admin = token(ADMIN);
        try {
            Respuesta r = patch("/api/herramientas/" + id + "/estado", admin, "{\"activa\":false}");
            assertEquals(200, r.estado(), "El administrador debería poder retirar la Guadañadora (200)");
            assertFalse(r.json().path("activa").asBoolean(), "La respuesta debería traer la herramienta con activa = false");
            assertFalse(nombres(get("/api/herramientas", token(PEDRO)).json()).contains("Guadañadora"),
                    "Después de retirarla, Pedro ya no debería ver la Guadañadora");
        } finally {
            Respuesta r = patch("/api/herramientas/" + id + "/estado", admin, "{\"activa\":true}");
            assertEquals(200, r.estado(), "El administrador debería poder reactivar la Guadañadora (200)");
            assertTrue(r.json().path("activa").asBoolean(), "Al reactivarla debería volver con activa = true");
        }
    }

    @Test
    void cambiarElEstadoDeUnaHerramientaQueNoExisteResponde404() {
        Respuesta r = patch("/api/herramientas/999999/estado", token(ADMIN), "{\"activa\":false}");
        assertEquals(404, r.estado(), "PATCH /api/herramientas/999999/estado debería responder 404");
        tieneMensaje(r, "Herramienta inexistente");
    }
}
