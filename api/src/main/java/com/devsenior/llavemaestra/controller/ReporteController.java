package com.devsenior.llavemaestra.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsenior.llavemaestra.dto.ResumenResponse;
import com.devsenior.llavemaestra.service.ReporteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Resúmenes y estadísticas del negocio (solo ADMIN)")
public class ReporteController {

    private final ReporteService reporteService;

    @Operation(summary = "Obtener resumen ejecutivo", description = "Facturado del mes, alquileres activos y herramientas vencidas")
    @GetMapping("/resumen")
    @PreAuthorize("hasAuthority('REPORTES_VER')")
    public ResponseEntity<ResumenResponse> obtenerResumen() {
        return ResponseEntity.ok(reporteService.obtenerResumen());
    }
}
