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

    private static final int MAX_DIAS = 15;
    private static final int MAX_ACTIVOS = 3;
    private static final List<EstadoAlquiler> ACTIVOS =
            List.of(EstadoAlquiler.RESERVADO, EstadoAlquiler.ENTREGADO);

    private final AlquilerRepository alquilerRepository;
    private final HerramientaRepository herramientaRepository;
    private final UsuarioRepository usuarioRepository;

    public AlquilerServiceImpl(AlquilerRepository alquilerRepository,
                               HerramientaRepository herramientaRepository,
                               UsuarioRepository usuarioRepository) {
        this.alquilerRepository = alquilerRepository;
        this.herramientaRepository = herramientaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public AlquilerResponse crear(AlquilerRequest request, String email) {
        Herramienta herramienta = herramientaRepository.findById(request.herramientaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una herramienta con id " + request.herramientaId()));
        if (!herramienta.isActiva()) {
            throw new ReglaNegocioException("La herramienta " + herramienta.getNombre() + " está retirada");
        }

        LocalDate inicio = request.fechaInicio();
        LocalDate fin = request.fechaFin();
        if (fin.isBefore(inicio)) {
            throw new DatoInvalidoException("La fecha de fin no puede ser anterior a la de inicio");
        }
        if (inicio.isBefore(LocalDate.now())) {
            throw new DatoInvalidoException("La fecha de inicio ya pasó");
        }
        int dias = (int) ChronoUnit.DAYS.between(inicio, fin) + 1;
        if (dias > MAX_DIAS) {
            throw new DatoInvalidoException("No se alquila por más de " + MAX_DIAS + " días");
        }

        Usuario cliente = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + email));
        if (alquilerRepository.countByUsuarioIdAndEstadoIn(cliente.getId(), ACTIVOS) >= MAX_ACTIVOS) {
            throw new ReglaNegocioException("Ya tienes " + MAX_ACTIVOS + " alquileres activos");
        }

        long cruces = alquilerRepository
                .countByHerramientaIdAndEstadoInAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                        herramienta.getId(), ACTIVOS, fin, inicio);
        if (cruces >= herramienta.getUnidades()) {
            throw new ReglaNegocioException("No hay unidades disponibles en esas fechas");
        }

        Alquiler alquiler = new Alquiler();
        alquiler.setHerramienta(herramienta);
        alquiler.setUsuario(cliente);
        alquiler.setFechaInicio(inicio);
        alquiler.setFechaFin(fin);
        alquiler.setDias(dias);
        alquiler.setValorTotal(dias * herramienta.getPrecioDia());
        alquiler.setDeposito(herramienta.getDeposito());
        alquiler.setEstado(EstadoAlquiler.RESERVADO);
        return toResponse(alquilerRepository.save(alquiler));
    }

    @Override
    public List<AlquilerResponse> listarMios(String email) {
        return alquilerRepository.findByUsuarioEmailOrderByCreadoEnDesc(email).stream()
                .map(this::toResponse).toList();
    }

    @Override
    public AlquilerResponse cancelar(Long id, Authentication autenticacion) {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un alquiler con id " + id));

        boolean gestor = Permiso.ALQUILERES_GESTIONAR.estaEn(autenticacion);
        boolean esSuyo = alquiler.getUsuario().getEmail().equalsIgnoreCase(autenticacion.getName());
        if (!gestor && !esSuyo) {
            throw new AccessDeniedException("Este alquiler no es tuyo");
        }
        if (alquiler.getEstado() != EstadoAlquiler.RESERVADO) {
            throw new ReglaNegocioException("Solo se cancelan alquileres en estado RESERVADO");
        }
        if (!alquiler.getFechaInicio().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("Un alquiler que empieza hoy o ya empezó no se puede cancelar");
        }
        alquiler.setEstado(EstadoAlquiler.CANCELADO);
        return toResponse(alquilerRepository.save(alquiler));
    }

    private AlquilerResponse toResponse(Alquiler a) {
        return new AlquilerResponse(
                a.getId(), a.getHerramienta().getId(), a.getHerramienta().getNombre(),
                a.getUsuario().getId(), a.getUsuario().getNombre(),
                a.getFechaInicio(), a.getFechaFin(), a.getDias(), a.getValorTotal(),
                a.getDeposito(), a.getEstado(), a.getCreadoEn());
    }
}
