package edu.uniquindio.cinemauq.infrastructure;

import edu.uniquindio.cinemauq.domain.Cliente;
import edu.uniquindio.cinemauq.domain.Compra;
import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.RepositorioCompras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class RepositorioComprasEnMemoria implements RepositorioCompras {
    private final Map<Long, Compra> compras;
    private final AtomicLong contadorId;

    public RepositorioComprasEnMemoria() {
        this.compras = new HashMap<>();
        this.contadorId = new AtomicLong(1);
    }

    @Override
    public synchronized Compra guardar(Compra compra) {
        if (compra == null) {
            throw new IllegalArgumentException("La compra es obligatoria");
        }
        if (compra.getId() == null || compra.getId() <= 0) {
            compra.setId(this.contadorId.getAndIncrement());
        }
        this.compras.put(compra.getId(), compra);
        return compra;
    }

    @Override
    public synchronized Optional<Compra> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.compras.get(id));
    }

    @Override
    public synchronized List<Compra> buscarPorCliente(Cliente cliente) {
        if (cliente == null || cliente.getId() == null) {
            return Collections.emptyList();
        }
        List<Compra> lista = new ArrayList<>();
        for (Compra compra : this.compras.values()) {
            if (compra.getCliente() != null && cliente.getId().equals(compra.getCliente().getId())) {
                lista.add(compra);
            }
        }
        return Collections.unmodifiableList(lista);
    }

    @Override
    public synchronized List<Compra> buscarPorFuncion(Funcion funcion) {
        if (funcion == null || funcion.getId() == null) {
            return Collections.emptyList();
        }
        List<Compra> lista = new ArrayList<>();
        for (Compra compra : this.compras.values()) {
            if (compra.getFuncion() != null && funcion.getId().equals(compra.getFuncion().getId())) {
                lista.add(compra);
            }
        }
        return Collections.unmodifiableList(lista);
    }

    @Override
    public synchronized List<Compra> listar() {
        return Collections.unmodifiableList(new ArrayList<>(this.compras.values()));
    }
}
