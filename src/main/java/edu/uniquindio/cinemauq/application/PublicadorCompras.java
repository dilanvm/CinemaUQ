package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.Compra;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PublicadorCompras {
    private final List<ObservadorCompra> observadores;

    public PublicadorCompras() {
        this.observadores = new ArrayList<>();
    }

    public void registrarObservador(ObservadorCompra observador) {
        if (observador != null && !this.observadores.contains(observador)) {
            this.observadores.add(observador);
        }
    }

    public void removerObservador(ObservadorCompra observador) {
        this.observadores.remove(observador);
    }

    public void notificarConfirmacion(Compra compra) {
        for (ObservadorCompra observador : this.observadores) {
            observador.alConfirmarse(compra);
        }
    }

    public void notificarCancelacion(Compra compra, BigDecimal porcentajeReembolsado) {
        for (ObservadorCompra observador : this.observadores) {
            observador.alCancelarse(compra, porcentajeReembolsado);
        }
    }
}
