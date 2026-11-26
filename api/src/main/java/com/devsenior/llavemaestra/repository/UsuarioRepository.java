package com.devsenior.llavemaestra.repository;

import java.util.List;
import java.util.Optional;

import com.devsenior.llavemaestra.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findAllByOrderByNombreAsc();

    long countByActivoTrue();

}
