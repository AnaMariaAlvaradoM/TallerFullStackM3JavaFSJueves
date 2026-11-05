package com.devsenior.llavemaestra.model;

import jakarta.persistence.*;

// Una herramienta del catalogo. "unidades" dice cuantas iguales tiene Hernan.
@Entity
@Table(name = "herramientas")
public class Herramienta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Integer precioDia;

    @Column(nullable = false)
    private Integer deposito;

    @Column(nullable = false)
    private Integer unidades;

    private String imagenUrl;

    // false = retirada (por ejemplo, en reparacion): el cliente no la ve ni la alquila
    @Column(nullable = false)
    private boolean activa = true;

    public Herramienta() {
    }

    public Herramienta(String nombre, Categoria categoria, String descripcion, Integer precioDia,
                       Integer deposito, Integer unidades, String imagenUrl, boolean activa) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precioDia = precioDia;
        this.deposito = deposito;
        this.unidades = unidades;
        this.imagenUrl = imagenUrl;
        this.activa = activa;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getPrecioDia() {
        return precioDia;
    }

    public Integer getDeposito() {
        return deposito;
    }

    public Integer getUnidades() {
        return unidades;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
