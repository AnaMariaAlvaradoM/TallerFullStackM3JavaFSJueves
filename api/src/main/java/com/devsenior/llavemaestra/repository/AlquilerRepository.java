package com.devsenior.llavemaestra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Usuario;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {
    List<Alquiler> findByCliente(Usuario cliente);
    List<Alquiler> findByClienteOrderByCreadoEnDesc(Usuario cliente);
    List<Alquiler> findAllByOrderByCreadoEnDesc();
    List<Alquiler> findByEstadoOrderByCreadoEnDesc(EstadoAlquiler estado);
    List<Alquiler> findByHerramientaAndEstadoIn(Herramienta herramienta, List<EstadoAlquiler> estados);
    long countByClienteAndEstadoIn(Usuario cliente, List<EstadoAlquiler> estados);
    long countByEstadoIn(List<EstadoAlquiler> estados);
}
