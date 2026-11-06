package com.devsenior.llavemaestra.pruebas.fase4;

import com.devsenior.llavemaestra.pruebas.PruebaApi;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// 4.3 · Documentacion: Swagger explica la API a quien la abre por primera vez
class Fase4SwaggerTest extends PruebaApi {

    private static final List<String> METODOS = List.of("get", "post", "put", "patch", "delete");

    private JsonNode documento() {
        Respuesta r = get("/v3/api-docs", null);
        assertEquals(200, r.estado(), "GET /v3/api-docs (sin token) debería responder 200: la documentación de Swagger es pública");
        return r.json();
    }

    // Cada operacion como "GET /api/herramientas" -> su nodo
    private List<Map.Entry<String, JsonNode>> operaciones(JsonNode doc) {
        List<Map.Entry<String, JsonNode>> lista = new ArrayList<>();
        for (Map.Entry<String, JsonNode> ruta : doc.path("paths").properties()) {
            for (String metodo : METODOS) {
                JsonNode op = ruta.getValue().path(metodo);
                if (!op.isMissingNode()) {
                    lista.add(Map.entry(metodo.toUpperCase() + " " + ruta.getKey(), op));
                }
            }
        }
        assertFalse(lista.isEmpty(), "Swagger no muestra ningún endpoint en /v3/api-docs");
        return lista;
    }

    @Test
    void elTituloEsLlaveMaestraApi() {
        JsonNode info = documento().path("info");
        assertEquals("Llave Maestra API", info.path("title").asString(),
                "El título de Swagger debería ser \"Llave Maestra API\" (@OpenAPIDefinition en OpenApiConfig)");
        assertFalse(info.path("description").asString().isBlank(), "Swagger debería tener una descripción de la API");
    }

    @Test
    void swaggerTieneElBotonAuthorize() {
        JsonNode esquemas = documento().path("components").path("securitySchemes");
        boolean bearer = false;
        for (JsonNode esquema : esquemas) {
            if ("http".equals(esquema.path("type").asString()) && "bearer".equalsIgnoreCase(esquema.path("scheme").asString())) {
                bearer = true;
            }
        }
        assertTrue(bearer, "Swagger debería tener un esquema de seguridad HTTP bearer (JWT) para el botón Authorize");
    }

    @Test
    void cadaEndpointTieneResumen() {
        List<String> sinResumen = new ArrayList<>();
        for (Map.Entry<String, JsonNode> op : operaciones(documento())) {
            if (op.getValue().path("summary").asString().isBlank()) {
                sinResumen.add(op.getKey());
            }
        }
        assertTrue(sinResumen.isEmpty(), "Estos endpoints no tienen @Operation(summary = \"...\"): " + sinResumen);
    }

    @Test
    void cadaEndpointEstaEnUnGrupoConNombre() {
        List<String> sinGrupo = new ArrayList<>();
        for (Map.Entry<String, JsonNode> op : operaciones(documento())) {
            JsonNode tags = op.getValue().path("tags");
            String tag = tags.isArray() && !tags.isEmpty() ? tags.get(0).asString() : "";
            if (tag.isBlank() || tag.endsWith("-controller")) {
                sinGrupo.add(op.getKey() + (tag.isBlank() ? "" : " (" + tag + ")"));
            }
        }
        assertTrue(sinGrupo.isEmpty(), "Estos endpoints siguen en el grupo automático \"…-controller\": ponle @Tag(name = \"…\") a su controlador: " + sinGrupo);
    }

    @Test
    void estanDocumentadosTodosLosEndpointsDelTaller() {
        List<String> claves = operaciones(documento()).stream().map(Map.Entry::getKey).toList();
        for (String esperado : List.of("POST /api/auth/login", "POST /api/auth/registro", "GET /api/herramientas",
                "POST /api/alquileres", "GET /api/alquileres/mios", "PATCH /api/alquileres/{id}/entregar",
                "GET /api/reportes/resumen", "GET /api/usuarios")) {
            assertTrue(claves.contains(esperado), "Swagger no muestra " + esperado);
        }
    }
}
