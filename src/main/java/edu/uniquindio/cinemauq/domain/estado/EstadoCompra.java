package edu.uniquindio.cinemauq.domain.estado;

import edu.uniquindio.cinemauq.domain.Compra;

public interface EstadoCompra {
    void confirmar(Compra compra);
    void cancelar(Compra compra);
    void marcarReembolsada(Compra compra);
    String getNombre();
}
