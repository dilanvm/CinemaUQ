package edu.uniquindio.cinemauq.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class MovimientoTarjeta {
    private final TipoMovimiento tipo;
    private final BigDecimal monto;
    private final LocalDateTime fechaHora;
    private final String descripcion;

    public MovimientoTarjeta(TipoMovimiento tipo, BigDecimal monto, LocalDateTime fechaHora, String descripcion) {
        if (tipo == null || monto == null || fechaHora == null || descripcion == null) {
            throw new IllegalArgumentException("Todos los campos del movimiento son obligatorios");
        }
        this.tipo = tipo;
        this.monto = monto.setScale(2, RoundingMode.HALF_UP);
        this.fechaHora = fechaHora;
        this.descripcion = descripcion;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
