package com.devsenior.llavemaestra.controller;

import com.devsenior.llavemaestra.dto.EstadoRequest;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PreAuthorize("hasAuthority('CLIENTES_GESTIONAR')")
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        // Las pruebas exigen las cuentas (las entidades devuelven datos limpios o sin
        // password si se omiten,
        // o puedes asegurarte de que el JSON no exponga el password).
        List<Usuario> usuarios = usuarioRepository.findAll();
        // Limpiamos los passwords por seguridad antes de enviarlos
        usuarios.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(usuarios);
    }

    @PreAuthorize("hasAuthority('CLIENTES_GESTIONAR')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoUsuario(@PathVariable Long id,
            @Valid @RequestBody EstadoRequest request,
            Authentication authentication) {
        Usuario usuarioAModificar = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String emailAutenticado = authentication.getName();

        // REGLA DE NEGOCIO: El administrador no puede bloquearse a sí mismo -> 409
        // Conflict
        if (usuarioAModificar.getEmail().equals(emailAutenticado) && !request.activa()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensajeErrorDto("El administrador no puede bloquearse a sí mismo"));
        }

        usuarioAModificar.setActivo(request.activa());
        Usuario actualizado = usuarioRepository.save(usuarioAModificar);
        actualizado.setPassword(null);

        return ResponseEntity.ok(actualizado);
    }

    // DTO interno auxiliar para el mensaje de conflicto
    public record MensajeErrorDto(String mensaje) {
    }
}