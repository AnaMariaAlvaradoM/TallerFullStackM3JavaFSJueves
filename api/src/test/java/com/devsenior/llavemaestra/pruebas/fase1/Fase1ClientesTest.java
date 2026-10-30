package com.devsenior.llavemaestra.pruebas.fase1;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 1.4 · Clientes y cuentas bloqueadas (solo con CLIENTES_GESTIONAR)
class Fase1ClientesTest extends PruebaApi {

    private Respuesta cambiarEstado(String token, long id, boolean activo) {
        return patch("/api/usuarios/" + id + "/estado", token, "{\"activa\":" + activo + "}");
    }

    @Test
    void elAdministradorVeLasCuentasSinContrasenas() {
        Respuesta r = get("/api/usuarios", token(ADMIN));
        assertEquals(200, r.estado(), "GET /api/usuarios como administrador debería responder 200");
        List<String> emails = new ArrayList<>();
        r.json().forEach(u -> emails.add(u.path("email").asString()));
        assertTrue(emails.containsAll(List.of(ADMIN, PEDRO, DIANA, JORGE)), "Deberían aparecer las 4 cuentas sembradas");
        for (JsonNode u : r.json()) {
            assertFalse(u.has("password"), "Las cuentas NUNCA deberían devolver la contraseña (usa un DTO de respuesta)");
        }
    }

    @Test
    void elClienteRecibe403AlListarCuentas() {
        assertEquals(403, get("/api/usuarios", token(PEDRO)).estado(), "Pedro no tiene CLIENTES_GESTIONAR: GET /api/usuarios debería responder 403");
    }

    @Test
    void elClienteRecibe403AlBloquearOtraCuenta() {
        long diana = usuario(DIANA).path("id").asLong();
        assertEquals(403, cambiarEstado(token(PEDRO), diana, false).estado(),
                "Pedro no debería poder bloquear a Diana (403)");
    }

    @Test
    void unaCuentaBloqueadaNoEntraYAlDesbloquearlaVuelveAEntrar() {
        long diana = usuario(DIANA).path("id").asLong();
        String admin = token(ADMIN);
        try {
            Respuesta r = cambiarEstado(admin, diana, false);
            assertEquals(200, r.estado(), "El administrador debería poder bloquear a Diana (200)");
            assertFalse(r.json().path("activa").asBoolean(), "La respuesta debería traer la cuenta con activa = false");
            assertEquals(401, login(DIANA, CLAVE).estado(), "Diana bloqueada no debería poder iniciar sesión (401)");
        } finally {
            assertEquals(200, cambiarEstado(admin, diana, true).estado(), "El administrador debería poder desbloquear a Diana (200)");
        }
        assertEquals(200, login(DIANA, CLAVE).estado(), "Al desbloquearla, Diana debería volver a entrar");
    }

    @Test
    void elTokenQueYaTeniaUnaCuentaBloqueadaDejaDeServir() {
        long diana = usuario(DIANA).path("id").asLong();
        String admin = token(ADMIN);
        String tokenViejo = token(DIANA);
        try {
            assertEquals(200, cambiarEstado(admin, diana, false).estado(), "El administrador debería poder bloquear a Diana (200)");
            assertEquals(401, get("/api/herramientas", tokenViejo).estado(),
                    "Con Diana bloqueada, su token anterior debería recibir 401 (el JwtFilter debe revisar que la cuenta siga activa)");
        } finally {
            cambiarEstado(admin, diana, true);
        }
    }

    @Test
    void elAdministradorNoPuedeBloquearseASiMismo() {
        long hernan = usuario(ADMIN).path("id").asLong();
        Respuesta r = cambiarEstado(token(ADMIN), hernan, false);
        assertEquals(409, r.estado(), "El administrador no debería poder bloquearse a sí mismo (409)");
        tieneMensaje(r, "Bloquearse a sí mismo");
        assertEquals(200, login(ADMIN, CLAVE).estado(), "Hernán debería seguir pudiendo entrar");
    }
}
