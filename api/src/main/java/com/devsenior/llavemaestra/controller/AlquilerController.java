package com.devsenior.llavemaestra.controller;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.service.AlquilerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController 
@Tag(name = "Alquiler", description = "Crear, consultar y cancelar reservas")
@RequestMapping("/api/alquileres")
public class AlquilerController {

    private final AlquilerService alquilerService;

    

    public AlquilerController(AlquilerService alquilerService) {
        this.alquilerService = alquilerService;
    }

    // Todos los alquileres (opcional: ?fecha=2026-10-20)
    @PreAuthorize("hasAuthority('ALQUILERES_VER_TODOS')")
    @Operation(summary = "Todos los alquileres", description = "Con ?fecha=2026-11-05 trae solo las de ese día. Es la Agenda de Mariana.")
    @GetMapping
    public ResponseEntity<List<AlquilerResponse>> listarTodas(@RequestParam(required = false) LocalDate fecha) {
        return ResponseEntity.ok(alquilerService.listarTodas(fecha));
    }

    // "Mis alquileres": el email sale del token, no de la URL
    @PreAuthorize("hasAuthority('ALQUILERES_VER_PROPIOS')")
    @Operation(summary = "Mis alquileres", description = "Solo las de quien hace la petición: el email sale del token.")
    @GetMapping("/mios")
    public ResponseEntity<List<AlquilerResponse>> listarMios(Authentication autenticacion) {
        return ResponseEntity.ok(alquilerService.listarMias(autenticacion.getName()));
    }

    @PreAuthorize("hasAuthority('ALQUILERES_CREAR')")
    @Operation(summary = "Crear una alquiler", description = "El miembro sale del token y el valor lo calcula el backend. 409 si se cruza con otra.")
    @PostMapping
    public ResponseEntity<AlquilerResponse> crear(@Valid @RequestBody AlquilerRequest request,
                                                 Authentication autenticacion) {
        AlquilerResponse creada = alquilerService.crear(request, autenticacion.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // Puede entrar quien tenga CUALQUIERA de los dos permisos;
    // el servicio verifica, ademas, que la alquiler sea suya si solo tiene "propias"
    @PreAuthorize("hasAnyAuthority('ALQUILERES_CANCELAR_PROPIOS')")
    @Operation(summary = "Cancelar una alquiler", description = "Un miembro cancela solo las suyas y antes de que empiecen; la administración, cualquiera.")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AlquilerResponse> cancelar(@PathVariable Long id, Authentication autenticacion) {
        return ResponseEntity.ok(alquilerService.cancelar(id, autenticacion));
    }
}
