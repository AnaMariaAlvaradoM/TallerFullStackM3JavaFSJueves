package com.devsenior.llavemaestra.service;

import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.model.Rol;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No existe el usuario " + email));

        return User.withUsername(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(autoridadesDe(usuario.getRol()))
                .disabled(!usuario.isActivo())
                .build();
    }

    private List<GrantedAuthority> autoridadesDe(Rol rol){
        List<GrantedAuthority> autoridades = new ArrayList<>();
        autoridades.add(new SimpleGrantedAuthority("ROLE_"+rol.name()));
        rol.getPermisos().forEach(permiso ->
                autoridades.add(new SimpleGrantedAuthority(permiso.name())));
        return autoridades;
    }

}
