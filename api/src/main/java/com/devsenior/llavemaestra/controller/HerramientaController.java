package com.devsenior.llavemaestra.controller;

import com.devsenior.llavemaestra.dto.EstadoRequest;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/herramientas")
public class HerramientaController {

    private final HerramientaRepository herramientaRepository;

    public HerramientaController(HerramientaRepository herramientaRepository) {
        this.herramientaRepository = herramientaRepository;
    }

    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    @GetMapping
    public ResponseEntity<List<Herramienta>> listar(Authentication authentication) {
        // Si el usuario tiene rol CLIENTE (o no es admin general con gestion total),
        // según las reglas, el cliente ve solo las activas. El ADMIN ve todas.
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("HERRAMIENTAS_GESTIONAR"));

        List<Herramienta> herramientas;
        if (esAdmin) {
            herramientas = herramientaRepository.findAll();
        } else {
            herramientas = herramientaRepository.findAll().stream()
                    .filter(Herramienta::isActiva)
                    .toList();
        }
        return ResponseEntity.ok(herramientas);
    }

    @PreAuthorize("hasAuthority('HERRAMIENTAS_VER')")
    @GetMapping("/{id}")
    public ResponseEntity<Herramienta> buscarPorId(@PathVariable Long id, Authentication authentication) {
        Herramienta herramienta = herramientaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Herramienta no encontrada"));

        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("HERRAMIENTAS_GESTIONAR"));

        if (!esAdmin && !herramienta.isActiva()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(herramienta);
    }

    @PreAuthorize("hasAuthority('HERRAMIENTAS_GESTIONAR')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<Herramienta> cambiarEstado(@PathVariable Long id,
            @Valid @RequestBody EstadoRequest request) {
        Herramienta herramienta = herramientaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una herramienta con id " + id));

        herramienta.setActiva(request.activa());
        Herramienta actualizada = herramientaRepository.save(herramienta);
        return ResponseEntity.ok(actualizada);
    }
}
