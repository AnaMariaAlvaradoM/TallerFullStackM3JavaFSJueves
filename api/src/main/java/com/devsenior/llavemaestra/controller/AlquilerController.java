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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.service.AlquilerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Alquileres", description = "Alquilar, ver los propios y cancelar")
@RequestMapping("/api/alquileres")
public class AlquilerController {

    private final AlquilerService alquilerService;

    public AlquilerController(AlquilerService alquilerService) {
        this.alquilerService = alquilerService;
    }

    @Operation(summary = "Alquilar una herramienta")
    @PreAuthorize("hasAuthority('ALQUILERES_CREAR')")
    @PostMapping
    public ResponseEntity<AlquilerResponse> crear(@Valid @RequestBody AlquilerRequest request,
                                                  Authentication autenticacion) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(alquilerService.crear(request, autenticacion.getName()));
    }

    @Operation(summary = "Ver mis alquileres")
    @PreAuthorize("hasAuthority('ALQUILERES_VER_PROPIOS')")
    @GetMapping("/mios")
    public ResponseEntity<List<AlquilerResponse>> mios(Authentication autenticacion) {
        return ResponseEntity.ok(alquilerService.listarMios(autenticacion.getName()));
    }

    @Operation(summary = "Cancelar un alquiler")
    @PreAuthorize("hasAnyAuthority('ALQUILERES_CANCELAR_PROPIOS', 'ALQUILERES_GESTIONAR')")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AlquilerResponse> cancelar(@PathVariable Long id, Authentication autenticacion) {
        return ResponseEntity.ok(alquilerService.cancelar(id, autenticacion));
    }
}
