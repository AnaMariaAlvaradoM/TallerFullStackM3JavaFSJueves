package com.devsenior.llavemaestra.controller;

import com.devsenior.llavemaestra.dto.UsuarioResponse;
import com.devsenior.llavemaestra.dto.EstadoRequest;
import com.devsenior.llavemaestra.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasAuthority('CLIENTES_GESTIONAR')")
public class UsuarioController {
    private  final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    };

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar(){
        return  ResponseEntity.ok(usuarioService.listar());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponse> cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request,
                                                         Authentication autentication){
        return  ResponseEntity.ok(usuarioService.cambiarEstado(id, request.activa(), autentication.getName()));
    }
}
