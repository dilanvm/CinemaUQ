package edu.uniquindio.cinemauq.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Combo implements ElementoVendible {
    private Long id;
    private List<ElementoVendible> elementos;
    private String nombre;

    public Combo(List<ElementoVendible> elementos, String nombre) {
        this(null, elementos, nombre);
    }

    public Combo(Long id, List<ElementoVendible> elementos, String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del combo es obligatorio");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.elementos = new ArrayList<>();
        if (elementos != null) {
            for (ElementoVendible elemento : elementos) {
                agregarElemento(elemento);
            }
        }
    }

    public void agregarElemento(ElementoVendible elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento es obligatorio");
        }
        this.elementos.add(elemento);
    }

    public void eliminarElemento(ElementoVendible elemento) {
        this.elementos.remove(elemento);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public BigDecimal getPrecio() {
        BigDecimal total = BigDecimal.ZERO;
        for (ElementoVendible elemento : elementos) {
            total = total.add(elemento.getPrecio());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public List<ElementoVendible> getElementos() {
        return Collections.unmodifiableList(elementos);
    }

    @Override
    public String getContenido() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elementos.size(); i++) {
            sb.append(elementos.get(i).getNombre());
            if (i < elementos.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
