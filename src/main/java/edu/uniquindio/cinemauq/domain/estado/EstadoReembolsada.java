package edu.uniquindio.cinemauq.domain.estado;

import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.CompraInvalidaException;
import edu.uniquindio.cinemauq.domain.ReembolsoDuplicadoException;

public class EstadoReembolsada implements EstadoCompra {
    @Override
    public void confirmar(Compra compra) {
        throw new CompraInvalidaException("No se puede confirmar una compra reembolsada");
    }

    @Override
    public void cancelar(Compra compra) {
        throw new CompraInvalidaException("La compra ya se encuentra reembolsada");
    }

    @Override
    public void marcarReembolsada(Compra compra) {
        throw new ReembolsoDuplicadoException("La compra ya ha sido reembolsada");
    }

    @Override
    public String getNombre() {
        return "REEMBOLSADA";
    }
}
