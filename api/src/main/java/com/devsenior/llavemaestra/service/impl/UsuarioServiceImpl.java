package com.devsenior.llavemaestra.service.impl;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.service.UsuarioService;
import com.devsenior.llavemaestra.dto.UsuarioResponse;
import com.devsenior.llavemaestra.exception.ConflictoException;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::aResponse)
                .toList();
    }

    @Override
    public UsuarioResponse cambiarEstado(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

        String emailActual = SecurityContextHolder.getContext().getAuthentication().getName();

        if (usuario.getEmail().equalsIgnoreCase(emailActual) && !activo) {
            throw new ConflictoException("Un administrador no puede bloquear su propia cuenta.");
        }

        usuario.setActivo(activo);
        Usuario guardado = usuarioRepository.save(usuario);
        return aResponse(guardado);
    }

    private UsuarioResponse aResponse(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNombre(),
                u.getEmail(),
                u.getRol(),
                u.isActivo(),
                u.getCreadoEn()
        );
    }
}


