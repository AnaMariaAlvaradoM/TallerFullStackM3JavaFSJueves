package com.devsenior.llavemaestra.pruebas.fase4;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 4.1 · Seguridad final: un token que no salio de TU backend no abre ninguna puerta
class Fase4TokensTest extends PruebaApi {

    private static final List<String> TODOS = List.of("ALQUILERES_CANCELAR_PROPIOS", "ALQUILERES_CREAR",
            "ALQUILERES_GESTIONAR", "ALQUILERES_VER_PROPIOS", "ALQUILERES_VER_TODOS", "CLIENTES_GESTIONAR",
            "HERRAMIENTAS_GESTIONAR", "HERRAMIENTAS_VER", "REPORTES_VER");

    @Value("${jwt.secret:}")
    private String clave;

    private static String base64(String json) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    private static String permisosJson() {
        return "[\"" + String.join("\",\"", TODOS) + "\"]";
    }

    // Toma el token real de Pedro y le cambia el payload, conservando la firma original
    private String adulterar(String sub, String rol) {
        String[] partes = token(PEDRO).split("\\.");
        long ahora = System.currentTimeMillis() / 1000;
        String payload = "{\"sub\":\"" + sub + "\",\"nombre\":\"Pedro Salazar\",\"rol\":\"" + rol
                + "\",\"permisos\":" + permisosJson() + ",\"iat\":" + ahora + ",\"exp\":" + (ahora + 3600) + "}";
        return partes[0] + "." + base64(payload) + "." + partes[2];
    }

    private String firmar(String secreto, String sub, Date expira) {
        return Jwts.builder()
                .subject(sub)
                .claim("nombre", "Intruso")
                .claim("rol", "ADMIN")
                .claim("permisos", TODOS)
                .issuedAt(new Date(System.currentTimeMillis() - 7_200_000))
                .expiration(expira)
                .signWith(Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    // Control: si el token VERDADERO tampoco entra, la prueba no demuestra nada
    private void elTokenRealFunciona() {
        Respuesta r = get("/api/herramientas", token(PEDRO));
        assertEquals(200, r.estado(), "Con el token verdadero de Pedro, GET /api/herramientas debería responder 200. "
                + "Primero haz que el login y el catálogo funcionen: si todo responde 401, esta prueba no demuestra nada");
    }

    private String claveReal() {
        assertFalse(clave.isBlank(), "No encuentro la propiedad jwt.secret en application.properties: "
                + "la clave del JWT debe seguir en jwt.secret=${JWT_SECRET:valor-de-desarrollo}");
        return clave;
    }

    @Test
    void unTokenConLosPermisosCambiadosResponde401() {
        elTokenRealFunciona();
        Respuesta r = get("/api/usuarios", adulterar(PEDRO, "ADMIN"));
        assertEquals(401, r.estado(), "Le cambié los permisos al token de Pedro (sin volver a firmarlo) y GET /api/usuarios respondió "
                + r.estado() + ". Un token adulterado debería responder 401: la firma ya no coincide");
    }

    @Test
    void unTokenConOtroDuenoResponde401() {
        elTokenRealFunciona();
        Respuesta r = resumen(adulterar(ADMIN, "ADMIN"));
        assertEquals(401, r.estado(), "Cambié el dueño del token de Pedro por " + ADMIN + " y GET /api/reportes/resumen respondió "
                + r.estado() + ". Debería responder 401");
    }

    @Test
    void unTokenFirmadoConOtraClaveResponde401() {
        elTokenRealFunciona();
        String falso = firmar("una-clave-que-tu-backend-no-conoce-2026-xyz", ADMIN, new Date(System.currentTimeMillis() + 3_600_000));
        Respuesta r = get("/api/usuarios", falso);
        assertEquals(401, r.estado(), "Un token firmado con OTRA clave entró a GET /api/usuarios (respondió " + r.estado()
                + "). Tu backend debería verificar la firma con su propia clave y responder 401");
    }

    @Test
    void unTokenSinFirmaResponde401() {
        elTokenRealFunciona();
        long ahora = System.currentTimeMillis() / 1000;
        String sinFirma = base64("{\"alg\":\"none\",\"typ\":\"JWT\"}") + "." + base64("{\"sub\":\"" + ADMIN
                + "\",\"rol\":\"ADMIN\",\"permisos\":" + permisosJson() + ",\"iat\":" + ahora + ",\"exp\":" + (ahora + 3600) + "}") + ".";
        Respuesta r = get("/api/usuarios", sinFirma);
        assertEquals(401, r.estado(), "Un token con \"alg\": \"none\" (sin firma) respondió " + r.estado() + ". Debería responder 401");
    }

    @Test
    void unTokenVencidoResponde401() {
        elTokenRealFunciona();
        String vencido = firmar(claveReal(), ADMIN, new Date(System.currentTimeMillis() - 60_000));
        Respuesta r = get("/api/usuarios", vencido);
        assertEquals(401, r.estado(), "Un token bien firmado pero VENCIDO hace un minuto respondió " + r.estado()
                + ". Debería responder 401: revisa la fecha de expiración");
    }

    @Test
    void unTokenDeUnaCuentaQueNoExisteResponde401() {
        elTokenRealFunciona();
        String fantasma = firmar(claveReal(), "nadie" + System.nanoTime() + "@correo.com", new Date(System.currentTimeMillis() + 3_600_000));
        Respuesta r = get("/api/herramientas", fantasma);
        assertEquals(401, r.estado(), "Un token bien firmado de una cuenta que NO existe respondió " + r.estado()
                + ". Debería responder 401: el backend busca a la persona en la base de datos en cada petición");
    }
}
