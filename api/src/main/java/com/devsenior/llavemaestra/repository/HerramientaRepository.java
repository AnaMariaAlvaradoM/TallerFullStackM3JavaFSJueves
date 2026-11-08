package com.devsenior.llavemaestra.repository;

import com.devsenior.llavemaestra.model.Herramienta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HerramientaRepository extends JpaRepository<Herramienta, Long> {

    // Todas (para el administrador)
    List<Herramienta> findAllByOrderByIdAsc();

    // Solo las activas (para el cliente)
    List<Herramienta> findByActivaTrueOrderByIdAsc();
}
