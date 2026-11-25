package com.devsenior.llavemaestra.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;

public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {

    
  // 1. Activos de un cliente
  long countByUsuarioIdAndEstadoIn(Long usuarioId, Collection<EstadoAlquiler> estados);


    // 2. Cruces de una herramienta 

    long countByHerramientaIdAndEstadoInAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
        Long herramientaId, Collection<EstadoAlquiler>estados, LocalDate finNuevo, LocalDate inicioNuevo);
    

    // 3. Mis alquileres

    List<Alquiler>findByUsuarioEmailOrderByCreadoEnDesc(String email);
}
