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

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Indicadores del negocio: ingresos del mes, activos, vencidos y ranking")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasAuthority('REPORTES_VER')")
    @Operation(summary = "Obtener resumen del negocio", description = "Devuelve ingresosMes, alquileresActivos, vencidos y masAlquiladas.")
    public ResponseEntity<ResumenResponse> obtenerResumen() {
        return ResponseEntity.ok(reporteService.obtenerResumen());
    }
}
