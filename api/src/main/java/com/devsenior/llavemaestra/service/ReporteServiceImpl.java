package com.devsenior.llavemaestra.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.dto.HerramientaMasAlquiladaResponse;
import com.devsenior.llavemaestra.dto.ResumenResponse;
import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import com.devsenior.llavemaestra.repository.AlquilerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final AlquilerRepository alquilerRepository;

    @Override
    public ResumenResponse obtenerResumen() {
        LocalDate hoy = LocalDate.now(ZoneId.systemDefault());
        LocalDate primerDiaMes = hoy.withDayOfMonth(1);

        List<Alquiler> todos = alquilerRepository.findAll();

        long ingresosMes = todos.stream()
                .filter(a -> a.getEstado() != EstadoAlquiler.CANCELADO)
                .filter(a -> a.getCreadoEn() != null
                        && !a.getCreadoEn().toLocalDate().isBefore(primerDiaMes)
                        && !a.getCreadoEn().toLocalDate().isAfter(hoy))
                .mapToLong(a -> a.getValorTotal() != null ? a.getValorTotal().longValue() : 0)
                .sum();

        long alquileresActivos = todos.stream()
                .filter(a -> a.getEstado() == EstadoAlquiler.RESERVADO || a.getEstado() == EstadoAlquiler.ENTREGADO)
                .count();

        List<AlquilerResponse> vencidos = todos.stream()
                .filter(a -> a.getEstado() == EstadoAlquiler.ENTREGADO && a.getFechaFin().isBefore(hoy))
                .map(this::aResponse)
                .toList();

        Map<String, Long> conteoPorHerramienta = todos.stream()
                .filter(a -> a.getEstado() != EstadoAlquiler.CANCELADO)
                .collect(Collectors.groupingBy(a -> a.getHerramienta().getNombre(), Collectors.counting()));

        List<HerramientaMasAlquiladaResponse> masAlquiladas = conteoPorHerramienta.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(e -> new HerramientaMasAlquiladaResponse(e.getKey(), e.getValue()))
                .toList();

        return new ResumenResponse(ingresosMes, alquileresActivos, vencidos, masAlquiladas);
    }

    private AlquilerResponse aResponse(Alquiler a) {
        return new AlquilerResponse(
                a.getId(),
                a.getHerramienta().getId(),
                a.getHerramienta().getNombre(),
                a.getCliente().getId(),
                a.getCliente().getNombre(),
                a.getFechaInicio(),
                a.getFechaFin(),
                a.getDias(),
                a.getValorTotal(),
                a.getDeposito(),
                a.getEstado(),
                a.getCreadoEn()
        );
    }
}
