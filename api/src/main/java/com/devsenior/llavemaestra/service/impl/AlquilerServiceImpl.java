package com.devsenior.llavemaestra.service.impl;

import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import java.util.List;


import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.exception.DatoInvalidoException;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.exception.ReglaNegocioException;
import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Permiso;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.AlquilerRepository;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import com.devsenior.llavemaestra.service.AlquilerService;

@Service
public class AlquilerServiceImpl implements AlquilerService {

    // Horario de atencion
    /*
     * private static final LocalTime APERTURA = LocalTime.of(7, 0);
     * private static final LocalTime CIERRE = LocalTime.of(5, 0);
     */

    private final AlquilerRepository alquilerRepository;
    private final HerramientaRepository herramientaRepository;
    private final UsuarioRepository usuarioRepository;

    public AlquilerServiceImpl(AlquilerRepository alquilerRepository, HerramientaRepository herramientaRepository,
            UsuarioRepository usuarioRepository) {
        this.alquilerRepository = alquilerRepository;
        this.herramientaRepository = herramientaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<AlquilerResponse> listarMias(String email) {
        return alquilerRepository.findByUsuarioEmailOrderByCreadaEnDescFechaInicioDesc(email)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<AlquilerResponse> listarTodas(LocalDate fecha) {
        List<Alquiler> alquilers = fecha == null
                ? alquilerRepository.findAllByOrderByCreadaEnDescFechaInicioDesc()
                : alquilerRepository.findByCreadaEnOrderByFechaInicioAsc(fecha);
        return alquilers.stream().map(this::toResponse).toList();
    }

    @Override
    public AlquilerResponse crear(AlquilerRequest request, String email) {

        Herramienta herramienta = herramientaRepository.findById(request.herramientaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        String.format("No existe una herramienta con este id %s", request.herramientaId())));

        if (!herramienta.isActiva()) {
            throw new ReglaNegocioException(
                    String.format("%s no esta disponibel para reservas", herramienta.getNombre()));
        }

        validarFecha(request.fechaInicio(), request.fechaFin());

        if (herramienta.getUnidades() <= 0) {
            throw new ReglaNegocioException(String.format("No hay suficiente unidades de %s",
                    herramienta.getNombre()));
        }

        long cruces = alquilerRepository.contarCruces(
                herramienta.getId(),
                List.of(EstadoAlquiler.RESERVADO, EstadoAlquiler.ENTREGADO),
                request.fechaInicio(),
                request.fechaFin());

        if (cruces >= herramienta.getUnidades()) {
            throw new ReglaNegocioException(
                    String.format("No hay unidades disponibles de %s en esas fechas", herramienta.getNombre()));
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un usuario " + email));
        validarMaximoAlquileres(usuario.getId());

        long dias = ChronoUnit.DAYS.between(request.fechaInicio(), request.fechaFin()) + 1;

        Alquiler alquiler = new Alquiler();
        alquiler.setHerramienta(herramienta);
        alquiler.setUsuario(usuario);
        alquiler.setFechaInicio(request.fechaInicio());
        alquiler.setFechaFin(request.fechaFin());
        alquiler.setDia((int) dias);
        alquiler.setValorTotal((int) (dias * herramienta.getPrecioDia()));
        alquiler.setDeposito(herramienta.getDeposito());

        return toResponse(alquilerRepository.save(alquiler));

    }

    @Override
    public AlquilerResponse cancelar(Long id, Authentication autenticacion) {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un alquiler con id " + id));

        // El permiso de cancelar "propias" no basta: hay que verificar que SEA suya.
        // Esto no lo puede decidir @PreAuthorize: depende del dato, no solo del
        // permiso.
        boolean puedeCancelarCualquiera = Permiso.ALQUILERES_GESTIONAR.estaEn(autenticacion);
        boolean esSuya = alquiler.getUsuario().getEmail().equals(autenticacion.getName());

        if (!puedeCancelarCualquiera && !esSuya) {
            throw new AccessDeniedException("Solo puedes cancelar tus propias Alquileres");
        }
        if (alquiler.getEstado() == EstadoAlquiler.CANCELADO) {
            throw new ReglaNegocioException("El alquiler ya estaba cancelado");
        }

        if (!alquiler.getFechaInicio().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("No se puede cancelar un alquiler que ya comenzó");
        }

        alquiler.setEstado(EstadoAlquiler.CANCELADO);
        return toResponse(alquilerRepository.save(alquiler));

    }

    // --- metoddos privados ---

    private static final int MAX_ALQUILERES_ACTIVOS = 3;

    private void validarMaximoAlquileres(Long usuarioId) {
        long activos = alquilerRepository.countByUsuarioIdAndEstadoIn(
                usuarioId,
                List.of(EstadoAlquiler.RESERVADO, EstadoAlquiler.ENTREGADO));

        if (activos >= MAX_ALQUILERES_ACTIVOS) {
            throw new ReglaNegocioException("Más de 3 activos: no puedes tener más de 3 alquileres activos.");
        }
    }

    private void validarFecha(LocalDate inicio, LocalDate fin) {
        if (inicio.isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("No puedes alquilar en una fecha que ya pasó");
        }
        if (fin.isBefore(inicio)) {
            throw new DatoInvalidoException("La fecha de fin no puede ser anterior a la de inicio");
        }
        long diasContados = ChronoUnit.DAYS.between(inicio, fin) + 1;
        if (diasContados > 15) {
            throw new DatoInvalidoException("El alquiler es máximo de 15 días");
        }
    }

    private AlquilerResponse toResponse(Alquiler alquiler) {
        Herramienta herramienta = alquiler.getHerramienta();
        Usuario usuario = alquiler.getUsuario();

        return new AlquilerResponse(
                alquiler.getId(),
                herramienta.getId(),
                herramienta.getNombre(),
                usuario.getId(),
                usuario.getNombre(),
                alquiler.getFechaInicio(),
                alquiler.getFechaFin(),
                alquiler.getDias(),
                alquiler.getValorTotal(),
                alquiler.getDeposito(),
                alquiler.getEstado(),
                alquiler.getCreadaEn());
    }
}
