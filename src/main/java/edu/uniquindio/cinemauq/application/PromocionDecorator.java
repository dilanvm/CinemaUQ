package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.ElementoVendible;

import java.math.BigDecimal;

public abstract class PromocionDecorator implements ElementoVendible {
    protected final ElementoVendible elemento;

    public PromocionDecorator(ElementoVendible elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento vendible es obligatorio");
        }
        this.elemento = elemento;
    }

    public ElementoVendible getElementoOriginal() {
        if (elemento instanceof PromocionDecorator decorator) {
            return decorator.getElementoOriginal();
        }
        return elemento;
    }

    @Override
    public String getNombre() {
        return elemento.getNombre();
    }

    @Override
    public String getContenido() {
        return elemento.getContenido();
    }
}
