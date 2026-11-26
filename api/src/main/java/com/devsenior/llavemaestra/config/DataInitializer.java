package com.devsenior.llavemaestra.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Rol;
import com.devsenior.llavemaestra.model.TipoCategoria;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.AlquilerRepository;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.repository.UsuarioRepository;

// Siembra datos de ejemplo SOLO si la base esta vacia.
// Las alquileres usan fechas relativas a HOY: siempre hay agenda reciente.
@Component
public class DataInitializer implements CommandLineRunner {

    private static final String CLAVE_DEMO = "llave123";

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

        if (usuarioRepository.count() > 0) {
            return;
        }

        usuario("Hernán Rojas", "admin@llavemaestra.co", Rol.ADMIN, true);
        Usuario pedro = usuario("Pedro Salazar", "pedro@correo.com", Rol.CLIENTE, true);
        Usuario diana = usuario("Diana Mejía", "diana@correo.com", Rol.CLIENTE, true);
        Usuario jorge = usuario("Jorge Pinzón", "jorge@correo.com", Rol.CLIENTE, false);

        Herramienta taladro = herramienta("Taladro percutor",
                TipoCategoria.ELECTRICA,
                3,
                100000,
                25000,
                "Taladro inalámbrico de alta potencia para trabajos de bricolaje y construcción.",
                "https://example.com/images/taladro.jpg",
                true);

        // 1. Pulidora angular
        Herramienta pulidora = herramienta(
                "Pulidora angular",
                TipoCategoria.ELECTRICA,
                2,
                80000,
                22000,
                "Pulidora angular potente para corte y desbaste de metal y mampostería.",
                "https://example.com/images/pulidora.jpg",
                true);

        // 2. Mezcladora de concreto
        Herramienta mezcladora = herramienta(
                "Mezcladora de concreto",
                TipoCategoria.CONSTRUCCION,
                1,
                300000,
                60000,
                "Mezcladora de cemento y concreto para obras y proyectos de construcción.",
                "https://example.com/images/mezcladora.jpg",
                true);

        // 3. Andamio tubular
        Herramienta andamio = herramienta(
                "Andamio tubular",
                TipoCategoria.CONSTRUCCION,
                4,
                120000,
                15000,
                "Estructura de andamio tubular metálico para trabajos en altura.",
                "https://example.com/images/andamio.jpg",
                true);

        // 4. Guadañadora
        Herramienta guadanadora = herramienta(
                "Guadañadora",
                TipoCategoria.JARDIN,
                2,
                150000,
                35000,
                "Guadañadora para mantenimiento de jardines y control de maleza.",
                "https://example.com/images/guadanadora.jpg",
                true);

        // 5. Nivel láser (Inactiva / false)
        Herramienta nivelLaser = herramienta(
                "Nivel láser",
                TipoCategoria.MEDICION,
                1,
                200000,
                30000,
                "Nivel láser de alta precisión para alineación y nivelación.",
                "https://example.com/images/nivel-laser.jpg",
                false);

        // datos alquiler
        LocalDate hoy = LocalDate.now();

        // 1. Finalizado, ya devuelto
        alquiler(taladro, pedro, hoy, hoy.minusDays(15).toString(), hoy.minusDays(12).toString(),
                1, 3, EstadoAlquiler.RESERVADO);

        // 2. Finalizado
        alquiler(pulidora, diana, hoy, hoy.minusDays(10).toString(), hoy.minusDays(8).toString(),
                1, 2, EstadoAlquiler.ENTREGADO);

        // 3. Activo (en curso)
        alquiler(mezcladora, pedro, hoy, hoy.minusDays(2).toString(), hoy.plusDays(3).toString(),
                1, 5, EstadoAlquiler.ENTREGADO);

        // 4. Activo, con 2 unidades
        alquiler(andamio, diana, hoy, hoy.minusDays(1).toString(), hoy.plusDays(6).toString(),
                2, 7, EstadoAlquiler.CANCELADO);

        // 5. Pendiente (reserva futura)
        alquiler(guadanadora, pedro, hoy, hoy.plusDays(2).toString(), hoy.plusDays(5).toString(),
                1, 3, EstadoAlquiler.DEVUELTO);

        // 6. Cancelado
        alquiler(taladro, diana, hoy, hoy.plusDays(4).toString(), hoy.plusDays(6).toString(),
                1, 2, EstadoAlquiler.CANCELADO);
    }

    private Usuario usuario(String nombre, String email, Rol rol, boolean activo) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(CLAVE_DEMO));
        usuario.setRol(rol);
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }

    private Herramienta herramienta(String nombre, TipoCategoria tipo, int unidades, int deposito, int precioDia,
            String descripcion, String imagenUrl, boolean activa) {
        Herramienta herramienta = new Herramienta();
        herramienta.setNombre(nombre);
        herramienta.setCategoria(tipo);
        herramienta.setUnidades(unidades);
        herramienta.setDeposito(deposito);
        herramienta.setPrecioDia(precioDia);
        herramienta.setDescripcion(descripcion);
        herramienta.setImagenUrl(imagenUrl);
        herramienta.setActiva(activa);
        return herramientaRepository.save(herramienta);
    }

    private void alquiler(Herramienta herramienta, Usuario usuario, LocalDate fecha, String inicio, String fin,
            int cantidad, int dia, EstadoAlquiler estado) {
        LocalDate fechaInicio = LocalDate.parse(inicio);
        LocalDate fechaFin = LocalDate.parse(fin);
        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin);

        Alquiler alquiler = new Alquiler();
        alquiler.setHerramienta(herramienta);
        alquiler.setUsuario(usuario);
        alquiler.setFechaInicio(fechaInicio);
        alquiler.setFechaFin(fechaFin);
        alquiler.setDeposito(herramienta.getDeposito());
        alquiler.setDia(dia);
        alquiler.setValorTotal((int) (herramienta.getPrecioDia() * dias * cantidad));
        alquiler.setEstado(estado);
        alquiler.setCreadaEn(fechaInicio.minusDays(1).atTime(LocalTime.of(9, 0)));
        alquilerRepository.save(alquiler);
    }

}
