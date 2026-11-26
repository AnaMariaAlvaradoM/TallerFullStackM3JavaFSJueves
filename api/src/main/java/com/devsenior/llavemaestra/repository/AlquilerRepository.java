package com.devsenior.llavemaestra.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {

    List<Alquiler> findByUsuarioEmailOrderByCreadaEnDescFechaInicioDesc(String email);

    List<Alquiler> findAllByOrderByCreadaEnDescFechaInicioDesc();

    // Alquileres vigentes en una fecha
    @Query("""
        SELECT a FROM Alquiler a
        WHERE a.fechaInicio <= :fecha AND a.fechaFin >= :fecha
        ORDER BY a.fechaInicio ASC
        """)
    List<Alquiler> findByCreadaEnOrderByFechaInicioAsc(@Param("fecha") LocalDate fecha);

    // Alquileres de una herramienta vigentes en una fecha, con cierto estado
    @Query("""
        SELECT a FROM Alquiler a
        WHERE a.herramienta.id = :herramientaId
          AND a.fechaInicio <= :fecha AND a.fechaFin >= :fecha
          AND a.estado = :estado
        ORDER BY a.fechaInicio ASC
        """)
    List<Alquiler> findByHerramientaIdAndCreadaEnAndEstadoOrderByHoraInicioAsc(
            @Param("herramientaId") Long herramientaId,
            @Param("fecha") LocalDate fecha,
            @Param("estado") EstadoAlquiler estado);

    @Query("""
        SELECT COUNT(a) FROM Alquiler a
        WHERE a.herramienta.id = :herramientaId
          AND a.estado IN :estados
          AND a.fechaInicio <= :fin
          AND a.fechaFin >= :inicio
        """)
    long contarCruces(@Param("herramientaId") Long herramientaId,
                      @Param("estados") List<EstadoAlquiler> estados,
                      @Param("inicio") LocalDate inicio,
                      @Param("fin") LocalDate fin);

    List<Alquiler> findByFechaInicioBetweenAndEstado(LocalDate desde, LocalDate hasta, EstadoAlquiler estado);

    Long countByUsuarioIdAndEstadoIn(Long usuarioId, List<EstadoAlquiler> estados);
}
