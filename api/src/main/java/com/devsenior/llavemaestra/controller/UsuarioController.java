package com.devsenior.llavemaestra.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de clientes (solo ADMIN)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listar clientes", description = "ADMIN: lista todos los usuarios registrados con rol CLIENTE")
    @GetMapping
    @PreAuthorize("hasAuthority('CLIENTES_GESTIONAR')")
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @Operation(summary = "Activar o bloquear cliente", description = "ADMIN: activa o bloquea la cuenta de un cliente")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('CLIENTES_GESTIONAR')")
    public ResponseEntity<UsuarioResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestBody EstadoUsuarioRequest request) {
        boolean activo = request.activo() != null && request.activo();
        return ResponseEntity.ok(usuarioService.cambiarEstado(id, activo));
    }
}
