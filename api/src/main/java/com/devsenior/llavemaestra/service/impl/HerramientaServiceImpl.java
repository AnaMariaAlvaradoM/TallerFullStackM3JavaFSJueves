package com.devsenior.llavemaestra.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.dto.FranjaOcupada;
import com.devsenior.llavemaestra.dto.HerramientaRequest;
import com.devsenior.llavemaestra.dto.HerramientaResponse;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.exception.ReglaNegocioException;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Permiso;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.service.HerramientaService;

@Service 
public class HerramientaServiceImpl implements HerramientaService {

    private final HerramientaRepository herramientaRepository;

    public HerramientaServiceImpl(HerramientaRepository herramientaRepository) {
        this.herramientaRepository = herramientaRepository;
    }

    // Mismo endpoint, distinta respuesta segun el permiso:
    // quien gestiona ve todos (activos e inactivos); un miembro solo los que puede reservar
    @Override
    public List<HerramientaResponse> listar(Authentication autenticacion) {
        List<Herramienta> herramientas = Permiso.HERRAMIENTAS_GESTIONAR.estaEn(autenticacion)
                ? herramientaRepository.findAllByOrderByNombreAsc()
                : herramientaRepository.findByActivaTrueOrderByNombreAsc();

        return herramientas.stream().map(this::toResponse).toList();
    }

    @Override
    public HerramientaResponse buscarPorId(Long id, Authentication autenticacion) {
        Herramienta herramienta = buscarEntidad(id);

        // Para un miembro, un espacio inactivo "no existe"
        if (!herramienta.isActiva() && !Permiso.HERRAMIENTAS_GESTIONAR.estaEn(autenticacion)) {
            throw new RecursoNoEncontradoException("No existe un espacio con id " + id);
        }
        return toResponse(herramienta);
    }

    @Override
    public HerramientaResponse crear(HerramientaRequest request) {
        if (herramientaRepository.existsByNombreIgnoreCase(request.nombre().trim())) {
            throw new ReglaNegocioException("Ya existe un espacio llamado " + request.nombre().trim());
        }
        Herramienta herramienta = new Herramienta();
        aplicarDatos(herramienta, request);
        return toResponse(herramientaRepository.save(herramienta));
    }

    @Override
    public HerramientaResponse actualizar(Long id, HerramientaRequest request) {
        Herramienta herramienta = buscarEntidad(id);
        if (herramientaRepository.existsByNombreIgnoreCaseAndIdNot(request.nombre().trim(), id)) {
            throw new ReglaNegocioException("Ya existe otro espacio llamado " + request.nombre().trim());
        }
        aplicarDatos(herramienta, request);
        return toResponse(herramientaRepository.save(herramienta));
    }

    @Override
    public HerramientaResponse cambiarEstado(Long id, boolean activa) {
        Herramienta herramienta = buscarEntidad(id);
        herramienta.setActiva(activa);
        return toResponse(herramientaRepository.save(herramienta));
    }

    private Herramienta buscarEntidad(Long id) {
        return herramientaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un espacio con id " + id));
    }

    private void aplicarDatos(Herramienta herramienta, HerramientaRequest request) {
        herramienta.setNombre(request.nombre().trim());
        herramienta.setCategoria(request.categoria());
        herramienta.setDescripcion(request.descripcion());
        herramienta.setPrecioDia(request.precioDia()); 
        herramienta.setDeposito(request.deposito());
        herramienta.setUnidades(request.unidades());
        herramienta.setImagenUrl(request.imagenUrl());
        herramienta.setActiva(true);
    }

    private HerramientaResponse toResponse(Herramienta herramienta) {
        return new HerramientaResponse(
                herramienta.getId(),
                herramienta.getNombre(),
                herramienta.getCategoria(),
                herramienta.getDescripcion(),
                herramienta.getPrecioDia(),
                herramienta.getDeposito(),
                herramienta.getUnidades(),
                herramienta.getImagenUrl(),
                herramienta.isActiva()
        );
    }

    @Override
    public List<FranjaOcupada> ocupacion(Long id, LocalDate fecha) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ocupacion'");
    }
}
