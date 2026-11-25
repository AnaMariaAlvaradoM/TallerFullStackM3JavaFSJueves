package com.devsenior.llavemaestra.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsenior.llavemaestra.dto.EstadoRequest;
import com.devsenior.llavemaestra.dto.UsuarioRequest;
import com.devsenior.llavemaestra.dto.UsuarioResponse;
import com.devsenior.llavemaestra.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController 
@Tag(name = "Clientes", description = "Cuentas de los Clientes: crear, listar y desactivar")
@RequestMapping("/api/usuarios")
@PreAuthorize("hasAuthority('CLIENTES_GESTIONAR')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Listar miembros")
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @Operation(summary = "Crear una cuenta")
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(request));
    }

    @Operation(summary = "Activar o desactivar una cuenta", description = "La administración no se puede desactivar a sí misma (409).")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponse> cambiarEstado(@PathVariable Long id,
                                                         @Valid @RequestBody EstadoRequest request,
                                                         Authentication autenticacion) {
        return ResponseEntity.ok(usuarioService.cambiarEstado(id, request.activo(), autenticacion.getName()));
    }
}
