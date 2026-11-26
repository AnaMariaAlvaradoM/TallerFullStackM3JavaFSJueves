package com.devsenior.llavemaestra.config;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.CategoriaHerramienta;
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
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.herramientaRepository = herramientaRepository;
        this.alquilerRepository = alquilerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0 || herramientaRepository.count() > 0) {
            return;
        }

        String claveCodificada = passwordEncoder.encode("llave123");

        Usuario hernan = crearUsuario("Hernán Rojas", "hernan@llavemaestra.co", claveCodificada, Rol.ADMIN, true);
        Usuario pedro = crearUsuario("Pedro Salazar", "pedro@correo.com", claveCodificada, Rol.CLIENTE, true);
        Usuario diana = crearUsuario("Diana Mejía", "diana@correo.com", claveCodificada, Rol.CLIENTE, true);
        Usuario jorge = crearUsuario("Jorge Pinzón", "jorge@correo.com", claveCodificada, Rol.CLIENTE, false);

        usuarioRepository.save(hernan);
        usuarioRepository.save(pedro);
        usuarioRepository.save(diana);
        usuarioRepository.save(jorge);

        Herramienta taladro = crearHerramienta(
                "Taladro percutor",
                CategoriaHerramienta.ELECTRICA,
                "Taladro percutor para perforación en concreto, mampostería y metal.",
                BigDecimal.valueOf(25000),
                BigDecimal.valueOf(100000),
                3,
                "https://loremflickr.com/600/400/drill,tool?lock=1",
                true
        );
        Herramienta pulidora = crearHerramienta(
                "Pulidora angular",
                CategoriaHerramienta.ELECTRICA,
                "Pulidora angular para corte y desbaste en obra.",
                BigDecimal.valueOf(22000),
                BigDecimal.valueOf(80000),
                2,
                "https://loremflickr.com/600/400/grinder,tool?lock=2",
                true
        );
        Herramienta mezcladora = crearHerramienta(
                "Mezcladora de concreto",
                CategoriaHerramienta.CONSTRUCCION,
                "Mezcladora de concreto para preparación de mezcla en obra.",
                BigDecimal.valueOf(60000),
                BigDecimal.valueOf(300000),
                1,
                "https://loremflickr.com/600/400/construction,concrete?lock=3",
                true
        );
        Herramienta andamio = crearHerramienta(
                "Andamio tubular",
                CategoriaHerramienta.CONSTRUCCION,
                "Andamio tubular metálico para trabajos en altura.",
                BigDecimal.valueOf(15000),
                BigDecimal.valueOf(120000),
                4,
                "https://loremflickr.com/600/400/scaffolding,construction?lock=4",
                true
        );
        Herramienta guadanadora = crearHerramienta(
                "Guadañadora",
                CategoriaHerramienta.JARDIN,
                "Guadañadora para corte de maleza y mantenimiento de zonas verdes.",
                BigDecimal.valueOf(35000),
                BigDecimal.valueOf(150000),
                2,
                "https://loremflickr.com/600/400/trimmer,garden?lock=5",
                true
        );
        Herramienta nivelLaser = crearHerramienta(
                "Nivel láser",
                CategoriaHerramienta.MEDICION,
                "Nivel láser de precisión para alineación y replanteo.",
                BigDecimal.valueOf(30000),
                BigDecimal.valueOf(200000),
                1,
                "https://loremflickr.com/600/400/laser,measurement?lock=6",
                false
        );

        herramientaRepository.save(taladro);
        herramientaRepository.save(pulidora);
        herramientaRepository.save(mezcladora);
        herramientaRepository.save(andamio);
        herramientaRepository.save(guadanadora);
        herramientaRepository.save(nivelLaser);

        if (alquilerRepository.count() == 0) {
            LocalDate hoy = LocalDate.now();
            LocalDate inicio = hoy.minusDays(10);
            LocalDate fin = hoy.minusDays(3);
            int dias = 8;
            BigDecimal valorTotal = pulidora.getPrecioDia().multiply(BigDecimal.valueOf(dias));

            Alquiler vencidoJorge = new Alquiler();
            vencidoJorge.setHerramienta(pulidora);
            vencidoJorge.setCliente(jorge);
            vencidoJorge.setFechaInicio(inicio);
            vencidoJorge.setFechaFin(fin);
            vencidoJorge.setDias(dias);
            vencidoJorge.setValorTotal(valorTotal);
            vencidoJorge.setDeposito(pulidora.getDeposito());
            vencidoJorge.setEstado(EstadoAlquiler.ENTREGADO);
            vencidoJorge.setCreadoEn(Instant.now().minus(10, ChronoUnit.DAYS));

            alquilerRepository.save(vencidoJorge);
        }
    }

    private Usuario crearUsuario(String nombre, String email, String passwordHash, Rol rol, boolean activo) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(passwordHash);
        u.setRol(rol);
        u.setActivo(activo);
        return u;
    }

    private Herramienta crearHerramienta(
            String nombre,
            CategoriaHerramienta categoria,
            String descripcion,
            BigDecimal precioDia,
            BigDecimal deposito,
            int unidades,
            String imagenUrl,
            boolean activa
    ) {
        Herramienta h = new Herramienta();
        h.setNombre(nombre);
        h.setCategoria(categoria);
        h.setDescripcion(descripcion);
        h.setPrecioDia(precioDia);
        h.setDeposito(deposito);
        h.setUnidades(unidades);
        h.setImagenUrl(imagenUrl);
        h.setActiva(activa);
        return h;
    }
}