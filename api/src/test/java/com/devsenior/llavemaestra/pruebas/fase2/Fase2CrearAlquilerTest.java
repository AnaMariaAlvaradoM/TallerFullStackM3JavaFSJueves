package com.devsenior.llavemaestra.pruebas.fase2;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 2.1 · Crear un alquiler: calculo, duenno y permisos
class Fase2CrearAlquilerTest extends PruebaApi {

    @Test
    void pedroAlquilaElTaladroTresDiasYElBackendCalculaElValor() {
        String pedro = token(PEDRO);
        List<Long> creados = new ArrayList<>();
        try {
            Respuesta r = alquilar(pedro, idHerramienta("Taladro percutor"), dia(300), dia(302));
            assertEquals(201, r.estado(), "Un alquiler válido debería responder 201 Created. Respondió " + r.estado() + ": " + r.mensaje());
            JsonNode a = r.json();
            creados.add(a.path("id").asLong());
            assertEquals("RESERVADO", a.path("estado").asString(), "Un alquiler nuevo queda en estado RESERVADO");
            assertEquals(3, a.path("dias").asInt(), "Del día 300 al 302 son 3 días (se cuentan el primero y el último)");
            assertEquals(75000, a.path("valorTotal").asInt(), "valorTotal = 3 días x 25.000 = 75.000");
            assertEquals(100000, a.path("deposito").asInt(), "El depósito es el de la herramienta: 100.000");
            assertEquals("Taladro percutor", a.path("herramientaNombre").asString(), "La respuesta debería traer herramientaNombre");
            assertEquals("Pedro Salazar", a.path("clienteNombre").asString(), "La respuesta debería traer clienteNombre");
        } finally {
            limpiar(pedro, creados);
        }
    }

    @Test
    void unAlquilerDeUnSoloDiaValeUnDia() {
        String pedro = token(PEDRO);
        List<Long> creados = new ArrayList<>();
        try {
            Respuesta r = alquilar(pedro, idHerramienta("Andamio tubular"), dia(305), dia(305));
            assertEquals(201, r.estado(), "Alquilar del día 305 al mismo día 305 es válido (201). Respondió " + r.estado() + ": " + r.mensaje());
            creados.add(r.json().path("id").asLong());
            assertEquals(1, r.json().path("dias").asInt(), "Si inicio y fin son el mismo día, dias = 1");
            assertEquals(15000, r.json().path("valorTotal").asInt(), "1 día de andamio = 15.000");
        } finally {
            limpiar(pedro, creados);
        }
    }

    @Test
    void elClienteSaleDelTokenAunqueElJsonDigaOtraCosa() {
        String pedro = token(PEDRO);
        long diana = usuario(DIANA).path("id").asLong();
        List<Long> creados = new ArrayList<>();
        try {
            Respuesta r = post("/api/alquileres", pedro, "{\"herramientaId\":" + idHerramienta("Taladro percutor")
                    + ",\"fechaInicio\":\"" + dia(310) + "\",\"fechaFin\":\"" + dia(310) + "\",\"clienteId\":" + diana + "}");
            assertEquals(201, r.estado(), "El alquiler debería crearse (201). Respondió " + r.estado() + ": " + r.mensaje());
            creados.add(r.json().path("id").asLong());
            assertEquals("Pedro Salazar", r.json().path("clienteNombre").asString(),
                    "El dueño del alquiler sale del TOKEN (Pedro), nunca del JSON (aunque mande clienteId de Diana)");
        } finally {
            limpiar(pedro, creados);
        }
    }

    @Test
    void elValorLoCalculaElBackendAunqueElJsonMandeOtro() {
        String pedro = token(PEDRO);
        List<Long> creados = new ArrayList<>();
        try {
            Respuesta r = post("/api/alquileres", pedro, "{\"herramientaId\":" + idHerramienta("Taladro percutor")
                    + ",\"fechaInicio\":\"" + dia(312) + "\",\"fechaFin\":\"" + dia(313) + "\",\"valorTotal\":1,\"deposito\":0}");
            assertEquals(201, r.estado(), "El alquiler debería crearse (201). Respondió " + r.estado() + ": " + r.mensaje());
            creados.add(r.json().path("id").asLong());
            assertEquals(50000, r.json().path("valorTotal").asInt(), "El valor lo calcula el backend (2 x 25.000), no el JSON");
            assertEquals(100000, r.json().path("deposito").asInt(), "El depósito lo pone el backend, no el JSON");
        } finally {
            limpiar(pedro, creados);
        }
    }

    @Test
    void elAdministradorNoAlquila() {
        Respuesta r = alquilar(token(ADMIN), idHerramienta("Taladro percutor"), dia(315), dia(316));
        assertEquals(403, r.estado(), "ADMIN no tiene ALQUILERES_CREAR: POST /api/alquileres debería responder 403");
    }

    @Test
    void sinTokenNoSeAlquila() {
        String pedro = token(PEDRO);
        long taladro = idHerramienta("Taladro percutor");
        limpiar(pedro, java.util.List.of(alquilarOk(pedro, taladro, dia(317), dia(317))));
        assertEquals(401, alquilar(null, taladro, dia(315), dia(316)).estado(),
                "POST /api/alquileres sin token debería responder 401");
    }
}
