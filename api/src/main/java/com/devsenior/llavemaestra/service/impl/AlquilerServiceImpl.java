package com.devsenior.llavemaestra.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.exception.DatoInvalidoException;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.exception.ReglaNegocioException;
import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.AlquilerRepository;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import com.devsenior.llavemaestra.service.AlquilerService;

@Service
public class AlquilerServiceImpl implements AlquilerService {

    private static final Set<EstadoAlquiler> ESTADOS_ACTIVOS =
            EnumSet.of(EstadoAlquiler.RESERVADO, EstadoAlquiler.ENTREGADO);

    private static final int MAX_DIAS_ALQUILER = 15;
    private static final int MAX_ALQUILERES_ACTIVOS_CLIENTE = 3;

    private final AlquilerRepository alquilerRepository;
    private final HerramientaRepository herramientaRepository;
    private final UsuarioRepository usuarioRepository;

    public AlquilerServiceImpl(
            AlquilerRepository alquilerRepository,
            HerramientaRepository herramientaRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.alquilerRepository = alquilerRepository;
        this.herramientaRepository = herramientaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public AlquilerResponse crear(AlquilerRequest request, String emailCliente) {
        if (request == null || request.herramientaId() == null || request.fechaInicio() == null || request.fechaFin() == null) {
            throw new DatoInvalidoException("Debes indicar herramientaId, fechaInicio y fechaFin");
        }

        Herramienta herramienta = herramientaRepository.findById(request.herramientaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("La herramienta solicitada no existe"));

        if (!herramienta.isActiva()) {
            throw new ReglaNegocioException("La herramienta está retirada y no se puede alquilar");
        }

        LocalDate inicio = request.fechaInicio();
        LocalDate fin = request.fechaFin();
        LocalDate hoy = LocalDate.now();

        if (fin.isBefore(inicio)) {
            throw new DatoInvalidoException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        if (inicio.isBefore(hoy)) {
            throw new DatoInvalidoException("La fecha de inicio no puede estar en el pasado");
        }

        int dias = (int) ChronoUnit.DAYS.between(inicio, fin) + 1;
        if (dias > MAX_DIAS_ALQUILER) {
            throw new DatoInvalidoException("No se puede alquilar una herramienta por más de 15 días seguidos");
        }

        Usuario cliente = usuarioRepository.findByEmailIgnoreCase(emailCliente)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        long activosCliente = alquilerRepository.countByClienteIdAndEstadoIn(cliente.getId(), ESTADOS_ACTIVOS);
        if (activosCliente >= MAX_ALQUILERES_ACTIVOS_CLIENTE) {
            throw new ReglaNegocioException("Ya tienes el máximo de 3 alquileres activos permitidos");
        }

        List<Alquiler> solapados = alquilerRepository.buscarSolapados(
                herramienta.getId(),
                ESTADOS_ACTIVOS,
                inicio,
                fin
        );
        if (solapados.size() >= herramienta.getUnidades()) {
            throw new ReglaNegocioException("No hay unidades disponibles de esta herramienta para las fechas seleccionadas");
        }

        Alquiler alquiler = new Alquiler();
        alquiler.setHerramienta(herramienta);
        alquiler.setCliente(cliente);
        alquiler.setFechaInicio(inicio);
        alquiler.setFechaFin(fin);
        alquiler.setDias(dias);
        alquiler.setValorTotal(herramienta.getPrecioDia().multiply(BigDecimal.valueOf(dias)));
        alquiler.setDeposito(herramienta.getDeposito());
        alquiler.setEstado(EstadoAlquiler.RESERVADO);

        return AlquilerResponse.fromEntity(alquilerRepository.save(alquiler));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerResponse> listarMios(String emailCliente) {
        Usuario cliente = usuarioRepository.findByEmailIgnoreCase(emailCliente)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        return alquilerRepository.findByClienteIdOrderByCreadoEnDescIdDesc(cliente.getId()).stream()
                .map(AlquilerResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlquilerResponse> listarTodos(EstadoAlquiler estado) {
        List<Alquiler> lista = (estado != null)
                ? alquilerRepository.findByEstadoOrderByCreadoEnDescIdDesc(estado)
                : alquilerRepository.findAllByOrderByCreadoEnDescIdDesc();

        return lista.stream()
                .map(AlquilerResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public AlquilerResponse cancelar(Long id, String emailAutenticado, boolean puedeGestionarTodos) {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alquiler no encontrado"));

        boolean esPropio = alquiler.getCliente().getEmail().equalsIgnoreCase(emailAutenticado);
        if (!esPropio && !puedeGestionarTodos) {
            throw new AccessDeniedException("No puedes cancelar un alquiler que no es tuyo");
        }

        if (alquiler.getEstado() != EstadoAlquiler.RESERVADO) {
            throw new ReglaNegocioException("Solo se puede cancelar un alquiler en estado RESERVADO");
        }

        if (!alquiler.getFechaInicio().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("No se puede cancelar un alquiler que empieza hoy o ya inició");
        }

        alquiler.setEstado(EstadoAlquiler.CANCELADO);
        return AlquilerResponse.fromEntity(alquilerRepository.save(alquiler));
    }

    @Override
    @Transactional
    public AlquilerResponse entregar(Long id) {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alquiler no encontrado"));

        if (alquiler.getEstado() != EstadoAlquiler.RESERVADO) {
            throw new ReglaNegocioException("Solo se puede entregar un alquiler en estado RESERVADO");
        }

        alquiler.setEstado(EstadoAlquiler.ENTREGADO);
        return AlquilerResponse.fromEntity(alquilerRepository.save(alquiler));
    }

    @Override
    @Transactional
    public AlquilerResponse devolver(Long id) {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alquiler no encontrado"));

        if (alquiler.getEstado() != EstadoAlquiler.ENTREGADO) {
            throw new ReglaNegocioException("Solo se puede registrar devolución de un alquiler en estado ENTREGADO");
        }

        alquiler.setEstado(EstadoAlquiler.DEVUELTO);
        return AlquilerResponse.fromEntity(alquilerRepository.save(alquiler));
    }
}