package com.devsenior.llavemaestra.dto;

import java.time.LocalDate;

// Solo el horario ocupado: NO dice quien reservo (privacidad de los miembros)
public record FranjaOcupada(LocalDate horaInicio, LocalDate horaFin) {
}
