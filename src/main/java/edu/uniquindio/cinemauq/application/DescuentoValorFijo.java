package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.ElementoVendible;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DescuentoValorFijo extends PromocionDecorator {
    private final BigDecimal descuentoFijo;

    public DescuentoValorFijo(ElementoVendible elemento, BigDecimal descuentoFijo) {
        super(elemento);
        if (descuentoFijo == null || descuentoFijo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El valor fijo de descuento debe ser mayor que cero");
        }
        this.descuentoFijo = descuentoFijo.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getPrecio() {
        BigDecimal resultado = elemento.getPrecio().subtract(descuentoFijo);
        if (resultado.compareTo(BigDecimal.ZERO) < 0) {
            resultado = BigDecimal.ZERO;
        }
        return resultado.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getDescuentoFijo() {
        return descuentoFijo;
    }
}
