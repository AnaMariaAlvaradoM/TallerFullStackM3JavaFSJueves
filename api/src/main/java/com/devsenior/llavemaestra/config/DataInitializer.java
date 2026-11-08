package com.devsenior.llavemaestra.config;

import com.devsenior.llavemaestra.model.*;
import com.devsenior.llavemaestra.repository.AlquilerRepository;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import com.devsenior.llavemaestra.service.AlquilerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

// Siembra los datos de ejemplo al arrancar (solo si la tabla esta vacia, para no duplicar)
@Component
public class DataInitializer implements CommandLineRunner {

    private static final String CLAVE = "llave123";

    private final UsuarioRepository usuarioRepository;
    private final HerramientaRepository herramientaRepository;
    private final AlquilerRepository alquilerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, HerramientaRepository herramientaRepository,
                           AlquilerRepository alquilerRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.herramientaRepository = herramientaRepository;
        this.alquilerRepository = alquilerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        sembrarUsuarios();
        sembrarHerramientas();
        sembrarAlquileres();
    }

    private void sembrarUsuarios() {
        if (usuarioRepository.count() > 0) return;
        String clave = passwordEncoder.encode(CLAVE);
        usuarioRepository.saveAll(List.of(
                new Usuario("Hernán Rojas", "admin@llavemaestra.co", clave, Rol.ADMIN, true),
                new Usuario("Pedro Salazar", "pedro@correo.com", clave, Rol.CLIENTE, true),
                new Usuario("Diana Mejía", "diana@correo.com", clave, Rol.CLIENTE, true),
                new Usuario("Jorge Pinzón", "jorge@correo.com", clave, Rol.CLIENTE, false)));
    }

    private void sembrarHerramientas() {
        if (herramientaRepository.count() > 0) return;
        herramientaRepository.saveAll(List.of(
                new Herramienta("Taladro percutor", Categoria.ELECTRICA,
                        "Taladro de 800 W con percusión para concreto, madera y metal.",
                        25000, 100000, 3, "https://loremflickr.com/640/480/drill?lock=1", true),
                new Herramienta("Pulidora angular", Categoria.ELECTRICA,
                        "Pulidora de 4 1/2\" para cortar y desbastar metal y cerámica.",
                        22000, 80000, 2, "https://loremflickr.com/640/480/grinder?lock=2", true),
                new Herramienta("Mezcladora de concreto", Categoria.CONSTRUCCION,
                        "Mezcladora de 1 bulto, motor eléctrico. Ideal para obras pequeñas.",
                        60000, 300000, 1, "https://loremflickr.com/640/480/concrete,mixer?lock=3", true),
                new Herramienta("Andamio tubular", Categoria.CONSTRUCCION,
                        "Cuerpo de andamio de 1,5 m con crucetas y plataforma.",
                        15000, 120000, 4, "https://loremflickr.com/640/480/scaffolding?lock=4", true),
                new Herramienta("Guadañadora", Categoria.JARDIN,
                        "Guadañadora a gasolina de 43 cc con cuchilla y nylon.",
                        35000, 150000, 2, "https://loremflickr.com/640/480/lawn,trimmer?lock=5", true),
                new Herramienta("Nivel láser", Categoria.MEDICION,
                        "Nivel láser de líneas cruzadas con trípode. En reparación.",
                        30000, 200000, 1, "https://loremflickr.com/640/480/laser,level?lock=6", false)));
    }

    // Pocos alquileres de ejemplo: Pedro y Diana con maximo 1 ACTIVO cada uno (regla de la Fase 2)
    private void sembrarAlquileres() {
        if (alquilerRepository.count() > 0) return;
        Usuario pedro = usuarioRepository.findByEmail("pedro@correo.com").orElseThrow();
        Usuario diana = usuarioRepository.findByEmail("diana@correo.com").orElseThrow();
        Herramienta taladro = buscarHerramienta("Taladro percutor");
        Herramienta guadanadora = buscarHerramienta("Guadañadora");

        LocalDate hoy = LocalDate.now();
        alquilerRepository.saveAll(List.of(
                // Pedro: 1 activo (reservado para dentro de 5 dias)
                nuevo(taladro, pedro, hoy.plusDays(5), hoy.plusDays(7), EstadoAlquiler.RESERVADO),
                // Diana: 1 ya devuelto (no es activo)
                nuevo(guadanadora, diana, hoy.minusDays(10), hoy.minusDays(8), EstadoAlquiler.DEVUELTO)));
    }

    private Herramienta buscarHerramienta(String nombre) {
        return herramientaRepository.findAll().stream()
                .filter(h -> h.getNombre().equals(nombre))
                .findFirst()
                .orElseThrow();
    }

    private Alquiler nuevo(Herramienta h, Usuario cliente, LocalDate inicio, LocalDate fin, EstadoAlquiler estado) {
        int dias = AlquilerService.calcularDias(inicio, fin);
        return new Alquiler(h, cliente, inicio, fin, dias, dias * h.getPrecioDia(), h.getDeposito(), estado);
    }
}
