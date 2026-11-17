package edu.uniquindio.cinemauq.domain;

import java.time.LocalDate;

public class LotePuntos {
    private final int cantidadOriginal;
    private int cantidadRestante;
    private final LocalDate fechaObtencion;
    private final LocalDate fechaVencimiento;

    public LotePuntos(int cantidadOriginal, LocalDate fechaObtencion) {
        this(cantidadOriginal, cantidadOriginal, fechaObtencion, fechaObtencion != null ? fechaObtencion.plusYears(1) : null);
    }

    public LotePuntos(int cantidadOriginal, int cantidadRestante, LocalDate fechaObtencion, LocalDate fechaVencimiento) {
        if (cantidadOriginal <= 0) {
            throw new IllegalArgumentException("La cantidad original debe ser mayor que cero");
        }
        if (fechaObtencion == null || fechaVencimiento == null) {
            throw new IllegalArgumentException("Las fechas son obligatorias");
        }
        this.cantidadOriginal = cantidadOriginal;
        this.cantidadRestante = Math.max(0, cantidadRestante);
        this.fechaObtencion = fechaObtencion;
        this.fechaVencimiento = fechaVencimiento;
    }

    public boolean estaVencido(LocalDate hoy) {
        if (hoy == null) {
            throw new IllegalArgumentException("La fecha actual es obligatoria");
        }
        return hoy.isAfter(this.fechaVencimiento);
    }

    public int descontar(int cantidad) {
        if (cantidad <= 0) {
            return 0;
        }
        int aDescontar = Math.min(cantidad, this.cantidadRestante);
        this.cantidadRestante -= aDescontar;
        return aDescontar;
    }

    public void reintegrar(int cantidad) {
        if (cantidad <= 0) {
            return;
        }
        this.cantidadRestante = Math.min(this.cantidadOriginal, this.cantidadRestante + cantidad);
    }

    public void revertir(int cantidad) {
        if (cantidad <= 0) {
            return;
        }
        this.cantidadRestante = Math.max(0, this.cantidadRestante - cantidad);
    }

    public int getCantidadOriginal() {
        return cantidadOriginal;
    }

    public int getCantidadRestante() {
        return cantidadRestante;
    }

    public LocalDate getFechaObtencion() {
        return fechaObtencion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
}
