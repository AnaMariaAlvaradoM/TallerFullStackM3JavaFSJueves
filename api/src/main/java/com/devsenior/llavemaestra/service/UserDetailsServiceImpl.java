package com.devsenior.llavemaestra.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .or(() -> usuarioRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con: " + username));

        List<SimpleGrantedAuthority> authorities = usuario.getRol().getPermisos().stream()
                .map(p -> new SimpleGrantedAuthority(p.name()))
                .toList();

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.isActivo(),
                true,
                true,
                true,
                authorities
        );
    }
}
