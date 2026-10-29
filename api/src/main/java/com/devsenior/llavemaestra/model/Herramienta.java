package com.devsenior.llavemaestra.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Herramientas")
public class Herramienta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private Integer precioDia;

    @Column(nullable = false)
    private Integer deposito;
    @Column(length = 500)
    private String descripcion;

    @Column(length = 300)
    private String imagenUrl;

    @Column(nullable = false)
    private Integer unidades;
    @Column(nullable = false)
    private boolean activa = true;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Integer getPrecioDia() {
        return precioDia;
    }
    public void setPrecioDia(Integer precioDia) {
        this.precioDia = precioDia;
    }

    public Integer getDeposito() {
        return deposito;
    }
    public void setDeposito(Integer deposito) {
        this.deposito = deposito;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Integer getUnidades() {
        return unidades;
    }
    public void setUnidades(Integer unidades) {
        this.unidades = unidades;
    }

    public boolean isActiva() {
        return activa;
    }
    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
