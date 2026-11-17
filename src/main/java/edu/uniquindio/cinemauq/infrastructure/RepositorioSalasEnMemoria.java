package edu.uniquindio.cinemauq.infrastructure;

import edu.uniquindio.cinemauq.domain.RepositorioSalas;
import edu.uniquindio.cinemauq.domain.Sala;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class RepositorioSalasEnMemoria implements RepositorioSalas {
    private final Map<Long, Sala> salas;
    private final AtomicLong contadorId;

    public RepositorioSalasEnMemoria() {
        this.salas = new LinkedHashMap<>();
        this.contadorId = new AtomicLong(1);
    }

    @Override
    public synchronized Sala guardar(Sala sala) {
        if (sala == null) {
            throw new IllegalArgumentException("La sala es obligatoria");
        }
        if (sala.getId() == null || sala.getId() <= 0) {
            sala.setId(this.contadorId.getAndIncrement());
        }
        this.salas.put(sala.getId(), sala);
        return sala;
    }

    @Override
    public synchronized Optional<Sala> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.salas.get(id));
    }

    @Override
    public synchronized List<Sala> listar() {
        return Collections.unmodifiableList(new ArrayList<>(this.salas.values()));
    }

    @Override
    public synchronized void eliminar(Long id) {
        if (id != null) {
            this.salas.remove(id);
        }
    }
}
