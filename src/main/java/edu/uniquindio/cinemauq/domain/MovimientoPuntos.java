package edu.uniquindio.cinemauq.domain;

import java.time.LocalDate;

public class MovimientoPuntos {
    private final TipoMovimientoPuntos tipo;
    private final int puntos;
    private final LocalDate fecha;
    private final String descripcion;

    public MovimientoPuntos(TipoMovimientoPuntos tipo, int puntos, LocalDate fecha, String descripcion) {
        if (tipo == null || fecha == null || descripcion == null) {
            throw new IllegalArgumentException("Todos los campos del movimiento de puntos son obligatorios");
        }
        this.tipo = tipo;
        this.puntos = puntos;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    public TipoMovimientoPuntos getTipo() {
        return tipo;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getCantidad() {
        return puntos;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
