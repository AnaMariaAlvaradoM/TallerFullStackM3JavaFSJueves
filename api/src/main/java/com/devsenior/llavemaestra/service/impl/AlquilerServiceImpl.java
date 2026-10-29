package com.devsenior.llavemaestra.service.impl;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.exception.ReglaNegocioException;
import com.devsenior.llavemaestra.model.*;
import com.devsenior.llavemaestra.repository.AlquilerRepository;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import com.devsenior.llavemaestra.service.AlquilerService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
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
        return alquilerRepository.findByUsuarioEmailOrderByFechaDescFechaInicioDesc(email)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<AlquilerResponse> listarTodas(LocalDate fecha) {
        List<Alquiler> alquileres = fecha==null
                ? alquilerRepository.findAllByOrderByFechaDescFechaInicioDesc()
                : alquilerRepository.findByFechaOrderByFechaInicioAsc(fecha);

        return  alquileres.stream().map(this::toResponse).toList();
    }

    @Override
    public AlquilerResponse crear(AlquilerRequest request, String email) {
        Herramienta herramienta = herramientaRepository.findById(request.herramientaId())
                .orElseThrow(()->new RecursoNoEncontradoException(
                        "No existe una herramienta con id " + request.herramientaId()));
        if (!herramienta.isActivo()){
            throw  new ReglaNegocioException(herramienta.getNombre() + "no esta disponible para alquilar");
        }
//        validarFecha(request.fecha(), request.fechaInicio(),request.fechaFin());

        boolean seCruza = alquilerRepository
                .existsByHerramientaIdAndFechaAndEstadoAndFechaInicioLessThanAndFechaInicioGreaterThan(
                        herramienta.getId(),request.fecha(), EstadoAlquiler.ALQUILADA,
                        request.fechaFin(),request.fechaInicio());
        if(seCruza){
            throw new ReglaNegocioException(herramienta.getNombre() + "Ya esta alquilada la herramienta para esa fecha");
        }

        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + email));
        long dias = Duration.between(request.fechaInicio(),request.fechaFin()).toDays();

        Alquiler alquiler = new Alquiler();

        alquiler.setHerramienta(herramienta);
        alquiler.setUsuario(usuario);
        alquiler.setFecha(request.fecha());
        alquiler.setFechaInicio(request.fechaInicio());
        alquiler.setFechaFin(request.fechaFin());
        alquiler.setNota(request.nota()==null?null: request.nota().trim());
        alquiler.setValorTotal((int) (herramienta.getPrecioDia() * dias));
        alquiler.setEstado(EstadoAlquiler.ALQUILADA);

        return  toResponse(alquilerRepository.save(alquiler));
    }

    @Override
    public AlquilerResponse cancelar(Long id, Authentication autentication) {
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(()-> new RecursoNoEncontradoException("No existe una reserva con id " + id));

        boolean puedeCancelarCualquiera = Permiso.ALQUILERES_CANCELAR_TODAS.estaEn(autentication);
        boolean esSuya = alquiler.getUsuario().getEmail().equals(autentication.getName());

        if (!puedeCancelarCualquiera && !esSuya){
            throw new AccessDeniedException("Solo puedes cancelar tus propios Alquileres");
        }

        if (alquiler.getEstado() == EstadoAlquiler.CANCELADA){
            throw new ReglaNegocioException("La Herramienta ya estaba cancelada");
        }


        return toResponse(alquilerRepository.save(alquiler));
    }


    private AlquilerResponse toResponse(Alquiler alquiler){
        Herramienta herramienta = alquiler.getHerramienta();
        Usuario usuario = alquiler.getUsuario();
        return new AlquilerResponse(
                alquiler.getId(),
                herramienta.getId(),
                herramienta.getNombre(),
                herramienta.getCategoria(),
                usuario.getId(),
                usuario.getNombre(),
                alquiler.getFecha(),
                alquiler.getFechaInicio(),
                alquiler.getFechaFin(),
                alquiler.getNota(),
                alquiler.getValorTotal(),
                alquiler.getEstado(),
                alquiler.getCreadaEn()
        );
    }
}
