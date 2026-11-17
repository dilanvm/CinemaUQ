package edu.uniquindio.cinemauq.domain.estado;

import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.CompraInvalidaException;

public class EstadoConfirmada implements EstadoCompra {
    @Override
    public void confirmar(Compra compra) {
        throw new CompraInvalidaException("La compra ya se encuentra confirmada");
    }

    @Override
    public void cancelar(Compra compra) {
        compra.setEstado(new EstadoCancelada());
    }

    @Override
    public void marcarReembolsada(Compra compra) {
        throw new CompraInvalidaException("No se puede marcar como reembolsada una compra sin haber sido cancelada previamente");
    }

    @Override
    public String getNombre() {
        return "CONFIRMADA";
    }
}
