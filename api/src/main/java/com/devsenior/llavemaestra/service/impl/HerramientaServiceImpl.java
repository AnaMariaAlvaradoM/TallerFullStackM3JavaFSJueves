package com.devsenior.llavemaestra.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsenior.llavemaestra.dto.HerramientaRequest;
import com.devsenior.llavemaestra.dto.HerramientaResponse;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.exception.ReglaNegocioException;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.service.HerramientaService;

@Service
public class HerramientaServiceImpl implements HerramientaService {

    private final HerramientaRepository herramientaRepository;

    public HerramientaServiceImpl(HerramientaRepository herramientaRepository) {
        this.herramientaRepository = herramientaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HerramientaResponse> listar(boolean puedeGestionar) {
        List<Herramienta> lista = puedeGestionar
                ? herramientaRepository.findAllByOrderByIdAsc()
                : herramientaRepository.findByActivaTrueOrderByIdAsc();

        return lista.stream()
                .map(HerramientaResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HerramientaResponse obtenerPorId(Long id, boolean puedeGestionar) {
        Herramienta herramienta = herramientaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Herramienta no encontrada"));

        if (!puedeGestionar && !herramienta.isActiva()) {
            throw new RecursoNoEncontradoException("Herramienta no encontrada");
        }

        return HerramientaResponse.fromEntity(herramienta);
    }

    @Override
    @Transactional
    public HerramientaResponse crear(HerramientaRequest request) {
        String nombreLimpio = request.nombre().trim();
        if (herramientaRepository.existsByNombreIgnoreCase(nombreLimpio)) {
            throw new ReglaNegocioException("Ya existe una herramienta con ese nombre");
        }

        Herramienta herramienta = new Herramienta();
        herramienta.setNombre(nombreLimpio);
        herramienta.setCategoria(request.categoria());
        herramienta.setDescripcion(limpiarTextoOpcional(request.descripcion()));
        herramienta.setPrecioDia(request.precioDia());
        herramienta.setDeposito(request.deposito());
        herramienta.setUnidades(request.unidades());
        herramienta.setImagenUrl(limpiarTextoOpcional(request.imagenUrl()));
        herramienta.setActiva(true);

        return HerramientaResponse.fromEntity(herramientaRepository.save(herramienta));
    }

    @Override
    @Transactional
    public HerramientaResponse actualizar(Long id, HerramientaRequest request) {
        Herramienta herramienta = herramientaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Herramienta no encontrada"));

        String nombreLimpio = request.nombre().trim();
        if (herramientaRepository.existsByNombreIgnoreCaseAndIdNot(nombreLimpio, id)) {
            throw new ReglaNegocioException("Ya existe otra herramienta con ese nombre");
        }

        herramienta.setNombre(nombreLimpio);
        herramienta.setCategoria(request.categoria());
        herramienta.setDescripcion(limpiarTextoOpcional(request.descripcion()));
        herramienta.setPrecioDia(request.precioDia());
        herramienta.setDeposito(request.deposito());
        herramienta.setUnidades(request.unidades());
        herramienta.setImagenUrl(limpiarTextoOpcional(request.imagenUrl()));

        return HerramientaResponse.fromEntity(herramientaRepository.save(herramienta));
    }

    @Override
    @Transactional
    public HerramientaResponse cambiarEstado(Long id, boolean activa) {
        Herramienta herramienta = herramientaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Herramienta no encontrada"));

        herramienta.setActiva(activa);
        return HerramientaResponse.fromEntity(herramientaRepository.save(herramienta));
    }

    private String limpiarTextoOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}