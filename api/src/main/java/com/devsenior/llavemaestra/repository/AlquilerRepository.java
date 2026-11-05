package com.devsenior.llavemaestra.repository;

import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler, Long> {

    // Los alquileres de un cliente, los que empiezan mas tarde primero
    List<Alquiler> findByClienteIdOrderByFechaInicioDescIdDesc(Long clienteId);

    // Cuantos alquileres en ciertos estados (por ejemplo, los activos) tiene un cliente
    long countByClienteIdAndEstadoIn(Long clienteId, Collection<EstadoAlquiler> estados);

    // Cuantos alquileres activos de una herramienta se cruzan con [inicio, fin].
    // Dos rangos se cruzan si comparten al menos un dia:
    // el otro empieza antes (o el mismo dia) de que yo termine
    // y termina despues (o el mismo dia) de que yo empiezo.
    @Query("""
            select count(a) from Alquiler a
            where a.herramienta.id = :herramientaId
              and a.estado in :estados
              and a.fechaInicio <= :fin
              and a.fechaFin >= :inicio
            """)
    long contarCruzados(@Param("herramientaId") Long herramientaId,
                        @Param("estados") Collection<EstadoAlquiler> estados,
                        @Param("inicio") LocalDate inicio,
                        @Param("fin") LocalDate fin);
}
