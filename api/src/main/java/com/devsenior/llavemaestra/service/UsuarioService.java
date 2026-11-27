package com.devsenior.llavemaestra.service;

import com.devsenior.llavemaestra.dto.UsuarioResponse;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.exception.ReglaNegocioException;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// La gestion de cuentas: listar y bloquear / desbloquear
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAllByOrderByIdAsc().stream().map(UsuarioResponse::desde).toList();
    }

    @Transactional
    public UsuarioResponse cambiarEstado(Long id, boolean activo, Usuario quienPide) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la cuenta con id " + id));

        // Regla de negocio: el administrador no se puede bloquear a si mismo (se quedaria por fuera)
        if (!activo && usuario.getId().equals(quienPide.getId())) {
            throw new ReglaNegocioException("No puedes bloquear tu propia cuenta");
        }

        usuario.setActivo(activo);
        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }
}
