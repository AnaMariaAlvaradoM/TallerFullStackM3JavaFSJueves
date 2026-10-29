package com.devsenior.llavemaestra.config;

import com.devsenior.llavemaestra.model.*;
import com.devsenior.llavemaestra.repository.AlquilerRepository;
import com.devsenior.llavemaestra.repository.UsuarioRepository;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final String CLAVE_DEMO = "llave123";

    private final UsuarioRepository usuarioRepository;
    private final HerramientaRepository herramientaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlquilerRepository alquilerRepository;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           HerramientaRepository herramientaRepository,
                           PasswordEncoder passwordEncoder,AlquilerRepository alquilerRepository) {
        this.usuarioRepository = usuarioRepository;
        this.herramientaRepository = herramientaRepository;
        this.passwordEncoder = passwordEncoder;
        this.alquilerRepository=alquilerRepository;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        // Clientes de LLaveMaestra
        Usuario hernanRojas = usuario("Hernán Rojas","admin@llavemaestra.co",Rol.ADMIN,true);
        Usuario pedroSalazar = usuario("Pedro Salazar","pedro@correo.com",Rol.CLIENTE,true);
        Usuario dianaMejia = usuario("Diana Mejía","diana@correo.com",Rol.CLIENTE,true);
        Usuario jorgePinzon = usuario("Jorge Pinzón","jorge@correo.com",Rol.CLIENTE,false);

        // Herramientas en el inventario
        Herramienta taladroPercutor = herramienta("Taladro percutor",Categoria.ELECTRICA,25000,100000,3,"Perfora y cincela superficies resistentes.","https://loremflickr.com/320/240/hammer,drill",true);
        Herramienta pulidoraAngular = herramienta("Pulidora angular",Categoria.ELECTRICA,22000,80000,2,"Corta, desbasta y pule materiales.","https://loremflickr.com/320/240/Angle,Grinder",true);
        Herramienta mezcladoraDeConcreto = herramienta("Mezcladora de concreto",Categoria.CONSTRUCCION,60000,30000,1,"Mezcla concreto y mortero uniformemente.","https://loremflickr.com/320/240/Concrete,Mixer",true);
        Herramienta andamioTubular = herramienta("Andamio tubular",Categoria.CONSTRUCCION,15000,120000,4,"Estructura para trabajo seguro en altura.","https://loremflickr.com/320/240/Tubular,Scaffolding",true);
        Herramienta guadanadora = herramienta("Guadañadora",Categoria.JARDIN,35000,150000,2,"Corta maleza y césped en exteriores.","https://loremflickr.com/320/240/Brush,Cutter",true);
        Herramienta nivelLáser = herramienta("Nivel láser",Categoria.MEDICION,30000,200000,1,"Proyecta líneas para nivelación precisa.","https://loremflickr.com/320/240/Laser,Level",false);

        // Herramientas Reservadas por los clientes
        LocalDate hoy = LocalDate.now();

        // Pedro Salazar

        alquiler(
                taladroPercutor,
                pedroSalazar,
                hoy,
                "2026-11-02",
                "2026-11-05",
                "Perforación para instalación eléctrica.",
                EstadoAlquiler.ALQUILADA
        );

        alquiler(
                pulidoraAngular,
                pedroSalazar,
                hoy,
                "2026-11-08",
                "2026-11-11",
                "Corte de perfiles metálicos.",
                EstadoAlquiler.ALQUILADA
        );

        alquiler(
                andamioTubular,
                pedroSalazar,
                hoy,
                "2026-11-15",
                "2026-11-18",
                "Pintura de fachada.",
                EstadoAlquiler.CANCELADA
        );


// Diana Mejía

        alquiler(
                mezcladoraDeConcreto,
                dianaMejia,
                hoy,
                "2026-11-03",
                "2026-11-06",
                "Preparación de concreto para ampliación.",
                EstadoAlquiler.ALQUILADA
        );

        alquiler(
                guadanadora,
                dianaMejia,
                hoy,
                "2026-11-10",
                "2026-11-13",
                "Mantenimiento de jardín.",
                EstadoAlquiler.ALQUILADA
        );

        alquiler(
                taladroPercutor,
                dianaMejia,
                hoy,
                "2026-11-20",
                "2026-11-23",
                "Instalación de repisas.",
                EstadoAlquiler.SIN_ALQUILAR
        );

        alquiler(
                pulidoraAngular,
                dianaMejia,
                hoy,
                "2026-11-25",
                "2026-11-28",
                "Pulido de estructuras metálicas.",
                EstadoAlquiler.CANCELADA
        );


// Jorge Pinzón

        alquiler(
                guadanadora,
                jorgePinzon,
                hoy,
                "2026-11-05",
                "2026-11-08",
                "Limpieza de lote.",
                EstadoAlquiler.ALQUILADA
        );

        alquiler(
                andamioTubular,
                jorgePinzon,
                hoy,
                "2026-11-12",
                "2026-11-15",
                "Trabajo en altura para remodelación.",
                EstadoAlquiler.SIN_ALQUILAR
        );

        alquiler(
                mezcladoraDeConcreto,
                jorgePinzon,
                hoy,
                "2026-11-18",
                "2026-11-21",
                "Preparación de mortero.",
                EstadoAlquiler.ALQUILADA
        );


    }

    private Usuario usuario(String nombre, String email, Rol rol, boolean activo) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(CLAVE_DEMO));
        usuario.setRol(rol);
        usuario.setActivo(activo);
        return  usuarioRepository.save(usuario);
    }

    private Herramienta herramienta(String nombre, Categoria categoria, Integer precioDia, Integer deposito, Integer unidades,String descripcion, String imagenUrl ,boolean activa){
        Herramienta herramienta = new Herramienta();
        herramienta.setNombre(nombre);
        herramienta.setCategoria(categoria);
        herramienta.setPrecioDia(precioDia);
        herramienta.setDeposito(deposito);
        herramienta.setUnidades(unidades);
        herramienta.setDescripcion(descripcion);
        herramienta.setImagenUrl(imagenUrl);
        herramienta.setActiva(activa);
        return  herramientaRepository.save(herramienta);
    }

    private void  alquiler(Herramienta herramienta, Usuario usuario, LocalDate fecha,String inicio, String fin,
                              String nota,EstadoAlquiler estado){
        LocalDate fechaInicio = LocalDate.parse(inicio);
        LocalDate fechaFin = LocalDate.parse(fin);
        long dias = ChronoUnit.DAYS.between(fechaInicio,fechaFin);

        Alquiler alquiler = new Alquiler();

        alquiler.setHerramienta(herramienta);
        alquiler.setUsuario(usuario);
        alquiler.setFecha(fecha);
        alquiler.setFechaInicio(fechaInicio);
        alquiler.setFechaFin(fechaFin);
        alquiler.setNota(nota);
        alquiler.setValorTotal((int) (herramienta.getPrecioDia()*dias));
        alquiler.setEstado(estado);
        alquilerRepository.save(alquiler);

    }
}


