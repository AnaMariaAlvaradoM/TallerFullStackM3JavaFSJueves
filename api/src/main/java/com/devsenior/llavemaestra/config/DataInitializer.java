package com.devsenior.llavemaestra.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.Categoria;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Rol;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.AlquilerRepository;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final HerramientaRepository herramientaRepository;
    private final AlquilerRepository alquilerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UsuarioRepository usuarioRepository,
            HerramientaRepository herramientaRepository,
            AlquilerRepository alquilerRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.herramientaRepository = herramientaRepository;
        this.alquilerRepository = alquilerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Sembrar Usuarios
        if (usuarioRepository.count() == 0) {
            String passwordDefault = passwordEncoder.encode("llave123");

            Usuario hernan = Usuario.builder()
                    .nombre("Hernán Rojas")
                    .email("admin@llavemaestra.co")
                    .username("admin@llavemaestra.co")
                    .password(passwordDefault)
                    .rol(Rol.ADMIN)
                    .activo(true)
                    .creadoEn(LocalDateTime.now(ZoneId.systemDefault()))
                    .build();

            Usuario pedro = Usuario.builder()
                    .nombre("Pedro Salazar")
                    .email("pedro@correo.com")
                    .username("pedro@correo.com")
                    .password(passwordDefault)
                    .rol(Rol.CLIENTE)
                    .activo(true)
                    .creadoEn(LocalDateTime.now(ZoneId.systemDefault()))
                    .build();

            Usuario diana = Usuario.builder()
                    .nombre("Diana Mejía")
                    .email("diana@correo.com")
                    .username("diana@correo.com")
                    .password(passwordDefault)
                    .rol(Rol.CLIENTE)
                    .activo(true)
                    .creadoEn(LocalDateTime.now(ZoneId.systemDefault()))
                    .build();

            Usuario jorge = Usuario.builder()
                    .nombre("Jorge Pinzón")
                    .email("jorge@correo.com")
                    .username("jorge@correo.com")
                    .password(passwordDefault)
                    .rol(Rol.CLIENTE)
                    .activo(false)
                    .creadoEn(LocalDateTime.now(ZoneId.systemDefault()))
                    .build();

            usuarioRepository.saveAll(List.of(hernan, pedro, diana, jorge));
        }

        // 2. Sembrar Herramientas
        if (herramientaRepository.count() == 0) {
            Herramienta h1 = Herramienta.builder()
                    .nombre("Taladro percutor")
                    .categoria(Categoria.ELECTRICA)
                    .descripcion("Taladro potente para concreto y madera")
                    .precioDia(BigDecimal.valueOf(25000))
                    .deposito(BigDecimal.valueOf(100000))
                    .unidades(3)
                    .imagenUrl("https://loremflickr.com/300/200/drill")
                    .activa(true)
                    .build();

            Herramienta h2 = Herramienta.builder()
                    .nombre("Pulidora angular")
                    .categoria(Categoria.ELECTRICA)
                    .descripcion("Ideal para cortes y desbaste en metal")
                    .precioDia(BigDecimal.valueOf(22000))
                    .deposito(BigDecimal.valueOf(80000))
                    .unidades(2)
                    .imagenUrl("https://loremflickr.com/300/200/grinder")
                    .activa(true)
                    .build();

            Herramienta h3 = Herramienta.builder()
                    .nombre("Mezcladora de concreto")
                    .categoria(Categoria.CONSTRUCCION)
                    .descripcion("Mezcladora trompo para obra")
                    .precioDia(BigDecimal.valueOf(60000))
                    .deposito(BigDecimal.valueOf(300000))
                    .unidades(1)
                    .imagenUrl("https://loremflickr.com/300/200/mixer")
                    .activa(true)
                    .build();

            Herramienta h4 = Herramienta.builder()
                    .nombre("Andamio tubular")
                    .categoria(Categoria.CONSTRUCCION)
                    .descripcion("Estructura metálica para trabajos en altura")
                    .precioDia(BigDecimal.valueOf(15000))
                    .deposito(BigDecimal.valueOf(120000))
                    .unidades(4)
                    .imagenUrl("https://loremflickr.com/300/200/scaffolding")
                    .activa(true)
                    .build();

            Herramienta h5 = Herramienta.builder()
                    .nombre("Guadañadora")
                    .categoria(Categoria.JARDIN)
                    .descripcion("Para mantenimiento de prados y maleza")
                    .precioDia(BigDecimal.valueOf(35000))
                    .deposito(BigDecimal.valueOf(150000))
                    .unidades(2)
                    .imagenUrl("https://loremflickr.com/300/200/brushcutter")
                    .activa(true)
                    .build();

            Herramienta h6 = Herramienta.builder()
                    .nombre("Nivel láser")
                    .categoria(Categoria.MEDICION)
                    .descripcion("Nivelación láser de alta precisión")
                    .precioDia(BigDecimal.valueOf(30000))
                    .deposito(BigDecimal.valueOf(200000))
                    .unidades(1)
                    .imagenUrl("https://loremflickr.com/300/200/laser")
                    .activa(false)
                    .build();

            herramientaRepository.saveAll(List.of(h1, h2, h3, h4, h5, h6));
        }

        // 3. Sembrar el alquiler vencido de Jorge Pinzón (Pulidora angular)
        if (alquilerRepository.count() == 0) {
            Usuario jorge = usuarioRepository.findByEmail("jorge@correo.com").orElse(null);
            Herramienta pulidora = herramientaRepository.findByNombre("Pulidora angular").orElse(null);

            if (jorge != null && pulidora != null) {
                LocalDate hoy = LocalDate.now(ZoneId.systemDefault());
                LocalDate inicio = hoy.minusDays(10);
                LocalDate fin = hoy.minusDays(3);
                int dias = 8;
                BigDecimal valorTotal = pulidora.getPrecioDia().multiply(BigDecimal.valueOf(dias));

                Alquiler vencido = Alquiler.builder()
                        .herramienta(pulidora)
                        .cliente(jorge)
                        .fechaInicio(inicio)
                        .fechaFin(fin)
                        .dias(dias)
                        .valorTotal(valorTotal)
                        .deposito(pulidora.getDeposito())
                        .estado(EstadoAlquiler.ENTREGADO)
                        .creadoEn(inicio.atTime(9, 0))
                        .build();

                alquilerRepository.save(vencido);
            }
        }
    }
}
