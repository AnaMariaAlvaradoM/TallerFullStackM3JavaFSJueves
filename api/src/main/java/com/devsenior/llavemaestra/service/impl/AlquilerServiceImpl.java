package com.devsenior.llavemaestra.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.service.AlquilerService;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlquilerServiceImpl implements AlquilerService {

    private final AlquilerRepository alquilerRepository;
    private final HerramientaRepository herramientaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public AlquilerResponse crearAlquiler(AlquilerRequest request) {
        if (request.herramientaId() == null) {
            throw new DatoInvalidoException("Falta la herramienta");
        }
        if (request.fechaInicio() == null) {
            throw new DatoInvalidoException("Falta la fecha de inicio");
        }
        if (request.fechaFin() == null) {
            throw new DatoInvalidoException("Falta la fecha de fin");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario cliente = usuarioRepository.findByEmail(email)
                .or(() -> usuarioRepository.findByUsername(email))
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + email));

        Herramienta herramienta = herramientaRepository.findById(request.herramientaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Herramienta inexistente"));

        if (!herramienta.isActiva()) {
            throw new ReglaNegocioException("Herramienta retirada: la herramienta no se encuentra activa");
        }

        LocalDate hoy = LocalDate.now(ZoneId.systemDefault());
        LocalDate inicio = request.fechaInicio();
        LocalDate fin = request.fechaFin();

        if (fin.isBefore(inicio)) {
            throw new DatoInvalidoException("Fechas invertidas: la fecha de fin no puede ser anterior a la de inicio");
        }

        if (inicio.isBefore(hoy)) {
            throw new DatoInvalidoException("Fecha pasada: no se puede iniciar un alquiler en el pasado");
        }

        long diasCalculados = ChronoUnit.DAYS.between(inicio, fin) + 1;
        if (diasCalculados > 15) {
            throw new DatoInvalidoException("Más de 15 días: el alquiler no puede superar los 15 días permitidos");
        }

        long alquileresActivosCliente = alquilerRepository.countByClienteAndEstadoIn(
                cliente, List.of(EstadoAlquiler.RESERVADO, EstadoAlquiler.ENTREGADO));
        if (alquileresActivosCliente >= 3) {
            throw new ReglaNegocioException("El cliente ya cuenta con 3 alquileres activos simultáneos");
        }

        List<Alquiler> alquileresHerramienta = alquilerRepository.findByHerramientaAndEstadoIn(
                herramienta, List.of(EstadoAlquiler.RESERVADO, EstadoAlquiler.ENTREGADO));

        long cruces = alquileresHerramienta.stream().filter(a -> {
            LocalDate aInicio = a.getFechaInicio();
            LocalDate aFin = a.getFechaFin();
            return !(fin.isBefore(aInicio) || inicio.isAfter(aFin));
        }).count();

        if (cruces >= herramienta.getUnidades()) {
            throw new ReglaNegocioException("Sin unidades: no hay unidades disponibles de la herramienta para las fechas seleccionadas");
        }

        BigDecimal valorTotal = herramienta.getPrecioDia().multiply(BigDecimal.valueOf(diasCalculados));
        BigDecimal deposito = herramienta.getDeposito();

        Alquiler alquiler = Alquiler.builder()
                .herramienta(herramienta)
                .cliente(cliente)
                .fechaInicio(inicio)
                .fechaFin(fin)
                .dias((int) diasCalculados)
                .valorTotal(valorTotal)
                .deposito(deposito)
                .estado(EstadoAlquiler.RESERVADO)
                .creadoEn(LocalDateTime.now(ZoneId.systemDefault()))
                .build();

        Alquiler guardado = alquilerRepository.save(alquiler);
        return aResponse(guardado);
    }

    @Override
    public List<AlquilerResponse> obtenerMisAlquileres() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario cliente = usuarioRepository.findByEmail(email)
                .or(() -> usuarioRepository.findByUsername(email))
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        return alquilerRepository.findByClienteOrderByCreadoEnDesc(cliente).stream()
                .map(this::aResponse)
                .toList();
    }

    @Override
    public AlquilerResponse cancelarAlquiler(Long id) {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alquiler inexistente con ID: " + id));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailActual = auth.getName();

        boolean esSuAlquiler = alquiler.getCliente().getEmail().equalsIgnoreCase(emailActual)
                || alquiler.getCliente().getUsername().equalsIgnoreCase(emailActual);
        boolean tienePermisoGestionar = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ALQUILERES_GESTIONAR"));

        if (!esSuAlquiler && !tienePermisoGestionar) {
            throw new AccessDeniedException("No tienes permisos para cancelar este alquiler");
        }

        if (alquiler.getEstado() != EstadoAlquiler.RESERVADO) {
            throw new ReglaNegocioException("Ya cancelado: solo se pueden cancelar alquileres en estado RESERVADO");
        }

        LocalDate hoy = LocalDate.now(ZoneId.systemDefault());
        if (!alquiler.getFechaInicio().isAfter(hoy)) {
            throw new ReglaNegocioException("Empieza hoy: no se puede cancelar un alquiler que comienza hoy o en el pasado");
        }

        alquiler.setEstado(EstadoAlquiler.CANCELADO);
        Alquiler actualizado = alquilerRepository.save(alquiler);
        return aResponse(actualizado);
    }

    @Override
    public List<AlquilerResponse> obtenerTodos(String estadoStr) {
        List<Alquiler> alquileres;
        if (estadoStr != null && !estadoStr.isBlank()) {
            EstadoAlquiler estado = EstadoAlquiler.valueOf(estadoStr.toUpperCase().trim());
            alquileres = alquilerRepository.findByEstadoOrderByCreadoEnDesc(estado);
        } else {
            alquileres = alquilerRepository.findAllByOrderByCreadoEnDesc();
        }

        return alquileres.stream().map(this::aResponse).toList();
    }

    @Override
    public AlquilerResponse entregarAlquiler(Long id) {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alquiler inexistente con ID: " + id));

        if (alquiler.getEstado() != EstadoAlquiler.RESERVADO) {
            throw new ReglaNegocioException("Solo se puede entregar un alquiler en estado RESERVADO");
        }

        alquiler.setEstado(EstadoAlquiler.ENTREGADO);
        Alquiler actualizado = alquilerRepository.save(alquiler);
        return aResponse(actualizado);
    }

    @Override
    public AlquilerResponse devolverAlquiler(Long id) {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alquiler inexistente con ID: " + id));

        if (alquiler.getEstado() != EstadoAlquiler.ENTREGADO) {
            throw new ReglaNegocioException("Devolver sin entregar: solo se puede devolver un alquiler en estado ENTREGADO");
        }

        alquiler.setEstado(EstadoAlquiler.DEVUELTO);
        Alquiler actualizado = alquilerRepository.save(alquiler);
        return aResponse(actualizado);
    }

    private AlquilerResponse aResponse(Alquiler a) {
        return new AlquilerResponse(
                a.getId(),
                a.getHerramienta().getId(),
                a.getHerramienta().getNombre(),
                a.getCliente().getId(),
                a.getCliente().getNombre(),
                a.getFechaInicio(),
                a.getFechaFin(),
                a.getDias(),
                a.getValorTotal(),
                a.getDeposito(),
                a.getEstado(),
                a.getCreadoEn()
        );
    }
}


