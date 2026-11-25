package com.devsenior.llavemaestra.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsenior.llavemaestra.dto.EstadoUsuarioRequest;
import com.devsenior.llavemaestra.dto.UsuarioResponse;
import com.devsenior.llavemaestra.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de cuentas de clientes y bloqueo/desbloqueo")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENTES_GESTIONAR')")
    @Operation(summary = "Listar usuarios", description = "Devuelve las cuentas registradas sin contraseña.")
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('CLIENTES_GESTIONAR')")
    @Operation(summary = "Cambiar estado de usuario", description = "Activa o bloquea una cuenta (no permite autobloqueo).")
    public ResponseEntity<UsuarioResponse> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoUsuarioRequest request,
            Authentication authentication
    ) {
        String emailActual = authentication != null ? authentication.getName() : "";
        return ResponseEntity.ok(usuarioService.cambiarEstado(id, request.activo(), emailActual));
    }
}