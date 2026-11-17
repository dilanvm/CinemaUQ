package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.ElementoVendible;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DescuentoPorcentaje extends PromocionDecorator {
    private final BigDecimal porcentaje;

    public DescuentoPorcentaje(ElementoVendible elemento, BigDecimal porcentaje) {
        super(elemento);
        if (porcentaje == null || porcentaje.compareTo(BigDecimal.ZERO) <= 0 || porcentaje.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100");
        }
        if (porcentaje.compareTo(BigDecimal.ONE) > 0) {
            this.porcentaje = porcentaje.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        } else {
            this.porcentaje = porcentaje;
        }
    }

    @Override
    public BigDecimal getPrecio() {
        BigDecimal factor = BigDecimal.ONE.subtract(porcentaje);
        BigDecimal precioCalculado = elemento.getPrecio().multiply(factor);
        return precioCalculado.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }
}
