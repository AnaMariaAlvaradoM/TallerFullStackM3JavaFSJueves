package com.devsenior.llavemaestra.repository;

import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;
import java.util.List;


public interface AlquilerRepository extends JpaRepository<Alquiler, Long > {
    List<Alquiler> findByUsuarioEmailOrderByFechaDescFechaInicioDesc(String email);
    List<Alquiler> findAllByOrderByFechaDescFechaInicioDesc();
    List<Alquiler> findByFechaOrderByFechaInicioAsc(LocalDate fecha);
    List<Alquiler> findByHerramientaIdAndFechaAndEstadoOrderByFechaInicioAsc(
            Long herramientaId, LocalDate fecha, EstadoAlquiler estado);

    boolean existsByHerramientaIdAndFechaAndEstadoAndFechaInicioLessThanAndFechaInicioGreaterThan(
            Long herramientaId, LocalDate fecha, EstadoAlquiler estado,LocalDate finNueva,LocalDate inicioNueva
    );

    List<Alquiler> findByFechaBetweenAndEstado(LocalDate desde, LocalDate hasta, EstadoAlquiler estado);




}
