package com.devsenior.llavemaestra.pruebas.fase1;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

// 1.1 · Login y token
class Fase1LoginTest extends PruebaApi {

    private Set<String> permisosDe(String token) {
        Set<String> permisos = new TreeSet<>();
        JsonNode lista = payload(token).path("permisos");
        assertTrue(lista.isArray(), "El token debería traer el claim \"permisos\" como una lista");
        lista.forEach(p -> permisos.add(p.asString()));
        return permisos;
    }

    @Test
    void elAdministradorIniciaSesionYRecibeUnToken() {
        Respuesta r = login(ADMIN, CLAVE);
        assertEquals(200, r.estado(), "POST /api/auth/login con " + ADMIN + " debería responder 200");
        assertEquals(3, r.json().path("token").asString().split("\\.").length,
                "La respuesta debería ser { \"token\": \"xxx.yyy.zzz\" }");
    }

    @Test
    void elTokenTraeEmailNombreYRol() {
        JsonNode datos = payload(token(ADMIN));
        assertEquals(ADMIN, datos.path("sub").asString(), "El claim \"sub\" del token debería ser el email");
        assertEquals("Hernán Rojas", datos.path("nombre").asString(), "El claim \"nombre\" debería ser Hernán Rojas");
        assertEquals("ADMIN", datos.path("rol").asString(), "El claim \"rol\" debería ser ADMIN");
    }

    @Test
    void elTokenDelAdministradorTraeSusSeisPermisos() {
        Set<String> esperados = new TreeSet<>(Set.of("HERRAMIENTAS_VER", "HERRAMIENTAS_GESTIONAR",
                "ALQUILERES_VER_TODOS", "ALQUILERES_GESTIONAR", "CLIENTES_GESTIONAR", "REPORTES_VER"));
        assertEquals(esperados, permisosDe(token(ADMIN)), "Los permisos de ADMIN no coinciden con la matriz (revisa Rol.java)");
    }

    @Test
    void elTokenDelClienteTraeSusCuatroPermisos() {
        Set<String> esperados = new TreeSet<>(Set.of("HERRAMIENTAS_VER", "ALQUILERES_CREAR",
                "ALQUILERES_VER_PROPIOS", "ALQUILERES_CANCELAR_PROPIOS"));
        JsonNode datos = payload(token(PEDRO));
        assertEquals("CLIENTE", datos.path("rol").asString(), "El rol de Pedro debería ser CLIENTE");
        assertEquals(esperados, permisosDe(token(PEDRO)), "Los permisos de CLIENTE no coinciden con la matriz (revisa Rol.java)");
    }

    @Test
    void unaClaveIncorrectaResponde401ConMensaje() {
        Respuesta r = login(PEDRO, "clave-equivocada");
        assertEquals(401, r.estado(), "Con una clave incorrecta el login debería responder 401");
        tieneMensaje(r, "Clave incorrecta");
    }

    @Test
    void unEmailQueNoExisteResponde401() {
        token(ADMIN);
        assertEquals(401, login("nadie@correo.com", CLAVE).estado(), "Con un email que no existe el login debería responder 401");
    }

    @Test
    void unaCuentaBloqueadaNoPuedeEntrar() {
        Respuesta r = login(JORGE, CLAVE);
        assertEquals(401, r.estado(), "Jorge está bloqueado (activo = false): su login debería responder 401");
        tieneMensaje(r, "Cuenta bloqueada");
    }
}
