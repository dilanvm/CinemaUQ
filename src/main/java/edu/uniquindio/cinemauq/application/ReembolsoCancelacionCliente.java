package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.CancelacionNoPermitidaException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

public class ReembolsoCancelacionCliente implements PoliticaReembolso {
    @Override
    public BigDecimal calcularPorcentaje(LocalDateTime inicioFuncion, LocalDateTime ahora) {
        if (inicioFuncion == null || ahora == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias");
        }
        Duration duracion = Duration.between(ahora, inicioFuncion);
        long minutos = duracion.toMinutes();
        if (minutos < 120) {
            throw new CancelacionNoPermitidaException("No se permite cancelar con menos de 2 horas de anticipacion");
        }
        if (minutos > 1440) {
            return new BigDecimal("1.00").setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal("0.80").setScale(2, RoundingMode.HALF_UP);
    }
}
