package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.Categoria;
import com.devsenior.llavemaestra.model.Herramienta;

// Lo que ve el frontend de una herramienta
public record HerramientaResponse(
        Long id,
        String nombre,
        Categoria categoria,
        String descripcion,
        Integer precioDia,
        Integer deposito,
        Integer unidades,
        String imagenUrl,
        boolean activa) {

    // Convierte la entidad en el DTO de respuesta
    public static HerramientaResponse desde(Herramienta h) {
        return new HerramientaResponse(h.getId(), h.getNombre(), h.getCategoria(), h.getDescripcion(),
                h.getPrecioDia(), h.getDeposito(), h.getUnidades(), h.getImagenUrl(), h.isActiva());
    }
}
