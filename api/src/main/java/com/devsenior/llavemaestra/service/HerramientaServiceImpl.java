package com.devsenior.llavemaestra.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.dto.HerramientaRequest;
import com.devsenior.llavemaestra.dto.HerramientaResponse;
import com.devsenior.llavemaestra.exception.ConflictoException;
import com.devsenior.llavemaestra.exception.DatoInvalidoException;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.repository.HerramientaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HerramientaServiceImpl implements HerramientaService {

    private final HerramientaRepository repository;

    @Override
    public List<HerramientaResponse> listarHerramientas() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean puedeGestionar = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("HERRAMIENTAS_GESTIONAR"));

        List<Herramienta> herramientas = puedeGestionar
                ? repository.findAll()
                : repository.findByActivaTrue();

        return herramientas.stream().map(this::aResponse).toList();
    }

    @Override
    public HerramientaResponse buscarPorId(Long id) {
        Herramienta herramienta = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Herramienta no encontrada con ID: " + id));
        return aResponse(herramienta);
    }

    @Override
    public HerramientaResponse cambiarEstado(Long id, boolean activa) {
        Herramienta herramienta = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Herramienta inexistente con ID: " + id));

        herramienta.setActiva(activa);
        Herramienta guardada = repository.save(herramienta);
        return aResponse(guardada);
    }

    @Override
    public HerramientaResponse crear(HerramientaRequest request) {
        validarCampos(request);

        if (repository.existsByNombre(request.nombre().trim())) {
            throw new ConflictoException("Ya existe una herramienta con el nombre " + request.nombre() + " (Nombre repetido)");
        }

        Herramienta nueva = Herramienta.builder()
                .nombre(request.nombre().trim())
                .categoria(request.categoria())
                .descripcion(request.descripcion() != null ? request.descripcion().trim() : "")
                .precioDia(request.precioDia())
                .deposito(request.deposito() != null ? request.deposito() : BigDecimal.ZERO)
                .unidades(request.unidades())
                .imagenUrl(request.imagenUrl() != null ? request.imagenUrl().trim() : "")
                .activa(true) // Una herramienta nueva entra activa
                .build();

        Herramienta guardada = repository.save(nueva);
        return aResponse(guardada);
    }

    @Override
    public HerramientaResponse actualizar(Long id, HerramientaRequest request) {
        Herramienta herramienta = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Herramienta no encontrada con ID: " + id));

        validarCampos(request);

        repository.findByNombre(request.nombre().trim()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new ConflictoException("Ya existe otra herramienta con el nombre " + request.nombre());
            }
        });

        herramienta.setNombre(request.nombre().trim());
        herramienta.setCategoria(request.categoria());
        herramienta.setDescripcion(request.descripcion() != null ? request.descripcion().trim() : "");
        herramienta.setPrecioDia(request.precioDia());
        herramienta.setDeposito(request.deposito() != null ? request.deposito() : BigDecimal.ZERO);
        herramienta.setUnidades(request.unidades());
        if (request.imagenUrl() != null) {
            herramienta.setImagenUrl(request.imagenUrl().trim());
        }

        Herramienta guardada = repository.save(herramienta);
        return aResponse(guardada);
    }

    private void validarCampos(HerramientaRequest request) {
        if (request.nombre() == null || request.nombre().isBlank()) {
            throw new DatoInvalidoException("El nombre de la herramienta es obligatorio");
        }
        if (request.nombre().length() > 80) {
            throw new DatoInvalidoException("El nombre no puede superar los 80 caracteres");
        }
        if (request.categoria() == null) {
            throw new DatoInvalidoException("La categoría es obligatoria");
        }
        if (request.precioDia() == null || request.precioDia().compareTo(BigDecimal.valueOf(1000)) < 0) {
            throw new DatoInvalidoException("El precio por día debe ser mínimo 1.000 (Precio inválido)");
        }
        if (request.deposito() != null && request.deposito().compareTo(BigDecimal.ZERO) < 0) {
            throw new DatoInvalidoException("El depósito no puede ser negativo");
        }
        if (request.unidades() == null || request.unidades() < 1 || request.unidades() > 50) {
            throw new DatoInvalidoException("Las unidades deben estar entre 1 y 50 (Sin unidades)");
        }
        if (request.descripcion() != null && request.descripcion().length() > 300) {
            throw new DatoInvalidoException("La descripción no puede superar los 300 caracteres");
        }
        if (request.imagenUrl() != null && request.imagenUrl().length() > 300) {
            throw new DatoInvalidoException("La URL de la imagen no puede superar los 300 caracteres");
        }
    }

    private HerramientaResponse aResponse(Herramienta h) {
        return new HerramientaResponse(
                h.getId(),
                h.getNombre(),
                h.getCategoria(),
                h.getDescripcion(),
                h.getPrecioDia(),
                h.getDeposito(),
                h.getUnidades(),
                h.getImagenUrl(),
                h.isActiva()
        );
    }
}
