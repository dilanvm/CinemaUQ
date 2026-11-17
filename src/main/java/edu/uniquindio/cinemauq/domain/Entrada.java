package edu.uniquindio.cinemauq.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Entrada {
    private final Funcion funcion;
    private final Asiento asiento;
    private final BigDecimal precio;

    public Entrada(Funcion funcion, Asiento asiento) {
        if (funcion == null || asiento == null) {
            throw new IllegalArgumentException("La funcion y el asiento son obligatorios");
        }
        this.funcion = funcion;
        this.asiento = asiento;
        this.precio = funcion.getPrecioBase() != null ? funcion.getPrecioBase().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public BigDecimal getPrecio() {
        return precio;
    }
}
