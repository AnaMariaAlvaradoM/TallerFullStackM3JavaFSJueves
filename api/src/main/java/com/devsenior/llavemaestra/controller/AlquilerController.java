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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import com.devsenior.llavemaestra.model.Permiso;
import com.devsenior.llavemaestra.service.AlquilerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/alquileres")
@Tag(name = "Alquileres", description = "Crear, consultar, cancelar, entregar y devolver alquileres")
public class AlquilerController {

    private final AlquilerService alquilerService;

    public AlquilerController(AlquilerService alquilerService) {
        this.alquilerService = alquilerService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ALQUILERES_CREAR')")
    @Operation(summary = "Crear alquiler", description = "Crea un alquiler en estado RESERVADO validando reglas de negocio.")
    public ResponseEntity<AlquilerResponse> crear(
            @Valid @RequestBody AlquilerRequest request,
            Authentication authentication
    ) {
        String emailCliente = authentication != null ? authentication.getName() : "";
        return ResponseEntity.status(HttpStatus.CREATED).body(alquilerService.crear(request, emailCliente));
    }

    @GetMapping("/mios")
    @PreAuthorize("hasAuthority('ALQUILERES_VER_PROPIOS')")
    @Operation(summary = "Listar alquileres propios", description = "Devuelve los alquileres del usuario autenticado.")
    public ResponseEntity<List<AlquilerResponse>> listarMios(Authentication authentication) {
        String emailCliente = authentication != null ? authentication.getName() : "";
        return ResponseEntity.ok(alquilerService.listarMios(emailCliente));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ALQUILERES_VER_TODOS')")
    @Operation(summary = "Listar todos los alquileres", description = "Devuelve todos los alquileres, opcionalmente filtrados por estado.")
    public ResponseEntity<List<AlquilerResponse>> listarTodos(
            @RequestParam(required = false) EstadoAlquiler estado
    ) {
        return ResponseEntity.ok(alquilerService.listarTodos(estado));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyAuthority('ALQUILERES_CANCELAR_PROPIOS', 'ALQUILERES_GESTIONAR')")
    @Operation(summary = "Cancelar alquiler", description = "Cancela un alquiler RESERVADO antes de su fecha de inicio.")
    public ResponseEntity<AlquilerResponse> cancelar(@PathVariable Long id, Authentication authentication) {
        String emailActual = authentication != null ? authentication.getName() : "";
        return ResponseEntity.ok(alquilerService.cancelar(id, emailActual, puedeGestionarTodos(authentication)));
    }

    @PatchMapping("/{id}/entregar")
    @PreAuthorize("hasAuthority('ALQUILERES_GESTIONAR')")
    @Operation(summary = "Entregar herramienta", description = "Cambia el estado de RESERVADO a ENTREGADO.")
    public ResponseEntity<AlquilerResponse> entregar(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.entregar(id));
    }

    @PatchMapping("/{id}/devolver")
    @PreAuthorize("hasAuthority('ALQUILERES_GESTIONAR')")
    @Operation(summary = "Recibir devolución", description = "Cambia el estado de ENTREGADO a DEVUELTO.")
    public ResponseEntity<AlquilerResponse> devolver(@PathVariable Long id) {
        return ResponseEntity.ok(alquilerService.devolver(id));
    }

    private boolean puedeGestionarTodos(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        String permisoGestion = Permiso.ALQUILERES_GESTIONAR.name();
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> permisoGestion.equals(auth.getAuthority()));
    }
}