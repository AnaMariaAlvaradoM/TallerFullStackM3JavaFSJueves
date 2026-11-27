package com.devsenior.llavemaestra.model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "alquileres")
public class Alquiler {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (optional = false)
    @JoinColumn (name = "herramienta_id", nullable = false)
    private Herramienta herramienta;

    @ManyToOne (optional = false)
    @JoinColumn (name = "cliente_id", nullable = false)
    private Usuario cliente;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

     // Estos tres valores los calcula SIEMPRE el backend, nunca el JSON
    @Column(nullable = false)
    private Integer dias;

    @Column(nullable = false)
    private Integer valorTotal;

    @Column(nullable = false)
    private Integer deposito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAlquiler estado = EstadoAlquiler.RESERVADO;

    @Column(nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public Alquiler() {
    }

    public Alquiler(Herramienta herramienta, Usuario cliente, LocalDate fechaInicio, LocalDate fechaFin, Integer dias, Integer valorTotal, Integer deposito, EstadoAlquiler estado) {
        this.herramienta = herramienta;
        this.cliente = cliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.dias = dias;
        this.valorTotal = valorTotal;
        this.deposito = deposito;
        this.estado = estado;
    }

    // ---------- Getters y setters ----------

    public Long getId() {
        return id;
    }

    public Herramienta getHerramienta() {
        return herramienta;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public Integer getDias() {
        return dias;
    }

    public Integer getValorTotal() {
        return valorTotal;
    }

    public Integer getDeposito() {
        return deposito;
    }

    public EstadoAlquiler getEstado() {
        return estado;
    }

    public void setEstado(EstadoAlquiler estado) {
        this.estado = estado;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }


}
