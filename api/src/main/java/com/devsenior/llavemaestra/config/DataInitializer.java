package com.devsenior.llavemaestra.config;

import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Rol;
import com.devsenior.llavemaestra.model.TipoHerramienta;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import com.devsenior.llavemaestra.repository.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final String CLAVE_DEMO = "llave123";

    private final UsuarioRepository usuarioRepository;
    private final HerramientaRepository herramientaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
        HerramientaRepository herramientaRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.herramientaRepository = herramientaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        usuario("Hernán Rojas", "admin@llavemaestra.co", Rol.ADMIN, true);
        usuario("Pedro Salazar", "pedro@correo.com", Rol.CLIENTE, true);
        usuario("Diana Mejía", "diana@correo.com", Rol.CLIENTE, true);
        usuario("Jorge Pinzón", "jorge@correo.com", Rol.CLIENTE, false);

        herramienta("Taladro percutor", TipoHerramienta.ELECTRICA,
        "Taladro percutor para perforar madera, metal y concreto.",
        25000, 100000, 3,
        "https://encrypted-tbn3.gstatic.com/licensed-image?q=tbn:ANd9GcQ_DvYDcSB7onNnXfRnrUTAcX6JodykOTC_5fn0IAx5kopRrIa_9xoH5ZxxgA3QnMSfV-sQkkZxn1aktLU", true);

herramienta("Pulidora angular", TipoHerramienta.ELECTRICA,
        "Pulidora angular para corte y desbaste de distintos materiales.",
        22000, 80000, 2,
        "https://encrypted-tbn2.gstatic.com/licensed-image?q=tbn:ANd9GcRpb3kHFZrvmEWOFoD6VH-hLeMr5ZQJQK0eoss-ko0gexyZru14BtVlG9jWTc0qwatZ9FoccsDMehxVu0Y", true);

herramienta("Mezcladora de concreto", TipoHerramienta.CONSTRUCCION,
        "Mezcladora para preparar concreto y mortero en obra.",
        60000, 300000, 1,
        "https://encrypted-tbn2.gstatic.com/licensed-image?q=tbn:ANd9GcR2XC93MXVWXnYeFmIwpad-WkyIbm8xjD78_CsGQtocMXAfCdaPbE3VBqWd3ZxtXmHK6hsxvTg-0yerQGI", true);

herramienta("Andamio tubular", TipoHerramienta.CONSTRUCCION,
        "Andamio tubular modular para trabajos en altura.",
        15000, 120000, 4,
        "https://encrypted-tbn1.gstatic.com/licensed-image?q=tbn:ANd9GcS6EZbslE5_iC7Nx9q-Rt5uBGM8IqXgcxs8qYFJRO_gHIvdt82228T5xbXKpKT4hn7dHzgT85Nc4Zqdf3Y", true);

herramienta("Guadañadora", TipoHerramienta.JARDIN,
        "Guadañadora para el mantenimiento de jardines y zonas verdes.",
        35000, 150000, 2,
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcROZBXUwbyHeFthIR3TRyXerGTdXA28V9dGcFQ4XjtPMa8275FQ7wR8XNY&s=10", true);

herramienta("Nivel láser", TipoHerramienta.MEDICION,
        "Nivel láser para realizar alineaciones y mediciones precisas.",
        30000, 200000, 1,
        "https://encrypted-tbn2.gstatic.com/licensed-image?q=tbn:ANd9GcSzL8vheI37KziS8ue4N2CMC9B42cJaAx7p4liKalopzUSotgs-w-STsf8Cw7D0BQAvjOZE23dyR3AwAjk", false);
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

    private Herramienta herramienta(String nombre, TipoHerramienta categoria, String descripcion,
                             int precioDia, int deposito, int unidades, String imagenUrl,
                             boolean activa) {
        Herramienta herramienta = new Herramienta();
        herramienta.setNombre(nombre);
        herramienta.setCategoria(categoria);
        herramienta.setDescripcion(descripcion);
        herramienta.setPrecioDia(precioDia);
        herramienta.setDeposito(deposito);
        herramienta.setUnidades(unidades);
        herramienta.setImagenUrl(imagenUrl);
        herramienta.setActiva(activa);
        return herramientaRepository.save(herramienta);
    }
}
