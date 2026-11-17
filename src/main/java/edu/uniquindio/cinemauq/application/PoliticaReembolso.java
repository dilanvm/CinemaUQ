package edu.uniquindio.cinemauq.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PoliticaReembolso {
    BigDecimal calcularPorcentaje(LocalDateTime inicioFuncion, LocalDateTime ahora);
}
