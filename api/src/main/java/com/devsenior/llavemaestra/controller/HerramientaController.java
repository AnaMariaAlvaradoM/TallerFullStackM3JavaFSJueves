package com.devsenior.llavemaestra.controller;

import com.devsenior.llavemaestra.dto.EstadoHerramientaRequest;
import com.devsenior.llavemaestra.dto.HerramientaResponse;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.service.HerramientaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// El controlador solo recibe la peticion, revisa el PERMISO y le pasa el trabajo al servicio
@RestController
@RequestMapping("/api/herramientas")
@Tag(name = "Herramientas")
public class HerramientaController {

    private final HerramientaService herramientaService;

    public HerramientaController(HerramientaService herramientaService) {
        this.herramientaService = herramientaService;
    }

    // @AuthenticationPrincipal = la cuenta que puso el JwtFilter (quien hace la peticion)
    @GetMapping
    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    public List<HerramientaResponse> listar(@AuthenticationPrincipal Usuario usuario) {
        return herramientaService.listar(usuario);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    public HerramientaResponse obtener(@PathVariable Long id) {
        return herramientaService.obtener(id);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    public HerramientaResponse cambiarEstado(@PathVariable Long id,
                                             @Valid @RequestBody EstadoHerramientaRequest datos) {
        return herramientaService.cambiarEstado(id, datos.activa());
    }
}
