package edu.uniquindio.cinemauq.domain.estado;

import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.CompraInvalidaException;

public class EstadoPendiente implements EstadoCompra {
    @Override
    public void confirmar(Compra compra) {
        compra.setEstado(new EstadoConfirmada());
    }

    @Override
    public void cancelar(Compra compra) {
        throw new CompraInvalidaException("No se puede cancelar una compra pendiente");
    }

    @Override
    public void marcarReembolsada(Compra compra) {
        throw new CompraInvalidaException("No se puede marcar como reembolsada una compra pendiente");
    }

    @Override
    public String getNombre() {
        return "PENDIENTE";
    }
}
