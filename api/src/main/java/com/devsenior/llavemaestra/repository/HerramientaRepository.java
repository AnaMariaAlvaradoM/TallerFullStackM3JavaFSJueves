package com.devsenior.llavemaestra.repository;

import com.devsenior.llavemaestra.model.Herramienta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HerramientaRepository extends JpaRepository<Herramienta,Long> {

    List<Herramienta> findAllByOrderByNombreAsc();
    List<Herramienta> findByActivoTrueOrderByNombreAsc();
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
    long countByActivoTrue();

}
