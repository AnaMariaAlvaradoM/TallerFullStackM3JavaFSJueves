package com.devsenior.llavemaestra.dto;

import com.devsenior.llavemaestra.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record UsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre admite maximo 80 carácteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no cumple con un formato válido. ej: nombre@ejemplo.com")
        String email,

        @NotBlank(message = "Debe de introducir una contraseña válida")
        @Size(min = 5, message = "La contraseña debe de contener al menos cinco caráacteres")
        String password,
        @NotNull(message = "el rol es obligatorio")
        Rol rol
) {
}
