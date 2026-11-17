package edu.uniquindio.cinemauq.application;

import edu.uniquindio.cinemauq.domain.CategoriaProducto;
import edu.uniquindio.cinemauq.domain.Combo;
import edu.uniquindio.cinemauq.domain.ElementoVendible;
import edu.uniquindio.cinemauq.domain.EntidadNoEncontradaException;
import edu.uniquindio.cinemauq.domain.Producto;
import edu.uniquindio.cinemauq.domain.RepositorioConfiteria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ServicioConfiteria {
    private final RepositorioConfiteria repositorioConfiteria;

    public ServicioConfiteria(RepositorioConfiteria repositorioConfiteria) {
        if (repositorioConfiteria == null) {
            throw new IllegalArgumentException("El repositorio de confiteria es obligatorio");
        }
        this.repositorioConfiteria = repositorioConfiteria;
    }

    public Producto crearProducto(String nombre, String descripcion, BigDecimal precio, boolean disponible, CategoriaProducto categoria) {
        Producto producto = new Producto(null, nombre, descripcion, precio, disponible, categoria);
        repositorioConfiteria.guardar(producto);
        return producto;
    }

    public Producto editarProducto(String nombreActual, String nuevoNombre, String descripcion, BigDecimal precio, boolean disponible, CategoriaProducto categoria) {
        ElementoVendible elem = buscarPorNombre(nombreActual);
        if (elem instanceof PromocionDecorator dec) {
            elem = dec.getElementoOriginal();
        }
        if (!(elem instanceof Producto producto)) {
            throw new IllegalArgumentException("El elemento no es un producto");
        }
        if (!nombreActual.equalsIgnoreCase(nuevoNombre)) {
            repositorioConfiteria.eliminar(nombreActual);
        }
        producto.setNombre(nuevoNombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setDisponible(disponible);
        producto.setCategoria(categoria);
        repositorioConfiteria.guardar(producto);
        return producto;
    }

    public void eliminarProducto(String nombre) {
        buscarPorNombre(nombre);
        repositorioConfiteria.eliminar(nombre);
    }

    public Combo crearCombo(String nombre, List<ElementoVendible> elementos) {
        Combo combo = new Combo(null, elementos, nombre);
        repositorioConfiteria.guardar(combo);
        return combo;
    }

    public Combo editarCombo(String nombreActual, String nuevoNombre, List<ElementoVendible> elementos) {
        ElementoVendible elem = buscarPorNombre(nombreActual);
        if (elem instanceof PromocionDecorator dec) {
            elem = dec.getElementoOriginal();
        }
        if (!(elem instanceof Combo combo)) {
            throw new IllegalArgumentException("El elemento no es un combo");
        }
        if (!nombreActual.equalsIgnoreCase(nuevoNombre)) {
            repositorioConfiteria.eliminar(nombreActual);
        }
        Combo comboActualizado = new Combo(combo.getId(), elementos, nuevoNombre);
        repositorioConfiteria.guardar(comboActualizado);
        return comboActualizado;
    }

    public void eliminarCombo(String nombre) {
        buscarPorNombre(nombre);
        repositorioConfiteria.eliminar(nombre);
    }

    public void aplicarDescuentoPorcentaje(String nombre, BigDecimal porcentaje) {
        ElementoVendible elem = buscarPorNombre(nombre);
        ElementoVendible base = elem instanceof PromocionDecorator dec ? dec.getElementoOriginal() : elem;
        DescuentoPorcentaje decorado = new DescuentoPorcentaje(base, porcentaje);
        repositorioConfiteria.guardar(decorado);
    }

    public void aplicarDescuentoValorFijo(String nombre, BigDecimal valor) {
        ElementoVendible elem = buscarPorNombre(nombre);
        ElementoVendible base = elem instanceof PromocionDecorator dec ? dec.getElementoOriginal() : elem;
        DescuentoValorFijo decorado = new DescuentoValorFijo(base, valor);
        repositorioConfiteria.guardar(decorado);
    }

    public void quitarPromocion(String nombre) {
        ElementoVendible elem = buscarPorNombre(nombre);
        if (elem instanceof PromocionDecorator dec) {
            repositorioConfiteria.guardar(dec.getElementoOriginal());
        }
    }

    public ElementoVendible buscarPorNombre(String nombre) {
        return repositorioConfiteria.buscarPorNombre(nombre)
                .orElseThrow(() -> new EntidadNoEncontradaException("Elemento vendible no encontrado: " + nombre));
    }

    public List<ElementoVendible> listar() {
        return repositorioConfiteria.listar();
    }

    public List<Producto> listarProductos() {
        List<Producto> productos = new ArrayList<>();
        for (ElementoVendible elem : repositorioConfiteria.listar()) {
            ElementoVendible base = elem instanceof PromocionDecorator dec ? dec.getElementoOriginal() : elem;
            if (base instanceof Producto p) {
                productos.add(p);
            }
        }
        return productos;
    }

    public List<Combo> listarCombos() {
        List<Combo> combos = new ArrayList<>();
        for (ElementoVendible elem : repositorioConfiteria.listar()) {
            ElementoVendible base = elem instanceof PromocionDecorator dec ? dec.getElementoOriginal() : elem;
            if (base instanceof Combo c) {
                combos.add(c);
            }
        }
        return combos;
    }
}
