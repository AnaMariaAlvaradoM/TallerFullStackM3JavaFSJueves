package com.devsenior.llavemaestra.controller;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.service.AlquilerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alquileres")
@Tag(name = "Alquileres")
public class AlquilerController {

    private final AlquilerService alquilerService;

    public AlquilerController(AlquilerService alquilerService) {
        this.alquilerService = alquilerService;
    }

    // 201 Created: se creo un recurso nuevo. El cliente sale del token, no del JSON.
    @PostMapping
    @PreAuthorize("hasAuthority('ALQUILERES_CREAR')")
    public ResponseEntity<AlquilerResponse> crear(@Valid @RequestBody AlquilerRequest datos,
                                                  @AuthenticationPrincipal Usuario cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alquilerService.crear(datos, cliente));
    }

    @GetMapping("/mios")
    @PreAuthorize("hasAuthority('ALQUILERES_VER_PROPIOS')")
    public List<AlquilerResponse> mios(@AuthenticationPrincipal Usuario cliente) {
        return alquilerService.mios(cliente);
    }

    // Aqui se revisa solo el PERMISO; la regla "solo lo tuyo" la revisa el servicio
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyAuthority('ALQUILERES_CANCELAR_PROPIOS', 'ALQUILERES_GESTIONAR')")
    public AlquilerResponse cancelar(@PathVariable Long id, @AuthenticationPrincipal Usuario quienPide) {
        return alquilerService.cancelar(id, quienPide);
    }
}
