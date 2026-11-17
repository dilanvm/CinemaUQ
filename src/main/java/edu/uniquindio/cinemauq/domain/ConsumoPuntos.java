package edu.uniquindio.cinemauq.domain;

public class ConsumoPuntos {
    private final LotePuntos lote;
    private final int cantidad;

    public ConsumoPuntos(LotePuntos lote, int cantidad) {
        if (lote == null) {
            throw new IllegalArgumentException("El lote es obligatorio");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad consumida debe ser mayor que cero");
        }
        this.lote = lote;
        this.cantidad = cantidad;
    }

    public LotePuntos getLote() {
        return lote;
    }

    public int getCantidad() {
        return cantidad;
    }
}
