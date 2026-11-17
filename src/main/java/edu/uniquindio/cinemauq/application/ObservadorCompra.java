package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Compra;

import java.math.BigDecimal;

public interface ObservadorCompra {
    void alConfirmarse(Compra compra);
    void alCancelarse(Compra compra, BigDecimal porcentajeReembolsado);
}
