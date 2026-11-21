package com.devsenior.llavemaestra.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "herramientas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Herramienta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precioDia;

    @Column(nullable = false)
    private BigDecimal deposito;

    @Column(nullable = false)
    private Integer unidades;

    @Column(nullable = false)
    private String imagenUrl;

    private boolean activa;
}
