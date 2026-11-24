package com.devsenior.llavemaestra.repository;

import com.devsenior.llavemaestra.model.Herramienta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HerramientaRepository extends JpaRepository<Herramienta, Long> {
}