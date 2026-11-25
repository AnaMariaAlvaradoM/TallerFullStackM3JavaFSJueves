package com.devsenior.llavemaestra.repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;

public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {

    List<Alquiler> findByClienteIdOrderByCreadoEnDescIdDesc(Long clienteId);

    List<Alquiler> findAllByOrderByCreadoEnDescIdDesc();

    List<Alquiler> findByEstadoOrderByCreadoEnDescIdDesc(EstadoAlquiler estado);

    long countByClienteIdAndEstadoIn(Long clienteId, Collection<EstadoAlquiler> estados);

    long countByEstadoIn(Collection<EstadoAlquiler> estados);

    @Query("""
            SELECT a FROM Alquiler a
            JOIN FETCH a.herramienta
            JOIN FETCH a.cliente
            WHERE a.herramienta.id = :herramientaId
              AND a.estado IN :estados
              AND a.fechaInicio <= :fechaFin
              AND a.fechaFin >= :fechaInicio
            """)
    List<Alquiler> buscarSolapados(
            @Param("herramientaId") Long herramientaId,
            @Param("estados") Collection<EstadoAlquiler> estados,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("""
            SELECT a FROM Alquiler a
            JOIN FETCH a.herramienta
            JOIN FETCH a.cliente
            WHERE a.estado = com.devsenior.llavemaestra.model.EstadoAlquiler.ENTREGADO
              AND a.fechaFin < :hoy
            ORDER BY a.fechaFin ASC, a.id ASC
            """)
    List<Alquiler> buscarVencidos(@Param("hoy") LocalDate hoy);

    @Query("""
            SELECT a FROM Alquiler a
            JOIN FETCH a.herramienta
            JOIN FETCH a.cliente
            WHERE a.estado <> com.devsenior.llavemaestra.model.EstadoAlquiler.CANCELADO
              AND a.creadoEn >= :inicioMes
            """)
    List<Alquiler> buscarNoCanceladosDesde(@Param("inicioMes") Instant inicioMes);

    @Query("""
            SELECT a FROM Alquiler a
            JOIN FETCH a.herramienta
            JOIN FETCH a.cliente
            WHERE a.estado <> com.devsenior.llavemaestra.model.EstadoAlquiler.CANCELADO
            """)
    List<Alquiler> buscarNoCancelados();
}