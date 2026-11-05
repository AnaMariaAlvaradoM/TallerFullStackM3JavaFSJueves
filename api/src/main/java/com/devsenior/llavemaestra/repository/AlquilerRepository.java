package com.devsenior.llavemaestra.repository;

import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import com.devsenior.llavemaestra.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


public interface AlquilerRepository extends JpaRepository<Alquiler, Long > {

List<Alquiler> findByUsuarioEmail(String email);
List<Alquiler> findAllByOrderByFechaInicio();
List<Alquiler> findByFechaInicio(LocalDate fecha);
List<Alquiler> findByHerramientaIdAndEstadoAndFechaInicio(Long herramientaId,EstadoAlquiler estado, LocalDate fechaInicio);
List<Alquiler> findByUsuarioAndEstado(Usuario usuario, EstadoAlquiler estado);
long countByUsuarioAndEstadoIn(Usuario usuario, Collection<EstadoAlquiler> estados);


boolean existsByHerramientaIdAndEstadoAndFechaInicioLessThanAndFechaFinGreaterThan(
        Long herramientaId, EstadoAlquiler estado, LocalDate finNueva, LocalDate inicioNueva);



List<Alquiler> findByFechaInicioBetweenAndEstado(LocalDate desde, LocalDate hasta, EstadoAlquiler estado);
}