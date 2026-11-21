package com.devsenior.llavemaestra.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsenior.llavemaestra.dto.ResumenResponse;
import com.devsenior.llavemaestra.service.ReporteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/resumen")
    @PreAuthorize("hasAuthority('REPORTES_VER')")
    public ResponseEntity<ResumenResponse> obtenerResumen() {
        return ResponseEntity.ok(reporteService.obtenerResumen());
    }
}
