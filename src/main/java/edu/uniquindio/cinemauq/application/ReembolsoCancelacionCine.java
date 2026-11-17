package edu.uniquindio.cinemauq.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class ReembolsoCancelacionCine implements PoliticaReembolso {
    @Override
    public BigDecimal calcularPorcentaje(LocalDateTime inicioFuncion, LocalDateTime ahora) {
        return new BigDecimal("1.00").setScale(2, RoundingMode.HALF_UP);
    }
}
