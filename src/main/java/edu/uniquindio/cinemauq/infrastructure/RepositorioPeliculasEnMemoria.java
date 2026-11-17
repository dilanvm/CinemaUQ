package edu.uniquindio.cinemauq.infrastructure;

import edu.uniquindio.cinemauq.domain.Pelicula;
import edu.uniquindio.cinemauq.domain.RepositorioPeliculas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class RepositorioPeliculasEnMemoria implements RepositorioPeliculas {
    private final Map<Long, Pelicula> peliculas;
    private final AtomicLong contadorId;

    public RepositorioPeliculasEnMemoria() {
        this.peliculas = new LinkedHashMap<>();
        this.contadorId = new AtomicLong(1);
    }

    @Override
    public synchronized Pelicula guardar(Pelicula pelicula) {
        if (pelicula == null) {
            throw new IllegalArgumentException("La pelicula es obligatoria");
        }
        if (pelicula.getId() == null || pelicula.getId() <= 0) {
            pelicula.setId(this.contadorId.getAndIncrement());
        }
        this.peliculas.put(pelicula.getId(), pelicula);
        return pelicula;
    }

    @Override
    public synchronized Optional<Pelicula> buscarPorId(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.peliculas.get(id));
    }

    @Override
    public synchronized List<Pelicula> listar() {
        return Collections.unmodifiableList(new ArrayList<>(this.peliculas.values()));
    }

    @Override
    public synchronized void eliminar(Long id) {
        if (id != null) {
            this.peliculas.remove(id);
        }
    }
}
