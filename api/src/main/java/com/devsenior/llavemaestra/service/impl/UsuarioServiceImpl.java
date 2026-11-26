package com.devsenior.llavemaestra.service.impl;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsenior.llavemaestra.dto.UsuarioResponse;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.exception.ReglaNegocioException;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import com.devsenior.llavemaestra.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .sorted(Comparator.comparing(Usuario::getId))
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public UsuarioResponse cambiarEstado(Long id, boolean activo, String emailAutenticado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (!activo && usuario.getEmail().equalsIgnoreCase(emailAutenticado)) {
            throw new ReglaNegocioException("No puedes bloquear tu propia cuenta de administrador");
        }

        usuario.setActivo(activo);
        return UsuarioResponse.fromEntity(usuarioRepository.save(usuario));
    }
}