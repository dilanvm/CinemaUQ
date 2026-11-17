package edu.uniquindio.cinemauq.domain.estado;

import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.CompraInvalidaException;

public class EstadoCancelada implements EstadoCompra {
    @Override
    public void confirmar(Compra compra) {
        throw new CompraInvalidaException("No se puede confirmar una compra cancelada");
    }

    @Override
    public void cancelar(Compra compra) {
        throw new CompraInvalidaException("La compra ya se encuentra cancelada");
    }

    @Override
    public void marcarReembolsada(Compra compra) {
        compra.setEstado(new EstadoReembolsada());
    }

    @Override
    public String getNombre() {
        return "CANCELADA";
    }
}
