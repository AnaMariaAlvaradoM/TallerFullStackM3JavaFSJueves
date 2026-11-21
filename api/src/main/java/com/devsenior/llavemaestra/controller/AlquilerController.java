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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alquileres")
@RequiredArgsConstructor
public class AlquilerController {

    private final AlquilerService alquilerService;

    @PostMapping
    @PreAuthorize("hasAuthority('ALQUILERES_CREAR')")
    public ResponseEntity<AlquilerResponse> crearAlquiler(@Valid @RequestBody AlquilerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alquilerService.crearAlquiler(request));
    }

    @GetMapping("/mios")
    @PreAuthorize("hasAuthority('ALQUILERES_VER_PROPIOS')")
    public ResponseEntity<List<AlquilerResponse>> obtenerMisAlquileres() {
        return ResponseEntity.ok(alquilerService.obtenerMisAlquileres());
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyAuthority('ALQUILERES_CANCELAR_PROPIOS', 'ALQUILERES_GESTIONAR')")
    public ResponseEntity<AlquilerResponse> cancelarAlquiler(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.cancelarAlquiler(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ALQUILERES_VER_TODOS')")
    public ResponseEntity<List<AlquilerResponse>> obtenerTodos(
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(alquilerService.obtenerTodos(estado));
    }

    @PatchMapping("/{id}/entregar")
    @PreAuthorize("hasAuthority('ALQUILERES_GESTIONAR')")
    public ResponseEntity<AlquilerResponse> entregarAlquiler(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.entregarAlquiler(id));
    }

    @PatchMapping("/{id}/devolver")
    @PreAuthorize("hasAuthority('ALQUILERES_GESTIONAR')")
    public ResponseEntity<AlquilerResponse> devolverAlquiler(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.devolverAlquiler(id));
    }
}
