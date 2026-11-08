package com.devsenior.llavemaestra.controller;

import com.devsenior.llavemaestra.dto.EstadoUsuarioRequest;
import com.devsenior.llavemaestra.dto.UsuarioResponse;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Todo este controlador es solo para quien tiene CLIENTES_GESTIONAR (Hernan)
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Clientes")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENTES_GESTIONAR')")
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('CLIENTES_GESTIONAR')")
    public UsuarioResponse cambiarEstado(@PathVariable Long id,
                                         @Valid @RequestBody EstadoUsuarioRequest datos,
                                         @AuthenticationPrincipal Usuario quienPide) {
        return usuarioService.cambiarEstado(id, datos.activo(), quienPide);
    }
}
