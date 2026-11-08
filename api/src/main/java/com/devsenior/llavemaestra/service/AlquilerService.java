package com.devsenior.llavemaestra.service;

import com.devsenior.llavemaestra.dto.AlquilerRequest;
import com.devsenior.llavemaestra.dto.AlquilerResponse;
import com.devsenior.llavemaestra.exception.DatoInvalidoException;
import com.devsenior.llavemaestra.exception.RecursoNoEncontradoException;
import com.devsenior.llavemaestra.exception.ReglaNegocioException;
import com.devsenior.llavemaestra.model.Alquiler;
import com.devsenior.llavemaestra.model.EstadoAlquiler;
import com.devsenior.llavemaestra.model.Herramienta;
import com.devsenior.llavemaestra.model.Permiso;
import com.devsenior.llavemaestra.model.Usuario;
import com.devsenior.llavemaestra.repository.AlquilerRepository;
import com.devsenior.llavemaestra.repository.HerramientaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Aqui viven TODAS las reglas del negocio de los alquileres (Fase 2)
@Service
public class AlquilerService {

    public static final int MAXIMO_DIAS = 15;
    public static final int MAXIMO_ACTIVOS = 3;

    private final AlquilerRepository alquilerRepository;
    private final HerramientaRepository herramientaRepository;

    public AlquilerService(AlquilerRepository alquilerRepository, HerramientaRepository herramientaRepository) {
        this.alquilerRepository = alquilerRepository;
        this.herramientaRepository = herramientaRepository;
    }

    // Se cuentan el primero y el ultimo: del 10 al 12 son 3 dias
    public static int calcularDias(LocalDate inicio, LocalDate fin) {
        return (int) ChronoUnit.DAYS.between(inicio, fin) + 1;
    }

    // ---------- Crear ----------

    @Transactional
    public AlquilerResponse crear(AlquilerRequest datos, Usuario cliente) {
        // (Regla 1, campos que faltan, ya la reviso @Valid en el controlador -> 400)

        // Regla 2: la herramienta debe existir -> 404
        Herramienta herramienta = herramientaRepository.findById(datos.herramientaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la herramienta con id " + datos.herramientaId()));

        // Regla 3: no se alquila una herramienta retirada -> 409
        if (!herramienta.isActiva()) {
            throw new ReglaNegocioException("La herramienta " + herramienta.getNombre()
                    + " está retirada y no se puede alquilar");
        }

        LocalDate inicio = datos.fechaInicio();
        LocalDate fin = datos.fechaFin();
        LocalDate hoy = LocalDate.now();

        // Regla 4: fin antes de inicio -> 400
        if (fin.isBefore(inicio)) {
            throw new DatoInvalidoException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        // Regla 5: no se alquila desde una fecha que ya paso (hoy SI se puede) -> 400
        if (inicio.isBefore(hoy)) {
            throw new DatoInvalidoException("La fecha de inicio ya pasó: elige hoy o una fecha futura");
        }

        // Regla 6: maximo 15 dias (15 exactos SI se puede) -> 400
        int dias = calcularDias(inicio, fin);
        if (dias > MAXIMO_DIAS) {
            throw new DatoInvalidoException("El alquiler máximo es de " + MAXIMO_DIAS
                    + " días (pediste " + dias + ")");
        }

        // Regla 7: maximo 3 alquileres activos por cliente -> 409
        long activosDelCliente = alquilerRepository.countByClienteIdAndEstadoIn(
                cliente.getId(), EstadoAlquiler.ACTIVOS);
        if (activosDelCliente >= MAXIMO_ACTIVOS) {
            throw new ReglaNegocioException("Ya tienes " + MAXIMO_ACTIVOS
                    + " alquileres activos: no puedes tener más de " + MAXIMO_ACTIVOS + " a la vez");
        }

        // Regla 8: no alquilar mas unidades de las que hay -> 409
        long cruzados = alquilerRepository.contarCruzados(
                herramienta.getId(), EstadoAlquiler.ACTIVOS, inicio, fin);
        if (cruzados >= herramienta.getUnidades()) {
            throw new ReglaNegocioException("No quedan unidades de " + herramienta.getNombre()
                    + " para esas fechas");
        }

        // Todo bien: el backend calcula el valor y el deposito, y el cliente sale del token
        Alquiler alquiler = new Alquiler(
                herramienta,
                cliente,
                inicio,
                fin,
                dias,
                dias * herramienta.getPrecioDia(),
                herramienta.getDeposito(),
                EstadoAlquiler.RESERVADO);

        return AlquilerResponse.desde(alquilerRepository.save(alquiler));
    }

    // ---------- Mis alquileres ----------

    @Transactional(readOnly = true)
    public List<AlquilerResponse> mios(Usuario cliente) {
        return alquilerRepository.findByClienteIdOrderByFechaInicioDescIdDesc(cliente.getId())
                .stream().map(AlquilerResponse::desde).toList();
    }

    // ---------- Cancelar ----------

    @Transactional
    public AlquilerResponse cancelar(Long id, Usuario quienPide) {
        // Regla 1: debe existir -> 404
        Alquiler alquiler = alquilerRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el alquiler con id " + id));

        // Regla 2: solo cancelas lo tuyo (salvo que tengas ALQUILERES_GESTIONAR) -> 403
        // Esta regla va AQUI y no en el controlador porque depende del DATO (de quien es el alquiler).
        boolean esDueno = alquiler.getCliente().getId().equals(quienPide.getId());
        if (!esDueno && !quienPide.tienePermiso(Permiso.ALQUILERES_GESTIONAR)) {
            throw new AccessDeniedException("Solo puedes cancelar tus propios alquileres");
        }

        // Regla 3: solo se cancela lo que esta RESERVADO -> 409
        if (alquiler.getEstado() != EstadoAlquiler.RESERVADO) {
            throw new ReglaNegocioException("Este alquiler está " + alquiler.getEstado()
                    + ": solo se puede cancelar un alquiler RESERVADO");
        }

        // Regla 4: si empieza hoy o ya empezo, ya no se cancela -> 409
        if (!alquiler.getFechaInicio().isAfter(LocalDate.now())) {
            throw new ReglaNegocioException("Ya no se puede cancelar: el alquiler empieza hoy o ya empezó");
        }

        alquiler.setEstado(EstadoAlquiler.CANCELADO);
        return AlquilerResponse.desde(alquilerRepository.save(alquiler));
    }
}
