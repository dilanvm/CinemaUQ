package edu.uniquindio.cinemauq.infrastructure;

import edu.uniquindio.cinemauq.domain.Funcion;
import edu.uniquindio.cinemauq.domain.RepositorioFunciones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class RepositorioFuncionesEnMemoria implements RepositorioFunciones {
    private final Map<Long, Funcion> funciones;
    private final AtomicLong contadorId;

    public RepositorioFuncionesEnMemoria() {
        this.funciones = new LinkedHashMap<>();
        this.contadorId = new AtomicLong(1);
    }

    @Override
    public synchronized Funcion guardar(Funcion funcion) {
        if (funcion == null) {
            throw new IllegalArgumentException("La funcion es obligatoria");
        }
        if (funcion.getId() == null || funcion.getId() <= 0) {
            funcion.setId(this.contadorId.getAndIncrement());
        }
        this.funciones.put(funcion.getId(), funcion);
        return funcion;
    }

    @Override
    public synchronized Optional<Funcion> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.funciones.get(id));
    }

    @Override
    public synchronized List<Funcion> listar() {
        return Collections.unmodifiableList(new ArrayList<>(this.funciones.values()));
    }

    @Override
    public synchronized void eliminar(Long id) {
        if (id != null) {
            this.funciones.remove(id);
        }
    }
}
