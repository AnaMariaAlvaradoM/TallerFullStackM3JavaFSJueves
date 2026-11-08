package com.devsenior.llavemaestra.service;

import com.devsenior.llavemaestra.dto.HerramientaResponse;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Permiso;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// La logica del catalogo de herramientas
@Service
public class HerramientaService {

    private final HerramientaRepository herramientaRepository;

    public HerramientaService(HerramientaRepository herramientaRepository) {
        this.herramientaRepository = herramientaRepository;
    }

    // Quien gestiona herramientas (Hernan) ve todas; el cliente, solo las activas
    @Transactional(readOnly = true)
    public List<HerramientaResponse> listar(Usuario usuario) {
        List<Herramienta> herramientas = usuario.tienePermiso(Permiso.HERRAMIENTAS_GESTIONAR)
                ? herramientaRepository.findAllByOrderByIdAsc()
                : herramientaRepository.findByActivaTrueOrderByIdAsc();
        return herramientas.stream().map(HerramientaResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public HerramientaResponse obtener(Long id) {
        return HerramientaResponse.desde(buscar(id));
    }

    // Retirar (activa = false) o volver a ofrecer (activa = true)
    @Transactional
    public HerramientaResponse cambiarEstado(Long id, boolean activa) {
        Herramienta herramienta = buscar(id);
        herramienta.setActiva(activa);
        return HerramientaResponse.desde(herramientaRepository.save(herramienta));
    }

    // Busca la herramienta o lanza 404 con un mensaje claro
    private Herramienta buscar(Long id) {
        return herramientaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la herramienta con id " + id));
    }
}
