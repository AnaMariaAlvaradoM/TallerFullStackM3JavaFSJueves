package com.devsenior.llavemaestra.service.impl;


import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


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

    @Override
    public List<HerramientaResponse> listar(Authentication authentication) {
        List<Herramienta> herramientas = Permiso.HERRAMIENTAS_GESTIONAR.estaEn(authentication)
                ? herramientaRepository.findAllByOrderByNombreAsc()
                : herramientaRepository.findByActivaTrueOrderByNombreAsc();

        return herramientas.stream().map(this::toResponse).toList();
    }

    @Override
    public HerramientaResponse buscarPorId(Long id, Authentication autenticacion) {
        Herramienta herramienta = buscarHerramienta(id);

        if(!herramienta.isActiva() && !Permiso.HERRAMIENTAS_GESTIONAR.estaEn(autenticacion)){

            throw new RecursoNoEncontradoException("No existe una Herramienta con id" + id);
        }
        return toResponse(herramienta);
    
    }

    @Override
    public HerramientaResponse crear(HerramientaRequest request) {
        if(herramientaRepository.existsByNombreIgnoreCase(request.nombre().trim())) {
            throw new ReglaNegocioException("Ya existe una Herramienta llamada" + request.nombre().trim());
        }
        Herramienta herramienta = new Herramienta();
        aplicarDatos(herramienta, request);
        return toResponse(herramientaRepository.save(herramienta));
    }

    @Override
    public HerramientaResponse actualizar(Long id, HerramientaRequest request) {
        Herramienta herramienta = buscarHerramienta(id);
        if(herramientaRepository.existsByNombreIgnoreCaseAndIdNot(request.nombre().trim(), id)) {
            throw new ReglaNegocioException("Ya existe otra Herramienta llamada" + request.nombre().trim());
        }
        aplicarDatos(herramienta, request);
        return toResponse(herramientaRepository.save(herramienta));
  
    }

    @Override
    public HerramientaResponse cambiarEstado(Long id, boolean activa) {
        Herramienta herramienta = buscarHerramienta(id);
        herramienta.setActiva(activa);
        return toResponse(herramientaRepository.save(herramienta));
    }

   

    private void aplicarDatos(Herramienta herramienta, HerramientaRequest request) {
        herramienta.setNombre(request.nombre().trim());
        herramienta.setCategoria(request.categoria());
        herramienta.setPrecioDia(request.precioDia());
        herramienta.setDeposito(request.deposito());
        herramienta.setUnidades(request.unidades());
        herramienta.setDescripcion(request.descripcion());
        herramienta.setImagenUrl(request.imagenUrl());

    }

    private Herramienta buscarHerramienta(Long id) {
        return herramientaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una herramienta con id  "  +  id));
    }

    private HerramientaResponse toResponse(Herramienta herramienta) {
        return new HerramientaResponse(
                herramienta.getId(),
                herramienta.getNombre(),
                herramienta.getCategoria(),
                herramienta.getPrecioDia(),
                herramienta.getDeposito(),
                herramienta.getUnidades(),
                herramienta.getDescripcion(),
                herramienta.getImagenUrl(),
                herramienta.isActiva());
    }
}

