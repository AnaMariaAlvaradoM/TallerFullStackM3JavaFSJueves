package com.devsenior.llavemaestra.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.dto.HerramientaRankingResponse;
import com.devsenior.llavemaestra.dto.ResumenResponse;
import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import com.devsenior.llavemaestra.repository.AlquilerRepository;
import com.devsenior.llavemaestra.service.ReporteService;

@Service
public class ReporteServiceImpl implements ReporteService {

    private final AlquilerRepository alquilerRepository;

    public ReporteServiceImpl(AlquilerRepository alquilerRepository) {
        this.alquilerRepository = alquilerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenResponse obtenerResumen() {
        LocalDate hoy = LocalDate.now();
        ZoneId zona = ZoneId.systemDefault();
        Instant inicioMes = hoy.withDayOfMonth(1).atStartOfDay(zona).toInstant();

        BigDecimal ingresosMes = alquilerRepository.buscarNoCanceladosDesde(inicioMes).stream()
                .map(Alquiler::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long alquileresActivos = alquilerRepository.countByEstadoIn(
                EnumSet.of(EstadoAlquiler.RESERVADO, EstadoAlquiler.ENTREGADO)
        );

        List<AlquilerResponse> vencidos = alquilerRepository.buscarVencidos(hoy).stream()
                .map(AlquilerResponse::fromEntity)
                .toList();

        Map<String, Long> conteoPorHerramienta = alquilerRepository.buscarNoCancelados().stream()
                .collect(Collectors.groupingBy(
                        a -> a.getHerramienta().getNombre(),
                        Collectors.counting()
                ));

        List<HerramientaRankingResponse> masAlquiladas = conteoPorHerramienta.entrySet().stream()
                .map(e -> new HerramientaRankingResponse(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(HerramientaRankingResponse::alquileres).reversed()
                        .thenComparing(HerramientaRankingResponse::herramienta))
                .toList();

        return new ResumenResponse(ingresosMes, alquileresActivos, vencidos, masAlquiladas);
    }
}