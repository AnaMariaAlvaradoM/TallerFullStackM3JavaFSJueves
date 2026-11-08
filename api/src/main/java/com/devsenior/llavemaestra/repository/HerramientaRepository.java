package com.devsenior.llavemaestra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.devsenior.llavemaestra.model.Herramienta;

public interface HerramientaRepository extends JpaRepository<Herramienta, Long> {

    List<Herramienta> findAllByOrderByNombreAsc();

    List<Herramienta> findByActivaTrueOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    long countByActivaTrue();
}
