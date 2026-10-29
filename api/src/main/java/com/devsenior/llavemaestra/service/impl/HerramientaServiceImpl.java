package com.devsenior.llavemaestra.service.impl;

import com.devsenior.llavemaestra.dto.HerramientaResponse;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Permiso;
import com.devsenior.llavemaestra.repository.AlquilerRepository;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.service.HerramientaService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HerramientaServiceImpl implements HerramientaService {

    private final HerramientaRepository herramientaRepository;
    private final AlquilerRepository alquilerRepository;

    public HerramientaServiceImpl(HerramientaRepository herramientaRepository, AlquilerRepository alquilerRepository) {
        this.herramientaRepository = herramientaRepository;
        this.alquilerRepository = alquilerRepository;
    }

    @Override
    public List<HerramientaResponse> listar(Authentication authentication) {
        List<Herramienta> herramientas = Permiso.HERRAMIENTAS_GESTIONAR.estaEn(authentication)
                ? herramientaRepository.findAllByOrderByNombreAsc()
                :herramientaRepository.findByActivaTrueOrderByNombreAsc();
        return herramientas.stream().map(this::toResponse).toList();
    }

    @Override
    public HerramientaResponse buscarPorId(Long id, Authentication authentication) {
        Herramienta herramienta = buscarEntidad(id);

        if (!herramienta.isActiva() &&!Permiso.HERRAMIENTAS_GESTIONAR.estaEn(authentication)){
            throw new RecursoNoEncontradoException("No existe una herramienta con id " + id);
        }

        return toResponse(herramienta);
    }

    @Override
    public HerramientaResponse cambiarEstado(Long id, boolean activa) {
        Herramienta herramienta = buscarEntidad(id);
        herramienta.setActiva(activa);

        return toResponse(herramientaRepository.save(herramienta));
    }

    private Herramienta buscarEntidad(Long id){
        return  herramientaRepository.findById(id).orElseThrow(()->new RecursoNoEncontradoException("No existe una herramienta con id " + id));
    }

    private HerramientaResponse toResponse(Herramienta herramienta){
        return  new HerramientaResponse(
                herramienta.getId(),
                herramienta.getNombre(),
                herramienta.getCategoria(),
                herramienta.getPrecioDia(),
                herramienta.getDeposito(),
                herramienta.getDescripcion(),
                herramienta.getImagenUrl(),
                herramienta.getUnidades(),
                herramienta.isActiva()

        );
    }
}
