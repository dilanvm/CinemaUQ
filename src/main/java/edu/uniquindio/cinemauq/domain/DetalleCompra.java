package edu.uniquindio.cinemauq.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DetalleCompra {
    private final ElementoVendible elemento;
    private final int cantidad;
    private final BigDecimal subtotal;

    public DetalleCompra(ElementoVendible elemento, int cantidad) {
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento vendible es obligatorio");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        this.elemento = elemento;
        this.cantidad = cantidad;
        BigDecimal precio = elemento.getPrecio() != null ? elemento.getPrecio() : BigDecimal.ZERO;
        this.subtotal = precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
    }

    public ElementoVendible getElemento() {
        return elemento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
