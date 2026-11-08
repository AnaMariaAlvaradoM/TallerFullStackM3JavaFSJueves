package com.devsenior.llavemaestra.config;

import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import com.devsenior.llavemaestra.repository.UsuarioRepository;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.model.Rol;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Categoria;

import org.springframework.security.crypto.password.PasswordEncoder;

// Siembra datos de ejemplo SOLO si la base esta vacia.

@Component
public class DataInitializer implements CommandLineRunner {
    private static final String CLAVE_DEMO = "llave123";

    private final UsuarioRepository usuarioRepository;
    private final HerramientaRepository herramientaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           HerramientaRepository herramientaRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.herramientaRepository = herramientaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        // ---------- Personas ----------
        usuario("Hernán Rojas", "admin@llavemaestra.co", Rol.ADMIN, true);
        usuario("Pedro Salazar", "pedro@correo.com", Rol.CLIENTE, true);
        usuario("Diana Mejía", "diana@correo.com", Rol.CLIENTE, true);
        usuario("Jorge Pinzón", "jorge@correo.com", Rol.CLIENTE, false);

        // ---------- Herramientas ----------
        herramienta("Taladro percutor", Categoria.ELECTRICA, 25000, 100000, 3,
                "Taladro percutor.",
                "https://loremflickr.com/800/500/drill?lock=1", true);
        herramienta("Pulidora angular", Categoria.ELECTRICA, 22000, 80000, 2,
                "Pulidora angular.",
                "https://loremflickr.com/800/500/grinder?lock=2", true);
        herramienta("Mezcladora de concreto", Categoria.CONSTRUCCION, 60000, 300000, 1,
                "Mezcladora de concreto.",
                "https://loremflickr.com/800/500/concrete,mixer?lock=3", true);
        herramienta("Andamio tubular", Categoria.CONSTRUCCION, 15000, 120000, 4,
                "Andamio tubular.",
                "https://loremflickr.com/800/500/scaffolding?lock=4", true);
        herramienta("Guadañadora", Categoria.JARDIN, 35000, 150000, 2,
                "Guadañadora.",
                "https://loremflickr.com/800/500/trimmer,garden?lock=5", true);
        herramienta("Nivel láser", Categoria.MEDICION, 30000, 200000, 1,
                "Nivel láser.",
                "https://loremflickr.com/800/500/laser,level?lock=6", false);

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

    private Herramienta herramienta(String nombre, Categoria tipo, int precioDia, int deposito,
                        int unidades, String descripcion, String imagenUrl, boolean activa) {
        Herramienta herramienta = new Herramienta();
        herramienta.setNombre(nombre);
        herramienta.setCategoria(tipo);
        herramienta.setPrecioDia(precioDia);
        herramienta.setDeposito(deposito);
        herramienta.setUnidades(unidades);
        herramienta.setDescripcion(descripcion);
        herramienta.setImagenUrl(imagenUrl);
        herramienta.setActiva(activa);
        return herramientaRepository.save(herramienta);
    }
}
