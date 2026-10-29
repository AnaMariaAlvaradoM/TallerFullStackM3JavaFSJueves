package com.devsenior.llavemaestra.service.impl;

import com.devsenior.llavemaestra.dto.UsuarioRequest;
import com.devsenior.llavemaestra.dto.UsuarioResponse;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.exception.ReglaNegocioException;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import com.devsenior.llavemaestra.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public List<UsuarioResponse> listar() {
        return  usuarioRepository.findAllByOrderByNombreAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        String email = request.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)){
            throw new ReglaNegocioException("Ya existe una cuenta con el email" + email);
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre().trim());
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(request.rol());
        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponse cambiarEstado(Long id, boolean activo, String emailDeQuienPide) {
       Usuario usuario = usuarioRepository.findById(id)
               .orElseThrow(()->new RecursoNoEncontradoException("No existe un usuario con id " + id));
       if (!activo && usuario.getEmail().equals(emailDeQuienPide)){
           throw new ReglaNegocioException("No puedes desactivar tu propia cuenta");
       }

       usuario.setActiva(activo);
        return toResponse(usuarioRepository.save(usuario));
    }

    private UsuarioResponse toResponse (Usuario usuario){
        return  new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.isActiva(),
                usuario.getCreadoEn()
        );
    }
}
