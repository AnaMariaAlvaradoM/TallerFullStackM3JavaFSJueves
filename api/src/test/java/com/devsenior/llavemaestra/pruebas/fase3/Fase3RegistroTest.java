package com.devsenior.llavemaestra.pruebas.fase3;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 3.3 · Registro publico: siempre CLIENTE, sin emails repetidos
class Fase3RegistroTest extends PruebaApi {

    private Respuesta registrar(String nombre, String email, String clave, String extra) {
        return post("/api/auth/registro", null, "{\"nombre\":\"" + nombre + "\",\"email\":\"" + email
                + "\",\"password\":\"" + clave + "\"" + extra + "}");
    }

    private List<String> permisos(JsonNode payload) {
        List<String> permisos = new ArrayList<>();
        payload.path("permisos").forEach(p -> permisos.add(p.asString()));
        return permisos;
    }

    @Test
    void elRegistroCreaUnClienteConSuToken() {
        Respuesta r = registrar("Camilo Torres", emailNuevo("camilo"), "obra2026segura", "");
        assertEquals(201, r.estado(), "POST /api/auth/registro con datos válidos debería responder 201. Respondió " + r.estado() + ": " + r.mensaje());
        assertTrue(r.json().has("token"), "El registro debería responder { \"token\": \"...\" }, igual que el login");
        JsonNode payload = payload(r.json().path("token").asString());
        assertEquals("CLIENTE", payload.path("rol").asString(), "Quien se registra es CLIENTE");
        assertEquals("Camilo Torres", payload.path("nombre").asString(), "El token debería traer el nombre de la cuenta nueva");
        assertTrue(permisos(payload).contains("ALQUILERES_CREAR"), "Un cliente nuevo debería tener ALQUILERES_CREAR en su token");
    }

    @Test
    void elRegistroIgnoraElRolQueVengaEnElJson() {
        Respuesta r = registrar("Intruso", emailNuevo("intruso"), "clave-larga-2026", ",\"rol\":\"ADMIN\"");
        assertEquals(201, r.estado(), "El registro debería crearse (201) e ignorar el campo rol. Respondió " + r.estado() + ": " + r.mensaje());
        String token = r.json().path("token").asString();
        JsonNode payload = payload(token);
        assertEquals("CLIENTE", payload.path("rol").asString(), "Aunque el JSON diga \"rol\": \"ADMIN\", el registro SIEMPRE crea un CLIENTE");
        assertFalse(permisos(payload).contains("REPORTES_VER"), "Una cuenta registrada no debería tener REPORTES_VER");
        assertEquals(403, resumen(token).estado(), "La cuenta registrada no debería poder ver el resumen (403)");
    }

    @Test
    void unEmailRepetidoResponde409() {
        Respuesta r = registrar("Otro Pedro", PEDRO, "clave-larga-2026", "");
        assertEquals(409, r.estado(), "Registrar un email que ya existe (" + PEDRO + ") debería responder 409");
        tieneMensaje(r, "Email repetido");
    }

    @Test
    void losDatosInvalidosResponden400() {
        Respuesta corta = registrar("Ana Ruiz", emailNuevo("ana"), "corta", "");
        assertEquals(400, corta.estado(), "Una contraseña de menos de 8 caracteres debería responder 400");
        tieneMensaje(corta, "Contraseña corta");
        assertEquals(400, registrar("Ana Ruiz", "esto-no-es-un-email", "clave-larga-2026", "").estado(),
                "Un email sin formato válido debería responder 400");
        assertEquals(400, registrar("", emailNuevo("sin-nombre"), "clave-larga-2026", "").estado(),
                "Un registro sin nombre debería responder 400");
    }

    @Test
    void laCuentaNuevaIniciaSesionParaAlquilar() {
        String email = emailNuevo("maestro");
        assertEquals(201, registrar("Maestro Nuevo", email, "clave-larga-2026", "").estado(), "El registro debería responder 201");
        Respuesta l = login(email, "clave-larga-2026");
        assertEquals(200, l.estado(), "Con la cuenta recién registrada el login debería responder 200");
        String token = l.json().path("token").asString();
        Respuesta a = alquilar(token, idHerramienta("Andamio tubular"), dia(650), dia(651));
        assertEquals(201, a.estado(), "La cuenta nueva es CLIENTE: debería poder alquilar (201). Respondió " + a.estado() + ": " + a.mensaje());
        cancelar(token, a.json().path("id").asLong());
    }
}
