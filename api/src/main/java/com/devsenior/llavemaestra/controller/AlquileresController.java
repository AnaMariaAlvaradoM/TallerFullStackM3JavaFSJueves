package com.devsenior.llavemaestra.controller;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.service.AlquilerService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alquileres")
public class AlquileresController {

    private final AlquilerService alquilerService;


    public AlquileresController(AlquilerService alquilerService) {
        this.alquilerService = alquilerService;
    }

    @PreAuthorize("hasAuthority('ALQUILERES_CREAR')")
    @PostMapping
    public ResponseEntity<AlquilerResponse> crear(@Valid@RequestBody AlquilerRequest request, Authentication authentication){
        AlquilerResponse creada = alquilerService.crear(request,authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }
    // Este controller permite que cada cliente vea sus propios alquileres
    @PreAuthorize("hasAuthority('ALQUILERES_VER_PROPIOS')")
    @GetMapping("/mios")
    public ResponseEntity<List<AlquilerResponse>> listarMias(Authentication authentication){
        return ResponseEntity.ok(alquilerService.listarMias(authentication.getName()));
    }

    @PreAuthorize("hasAuthority('ALQUILERES_CANCELAR_PROPIOS')")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AlquilerResponse> cancelar(@PathVariable Long id,Authentication authentication){
        return ResponseEntity.ok(alquilerService.cancelar(id,authentication));
    }
}
