package com.devsenior.llavemaestra.service.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.time.Duration;


import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.dto.OcupacionHerramienta;
import com.devsenior.llavemaestra.dto.ResumenResponse;
import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.repository.AlquilerRepository;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import com.devsenior.llavemaestra.service.ReporteService;

@Service 
public class ReporteServiceImpl implements  ReporteService{

    private final AlquilerRepository alquilerRepository;
    private final UsuarioRepository usuarioRepository;
    private final HerramientaRepository herramientaRepository;

    public ReporteServiceImpl(AlquilerRepository alquilerRepository, UsuarioRepository usuarioRepository,
            HerramientaRepository herramientaRepository) {
        this.alquilerRepository = alquilerRepository;
        this.usuarioRepository = usuarioRepository;
        this.herramientaRepository = herramientaRepository;
    }

    @Override
    public ResumenResponse resumen() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        List<Alquiler> delMes = alquilerRepository
                .findByFechaInicioBetweenAndEstado(inicioMes, finMes, EstadoAlquiler.RESERVADO);

        long reservasHoy = delMes.stream().filter(r -> r.getCreadaEn().equals(hoy)).count();
        int ingresosMes = delMes.stream().mapToInt(Alquiler::getValorTotal).sum();

        List<OcupacionHerramienta> ocupacion = herramientaRepository.findAllByOrderByNombreAsc().stream()
                .map(herramienta -> ocupacionDe(herramienta, delMes))
                .sorted(Comparator.comparingDouble(OcupacionHerramienta::diasReservados).reversed())
                .toList();

        return new ResumenResponse(
                reservasHoy,
                delMes.size(),
                ingresosMes,
                usuarioRepository.countByActivoTrue(),
                herramientaRepository.countByActivaTrue(),
                ocupacion
        );
    }

    private OcupacionHerramienta ocupacionDe(Herramienta herramienta, List<Alquiler> reservas) {
        List<Alquiler> suyas = reservas.stream()
                .filter(r -> r.getHerramienta().getId().equals(herramienta.getId()))
                .toList();
        long dias = suyas.stream()
                .mapToLong(r -> Duration.between(r.getFechaInicio(), r.getFechaFin()).toDays())
                .sum();
        int ingresos = suyas.stream().mapToInt(Alquiler::getValorTotal).sum();
        return new OcupacionHerramienta(herramienta.getNombre(), dias, ingresos);
    }

}
