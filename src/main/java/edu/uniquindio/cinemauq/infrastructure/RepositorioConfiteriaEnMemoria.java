package edu.uniquindio.cinemauq.infrastructure;

import edu.uniquindio.cinemauq.domain.Combo;
import edu.uniquindio.cinemauq.domain.ElementoVendible;
import edu.uniquindio.cinemauq.domain.Producto;
import edu.uniquindio.cinemauq.domain.RepositorioConfiteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class RepositorioConfiteriaEnMemoria implements RepositorioConfiteria {
    private final Map<String, ElementoVendible> elementos;
    private final AtomicLong contadorId;

    public RepositorioConfiteriaEnMemoria() {
        this.elementos = new LinkedHashMap<>();
        this.contadorId = new AtomicLong(1);
    }

    @Override
    public synchronized ElementoVendible guardar(ElementoVendible elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento vendible es obligatorio");
        }
        if (elemento instanceof Producto producto) {
            if (producto.getId() == null || producto.getId() <= 0) {
                producto.setId(this.contadorId.getAndIncrement());
            }
        } else if (elemento instanceof Combo combo) {
            if (combo.getId() == null || combo.getId() <= 0) {
                combo.setId(this.contadorId.getAndIncrement());
            }
        }
        this.elementos.put(elemento.getNombre().trim().toLowerCase(), elemento);
        return elemento;
    }

    @Override
    public synchronized Optional<ElementoVendible> buscarPorNombre(String nombre) {
        if (nombre == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.elementos.get(nombre.trim().toLowerCase()));
    }

    @Override
    public synchronized List<ElementoVendible> listar() {
        return Collections.unmodifiableList(new ArrayList<>(this.elementos.values()));
    }

    @Override
    public synchronized void eliminar(String nombre) {
        if (nombre != null) {
            this.elementos.remove(nombre.trim().toLowerCase());
        }
    }
}
