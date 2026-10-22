package com.devsenior.llavemaestra.pruebas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Base de todas las pruebas del backend. No es un archivo de pruebas.
// Levanta TU aplicacion con una base de datos en memoria y le habla por HTTP,
// igual que lo haria Angular, Swagger o Postman.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class PruebaApi {

    protected static final String CLAVE = "llave123";
    protected static final String ADMIN = "admin@llavemaestra.co";
    protected static final String PEDRO = "pedro@correo.com";
    protected static final String DIANA = "diana@correo.com";
    protected static final String JORGE = "jorge@correo.com";

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    protected static final JsonMapper JSON = JsonMapper.builder().build();

    @Value("${local.server.port}")
    private int puerto;

    protected record Respuesta(int estado, String cuerpo) {

        public JsonNode json() {
            try {
                return JSON.readTree(cuerpo == null || cuerpo.isBlank() ? "null" : cuerpo);
            } catch (Exception e) {
                fail("La respuesta no es un JSON válido: " + resumir(cuerpo));
                return null;
            }
        }

        public String mensaje() {
            JsonNode nodo = json();
            return nodo != null && nodo.has("mensaje") ? nodo.get("mensaje").asString() : "";
        }
    }

    // ---------- Peticiones ----------

    protected Respuesta get(String ruta, String token) {
        return enviar("GET", ruta, token, null);
    }

    protected Respuesta post(String ruta, String token, String cuerpoJson) {
        return enviar("POST", ruta, token, cuerpoJson);
    }

    protected Respuesta patch(String ruta, String token, String cuerpoJson) {
        return enviar("PATCH", ruta, token, cuerpoJson);
    }

    private Respuesta enviar(String metodo, String ruta, String token, String cuerpoJson) {
        HttpRequest.Builder peticion = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json");
        if (token != null) {
            peticion.header("Authorization", "Bearer " + token);
        }
        if (cuerpoJson != null) {
            peticion.header("Content-Type", "application/json");
            peticion.method(metodo, HttpRequest.BodyPublishers.ofString(cuerpoJson, StandardCharsets.UTF_8));
        } else {
            peticion.method(metodo, HttpRequest.BodyPublishers.noBody());
        }
        try {
            HttpResponse<String> r = HTTP.send(peticion.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return new Respuesta(r.statusCode(), r.body());
        } catch (Exception e) {
            fail("No pude hablar con tu API en " + metodo + " " + ruta + ": " + e.getMessage());
            return null;
        }
    }

    // ---------- Sesion ----------

    protected Respuesta login(String email, String clave) {
        return post("/api/auth/login", null,
                "{\"email\":\"" + email + "\",\"password\":\"" + clave + "\"}");
    }

    protected String token(String email) {
        Respuesta r = login(email, CLAVE);
        assertEquals(200, r.estado(), "POST /api/auth/login con " + email + " / " + CLAVE
                + " debería responder 200 (¿sembraste ese usuario en DataInitializer?)");
        JsonNode nodo = r.json();
        assertTrue(nodo != null && nodo.has("token"), "El login debería responder { \"token\": \"...\" }");
        return nodo.get("token").asString();
    }

    // Lee el payload del JWT (la parte del medio), igual que Angular
    protected JsonNode payload(String token) {
        String[] partes = token.split("\\.");
        assertEquals(3, partes.length, "Un JWT tiene 3 partes separadas por puntos");
        String json = new String(Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8);
        try {
            return JSON.readTree(json);
        } catch (Exception e) {
            fail("El payload del token no es un JSON válido");
            return null;
        }
    }

    // ---------- Datos sembrados ----------

    protected JsonNode herramienta(String nombre) {
        Respuesta r = get("/api/herramientas", token(ADMIN));
        assertEquals(200, r.estado(), "GET /api/herramientas como administrador debería responder 200");
        for (JsonNode h : r.json()) {
            if (nombre.equals(h.path("nombre").asString())) {
                return h;
            }
        }
        fail("No encontré la herramienta \"" + nombre + "\" en GET /api/herramientas (revisa el nombre exacto en DataInitializer)");
        return null;
    }

    protected long idHerramienta(String nombre) {
        return herramienta(nombre).path("id").asLong();
    }

    protected JsonNode usuario(String email) {
        Respuesta r = get("/api/usuarios", token(ADMIN));
        assertEquals(200, r.estado(), "GET /api/usuarios como administrador debería responder 200");
        for (JsonNode u : r.json()) {
            if (email.equals(u.path("email").asString())) {
                return u;
            }
        }
        fail("No encontré a " + email + " en GET /api/usuarios");
        return null;
    }

    protected List<String> nombres(JsonNode lista) {
        List<String> nombres = new ArrayList<>();
        lista.forEach(n -> nombres.add(n.path("nombre").asString()));
        return nombres;
    }

    // ---------- Alquileres ----------

    // Las pruebas usan fechas lejanas para no cruzarse con tus datos sembrados
    protected static LocalDate dia(int diasDesdeHoy) {
        return LocalDate.now().plusDays(diasDesdeHoy);
    }

    protected Respuesta alquilar(String token, long herramientaId, LocalDate inicio, LocalDate fin) {
        return post("/api/alquileres", token, "{\"herramientaId\":" + herramientaId
                + ",\"fechaInicio\":\"" + inicio + "\",\"fechaFin\":\"" + fin + "\"}");
    }

    protected long alquilarOk(String token, long herramientaId, LocalDate inicio, LocalDate fin) {
        Respuesta r = alquilar(token, herramientaId, inicio, fin);
        assertEquals(201, r.estado(), "POST /api/alquileres válido debería responder 201. Respondió "
                + r.estado() + ": " + resumir(r.mensaje().isEmpty() ? r.cuerpo() : r.mensaje()));
        return r.json().path("id").asLong();
    }

    protected Respuesta cancelar(String token, long alquilerId) {
        return patch("/api/alquileres/" + alquilerId + "/cancelar", token, null);
    }

    // Deja la base como estaba: cancela lo que una prueba creo
    protected void limpiar(String token, List<Long> alquileres) {
        for (Long id : alquileres) {
            cancelar(token, id);
        }
    }

    protected static String resumir(String texto) {
        if (texto == null) return "(vacío)";
        return texto.length() > 160 ? texto.substring(0, 160) + "…" : texto;
    }

    protected void tieneMensaje(Respuesta r, String contexto) {
        assertFalse(r.mensaje().isBlank(), contexto
                + ": la respuesta de error debería tener el formato { codigo, mensaje, fecha } con un mensaje claro");
    }
}
