package edu.uniquindio.cinemauq.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Producto implements ElementoVendible {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private boolean disponible;
    private CategoriaProducto categoria;

    public Producto(String nombre, String descripcion, BigDecimal precio, boolean disponible) {
        this(null, nombre, descripcion, precio, disponible, CategoriaProducto.COMIDA);
    }

    public Producto(Long id, String nombre, String descripcion, BigDecimal precio, boolean disponible, CategoriaProducto categoria) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio debe ser mayor o igual a cero");
        }
        this.id = id;
        this.nombre = nombre.trim();
        this.descripcion = descripcion != null ? descripcion.trim() : "";
        this.precio = precio.setScale(2, RoundingMode.HALF_UP);
        this.disponible = disponible;
        this.categoria = categoria != null ? categoria : CategoriaProducto.COMIDA;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio != null ? precio.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProducto categoria) {
        this.categoria = categoria;
    }

    @Override
    public String getContenido() {
        return descripcion != null && !descripcion.isEmpty() ? descripcion : nombre;
    }
}
