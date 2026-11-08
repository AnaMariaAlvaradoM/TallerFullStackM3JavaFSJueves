package com.devsenior.llavemaestra.service.impl;

import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.service.HerramientaService;
import java.util.List;
import org.springframework.security.core.Authentication;
import com.devsenior.llavemaestra.dto.HerramientaResponse;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Permiso;
import com.devsenior.llavemaestra.repository.HerramientaRepository;

@Service
public class HerramientaServiceImpl implements HerramientaService {
    private final HerramientaRepository herramientaRepository;

    public HerramientaServiceImpl(HerramientaRepository herramientaRepository) {
        this.herramientaRepository = herramientaRepository;
    }

    // Mismo endpoint, distinta respuesta segun el permiso:
    // quien gestiona ve todas (activas y retiradas); un cliente solo las que puede alquilar
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

        // Para un cliente, una herramienta retirada "no existe"
        if (!herramienta.isActiva() && !Permiso.HERRAMIENTAS_GESTIONAR.estaEn(autenticacion)) {
            throw new RecursoNoEncontradoException("No existe un herramienta con id " + id);
        }
        return toResponse(herramienta);
    }

    @Override
    public HerramientaResponse cambiarEstado(Long id, boolean activo) {
        Herramienta herramienta = buscarEntidad(id);
        herramienta.setActiva(activo);
        return toResponse(herramientaRepository.save(herramienta));
    }

    private Herramienta buscarEntidad(Long id) {
        return herramientaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un herramienta con id " + id));
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
}
