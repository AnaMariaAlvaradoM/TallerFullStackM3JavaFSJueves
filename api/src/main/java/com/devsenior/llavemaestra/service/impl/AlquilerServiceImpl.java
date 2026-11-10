package com.devsenior.llavemaestra.service.impl;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.exception.DatoInvalidoException;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.exception.ReglaNegocioException;
import com.devsenior.llavemaestra.model.*;
import com.devsenior.llavemaestra.repository.AlquilerRepository;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import com.devsenior.llavemaestra.service.AlquilerService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AlquilerServiceImpl implements AlquilerService {

    private  final AlquilerRepository alquilerRepository;
    private final UsuarioRepository usuarioRepository;
    private final HerramientaRepository herramientaRepository;

    public AlquilerServiceImpl(AlquilerRepository alquilerRepository, UsuarioRepository usuarioRepository, HerramientaRepository herramientaRepository) {
        this.alquilerRepository = alquilerRepository;
        this.usuarioRepository = usuarioRepository;
        this.herramientaRepository = herramientaRepository;
    }


    @Override
    public List<AlquilerResponse> listarMias(String email) {
        return alquilerRepository.findByUsuarioEmail(email)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<AlquilerResponse> listarTodas(LocalDate fecha) {
        List<Alquiler> alquileres = fecha == null
                ? alquilerRepository.findAllByOrderByFechaInicio()
                : alquilerRepository.findByFechaInicio(fecha);
        return  alquileres.stream().map(this::toResponse).toList();
    }

    @Override
    public AlquilerResponse crear(AlquilerRequest request, String email) {
        Herramienta herramienta = herramientaRepository.findById(request.herramientaId())
                .orElseThrow(() -> new RecursoNoEncontradoException( //Herramienta no existe 404
                        "No existe una herramienta con id " + request.herramientaId()
                ));
        if (!herramienta.isActiva()) { //la herramienta esta retirada 409
            throw new ReglaNegocioException(herramienta.getNombre() + "No esta disponible para alquilar");
        }
        limiteAlquiler(email);
        validarDias(request.fechaInicio(),request.fechaFin());

//        boolean seCruza = alquilerRepository
//                .existsByHerramientaIdAndEstadoInAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
//                        herramienta.getId(), List.of(EstadoAlquiler.RESERVADO,EstadoAlquiler.ENTREGADO),
//                        request.fechaFin(),request.fechaInicio());
//
//        if (seCruza){
//            throw new ReglaNegocioException( herramienta.getNombre() +" ya se encuentra alquilada en esta fecha");
//        }

        long seCruza = alquilerRepository
                .countByHerramientaIdAndEstadoInAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                        herramienta.getId(), List.of(EstadoAlquiler.RESERVADO,EstadoAlquiler.ENTREGADO),
                        request.fechaFin(),request.fechaInicio());

        if (seCruza>=herramienta.getUnidades()){
            throw new ReglaNegocioException( herramienta.getNombre() +" ya se encuentra alquilada en esta fecha");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(()-> new RecursoNoEncontradoException("No existe el usuario "+ email));
        int dias = (int) ChronoUnit.DAYS.between(request.fechaInicio(),request.fechaFin())+1;

        int valorTotal = dias * (herramienta.getPrecioDia());

        Alquiler alquiler = new Alquiler();
        alquiler.setHerramienta(herramienta);
        alquiler.setUsuario(usuario);
        alquiler.setDias(dias);
        alquiler.setFechaInicio(request.fechaInicio());
        alquiler.setFechaFin(request.fechaFin());
        alquiler.setValorTotal(valorTotal);
        alquiler.setEstado(EstadoAlquiler.RESERVADO);

        return toResponse(alquilerRepository.save(alquiler));
    }

    @Override
    public AlquilerResponse cancelar(Long id, Authentication authentication)  {
        Alquiler alquiler =alquilerRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un alquiler con id "+id));

        boolean puedeCancelarCualquiera = Permiso.ALQUILERES_CANCELAR_TODAS.estaEn(authentication);
        boolean esSuya = alquiler.getUsuario().getEmail().equals(authentication.getName());

        if (!puedeCancelarCualquiera && !esSuya){
            throw new AccessDeniedException("Solo puedes cancelar tu propios alquileres");
        }

        if (alquiler.getEstado()==EstadoAlquiler.CANCELADO || alquiler.getEstado() == EstadoAlquiler.DEVUELTO){
            throw  new ReglaNegocioException("La reserva ya estaba cancelada");
        }

        LocalDate inicio = alquiler.getFechaInicio();

        if ((inicio.isBefore(LocalDate.now()))){
            throw  new ReglaNegocioException("No se puede cancelar una reserva que ya  comenzó");
        }

        if (inicio.isEqual(LocalDate.now())){
            throw  new ReglaNegocioException("No se puede Cancelar una reserva hecha el mismo día");
        }

        alquiler.setEstado(EstadoAlquiler.CANCELADO);
        return toResponse(alquilerRepository.save(alquiler));
    }

//    private long limiteCantidades(long herramientaId){
//        Herramienta herramienta = herramientaRepository.findById(herramientaId)
//                .orElseThrow(()-> new RecursoNoEncontradoException("No existe una herramienta con id " + herramientaId));
//        List<EstadoAlquiler> estados = List.of(EstadoAlquiler.ENTREGADO,EstadoAlquiler.RESERVADO);
//
//        long unidadesHerramientaAlquilada = alquilerRepository.countByHerramientaIdAndEstadoInAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(herramientaId,estados);
//        return unidadesHerramientaAlquilada;
//    }
    private void limiteAlquiler(String email){
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(()->new RecursoNoEncontradoException("No existe un usuario con email " + email));

        //Crea una lista que contiene estos dos estados RESERVADO O ENTREGADO -> RESERVADO
        List<EstadoAlquiler> estado = List.of(EstadoAlquiler.RESERVADO,EstadoAlquiler.ENTREGADO);

        long cantidadAlquileres = alquilerRepository.countByUsuarioAndEstadoIn(usuario,estado);

        if (cantidadAlquileres>=3){
            throw new ReglaNegocioException("No puedes tener más de tres alquileres al mismo tiempo. tienes: " + cantidadAlquileres );
        }
    }
    // valida que los días cumplan con las reglas establecidas
    private void validarDias(LocalDate inicio, LocalDate fin){
        // Reserva máximo por 15 día
        if (ChronoUnit.DAYS.between(inicio,fin)+1>15){
            throw  new DatoInvalidoException("Solo se pueden alquilar herramientas por máximo 15 días");

        }
        // Fecha fin debe de ser posterior a la fecha de inicio
        if (fin.isBefore(inicio)){
            throw  new DatoInvalidoException("La fecha de fin debe ser posterior a la fecha de inicio del alquiler");
        }
        // No se puede hacer una reserva en un día que ya pasó
        if (inicio.isBefore(LocalDate.now())){
            throw  new DatoInvalidoException("No se puede hacer una reserva en un día que ya pasó");
        }
    }



    private AlquilerResponse toResponse(Alquiler alquiler){
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
                herramienta.getDeposito(),
                alquiler.getEstado(),
                alquiler.getCreadoEn()

        );
    }
}
