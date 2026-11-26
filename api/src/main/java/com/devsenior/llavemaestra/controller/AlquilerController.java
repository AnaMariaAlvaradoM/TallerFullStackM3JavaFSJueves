package com.devsenior.llavemaestra.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.service.AlquilerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alquileres")
@RequiredArgsConstructor
@Tag(name = "Alquileres", description = "Crear, consultar, cancelar, entregar y devolver alquileres")
public class AlquilerController {

    private final AlquilerService alquilerService;

    @Operation(summary = "Crear alquiler", description = "CLIENTE: reserva una herramienta para las fechas indicadas")
    @PostMapping
    @PreAuthorize("hasAuthority('ALQUILERES_CREAR')")
    public ResponseEntity<AlquilerResponse> crearAlquiler(@Valid @RequestBody AlquilerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alquilerService.crearAlquiler(request));
    }

    @Operation(summary = "Ver mis alquileres", description = "CLIENTE: lista los alquileres del usuario autenticado")
    @GetMapping("/mios")
    @PreAuthorize("hasAuthority('ALQUILERES_VER_PROPIOS')")
    public ResponseEntity<List<AlquilerResponse>> obtenerMisAlquileres() {
        return ResponseEntity.ok(alquilerService.obtenerMisAlquileres());
    }

    @Operation(summary = "Cancelar alquiler", description = "CLIENTE o ADMIN: cancela un alquiler en estado RESERVADO")
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyAuthority('ALQUILERES_CANCELAR_PROPIOS', 'ALQUILERES_GESTIONAR')")
    public ResponseEntity<AlquilerResponse> cancelarAlquiler(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.cancelarAlquiler(id));
    }

    @Operation(summary = "Listar todos los alquileres", description = "ADMIN: lista todos los alquileres, con filtro opcional por estado")
    @GetMapping
    @PreAuthorize("hasAuthority('ALQUILERES_VER_TODOS')")
    public ResponseEntity<List<AlquilerResponse>> obtenerTodos(
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(alquilerService.obtenerTodos(estado));
    }

    @Operation(summary = "Entregar herramienta", description = "ADMIN: cambia estado RESERVADO → ENTREGADO. 409 si no está reservado")
    @PatchMapping("/{id}/entregar")
    @PreAuthorize("hasAuthority('ALQUILERES_GESTIONAR')")
    public ResponseEntity<AlquilerResponse> entregarAlquiler(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.entregarAlquiler(id));
    }

    @Operation(summary = "Devolver herramienta", description = "ADMIN: cambia estado ENTREGADO → DEVUELTO. 409 si no está entregado")
    @PatchMapping("/{id}/devolver")
    @PreAuthorize("hasAuthority('ALQUILERES_GESTIONAR')")
    public ResponseEntity<AlquilerResponse> devolverAlquiler(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.devolverAlquiler(id));
    }
}
