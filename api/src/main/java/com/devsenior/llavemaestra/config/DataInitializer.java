package com.devsenior.llavemaestra.config;

import com.devsenior.llavemaestra.model.Categoria;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Rol;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UsuarioRepository usuarioRepository,
            HerramientaRepository herramientaRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                String passwordHash = passwordEncoder.encode("llave123");

                Usuario hernan = new Usuario();
                hernan.setNombre("Hernán Rojas");
                hernan.setEmail("admin@llavemaestra.co");
                hernan.setPassword(passwordHash);
                hernan.setRol(Rol.ADMIN);
                hernan.setActivo(true);
                hernan.setCreadoEn(LocalDateTime.now());

                Usuario pedro = new Usuario();
                pedro.setNombre("Pedro Salazar");
                pedro.setEmail("pedro@correo.com");
                pedro.setPassword(passwordHash);
                pedro.setRol(Rol.CLIENTE);
                pedro.setActivo(true);
                pedro.setCreadoEn(LocalDateTime.now());

                Usuario diana = new Usuario();
                diana.setNombre("Diana Mejía");
                diana.setEmail("diana@correo.com");
                diana.setPassword(passwordHash);
                diana.setRol(Rol.CLIENTE);
                diana.setActivo(true);
                diana.setCreadoEn(LocalDateTime.now());

                Usuario jorge = new Usuario();
                jorge.setNombre("Jorge Pinzón");
                jorge.setEmail("jorge@correo.com");
                jorge.setPassword(passwordHash);
                jorge.setRol(Rol.CLIENTE);
                jorge.setActivo(false);
                jorge.setCreadoEn(LocalDateTime.now());

                usuarioRepository.saveAll(List.of(hernan, pedro, diana, jorge));
            }

            if (herramientaRepository.count() == 0) {
                Herramienta taladro = new Herramienta();
                taladro.setNombre("Taladro percutor");
                taladro.setCategoria(Categoria.ELECTRICA);
                taladro.setPrecioDia(25000.0);
                taladro.setDeposito(100000.0);
                taladro.setUnidades(3);
                taladro.setActiva(true);
                taladro.setDescripcion("Taladro percutor profesional.");
                taladro.setImagenUrl("https://loremflickr.com/320/240/drill");

                Herramienta pulidora = new Herramienta();
                pulidora.setNombre("Pulidora angular");
                pulidora.setCategoria(Categoria.ELECTRICA);
                pulidora.setPrecioDia(22000.0);
                pulidora.setDeposito(80000.0);
                pulidora.setUnidades(2);
                pulidora.setActiva(true);
                pulidora.setDescripcion("Pulidora angular.");
                pulidora.setImagenUrl("https://loremflickr.com/320/240/grinder");

                Herramienta mezcladora = new Herramienta();
                mezcladora.setNombre("Mezcladora de concreto");
                mezcladora.setCategoria(Categoria.CONSTRUCCION);
                mezcladora.setPrecioDia(60000.0);
                mezcladora.setDeposito(300000.0);
                mezcladora.setUnidades(1);
                mezcladora.setActiva(true);
                mezcladora.setDescripcion("Mezcladora de cemento.");
                mezcladora.setImagenUrl("https://loremflickr.com/320/240/mixer");

                Herramienta andamio = new Herramienta();
                andamio.setNombre("Andamio tubular");
                andamio.setCategoria(Categoria.CONSTRUCCION);
                andamio.setPrecioDia(15000.0);
                andamio.setDeposito(120000.0);
                andamio.setUnidades(4);
                andamio.setActiva(true);
                andamio.setDescripcion("Andamio tubular metálico.");
                andamio.setImagenUrl("https://loremflickr.com/320/240/scaffolding");

                Herramienta guadanadora = new Herramienta();
                guadanadora.setNombre("Guadañadora");
                guadanadora.setCategoria(Categoria.JARDIN);
                guadanadora.setPrecioDia(35000.0);
                guadanadora.setDeposito(150000.0);
                guadanadora.setUnidades(2);
                guadanadora.setActiva(true);
                guadanadora.setDescripcion("Guadaña a gasolina.");
                guadanadora.setImagenUrl("https://loremflickr.com/320/240/mower");

                Herramienta nivel = new Herramienta();
                nivel.setNombre("Nivel láser");
                nivel.setCategoria(Categoria.MEDICION);
                nivel.setPrecioDia(30000.0);
                nivel.setDeposito(200000.0);
                nivel.setUnidades(1);
                nivel.setActiva(false); // Retirada
                nivel.setDescripcion("Nivel láser autonivelante.");
                nivel.setImagenUrl("https://loremflickr.com/320/240/laser");

                herramientaRepository.saveAll(List.of(taladro, pulidora, mezcladora, andamio, guadanadora, nivel));
            }
        };
    }
}