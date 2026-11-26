package com.devsenior.llavemaestra.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.dto.FranjaOcupada;
import com.devsenior.llavemaestra.dto.HerramientaRequest;
import com.devsenior.llavemaestra.dto.HerramientaResponse;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.exception.ReglaNegocioException;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Permiso;
import com.devsenior.llavemaestra.repository.AlquilerRepository;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.service.HerramientaService;


@Service 
public class HerramientaServiceImpl implements HerramientaService{

    private final HerramientaRepository herramientaRepository;
    private final AlquilerRepository alquilerRepository;

    

    public HerramientaServiceImpl(HerramientaRepository herramientaRepository, AlquilerRepository alquilerRepository) {
        this.herramientaRepository = herramientaRepository;
        this.alquilerRepository = alquilerRepository;
    }

    // Mismo endpoint, distinta respuesta segun el permiso:
    // quien gestiona ve todos (activos e inactivos); un miembro solo los que puede reservar
    @Override
    public List<HerramientaResponse> listar(Authentication autenticacion) {
        List<Herramienta> herramientas = Permiso.HERRAMIENTAS_GESTIONAR.estaEn(autenticacion)
                ? herramientaRepository.findAll()
                : herramientaRepository.findByActivaTrueOrderByNombreAsc();
        
        return  herramientas.stream().map(this::toResponse).toList();
    }

    @Override
    public HerramientaResponse buscarPorId(Long id, Authentication autenticacion) {
        Herramienta herramienta = buscarEntidad(id);
         
        if(!herramienta.isActiva() && !Permiso.HERRAMIENTAS_GESTIONAR.estaEn(autenticacion)){
            throw new RecursoNoEncontradoException("No existe una herramienta con este id: "+id);
        }
        return toResponse(herramienta);
    }

    @Override
    public HerramientaResponse crear(HerramientaRequest request) {
        if(herramientaRepository.existsByNombreIgnoreCase(request.nombre().trim())){
            throw new ReglaNegocioException(String.format("Ya existe esta herramienta llamada %s",request.nombre()));
        }
        
        Herramienta herramienta = new Herramienta();
        aplicarDatos(herramienta, request);
        return toResponse(herramientaRepository.save(herramienta));
    }

    @Override
    public HerramientaResponse actualizar(Long id, HerramientaRequest request) {
        Herramienta herramienta = buscarEntidad(id);
        if(herramientaRepository.existsByNombreIgnoreCaseAndIdNot(herramienta.getNombre(), id)){
            throw new ReglaNegocioException(String.format("Ya existe esta herramienta llamada %s",request.nombre()));
        }
        aplicarDatos(herramienta, request);
        return  toResponse(herramientaRepository.save(herramienta));
    }

    @Override
    public HerramientaResponse cambiarEstado(Long id, boolean activo) {
        Herramienta herramienta = buscarEntidad(id);
        herramienta.setActiva(activo);
        return  toResponse(herramientaRepository.save(herramienta));
    }

    // Franjas ocupadas de un dia: sirve para pintar la disponibilidad
    @Override
    public List<FranjaOcupada> ocupacion(Long id, LocalDate fecha) {
        buscarEntidad(id);
        return  alquilerRepository.findByHerramientaIdAndCreadaEnAndEstadoOrderByHoraInicioAsc(id, fecha, EstadoAlquiler.RESERVADO)
                .stream()
                .map(h -> new FranjaOcupada(h.getFechaInicio(),h.getFechaFin()))
                .toList();
    }

    //--- metodos privados ---

    private void aplicarDatos (Herramienta herramienta, HerramientaRequest request){
        herramienta.setNombre(request.nombre().trim());
        herramienta.setCategoria(request.categoria());
        herramienta.setDeposito(request.deposito());
        herramienta.setPrecioDia(request.precioDia());
        herramienta.setDescripcion(request.descripcion());
        herramienta.setUnidades(request.unidades());
        herramienta.setImagenUrl(request.imagenUrl());
    }

    private Herramienta buscarEntidad (Long id){
        return herramientaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("No existe una herramienta con id " + id));
    }
    private  HerramientaResponse toResponse(Herramienta herramienta){
        return new HerramientaResponse(
            herramienta.getId(),
            herramienta.getNombre(),
            herramienta.getCategoria(),
            herramienta.getDeposito(),
            herramienta.getPrecioDia(),
            herramienta.getDescripcion(),
            herramienta.getUnidades(),
            herramienta.getImagenUrl(),
            herramienta.isActiva()
        );
    }
}
