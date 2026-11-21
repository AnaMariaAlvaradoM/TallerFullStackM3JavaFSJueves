package com.devsenior.llavemaestra.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsenior.llavemaestra.model.Herramienta;

@Repository
public interface HerramientaRepository extends JpaRepository<Herramienta, Long> {
    List<Herramienta> findByActivaTrue();
    Optional<Herramienta> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
