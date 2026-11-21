package com.devsenior.llavemaestra.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alquileres")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alquiler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "herramienta_id", nullable = false)
    private Herramienta herramienta;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario cliente;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private int dias;
    private BigDecimal valorTotal;
    private BigDecimal deposito;

    @Enumerated(EnumType.STRING)
    private EstadoAlquiler estado;

    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now(ZoneId.systemDefault());

    @PrePersist
    private void prePersist() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now(ZoneId.systemDefault());
        }
    }
}
